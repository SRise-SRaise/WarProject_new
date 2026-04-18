package com.springboot.service.homework.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.common.ErrorCode;
import com.springboot.exception.BusinessException;
import com.springboot.mapper.homework.EduExerciseItemMapper;
import com.springboot.mapper.homework.EduExerciseMapper;
import com.springboot.mapper.homework.RelExerciseClassMapper;
import com.springboot.mapper.homework.ResExerciseRecordMapper;
import com.springboot.mapper.user.AuthClassMapper;
import com.springboot.mapper.user.AuthStudentMapper;
import com.springboot.mapper.user.AuthTeacherMapper;
import com.springboot.model.dto.homework.EduExerciseQueryRequest;
import com.springboot.model.dto.homework.ExercisePublishRequest;
import com.springboot.model.dto.homework.StudentExerciseQueryRequest;
import com.springboot.model.entity.homework.EduExercise;
import com.springboot.model.entity.homework.EduExerciseItem;
import com.springboot.model.entity.homework.RelExerciseClass;
import com.springboot.model.entity.homework.ResExerciseRecord;
import com.springboot.model.entity.user.AuthClass;
import com.springboot.model.entity.user.AuthStudent;
import com.springboot.model.entity.user.AuthTeacher;
import com.springboot.model.vo.homework.EduExerciseVO;
import com.springboot.model.vo.homework.StudentExerciseVO;
import com.springboot.service.homework.EduExerciseService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EduExerciseServiceImpl extends ServiceImpl<EduExerciseMapper, EduExercise> implements EduExerciseService {

    @Resource
    private RelExerciseClassMapper relExerciseClassMapper;

    @Resource
    private EduExerciseItemMapper eduExerciseItemMapper;

    @Resource
    private ResExerciseRecordMapper resExerciseRecordMapper;

    @Resource
    private AuthStudentMapper authStudentMapper;

    @Resource
    private AuthClassMapper authClassMapper;

    @Resource
    private AuthTeacherMapper authTeacherMapper;

    @Override
    public void validEduExercise(EduExercise eduExercise, boolean add) {
        ServiceMethodSupport.validEntity(eduExercise);
    }

    @Override
    public QueryWrapper<EduExercise> getQueryWrapper(EduExerciseQueryRequest queryRequest) {
        QueryWrapper<EduExercise> queryWrapper = ServiceMethodSupport.buildQueryWrapper(queryRequest);
        if (queryRequest.getTeacherId() != null) {
            queryWrapper.eq("teacher_id", queryRequest.getTeacherId());
        }
        if (queryRequest.getPublishStatus() != null) {
            queryWrapper.eq("publish_status", queryRequest.getPublishStatus());
        }
        if (queryRequest.getTaskName() != null && !queryRequest.getTaskName().isEmpty()) {
            queryWrapper.like("task_name", queryRequest.getTaskName());
        }
        return queryWrapper;
    }

    @Override
    public EduExerciseVO getEduExerciseVO(EduExercise eduExercise, HttpServletRequest request) {
        return EduExerciseVO.objToVo(eduExercise);
    }

    @Override
    public Page<EduExerciseVO> getEduExerciseVOPage(Page<EduExercise> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, EduExerciseVO::objToVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean publishExercise(ExercisePublishRequest publishRequest) {
        Long exerciseId = publishRequest.getExerciseId();
        List<String> classCodes = publishRequest.getClassCodes();

        if (exerciseId == null || CollUtil.isEmpty(classCodes)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "作业ID或班级列表为空");
        }

        // 查询作业是否存在
        EduExercise exercise = this.getById(exerciseId);
        if (exercise == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "作业不存在");
        }

        // 更新作业发布状态和时间
        exercise.setPublishStatus(1); // 已发布
        if (publishRequest.getStartTime() != null) {
            exercise.setStartTime(publishRequest.getStartTime());
        }
        if (publishRequest.getEndTime() != null) {
            exercise.setEndTime(publishRequest.getEndTime());
        }
        this.updateById(exercise);

        replaceExerciseClasses(exerciseId, classCodes);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveDraftAssign(ExercisePublishRequest publishRequest) {
        Long exerciseId = publishRequest.getExerciseId();
        if (exerciseId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "作业ID为空");
        }

        EduExercise exercise = this.getById(exerciseId);
        if (exercise == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "作业不存在");
        }

        if (publishRequest.getStartTime() != null) {
            exercise.setStartTime(publishRequest.getStartTime());
        }
        if (publishRequest.getEndTime() != null) {
            exercise.setEndTime(publishRequest.getEndTime());
        }
        this.updateById(exercise);

        if (publishRequest.getClassCodes() != null) {
            replaceExerciseClasses(exerciseId, publishRequest.getClassCodes());
        }
        return true;
    }

    private void replaceExerciseClasses(Long exerciseId, List<String> classCodes) {
        QueryWrapper<RelExerciseClass> deleteQuery = new QueryWrapper<>();
        deleteQuery.eq("exercise_id", exerciseId);
        relExerciseClassMapper.delete(deleteQuery);

        if (CollUtil.isEmpty(classCodes)) {
            return;
        }

        for (String classCode : classCodes) {
            RelExerciseClass rel = new RelExerciseClass();
            rel.setExerciseId(exerciseId);
            rel.setClassCode(classCode);
            rel.setCreatedAt(new Date());
            relExerciseClassMapper.insert(rel);
        }
    }

    @Override
    public Boolean closeExercise(Long exerciseId) {
        if (exerciseId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        EduExercise exercise = this.getById(exerciseId);
        if (exercise == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "作业不存在");
        }

        exercise.setPublishStatus(2); // 已关闭
        this.updateById(exercise);

        return true;
    }

    @Override
    public List<StudentExerciseVO> listForStudent(StudentExerciseQueryRequest queryRequest) {
        Long studentId = queryRequest.getStudentId();
        if (studentId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "学生ID为空");
        }

        // 查询学生信息获取班级
        AuthStudent student = authStudentMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "学生不存在");
        }
        String classCode = student.getClassCode();

        // 查询学生班级关联的作业
        QueryWrapper<RelExerciseClass> relQuery = new QueryWrapper<>();
        relQuery.eq("class_code", classCode);
        List<RelExerciseClass> rels = relExerciseClassMapper.selectList(relQuery);

        if (CollUtil.isEmpty(rels)) {
            return new ArrayList<>();
        }

        List<Long> exerciseIds = rels.stream()
                .map(RelExerciseClass::getExerciseId)
                .collect(Collectors.toList());

        // 查询作业列表
        QueryWrapper<EduExercise> exerciseQuery = new QueryWrapper<>();
        exerciseQuery.in("id", exerciseIds);
        exerciseQuery.eq("publish_status", 1); // 只显示已发布的
        exerciseQuery.orderByDesc("created_at");
        List<EduExercise> exercises = this.list(exerciseQuery);

        // 查询各作业的题目数量
        Map<Long, Integer> itemCountMap = new HashMap<>();
        for (Long exerciseId : exerciseIds) {
            QueryWrapper<EduExerciseItem> itemQuery = new QueryWrapper<>();
            itemQuery.eq("exercise_id", exerciseId);
            Long count = eduExerciseItemMapper.selectCount(itemQuery);
            itemCountMap.put(exerciseId, count.intValue());
        }

        // 查询学生答题记录
        QueryWrapper<ResExerciseRecord> recordQuery = new QueryWrapper<>();
        recordQuery.in("exercise_id", exerciseIds);
        recordQuery.eq("student_id", studentId);
        List<ResExerciseRecord> records = resExerciseRecordMapper.selectList(recordQuery);

        // 按作业ID分组答题记录
        Map<Long, List<ResExerciseRecord>> recordMap = records.stream()
                .collect(Collectors.groupingBy(ResExerciseRecord::getExerciseId));

        // 查询教师信息
        Map<Long, String> teacherNameMap = new HashMap<>();
        for (EduExercise exercise : exercises) {
            if (exercise.getTeacherId() != null) {
                AuthTeacher teacher = authTeacherMapper.selectById(exercise.getTeacherId());
                teacherNameMap.put(exercise.getTeacherId(), teacher != null ? teacher.getRealName() : "");
            }
        }

        // 构建学生作业视图
        List<StudentExerciseVO> result = new ArrayList<>();
        Date now = new Date();

        for (EduExercise exercise : exercises) {
            StudentExerciseVO vo = new StudentExerciseVO();
            vo.setId(exercise.getId());
            vo.setTaskName(exercise.getTaskName());
            vo.setDescription(exercise.getDescription());
            vo.setStartTime(exercise.getStartTime());
            vo.setEndTime(exercise.getEndTime());
            vo.setPublishStatus(exercise.getPublishStatus());
            vo.setItemCount(itemCountMap.getOrDefault(exercise.getId(), 0));
            vo.setTeacherName(teacherNameMap.getOrDefault(exercise.getTeacherId(), ""));

            // 计算作业满分
            QueryWrapper<EduExerciseItem> itemQuery = new QueryWrapper<>();
            itemQuery.eq("exercise_id", exercise.getId());
            List<EduExerciseItem> items = eduExerciseItemMapper.selectList(itemQuery);
            int maxScore = items.stream()
                    .map(i -> i.getMaxScore() != null ? i.getMaxScore() : 0)
                    .reduce(0, Integer::sum);
            vo.setTotalScore(maxScore);

            // 判断状态
            List<ResExerciseRecord> exerciseRecords = recordMap.getOrDefault(exercise.getId(), new ArrayList<>());
            if (exerciseRecords.isEmpty()) {
                // 未答题
                if (exercise.getEndTime() != null && exercise.getEndTime().before(now)) {
                    vo.setStatus("overdue");
                } else {
                    vo.setStatus("pending");
                }
                vo.setStudentScore(0);
            } else {
                // 已答题，判断是否批阅完成
                boolean allReviewed = exerciseRecords.stream()
                        .allMatch(r -> r.getGradingStatus() != null && r.getGradingStatus() > 0);
                if (allReviewed) {
                    vo.setStatus("reviewed");
                    int totalScore = exerciseRecords.stream()
                            .map(r -> r.getScore() != null ? r.getScore() : 0)
                            .reduce(0, Integer::sum);
                    vo.setStudentScore(totalScore);
                } else {
                    vo.setStatus("submitted");
                    // 计算已自动评分部分
                    int autoScore = exerciseRecords.stream()
                            .filter(r -> r.getGradingStatus() != null && r.getGradingStatus() == 1)
                            .map(r -> r.getScore() != null ? r.getScore() : 0)
                            .reduce(0, Integer::sum);
                    vo.setStudentScore(autoScore);
                }
            }

            result.add(vo);
        }

        // 状态筛选
        if (queryRequest.getStatus() != null && !queryRequest.getStatus().isEmpty()) {
            result = result.stream()
                    .filter(vo -> vo.getStatus().equals(queryRequest.getStatus()))
                    .collect(Collectors.toList());
        }

        // 关键词搜索
        if (queryRequest.getKeyword() != null && !queryRequest.getKeyword().isEmpty()) {
            String keyword = queryRequest.getKeyword().toLowerCase();
            result = result.stream()
                    .filter(vo -> vo.getTaskName() != null && vo.getTaskName().toLowerCase().contains(keyword))
                    .collect(Collectors.toList());
        }

        return result;
    }
}
