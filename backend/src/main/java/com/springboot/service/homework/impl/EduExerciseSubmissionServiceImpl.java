package com.springboot.service.homework.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.springboot.common.ErrorCode;
import com.springboot.exception.BusinessException;
import com.springboot.mapper.homework.EduExerciseItemMapper;
import com.springboot.mapper.homework.EduExerciseMapper;
import com.springboot.mapper.homework.RelExerciseItemMapper;
import com.springboot.mapper.homework.ResExerciseRecordMapper;
import com.springboot.mapper.user.AuthStudentMapper;
import com.springboot.model.dto.homework.*;
import com.springboot.model.entity.homework.EduExercise;
import com.springboot.model.entity.homework.EduExerciseItem;
import com.springboot.model.entity.homework.RelExerciseItem;
import com.springboot.model.entity.homework.ResExerciseRecord;
import com.springboot.model.entity.user.AuthStudent;
import com.springboot.model.vo.homework.*;
import com.springboot.service.homework.EduExerciseSubmissionService;
import com.springboot.service.homework.support.assembler.StudentScoreAssembler;
import com.springboot.service.homework.support.assembler.SubmissionDetailAssembler;
import com.springboot.service.homework.support.flow.SaveDraftFlow;
import com.springboot.service.homework.support.flow.SubmissionProcessSummary;
import com.springboot.service.homework.support.flow.SubmitAnswerFlow;
import com.springboot.service.homework.support.strategy.AnswerPersistenceStrategyFactory;
import com.springboot.service.homework.support.strategy.AutoGradeStrategyFactory;
import com.springboot.service.homework.support.status.GradingStatusMachine;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EduExerciseSubmissionServiceImpl implements EduExerciseSubmissionService {

    private final AnswerPersistenceStrategyFactory answerPersistenceStrategyFactory = new AnswerPersistenceStrategyFactory();
    private final AutoGradeStrategyFactory autoGradeStrategyFactory = new AutoGradeStrategyFactory();
    private final SubmitAnswerFlow submitAnswerFlow = new SubmitAnswerFlow(answerPersistenceStrategyFactory, autoGradeStrategyFactory);
    private final SaveDraftFlow saveDraftFlow = new SaveDraftFlow(answerPersistenceStrategyFactory);
    private final GradingStatusMachine gradingStatusMachine = new GradingStatusMachine();
    private final StudentScoreAssembler studentScoreAssembler = new StudentScoreAssembler();
    private final SubmissionDetailAssembler submissionDetailAssembler = new SubmissionDetailAssembler();

    @Resource
    private EduExerciseItemMapper eduExerciseItemMapper;

    @Resource
    private EduExerciseMapper eduExerciseMapper;

    @Resource
    private ResExerciseRecordMapper resExerciseRecordMapper;

    @Resource
    private RelExerciseItemMapper relExerciseItemMapper;

    @Resource
    private AuthStudentMapper authStudentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubmissionResultVO submitAnswers(ExerciseSubmitRequest submitRequest) {
        if (submitRequest == null || submitRequest.getExerciseId() == null || submitRequest.getStudentId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long exerciseId = submitRequest.getExerciseId();
        Long studentId = submitRequest.getStudentId();
        List<ExerciseSubmitRequest.AnswerItem> answers = submitRequest.getAnswers();

        if (CollUtil.isEmpty(answers)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案列表为空");
        }

        List<EduExerciseItem> items = listItemsByExerciseId(exerciseId);

        if (CollUtil.isEmpty(items)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "作业题目不存在");
        }

        SubmissionProcessSummary summary = new SubmissionProcessSummary();

        for (ExerciseSubmitRequest.AnswerItem answer : answers) {
            EduExerciseItem item = items.stream()
                    .filter(i -> i.getId().equals(answer.getItemId()))
                    .findFirst()
                    .orElse(null);

            if (item == null) continue;

            // 查找或创建答题记录
            QueryWrapper<ResExerciseRecord> recordQuery = new QueryWrapper<>();
            recordQuery.eq("student_id", studentId);
            recordQuery.eq("item_id", answer.getItemId());
            ResExerciseRecord record = resExerciseRecordMapper.selectOne(recordQuery);

            if (record == null) {
                record = new ResExerciseRecord();
                record.setExerciseId(exerciseId);
                record.setItemId(answer.getItemId());
                record.setStudentId(studentId);
            }

            submitAnswerFlow.process(record, item, answer, summary);

            // 保存或更新记录
            if (record.getId() == null) {
                resExerciseRecordMapper.insert(record);
            } else {
                resExerciseRecordMapper.updateById(record);
            }
        }

        SubmissionResultVO result = new SubmissionResultVO();
        result.setSuccess(true);
        result.setSubmittedCount(summary.getSubmittedCount());
        result.setAutoScore(summary.getAutoScore());
        result.setPendingReviewCount(summary.getPendingReviewCount());
        result.setMessage("提交成功，客观题已自动评分，主观题等待教师批阅");

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveDraft(ExerciseSubmitRequest saveRequest) {
        if (saveRequest == null || saveRequest.getExerciseId() == null || saveRequest.getStudentId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long exerciseId = saveRequest.getExerciseId();
        Long studentId = saveRequest.getStudentId();
        List<ExerciseSubmitRequest.AnswerItem> answers = saveRequest.getAnswers();

        if (CollUtil.isEmpty(answers)) {
            return true; // 无答案直接返回成功
        }

        SubmissionProcessSummary summary = new SubmissionProcessSummary();
        for (ExerciseSubmitRequest.AnswerItem answer : answers) {
            // 查找或创建答题记录
            QueryWrapper<ResExerciseRecord> recordQuery = new QueryWrapper<>();
            recordQuery.eq("student_id", studentId);
            recordQuery.eq("item_id", answer.getItemId());
            ResExerciseRecord record = resExerciseRecordMapper.selectOne(recordQuery);

            if (record == null) {
                record = new ResExerciseRecord();
                record.setExerciseId(exerciseId);
                record.setItemId(answer.getItemId());
                record.setStudentId(studentId);
            }

            saveDraftFlow.process(record, null, answer, summary);

            if (record.getId() == null) {
                resExerciseRecordMapper.insert(record);
            } else {
                resExerciseRecordMapper.updateById(record);
            }
        }

        return true;
    }

    @Override
    public StudentProgressVO getProgress(Long exerciseId, Long studentId) {
        if (exerciseId == null || studentId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        List<EduExerciseItem> items = listItemsByExerciseId(exerciseId);

        // 查询学生答题记录
        QueryWrapper<ResExerciseRecord> recordQuery = new QueryWrapper<>();
        recordQuery.eq("exercise_id", exerciseId);
        recordQuery.eq("student_id", studentId);
        List<ResExerciseRecord> records = resExerciseRecordMapper.selectList(recordQuery);

        Map<Long, ResExerciseRecord> recordMap = records.stream()
                .collect(Collectors.toMap(ResExerciseRecord::getItemId, r -> r));

        StudentProgressVO progress = new StudentProgressVO();
        progress.setExerciseId(exerciseId);
        progress.setStudentId(studentId);
        progress.setTotalCount(items.size());
        progress.setAnsweredCount(records.size());

        // 判断是否已正式提交（所有题目都有 submittedAt 且有 gradingStatus）
        boolean isSubmitted = !records.isEmpty() && records.stream()
                .allMatch(r -> r.getSubmittedAt() != null && r.getGradingStatus() != null);
        progress.setIsSubmitted(isSubmitted);

        // 设置最后更新时间
        Date lastUpdate = records.stream()
                .map(ResExerciseRecord::getSubmittedAt)
                .filter(Objects::nonNull)
                .max(Date::compareTo)
                .orElse(null);
        progress.setLastUpdateTime(lastUpdate);

        // 各题答题状态
        List<StudentProgressVO.ItemProgressVO> itemProgressList = new ArrayList<>();
        int order = 1;
        for (EduExerciseItem item : items) {
            StudentProgressVO.ItemProgressVO itemProgress = new StudentProgressVO.ItemProgressVO();
            itemProgress.setItemId(item.getId());
            itemProgress.setItemOrder(order++);
            itemProgress.setQuestionType(item.getQuestionType());

            ResExerciseRecord record = recordMap.get(item.getId());
            itemProgress.setIsAnswered(record != null);
            if (record != null) {
                itemProgress.setChoiceAnswer(record.getChoiceAnswer());
                itemProgress.setTextContent(record.getTextContent());
                if (record.getTextContent() != null && record.getTextContent().contains("|")) {
                    itemProgress.setFillCount(record.getTextContent().split("\\|").length);
                }
            }
            itemProgressList.add(itemProgress);
        }
        progress.setItems(itemProgressList);

        return progress;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer autoGrade(Long exerciseId, Long studentId) {
        if (exerciseId == null || studentId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        List<EduExerciseItem> items = listItemsByExerciseId(exerciseId);

        // 查询学生答题记录
        QueryWrapper<ResExerciseRecord> recordQuery = new QueryWrapper<>();
        recordQuery.eq("exercise_id", exerciseId);
        recordQuery.eq("student_id", studentId);
        List<ResExerciseRecord> records = resExerciseRecordMapper.selectList(recordQuery);

        int totalScore = 0;

        for (ResExerciseRecord record : records) {
            EduExerciseItem item = items.stream()
                    .filter(i -> i.getId().equals(record.getItemId()))
                    .findFirst()
                    .orElse(null);

            if (item == null) continue;

            // 只对客观题自动评分（题目类型 1-4）
            if (item.getQuestionType() != null && item.getQuestionType() <= 4) {
                ExerciseSubmitRequest.AnswerItem answerItem = new ExerciseSubmitRequest.AnswerItem();
                answerItem.setQuestionType(item.getQuestionType());
                answerItem.setChoiceAnswer(record.getChoiceAnswer());
                if (record.getTextContent() != null) {
                    String[] fills = record.getTextContent().split("\\|");
                    List<ExerciseSubmitRequest.FillBlankAnswer> fillBlanks = new ArrayList<>();
                    for (int i = 0; i < fills.length; i++) {
                        ExerciseSubmitRequest.FillBlankAnswer fill = new ExerciseSubmitRequest.FillBlankAnswer();
                        fill.setBlankIndex(i + 1);
                        fill.setAnswerContent(fills[i]);
                        fillBlanks.add(fill);
                    }
                    answerItem.setFillBlanks(fillBlanks);
                }

                Integer itemScore = autoGradeItem(item, answerItem);
                if (itemScore != null) {
                    record.setScore(itemScore);
                    record.setGradingStatus(1);
                    resExerciseRecordMapper.updateById(record);
                    totalScore += itemScore;
                }
            }
        }

        return totalScore;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchAutoGrade(Long exerciseId) {
        if (exerciseId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 查询所有未批改的记录
        QueryWrapper<ResExerciseRecord> recordQuery = new QueryWrapper<>();
        recordQuery.eq("exercise_id", exerciseId);
        recordQuery.eq("grading_status", 0);
        List<ResExerciseRecord> records = resExerciseRecordMapper.selectList(recordQuery);

        List<EduExerciseItem> items = listItemsByExerciseId(exerciseId);

        int gradedCount = 0;

        for (ResExerciseRecord record : records) {
            EduExerciseItem item = items.stream()
                    .filter(i -> i.getId().equals(record.getItemId()))
                    .findFirst()
                    .orElse(null);

            if (item == null) continue;

            // 只对客观题自动评分
            if (item.getQuestionType() != null && item.getQuestionType() <= 4) {
                ExerciseSubmitRequest.AnswerItem answerItem = new ExerciseSubmitRequest.AnswerItem();
                answerItem.setQuestionType(item.getQuestionType());
                answerItem.setChoiceAnswer(record.getChoiceAnswer());
                if (record.getTextContent() != null) {
                    String[] fills = record.getTextContent().split("\\|");
                    List<ExerciseSubmitRequest.FillBlankAnswer> fillBlanks = new ArrayList<>();
                    for (int i = 0; i < fills.length; i++) {
                        ExerciseSubmitRequest.FillBlankAnswer fill = new ExerciseSubmitRequest.FillBlankAnswer();
                        fill.setBlankIndex(i + 1);
                        fill.setAnswerContent(fills[i]);
                        fillBlanks.add(fill);
                    }
                    answerItem.setFillBlanks(fillBlanks);
                }

                Integer itemScore = autoGradeItem(item, answerItem);
                if (itemScore != null) {
                    record.setScore(itemScore);
                    record.setGradingStatus(1);
                    resExerciseRecordMapper.updateById(record);
                    gradedCount++;
                }
            }
        }

        return gradedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean reviewItem(ReviewItemRequest reviewRequest) {
        if (reviewRequest == null || reviewRequest.getRecordId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        ResExerciseRecord record = resExerciseRecordMapper.selectById(reviewRequest.getRecordId());
        if (record == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "答题记录不存在");
        }

        record.setScore(reviewRequest.getScore());
        record.setComment(reviewRequest.getComment());
        record.setGradingStatus(2); // 教师已批改

        resExerciseRecordMapper.updateById(record);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean completeReview(CompleteReviewRequest reviewRequest) {
        if (reviewRequest == null || reviewRequest.getExerciseId() == null || reviewRequest.getStudentId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 此方法可用于标记整体批阅完成，当前实现为空
        // 实际的批阅是通过 reviewItem 完成的
        return true;
    }

    @Override
    public StudentScoreVO getMyScore(Long exerciseId, Long studentId) {
        if (exerciseId == null || studentId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        EduExercise exercise = eduExerciseMapper.selectById(exerciseId);
        AuthStudent student = authStudentMapper.selectById(studentId);

        List<EduExerciseItem> items = listItemsByExerciseId(exerciseId);

        // 查询学生答题记录
        QueryWrapper<ResExerciseRecord> recordQuery = new QueryWrapper<>();
        recordQuery.eq("exercise_id", exerciseId);
        recordQuery.eq("student_id", studentId);
        List<ResExerciseRecord> records = resExerciseRecordMapper.selectList(recordQuery);

        Map<Long, ResExerciseRecord> recordMap = records.stream()
                .collect(Collectors.toMap(ResExerciseRecord::getItemId, r -> r));

        return studentScoreAssembler.assemble(exercise, student, items, records);
    }

    @Override
    public SubmissionDetailVO getSubmissionDetail(Long exerciseId, Long studentId) {
        if (exerciseId == null || studentId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        EduExercise exercise = eduExerciseMapper.selectById(exerciseId);
        // 查询学生信息
        AuthStudent student = authStudentMapper.selectById(studentId);

        List<EduExerciseItem> items = listItemsByExerciseId(exerciseId);

        // 查询学生答题记录
        QueryWrapper<ResExerciseRecord> recordQuery = new QueryWrapper<>();
        recordQuery.eq("exercise_id", exerciseId);
        recordQuery.eq("student_id", studentId);
        List<ResExerciseRecord> records = resExerciseRecordMapper.selectList(recordQuery);

        return submissionDetailAssembler.assemble(exercise, student, items, records);
    }

    @Override
    public List<SubmissionRecordVO> listSubmissionRecords(Long exerciseId) {
        if (exerciseId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        List<ResExerciseRecord> records = resExerciseRecordMapper.selectList(
                new QueryWrapper<ResExerciseRecord>()
                        .eq("exercise_id", exerciseId)
                        .orderByDesc("submitted_at")
                        .orderByDesc("id")
        );
        if (CollUtil.isEmpty(records)) {
            return Collections.emptyList();
        }

        Map<Long, List<ResExerciseRecord>> recordsByStudent = records.stream()
                .filter(record -> record.getStudentId() != null)
                .collect(Collectors.groupingBy(ResExerciseRecord::getStudentId, LinkedHashMap::new, Collectors.toList()));
        if (recordsByStudent.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, AuthStudent> studentMap = authStudentMapper.selectBatchIds(new ArrayList<>(recordsByStudent.keySet())).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(AuthStudent::getId, student -> student));

        return recordsByStudent.entrySet().stream()
                .map(entry -> toSubmissionRecordVO(exerciseId, entry.getKey(), entry.getValue(), studentMap.get(entry.getKey())))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(SubmissionRecordVO::getSubmittedAt, Comparator.nullsLast(Date::compareTo)).reversed())
                .collect(Collectors.toList());
    }

    private SubmissionRecordVO toSubmissionRecordVO(Long exerciseId,
                                                    Long studentId,
                                                    List<ResExerciseRecord> studentRecords,
                                                    AuthStudent student) {
        if (studentId == null || CollUtil.isEmpty(studentRecords)) {
            return null;
        }

        ResExerciseRecord latestRecord = studentRecords.stream()
                .max(Comparator.comparing(ResExerciseRecord::getSubmittedAt, Comparator.nullsLast(Date::compareTo))
                        .thenComparing(ResExerciseRecord::getId, Comparator.nullsLast(Long::compareTo)))
                .orElse(studentRecords.get(0));

        int totalScore = studentRecords.stream()
                .map(ResExerciseRecord::getScore)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
        long pendingCount = gradingStatusMachine.pendingCount(studentRecords);

        SubmissionRecordVO recordVO = new SubmissionRecordVO();
        recordVO.setId(latestRecord.getId());
        recordVO.setExerciseId(exerciseId);
        recordVO.setStudentId(studentId);
        recordVO.setStudentName(student != null ? student.getStudentName() : "学生" + studentId);
        recordVO.setClassCode(student != null ? student.getClassCode() : "");
        recordVO.setClassName(student != null ? student.getClassCode() : "");
        recordVO.setSubmittedAt(latestRecord.getSubmittedAt());
        recordVO.setTotalScore(totalScore);
        recordVO.setStatus(pendingCount == 0 ? "reviewed" : "submitted");
        recordVO.setGradingSummary(pendingCount == 0 ? "全部题目已完成批阅" : "还有 " + pendingCount + " 题待教师批阅");
        return recordVO;
    }

    private List<EduExerciseItem> listItemsByExerciseId(Long exerciseId) {
        List<RelExerciseItem> rels = relExerciseItemMapper.selectList(
                new QueryWrapper<RelExerciseItem>()
                        .eq("exercise_id", exerciseId)
                        .orderByAsc("item_order")
                        .orderByAsc("id")
        );
        if (CollUtil.isEmpty(rels)) {
            return Collections.emptyList();
        }
        List<Long> itemIds = rels.stream().map(RelExerciseItem::getItemId).collect(Collectors.toList());
        List<EduExerciseItem> items = eduExerciseItemMapper.selectBatchIds(itemIds);
        if (CollUtil.isEmpty(items)) {
            return Collections.emptyList();
        }
        Map<Long, EduExerciseItem> itemMap = items.stream().collect(Collectors.toMap(EduExerciseItem::getId, item -> item));
        List<EduExerciseItem> orderedItems = new ArrayList<>();
        for (RelExerciseItem rel : rels) {
            EduExerciseItem item = itemMap.get(rel.getItemId());
            if (item != null) {
                if (rel.getItemScore() != null) {
                    item.setMaxScore(rel.getItemScore());
                }
                orderedItems.add(item);
            }
        }
        return orderedItems;
    }

    /**
     * 自动评分单个题目
     * @param item 题目
     * @param answer 学生答案
     * @return 得分（null表示无法自动评分）
     */
    private Integer autoGradeItem(EduExerciseItem item, ExerciseSubmitRequest.AnswerItem answer) {
        return autoGradeStrategyFactory.resolve(item.getQuestionType()).grade(item, answer);
    }
}
