package com.springboot.service.experiment.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.experiment.EduExperimentItemMapper;
import com.springboot.mapper.experiment.ResExperimentResultMapper;
import com.springboot.mapper.user.AuthStudentMapper;
import com.springboot.mapper.user.AuthClassMapper;
import com.springboot.model.dto.experiment.EduExperimentReportGradeRequest;
import com.springboot.model.dto.experiment.EduExperimentReportQueryRequest;
import com.springboot.model.dto.experiment.EduExperimentReportSubmitRequest;
import com.springboot.model.entity.experiment.EduExperiment;
import com.springboot.model.entity.experiment.EduExperimentItem;
import com.springboot.model.entity.experiment.ResExperimentResult;
import com.springboot.model.entity.user.AuthClass;
import com.springboot.model.entity.user.AuthStudent;
import com.springboot.model.vo.experiment.EduExperimentReportListVO;
import com.springboot.model.vo.experiment.EduExperimentReportVO;
import com.springboot.service.experiment.EduExperimentReportService;
import com.springboot.service.experiment.EduExperimentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EduExperimentReportServiceImpl implements EduExperimentReportService {

    @Resource
    private EduExperimentItemMapper eduExperimentItemMapper;

    @Resource
    private ResExperimentResultMapper resExperimentResultMapper;

    @Resource
    private AuthStudentMapper authStudentMapper;

    @Resource
    private AuthClassMapper authClassMapper;

    @Resource
    private EduExperimentService eduExperimentService;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    public EduExperimentReportVO getStudentReport(Long experimentId, Long studentId) {
        if (experimentId == null || studentId == null) {
            return null;
        }

        EduExperimentReportVO vo = new EduExperimentReportVO();
        vo.setExperimentId(experimentId);
        vo.setStudentId(studentId);

        // 获取实验信息
        EduExperiment experiment = eduExperimentService.getById(experimentId);
        if (experiment != null) {
            vo.setExperimentName(experiment.getName());
            vo.setCourseName(experiment.getName());
            if (experiment.getCreatedAt() != null) {
                vo.setSchedule(DATE_FORMAT.format(experiment.getCreatedAt()));
            }
            vo.setObjective(experiment.getRequirement());
            vo.setContent(experiment.getContentDesc());
        }

        // 获取学生信息
        AuthStudent student = authStudentMapper.selectById(studentId);
        if (student != null) {
            vo.setStudentNo(student.getStudentCode());
            vo.setStudentName(student.getStudentName());
            if (student.getClassCode() != null) {
                AuthClass clazz = authClassMapper.selectById(student.getClassCode());
                if (clazz != null) {
                    vo.setClazzNo(clazz.getClassCode());
                }
            }
        }

        // 查询学生提交的答题记录
        List<EduExperimentReportVO.ReportQuestionVO> questions = new ArrayList<>();

        // 直接使用 Mapper 查询，带上正确的表名和字段名
        QueryWrapper<EduExperimentItem> itemQuery = new QueryWrapper<>();
        itemQuery.eq("experiment_id", experimentId);
        itemQuery.orderByAsc("experiment_item_no");
        List<EduExperimentItem> items = eduExperimentItemMapper.selectList(itemQuery);

        int totalScore = 0;
        Date latestSubmitTime = null;
        String summaryNote = "";

        if (items != null && !items.isEmpty()) {
            for (int i = 0; i < items.size(); i++) {
                EduExperimentItem item = items.get(i);
                EduExperimentReportVO.ReportQuestionVO questionVO = new EduExperimentReportVO.ReportQuestionVO();
                questionVO.setId(String.valueOf(item.getId()));
                questionVO.setExperimentItemId(String.valueOf(item.getId()));
                questionVO.setStepNo(i + 1);
                questionVO.setTitle(item.getItemName());
                questionVO.setType(item.getQuestionType());
                questionVO.setContent(item.getQuestionContent());
                questionVO.setScore(item.getMaxScore());
                questionVO.setStandardAnswer(item.getStandardAnswer());

                // 尝试查询学生的答题记录（如果没有 res_experiment_result 表则跳过）
                try {
                    QueryWrapper<ResExperimentResult> resultQuery = new QueryWrapper<>();
                    resultQuery.eq("item_id", item.getId());
                    resultQuery.eq("student_id", studentId);
                    resultQuery.orderByDesc("fill_time");
                    resultQuery.last("LIMIT 1");
                    System.out.println("[getStudentReport] 查询答题记录: itemId=" + item.getId() + ", studentId=" + studentId);
                    ResExperimentResult result = resExperimentResultMapper.selectOne(resultQuery);

                    System.out.println("[getStudentReport] 查询结果: " + (result != null ? "找到记录, subContent=" + (result.getSubContent() != null ? result.getSubContent().substring(0, Math.min(30, result.getSubContent().length())) : "null") + ", gradingStatus=" + result.getGradingStatus() : "未找到记录"));

                    if (result != null) {
                        questionVO.setStudentAnswer(result.getSubContent());
                        if (result.getScore() != null) {
                            questionVO.setTeacherScore(result.getScore());
                            totalScore += result.getScore();
                        }
                        if (result.getGradingStatus() != null && result.getGradingStatus() == 2) {
                            questionVO.setTeacherComment("已批改");
                        }
                        if (result.getFillTime() != null) {
                            if (latestSubmitTime == null || result.getFillTime().after(latestSubmitTime)) {
                                latestSubmitTime = result.getFillTime();
                            }
                        }

                        // 检查是否是"实验小结"
                        if (item.getItemName() != null && item.getItemName().contains("小结")) {
                            summaryNote = result.getSubContent() != null ? result.getSubContent() : "";
                        }
                    }
                } catch (Exception e) {
                    // 表不存在时忽略，继续使用空的学生答题信息
                }

                questions.add(questionVO);
            }
        }

        // 设置提交时间
        if (latestSubmitTime != null) {
            vo.setSubmittedAt(DATE_FORMAT.format(latestSubmitTime));
        }

        // 设置总得分
        if (totalScore > 0) {
            vo.setTeacherScore(String.valueOf(totalScore));
        }

        // 设置状态
        if (latestSubmitTime != null) {
            // 检查是否有批改记录
            boolean hasGraded = questions.stream()
                    .anyMatch(q -> q.getTeacherScore() != null && q.getTeacherComment() != null);
            vo.setStatus(hasGraded ? "reviewed" : "submitted");
        } else {
            vo.setStatus("pending");
        }

        vo.setSummaryNote(summaryNote);
        vo.setQuestions(questions);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveStudentAnswer(EduExperimentReportSubmitRequest request) {
        if (request == null || request.getExperimentId() == null || request.getStudentId() == null) {
            System.out.println("[saveStudentAnswer] 参数校验失败: request=" + request + ", experimentId=" + request.getExperimentId() + ", studentId=" + request.getStudentId());
            return false;
        }

        System.out.println("[saveStudentAnswer] 开始保存学生答案: experimentId=" + request.getExperimentId() + ", studentId=" + request.getStudentId() + ", answers数量=" + (request.getAnswers() != null ? request.getAnswers().size() : 0));

        Date now = new Date();

        if (request.getAnswers() != null && !request.getAnswers().isEmpty()) {
            for (EduExperimentReportSubmitRequest.QuestionAnswer answer : request.getAnswers()) {
                if (answer.getItemId() == null) {
                    System.out.println("[saveStudentAnswer] 跳过空的itemId");
                    continue;
                }

                System.out.println("[saveStudentAnswer] 处理答案: itemId=" + answer.getItemId() + " (类型:" + (answer.getItemId() != null ? answer.getItemId().getClass().getName() : "null") + "), answer=" + (answer.getAnswer() != null ? answer.getAnswer().substring(0, Math.min(50, answer.getAnswer().length())) : "null"));

                // 查询是否已有记录
                QueryWrapper<ResExperimentResult> resultQuery = new QueryWrapper<>();
                resultQuery.eq("item_id", answer.getItemId());
                resultQuery.eq("student_id", request.getStudentId());
                System.out.println("[saveStudentAnswer] 执行查询: SELECT * FROM t_student_item WHERE item_id=" + answer.getItemId() + " AND student_id=" + request.getStudentId());
                ResExperimentResult existingResult = resExperimentResultMapper.selectOne(resultQuery);

                if (existingResult != null) {
                    System.out.println("[saveStudentAnswer] 找到已有记录, id=" + existingResult.getId() + ", 原内容=" + existingResult.getSubContent());
                    // 更新已有记录
                    existingResult.setSubContent(answer.getAnswer());
                    existingResult.setFillTime(now);
                    existingResult.setGradingStatus(1); // 已提交
                    int updateCount = resExperimentResultMapper.updateById(existingResult);
                    System.out.println("[saveStudentAnswer] 更新记录, 影响行数=" + updateCount);
                } else {
                    System.out.println("[saveStudentAnswer] 未找到已有记录, 创建新记录");
                    // 新增记录
                    ResExperimentResult newResult = new ResExperimentResult();
                    newResult.setItemId(answer.getItemId());
                    newResult.setStudentId(request.getStudentId());
                    newResult.setSubContent(answer.getAnswer());
                    newResult.setFillTime(now);
                    newResult.setGradingStatus(1); // 已提交
                    int insertCount = resExperimentResultMapper.insert(newResult);
                    System.out.println("[saveStudentAnswer] 新增记录, 影响行数=" + insertCount + ", 新记录ID=" + newResult.getId());
                }
            }
        } else {
            System.out.println("[saveStudentAnswer] answers为空或null");
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean gradeReport(EduExperimentReportGradeRequest request) {
        if (request == null || request.getExperimentId() == null || request.getStudentId() == null) {
            return false;
        }

        Date now = new Date();

        if (request.getScores() != null && !request.getScores().isEmpty()) {
            for (EduExperimentReportGradeRequest.QuestionScore score : request.getScores()) {
                if (score.getItemId() == null) continue;

                QueryWrapper<ResExperimentResult> resultQuery = new QueryWrapper<>();
                resultQuery.eq("item_id", score.getItemId());
                resultQuery.eq("student_id", request.getStudentId());
                ResExperimentResult result = resExperimentResultMapper.selectOne(resultQuery);

                if (result != null) {
                    result.setScore(score.getScore());
                    result.setGradingStatus(2); // 教师已批改
                    resExperimentResultMapper.updateById(result);
                }
            }
        }

        return true;
    }

    @Override
    public List<EduExperimentReportVO> getStudentReportList(Long studentId) {
        System.out.println("[学生报告列表] 开始查询, studentId=" + studentId);

        if (studentId == null) {
            return Collections.emptyList();
        }

        Set<Long> experimentIds = new HashSet<>();

        // 获取所有已发布的实验（不需要 res_experiment_result 表）
        List<EduExperiment> allExperiments = eduExperimentService.list();
        System.out.println("[学生报告列表] 总实验数: " + (allExperiments != null ? allExperiments.size() : 0));

        for (EduExperiment experiment : allExperiments) {
            // state = 1 或 publishStatus = 1 表示已发布
            if (experiment.getPublishStatus() != null && experiment.getPublishStatus() == 1) {
                experimentIds.add(experiment.getId());
            }
        }

        System.out.println("[学生报告列表] 已发布实验数: " + experimentIds.size());

        // 为每个实验生成报告
        List<EduExperimentReportVO> result = experimentIds.stream()
                .map(expId -> getStudentReport(expId, studentId))
                .filter(Objects::nonNull)
                // 只返回学生至少提交过一次答题的实验报告
                .filter(report -> report.getSubmittedAt() != null && !report.getSubmittedAt().isEmpty())
                .collect(Collectors.toList());

        System.out.println("[学生报告列表] 返回报告数: " + result.size());
        return result;
    }

    @Override
    public List<EduExperimentReportVO> getExperimentReportList(Long experimentId) {
        if (experimentId == null) {
            return Collections.emptyList();
        }

        // 查询实验下的所有题目
        QueryWrapper<EduExperimentItem> itemQuery = new QueryWrapper<>();
        itemQuery.eq("experiment_id", experimentId);
        List<EduExperimentItem> items = eduExperimentItemMapper.selectList(itemQuery);

        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }

        // 收集所有已提交的学生ID
        Set<Long> studentIds = new HashSet<>();
        try {
            for (EduExperimentItem item : items) {
                QueryWrapper<ResExperimentResult> resultQuery = new QueryWrapper<>();
                resultQuery.eq("item_id", item.getId());
                List<ResExperimentResult> results = resExperimentResultMapper.selectList(resultQuery);
                for (ResExperimentResult result : results) {
                    if (result.getStudentId() != null) {
                        studentIds.add(result.getStudentId());
                    }
                }
            }
        } catch (Exception e) {
            // 表不存在时返回空列表
            return Collections.emptyList();
        }

        // 为每个学生生成报告
        return studentIds.stream()
                .map(studentId -> getStudentReport(experimentId, studentId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Page<EduExperimentReportListVO> getExperimentReportListPage(EduExperimentReportQueryRequest queryRequest) {
        System.out.println("[教师报告列表] 开始查询, queryRequest=" + queryRequest + ", clazzNo=" + (queryRequest != null ? queryRequest.getClazzNo() : "null"));

        if (queryRequest == null) {
            System.out.println("[教师报告列表] 参数验证失败: queryRequest is null");
            return new Page<>();
        }

        Long current = queryRequest.getCurrent() != null ? queryRequest.getCurrent() : 1L;
        Long pageSize = queryRequest.getPageSize() != null ? queryRequest.getPageSize() : 10L;

        String experimentName = "";
        Integer experimentNo = null;
        Set<Long> itemIds = new HashSet<>();

        if (queryRequest.getExperimentId() != null) {
            System.out.println("[教师报告列表] 查询实验信息, experimentId=" + queryRequest.getExperimentId());
            EduExperiment experiment = eduExperimentService.getById(queryRequest.getExperimentId());
            experimentName = experiment != null ? experiment.getName() : "";
            experimentNo = experiment != null ? experiment.getSortOrder() : null;

            QueryWrapper<EduExperimentItem> itemQuery = new QueryWrapper<>();
            itemQuery.eq("experiment_id", queryRequest.getExperimentId());
            itemQuery.orderByAsc("experiment_item_no");
            List<EduExperimentItem> items = eduExperimentItemMapper.selectList(itemQuery);
            itemIds = items.stream()
                    .map(EduExperimentItem::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        } else {
            System.out.println("[教师报告列表] 查询全部实验报告");
            List<EduExperimentItem> items = eduExperimentItemMapper.selectList(new QueryWrapper<>());
            itemIds = items.stream()
                    .map(EduExperimentItem::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }

        // 查询所有学生（不依赖 res_experiment_result 表）
        System.out.println("[教师报告列表] 开始查询学生列表");
        QueryWrapper<AuthStudent> studentQuery = new QueryWrapper<>();
        // 按班级筛选
        if (queryRequest.getClazzNo() != null && !queryRequest.getClazzNo().isEmpty()) {
            studentQuery.eq("class_code", queryRequest.getClazzNo());
        }
        List<AuthStudent> students = authStudentMapper.selectList(studentQuery);
        System.out.println("[教师报告列表] 查询到学生数量: " + (students != null ? students.size() : 0));
        if (students != null && !students.isEmpty()) {
            AuthStudent firstStudent = students.get(0);
            System.out.println("[教师报告列表] 首个学生: id=" + firstStudent.getId()
                    + ", studentCode=" + firstStudent.getStudentCode()
                    + ", studentName=" + firstStudent.getStudentName()
                    + ", classCode=" + firstStudent.getClassCode());
        }

        if (students == null || students.isEmpty()) {
            System.out.println("[教师报告列表] 学生列表为空，返回空分页");
            Page<EduExperimentReportListVO> resultPage = new Page<>(current, pageSize);
            resultPage.setTotal(0);
            resultPage.setRecords(new ArrayList<>());
            return resultPage;
        }

        // 查询班级信息
        Set<String> classCodes = students.stream()
                .map(AuthStudent::getClassCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<String, AuthClass> classMap = new HashMap<>();
        if (!classCodes.isEmpty()) {
            List<AuthClass> classes = authClassMapper.selectBatchIds(classCodes);
            for (AuthClass clazz : classes) {
                classMap.put(clazz.getClassCode(), clazz);
            }
        }

        // 批量查询当前实验下、当前学生范围内的所有提交记录
        Map<Long, List<ResExperimentResult>> studentResultMap = new HashMap<>();
        if (!itemIds.isEmpty()) {
            Set<Long> studentIds = students.stream()
                    .map(AuthStudent::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            if (!studentIds.isEmpty()) {
                QueryWrapper<ResExperimentResult> resultQuery = new QueryWrapper<>();
                resultQuery.in("item_id", itemIds);
                resultQuery.in("student_id", studentIds);
                List<ResExperimentResult> allResults = resExperimentResultMapper.selectList(resultQuery);
                studentResultMap = allResults.stream()
                        .filter(result -> result.getStudentId() != null)
                        .collect(Collectors.groupingBy(ResExperimentResult::getStudentId));
            }
        }

        // 构建报告列表
        List<EduExperimentReportListVO> reportList = new ArrayList<>();
        for (AuthStudent student : students) {
            EduExperimentReportListVO vo = new EduExperimentReportListVO();
            vo.setId(student.getId());
            vo.setExperimentId(queryRequest.getExperimentId());
            vo.setExperimentName(experimentName);
            vo.setExperimentNo(experimentNo);
            vo.setStudentId(student.getId());
            vo.setStudentNo(student.getStudentCode());
            vo.setStudentName(student.getStudentName());

            // 班级信息
            String classCode = student.getClassCode();
            if (classCode != null && classMap.containsKey(classCode)) {
                vo.setClazzNo(classMap.get(classCode).getClassCode());
            } else {
                vo.setClazzNo(classCode);
            }

            List<ResExperimentResult> studentResults = studentResultMap.getOrDefault(student.getId(), Collections.emptyList());

            if (queryRequest.getExperimentId() == null && !studentResults.isEmpty()) {
                ResExperimentResult latestResult = studentResults.stream()
                        .filter(result -> result.getItemId() != null)
                        .max(Comparator.comparing(ResExperimentResult::getFillTime, Comparator.nullsLast(Date::compareTo)))
                        .orElse(null);
                if (latestResult != null) {
                    EduExperimentItem latestItem = eduExperimentItemMapper.selectById(latestResult.getItemId());
                    if (latestItem != null) {
                        vo.setExperimentId(latestItem.getExperimentId());
                        EduExperiment latestExperiment = eduExperimentService.getById(latestItem.getExperimentId());
                        if (latestExperiment != null) {
                            vo.setExperimentName(latestExperiment.getName());
                            vo.setExperimentNo(latestExperiment.getSortOrder());
                        }
                    }
                }
            }
            if (studentResults.isEmpty()) {
                vo.setStatus("pending");
                vo.setTotalScore(null);
                vo.setSubmittedAt(null);
                vo.setReviewedAt(null);
            } else {
                Date latestSubmitTime = studentResults.stream()
                        .map(ResExperimentResult::getFillTime)
                        .filter(Objects::nonNull)
                        .max(Date::compareTo)
                        .orElse(null);
                boolean allReviewed = studentResults.stream()
                        .allMatch(result -> result.getGradingStatus() != null && result.getGradingStatus() == 2);
                int totalScore = studentResults.stream()
                        .map(ResExperimentResult::getScore)
                        .filter(Objects::nonNull)
                        .mapToInt(Integer::intValue)
                        .sum();

                vo.setStatus(allReviewed ? "reviewed" : "submitted");
                vo.setTotalScore(studentResults.stream().anyMatch(result -> result.getScore() != null) ? totalScore : null);
                vo.setSubmittedAt(latestSubmitTime != null ? DATE_FORMAT.format(latestSubmitTime) : null);
                vo.setReviewedAt(allReviewed && latestSubmitTime != null ? DATE_FORMAT.format(latestSubmitTime) : null);
            }

            reportList.add(vo);
        }

        // 状态筛选（只显示未提交的）
        if (queryRequest.getStatus() != null && !"all".equals(queryRequest.getStatus())) {
            reportList = reportList.stream()
                    .filter(vo -> queryRequest.getStatus().equals(vo.getStatus()))
                    .collect(Collectors.toList());
        }

        // 分页
        int total = reportList.size();
        int fromIndex = (int) ((current - 1) * pageSize);
        int toIndex = Math.min(fromIndex + pageSize.intValue(), total);

        Page<EduExperimentReportListVO> resultPage = new Page<>(current, pageSize);
        resultPage.setTotal(total);

        if (fromIndex < total) {
            resultPage.setRecords(reportList.subList(fromIndex, toIndex));
        } else {
            resultPage.setRecords(new ArrayList<>());
        }

        System.out.println("[教师报告列表] 查询完成，返回记录数: " + resultPage.getRecords().size() + ", 总数: " + total);
        return resultPage;
    }
}
