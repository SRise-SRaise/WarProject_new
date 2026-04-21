package com.springboot.service.exam.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.common.ErrorCode;
import com.springboot.exception.BusinessException;
import com.springboot.mapper.exam.ResExamRecordMapper;
import com.springboot.model.dto.exam.ResExamRecordQueryRequest;
import com.springboot.model.entity.exam.RelPaperQuestion;
import com.springboot.model.entity.exam.ResExamRecord;
import com.springboot.model.vo.exam.ResExamRecordVO;
import com.springboot.service.exam.EduExamService;
import com.springboot.service.exam.EduPaperService;
import com.springboot.service.exam.RelPaperQuestionService;
import com.springboot.service.exam.ResExamRecordService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResExamRecordServiceImpl extends ServiceImpl<ResExamRecordMapper, ResExamRecord> implements ResExamRecordService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private RelPaperQuestionService relPaperQuestionService;

    @Resource
    private EduPaperService eduPaperService;

    @Resource
    private EduExamService eduExamService;

    @Override
    public void validResExamRecord(ResExamRecord resExamRecord, boolean add) {
        if (resExamRecord == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (resExamRecord.getStudentId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "学生不能为空");
        }
        if (resExamRecord.getQuestionId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目不能为空");
        }
    }

    @Override
    public QueryWrapper<ResExamRecord> getQueryWrapper(ResExamRecordQueryRequest queryRequest) {
        QueryWrapper<ResExamRecord> wrapper = new QueryWrapper<>();
        if (queryRequest == null) {
            return wrapper.orderByDesc("updated_at");
        }
        wrapper.eq(queryRequest.getId() != null, "id", queryRequest.getId());
        wrapper.eq(queryRequest.getStudentId() != null, "student_id", queryRequest.getStudentId());
        wrapper.eq(queryRequest.getQuestionId() != null, "question_id", queryRequest.getQuestionId());
        wrapper.like(StringUtils.isNotBlank(queryRequest.getStudentAnswer()), "student_answer", queryRequest.getStudentAnswer());
        wrapper.orderByDesc("updated_at");
        return wrapper;
    }

    @Override
    public ResExamRecordVO getResExamRecordVO(ResExamRecord resExamRecord, HttpServletRequest request) {
        return ResExamRecordVO.objToVo(resExamRecord);
    }

    @Override
    public Page<ResExamRecordVO> getResExamRecordVOPage(Page<ResExamRecord> entityPage, HttpServletRequest request) {
        Page<ResExamRecordVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(ResExamRecordVO.objToVo(entityPage.getRecords()));
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> submitExam(Long examId, Long studentId, Map<String, Object> answers) {
        if (examId == null || studentId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "考试和学生不能为空");
        }
        Map<String, Object> examRow = getExamRow(examId);
        if (examRow == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "考试不存在");
        }
        if (!toBoolean(examRow.get("is_published"))) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "当前考试未开放");
        }
        validateExamTimeWindow(examRow);
        Long paperId = toLong(examRow.get("paper_id"));
        if (paperId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "当前考试未关联试卷");
        }
        List<RelPaperQuestion> paperQuestions = relPaperQuestionService.listByPaperId(paperId);
        if (paperQuestions.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "试卷中暂无题目");
        }

        this.remove(new QueryWrapper<ResExamRecord>().eq("exam_id", examId).eq("student_id", studentId));

        List<ResExamRecord> entities = new ArrayList<>();
        for (RelPaperQuestion question : paperQuestions) {
            ResExamRecord entity = new ResExamRecord();
            entity.setExamId(examId);
            entity.setPaperId(paperId);
            entity.setPaperQuestionId(question.getId());
            entity.setStudentId(studentId);
            entity.setQuestionId(question.getQuestionId());
            entity.setStudentAnswer(stringifyAnswer(resolveAnswer(answers, question.getQuestionId())));
            entities.add(entity);
        }
        boolean saved = this.saveBatch(entities);
        if (!saved) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "提交考试失败");
        }

        refreshExamSummary(examId, studentId, paperId);

        List<ResExamRecord> persisted = this.list(new QueryWrapper<ResExamRecord>()
                .eq("exam_id", examId)
                .eq("student_id", studentId)
                .orderByAsc("paper_question_id"));
        Map<Long, Integer> maxScoreMap = new LinkedHashMap<>();
        int totalScore = 0;
        for (RelPaperQuestion question : paperQuestions) {
            int maxScore = question.getScore() == null ? 0 : question.getScore();
            maxScoreMap.put(question.getQuestionId(), maxScore);
            totalScore += maxScore;
        }
        int earnedScore = 0;
        Map<Long, Map<String, Object>> questionScores = new LinkedHashMap<>();
        for (ResExamRecord record : persisted) {
            int earned = record.getScore() == null ? 0 : record.getScore();
            int max = maxScoreMap.getOrDefault(record.getQuestionId(), 0);
            earnedScore += earned;
            Map<String, Object> scoreItem = new LinkedHashMap<>();
            scoreItem.put("earned", earned);
            scoreItem.put("max", max);
            questionScores.put(record.getQuestionId(), scoreItem);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalScore", totalScore);
        result.put("earnedScore", earnedScore);
        result.put("questionScores", questionScores);
        return result;
    }

    @Override
    public Map<String, Object> getStudentExamResult(Long examId, Long studentId) {
        if (examId == null || studentId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "考试和学生不能为空");
        }
        Map<String, Object> studentRecord = null;
        for (Map<String, Object> record : listStudentAnswerRecords(examId)) {
            if (studentId.equals(toLong(record.get("studentId")))) {
                studentRecord = record;
                break;
            }
        }
        // 获取考试信息
        Map<String, Object> examCard = eduExamService.getAdminExamCard(examId);

        if (studentRecord == null) {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("record", null);
            result.put("exam", examCard);
            result.put("paper", null);
            return result;
        }

        // 从 res_exam_summary 查 paper_id，获取试卷详情（含标准答案）
        Map<String, Object> paperDetail = null;
        List<Map<String, Object>> summaries = jdbcTemplate.queryForList(
                "SELECT paper_id FROM res_exam_summary WHERE exam_id = ? AND student_id = ? LIMIT 1",
                examId, studentId);
        if (!summaries.isEmpty()) {
            Long paperId = toLong(summaries.get(0).get("paper_id"));
            if (paperId != null) {
                paperDetail = eduPaperService.getPaperDetail(paperId);
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("record", studentRecord);
        result.put("exam", examCard);
        result.put("paper", paperDetail);
        return result;
    }

    @Override
    public List<Map<String, Object>> listExamRecordCards(Long examId) {
        List<Map<String, Object>> grouped = listStudentAnswerRecords(examId);
        List<Map<String, Object>> cards = new ArrayList<>();
        for (Map<String, Object> record : grouped) {
            Map<String, Object> card = new LinkedHashMap<>();
            card.put("id", record.get("id"));
            card.put("examId", record.get("examId"));
            card.put("studentName", record.get("studentName"));
            card.put("className", record.get("className"));
            card.put("status", record.get("status"));
            card.put("startedAt", record.get("submittedAt"));
            card.put("submittedAt", record.get("submittedAt"));
            card.put("score", String.valueOf(record.get("earnedScore")));
            String status = String.valueOf(record.get("status"));
            card.put("risk", "graded".equals(status) ? "low" : "medium");
            card.put("note", "graded".equals(status) ? "当前学生答卷已完成批改" : "当前学生答卷仍有主观题待批改");
            cards.add(card);
        }
        return cards;
    }

    @Override
    public List<Map<String, Object>> listStudentAnswerRecords(Long examId) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT r.exam_id, r.student_id, r.question_id, r.student_answer, r.score, r.grading_status, r.comment, r.created_at, r.updated_at, r.paper_id, r.paper_question_id, "
                        + "s.student_name, s.student_code, s.class_code, rpq.score AS max_score, qb.question_type "
                        + "FROM res_exam_record r "
                        + "LEFT JOIN auth_student s ON r.student_id = s.id "
                        + "LEFT JOIN rel_paper_question rpq ON r.paper_question_id = rpq.id "
                        + "LEFT JOIN edu_question_bank qb ON r.question_id = qb.id "
                        + "WHERE r.exam_id = ? ORDER BY r.student_id ASC, r.paper_question_id ASC, r.id ASC",
                examId);
        List<Map<String, Object>> summaryRows = jdbcTemplate.queryForList(
                "SELECT id, exam_id, student_id, paper_id, total_score, objective_score, subjective_score FROM res_exam_summary WHERE exam_id = ?",
                examId);
        Map<Long, Map<String, Object>> summaryByStudent = new LinkedHashMap<>();
        for (Map<String, Object> row : summaryRows) {
            summaryByStudent.put(toLong(row.get("student_id")), row);
        }

        Map<Long, Map<String, Object>> grouped = new LinkedHashMap<>();
        Map<Long, Integer> pendingManualCount = new LinkedHashMap<>();
        Map<Long, Integer> manualAnsweredCount = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            Long studentId = toLong(row.get("student_id"));
            if (studentId == null) {
                continue;
            }
            Map<String, Object> current = grouped.computeIfAbsent(studentId, key -> createStudentRecordSkeleton(examId, row, summaryByStudent.get(studentId)));
            @SuppressWarnings("unchecked")
            Map<Long, Map<String, Object>> answers = (Map<Long, Map<String, Object>>) current.get("answers");
            Long questionId = toLong(row.get("question_id"));
            Integer questionType = toInteger(row.get("question_type"));
            Integer gradingStatus = toInteger(row.get("grading_status"));
            Integer score = toInteger(row.get("score"));
            Integer maxScore = toInteger(row.get("max_score"));
            Map<String, Object> answerItem = new LinkedHashMap<>();
            answerItem.put("answer", parseAnswerForFrontend(row.get("student_answer"), questionType));
            answerItem.put("autoScore", isObjective(questionType) ? score : null);
            answerItem.put("manualScore", isObjective(questionType) ? null : (gradingStatus != null && gradingStatus == 2 ? score : null));
            answerItem.put("maxScore", maxScore == null ? 0 : maxScore);
            if (row.get("comment") != null) {
                answerItem.put("comment", row.get("comment"));
            }
            answers.put(questionId, answerItem);

            current.put("submittedAt", maxDateString(asNullableString(current.get("submittedAt")), resolveSubmittedAt(row)));
            if (isSubjective(questionType)) {
                manualAnsweredCount.put(studentId, manualAnsweredCount.getOrDefault(studentId, 0) + 1);
                if (gradingStatus == null || gradingStatus != 2) {
                    pendingManualCount.put(studentId, pendingManualCount.getOrDefault(studentId, 0) + 1);
                }
            }
        }

        for (Map.Entry<Long, Map<String, Object>> entry : grouped.entrySet()) {
            Long studentId = entry.getKey();
            Map<String, Object> record = entry.getValue();
            int pending = pendingManualCount.getOrDefault(studentId, 0);
            int manualTotal = manualAnsweredCount.getOrDefault(studentId, 0);
            if (manualTotal == 0 || pending == 0) {
                record.put("status", "graded");
            } else if (pending < manualTotal) {
                record.put("status", "grading");
            } else {
                record.put("status", "submitted");
            }
        }
        return new ArrayList<>(grouped.values());
    }

    @Override
    public Map<String, Object> getStudentAnswerRecordDetail(Long recordId) {
        List<Map<String, Object>> summaries = jdbcTemplate.queryForList(
                "SELECT id, exam_id, student_id, paper_id FROM res_exam_summary WHERE id = ? LIMIT 1",
                recordId);
        if (summaries.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "批改记录不存在");
        }
        Map<String, Object> summary = summaries.get(0);
        Long examId = toLong(summary.get("exam_id"));
        Long studentId = toLong(summary.get("student_id"));
        Long paperId = toLong(summary.get("paper_id"));
        Map<String, Object> detailRecord = null;
        for (Map<String, Object> item : listStudentAnswerRecords(examId)) {
            if (studentId != null && studentId.equals(toLong(item.get("studentId")))) {
                detailRecord = item;
                break;
            }
        }
        if (detailRecord == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到学生答卷详情");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("record", detailRecord);
        result.put("exam", eduExamService.getAdminExamCard(examId));
        result.put("paper", eduPaperService.getPaperDetail(paperId));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean gradeAnswer(Long recordId, Long questionId, Integer score, String comment) {
        List<Map<String, Object>> summaries = jdbcTemplate.queryForList(
                "SELECT id, exam_id, student_id, paper_id FROM res_exam_summary WHERE id = ? LIMIT 1",
                recordId);
        if (summaries.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "批改记录不存在");
        }
        Map<String, Object> summary = summaries.get(0);
        Long examId = toLong(summary.get("exam_id"));
        Long studentId = toLong(summary.get("student_id"));
        int updated = jdbcTemplate.update(
                "UPDATE res_exam_record SET score = ?, comment = ?, grading_status = 2, updated_at = CURRENT_TIMESTAMP WHERE exam_id = ? AND student_id = ? AND question_id = ?",
                score, comment, examId, studentId, questionId);
        if (updated <= 0) {
            return false;
        }
        refreshExamSummary(examId, studentId, toLong(summary.get("paper_id")));
        return true;
    }

    @Override
    public Map<String, Object> getScoreStatistics(Long examId) {
        List<Map<String, Object>> records = listStudentAnswerRecords(examId);
        int submittedCount = records.size();
        int gradedCount = 0;
        int pendingCount = 0;
        int highestScore = 0;
        int lowestScore = 0;
        int total = 0;
        int passCount = 0;
        boolean initialized = false;
        for (Map<String, Object> record : records) {
            String status = String.valueOf(record.get("status"));
            if ("graded".equals(status)) {
                gradedCount++;
            } else {
                pendingCount++;
            }
            int earned = toInteger(record.get("earnedScore")) == null ? 0 : toInteger(record.get("earnedScore"));
            int totalScore = toInteger(record.get("totalScore")) == null ? 0 : toInteger(record.get("totalScore"));
            total += earned;
            if (!initialized) {
                highestScore = earned;
                lowestScore = earned;
                initialized = true;
            } else {
                highestScore = Math.max(highestScore, earned);
                lowestScore = Math.min(lowestScore, earned);
            }
            if (totalScore > 0 && earned * 100 >= totalScore * 60) {
                passCount++;
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalStudents", submittedCount);
        result.put("submittedCount", submittedCount);
        result.put("gradedCount", gradedCount);
        result.put("pendingCount", pendingCount);
        result.put("averageScore", submittedCount == 0 ? 0 : Double.parseDouble(String.format("%.1f", total * 1.0 / submittedCount)));
        result.put("highestScore", submittedCount == 0 ? 0 : highestScore);
        result.put("lowestScore", submittedCount == 0 ? 0 : lowestScore);
        result.put("passRate", submittedCount == 0 ? 0 : Integer.parseInt(String.format("%.0f", passCount * 100.0 / submittedCount)));
        return result;
    }

    private Map<String, Object> createStudentRecordSkeleton(Long examId, Map<String, Object> row, Map<String, Object> summaryRow) {
        Map<String, Object> record = new LinkedHashMap<>();
        record.put("id", summaryRow == null ? toLong(row.get("student_id")) : toLong(summaryRow.get("id")));
        record.put("examId", examId);
        record.put("studentId", toLong(row.get("student_id")));
        record.put("studentName", row.get("student_name"));
        record.put("studentNo", row.get("student_code"));
        record.put("className", row.get("class_code"));
        record.put("submittedAt", resolveSubmittedAt(row));
        record.put("totalScore", computeFullScoreBySummary(summaryRow, row.get("paper_id")));
        record.put("earnedScore", summaryRow == null ? 0 : (toInteger(summaryRow.get("total_score")) == null ? 0 : toInteger(summaryRow.get("total_score"))));
        record.put("status", "submitted");
        record.put("answers", new LinkedHashMap<Long, Map<String, Object>>());
        return record;
    }

    private int computeFullScoreBySummary(Map<String, Object> summaryRow, Object paperIdValue) {
        Long paperId = summaryRow == null ? toLong(paperIdValue) : toLong(summaryRow.get("paper_id"));
        if (paperId == null) {
            return 0;
        }
        List<RelPaperQuestion> questions = relPaperQuestionService.listByPaperId(paperId);
        int total = 0;
        for (RelPaperQuestion question : questions) {
            total += question.getScore() == null ? 0 : question.getScore();
        }
        return total;
    }

    private void refreshExamSummary(Long examId, Long studentId, Long paperId) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT r.score, qb.question_type FROM res_exam_record r LEFT JOIN edu_question_bank qb ON qb.id = r.question_id WHERE r.exam_id = ? AND r.student_id = ?",
                examId, studentId);
        int objectiveScore = 0;
        int subjectiveScore = 0;
        for (Map<String, Object> row : rows) {
            Integer score = toInteger(row.get("score"));
            Integer questionType = toInteger(row.get("question_type"));
            if (score == null) {
                continue;
            }
            if (isObjective(questionType)) {
                objectiveScore += score;
            } else {
                subjectiveScore += score;
            }
        }
        int totalScore = objectiveScore + subjectiveScore;
        List<Map<String, Object>> summaries = jdbcTemplate.queryForList(
                "SELECT id FROM res_exam_summary WHERE exam_id = ? AND student_id = ? LIMIT 1",
                examId, studentId);
        if (summaries.isEmpty()) {
            jdbcTemplate.update(
                    "INSERT INTO res_exam_summary (exam_id, student_id, paper_id, total_score, objective_score, subjective_score, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                    examId, studentId, paperId, totalScore, objectiveScore, subjectiveScore);
            return;
        }
        jdbcTemplate.update(
                "UPDATE res_exam_summary SET paper_id = ?, total_score = ?, objective_score = ?, subjective_score = ?, updated_at = CURRENT_TIMESTAMP WHERE exam_id = ? AND student_id = ?",
                paperId, totalScore, objectiveScore, subjectiveScore, examId, studentId);
    }

    private Map<String, Object> getExamRow(Long examId) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, paper_id, start_time, end_time, duration_min, is_published FROM edu_exam WHERE id = ? LIMIT 1",
                examId);
        return rows.isEmpty() ? null : rows.get(0);
    }

    private void validateExamTimeWindow(Map<String, Object> examRow) {
        Date startTime = toDate(examRow.get("start_time"));
        Date endTime = resolveEndTime(startTime, toDate(examRow.get("end_time")), toInteger(examRow.get("duration_min")));
        Date now = new Date();
        if (startTime != null && now.before(startTime)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "考试尚未开始");
        }
        if (endTime != null && now.after(endTime)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "考试已结束");
        }
    }

    private Object resolveAnswer(Map<String, Object> answers, Long questionId) {
        if (answers == null || questionId == null) {
            return null;
        }
        if (answers.containsKey(String.valueOf(questionId))) {
            return answers.get(String.valueOf(questionId));
        }
        return answers.get(questionId);
    }

    private String stringifyAnswer(Object answer) {
        if (answer == null) {
            return "";
        }
        if (answer instanceof Collection<?> collection) {
            List<String> parts = new ArrayList<>();
            for (Object item : collection) {
                parts.add(String.valueOf(item));
            }
            return String.join(",", parts);
        }
        return String.valueOf(answer);
    }

    private Object parseAnswerForFrontend(Object value, Integer questionType) {
        if (value == null) {
            return "";
        }
        String text = String.valueOf(value);
        if (questionType != null && questionType == 3
) {
            return List.of(text.split(","));
        }
        return text;
    }

    private boolean isObjective(Integer questionType) {
        return questionType != null && questionType >= 1 && questionType <= 4;
    }

    private boolean isSubjective(Integer questionType) {
        return questionType != null && questionType >= 5 && questionType <= 7;
    }

    private Date resolveEndTime(Date startTime, Date endTime, Integer durationMin) {
        if (endTime != null) {
            return endTime;
        }
        if (startTime == null || durationMin == null) {
            return null;
        }
        return new Date(startTime.getTime() + durationMin.longValue() * 60000L);
    }

    private String maxDateString(String current, String next) {
        if (current == null || current.isBlank()) {
            return next;
        }
        if (next == null || next.isBlank()) {
            return current;
        }
        return current.compareTo(next) >= 0 ? current : next;
    }

    private String resolveSubmittedAt(Map<String, Object> row) {
        String createdAt = formatDate(toDate(row.get("created_at")));
        if (createdAt != null && !createdAt.isBlank()) {
            return createdAt;
        }
        return formatDate(toDate(row.get("updated_at")));
    }

    private String asNullableString(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value);
        return text.isBlank() || "null".equalsIgnoreCase(text) ? null : text;
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }

    private boolean toBoolean(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof Number number) {
            return number.intValue() != 0;
        }
        return "1".equals(String.valueOf(value)) || "true".equalsIgnoreCase(String.valueOf(value));
    }

    private Date toDate(Object value) {
        if (value instanceof Date date) {
            return date;
        }
        if (value instanceof LocalDateTime localDateTime) {
            return Timestamp.valueOf(localDateTime);
        }
        if (value instanceof CharSequence sequence) {
            String text = sequence.toString().trim();
            if (text.isEmpty()) {
                return null;
            }
            try {
                return Timestamp.valueOf(text.replace("T", " "));
            } catch (IllegalArgumentException ignored) {
                try {
                    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(text);
                } catch (ParseException ignoredToo) {
                    try {
                        return Timestamp.valueOf(LocalDateTime.parse(text, DateTimeFormatter.ISO_DATE_TIME));
                    } catch (Exception ignoredThree) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    private String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
}
