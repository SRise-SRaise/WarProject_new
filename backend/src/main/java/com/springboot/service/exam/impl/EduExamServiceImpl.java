package com.springboot.service.exam.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.common.ErrorCode;
import com.springboot.exception.BusinessException;
import com.springboot.mapper.exam.EduExamMapper;
import com.springboot.model.dto.exam.EduExamQueryRequest;
import com.springboot.model.entity.exam.EduExam;
import com.springboot.model.vo.exam.EduExamVO;
import com.springboot.service.exam.EduExamService;
import com.springboot.service.exam.EduPaperService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

@Service
public class EduExamServiceImpl extends ServiceImpl<EduExamMapper, EduExam> implements EduExamService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private EduPaperService eduPaperService;

    @Override
    public void validEduExam(EduExam eduExam, boolean add) {
        if (eduExam == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isBlank(eduExam.getExamName())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "考试名称不能为空");
        }
        if (eduExam.getDurationMin() != null && eduExam.getDurationMin() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "考试时长必须大于 0");
        }
    }

    @Override
    public QueryWrapper<EduExam> getQueryWrapper(EduExamQueryRequest queryRequest) {
        QueryWrapper<EduExam> wrapper = new QueryWrapper<>();
        if (queryRequest == null) {
            return wrapper.orderByDesc("updated_at");
        }
        wrapper.eq(queryRequest.getId() != null, "id", queryRequest.getId());
        wrapper.like(StringUtils.isNotBlank(queryRequest.getExamName()), "exam_name", queryRequest.getExamName());
        wrapper.eq(queryRequest.getIsPublished() != null, "is_published", queryRequest.getIsPublished());
        wrapper.orderByDesc("updated_at");
        return wrapper;
    }

    @Override
    public EduExamVO getEduExamVO(EduExam eduExam, HttpServletRequest request) {
        return EduExamVO.objToVo(eduExam);
    }

    @Override
    public Page<EduExamVO> getEduExamVOPage(Page<EduExam> entityPage, HttpServletRequest request) {
        Page<EduExamVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(EduExamVO.objToVo(entityPage.getRecords()));
        return voPage;
    }

    @Override
    public Map<String, Object> listExamPage(EduExamQueryRequest queryRequest) {
        long current = queryRequest == null || queryRequest.getCurrent() <= 0 ? 1 : queryRequest.getCurrent();
        long size = queryRequest == null || queryRequest.getPageSize() <= 0 ? 10 : queryRequest.getPageSize();
        List<Object> params = new ArrayList<>();
        String whereSql = buildWhereSql(queryRequest, params);
        Long total = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM edu_exam e" + whereSql, Long.class, params.toArray());

        List<Object> dataParams = new ArrayList<>(params);
        dataParams.add(size);
        dataParams.add((current - 1) * size);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT e.id, e.exam_name, e.paper_id, e.duration_min, e.start_time, e.end_time, e.is_published, e.created_at, e.updated_at FROM edu_exam e"
                        + whereSql + " ORDER BY e.updated_at DESC LIMIT ? OFFSET ?",
                dataParams.toArray());
        Map<Long, Map<String, Object>> paperMap = buildPaperMap(extractPaperIds(rows));
        List<Map<String, Object>> records = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            records.add(toAdminExamMap(row, paperMap, fetchExamProgress(toLong(row.get("id")))));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("records", records);
        result.put("total", total == null ? 0L : total);
        result.put("current", current);
        result.put("size", size);
        return result;
    }

    @Override
    public Map<String, Object> getExamStats() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT id, start_time, end_time, duration_min, is_published, paper_id FROM edu_exam");
        int total = rows.size();
        int published = 0;
        int draft = 0;
        int ongoing = 0;
        Date now = new Date();
        for (Map<String, Object> row : rows) {
            boolean isPublished = toBoolean(row.get("is_published"));
            Long paperId = toLong(row.get("paper_id"));
            if (isPublished) {
                published++;
            }
            if (!isPublished) {
                draft++;
            }
            Date startTime = toDate(row.get("start_time"));
            Date endTime = resolveEndTime(startTime, toDate(row.get("end_time")), toInteger(row.get("duration_min")));
            if (isPublished && paperId != null && startTime != null && !now.before(startTime) && (endTime == null || !now.after(endTime))) {
                ongoing++;
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", total);
        result.put("published", published);
        result.put("draft", draft);
        result.put("ongoing", ongoing);
        return result;
    }

    @Override
    public List<Map<String, Object>> listAllPapersForExam() {
        return eduPaperService.listAllPaperOptions();
    }

    @Override
    public Long createExam(String examName, Long paperId, Integer durationMin, Date startTime, Date endTime, Boolean isPublished) {
        if (StringUtils.isBlank(examName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "考试名称不能为空");
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO edu_exam (exam_name, paper_id, duration_min, start_time, end_time, is_published, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, examName.trim());
            if (paperId == null) {
                ps.setNull(2, java.sql.Types.BIGINT);
            } else {
                ps.setLong(2, paperId);
            }
            if (durationMin == null) {
                ps.setNull(3, java.sql.Types.INTEGER);
            } else {
                ps.setInt(3, durationMin);
            }
            if (startTime == null) {
                ps.setNull(4, java.sql.Types.TIMESTAMP);
            } else {
                ps.setTimestamp(4, new Timestamp(startTime.getTime()));
            }
            if (endTime == null) {
                ps.setNull(5, java.sql.Types.TIMESTAMP);
            } else {
                ps.setTimestamp(5, new Timestamp(endTime.getTime()));
            }
            ps.setBoolean(6, Boolean.TRUE.equals(isPublished));
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建考试失败");
        }
        return key.longValue();
    }

    @Override
    public boolean updateExamCustom(Long id, String examName, Long paperId, Integer durationMin, Date startTime, Date endTime, Boolean isPublished) {
        int updated = jdbcTemplate.update(
                "UPDATE edu_exam SET exam_name = ?, paper_id = ?, duration_min = ?, start_time = ?, end_time = ?, is_published = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?",
                examName,
                paperId,
                durationMin,
                startTime == null ? null : new Timestamp(startTime.getTime()),
                endTime == null ? null : new Timestamp(endTime.getTime()),
                Boolean.TRUE.equals(isPublished),
                id);
        return updated > 0;
    }

    @Override
    public boolean publishExam(Long id) {
        return jdbcTemplate.update("UPDATE edu_exam SET is_published = 1, updated_at = CURRENT_TIMESTAMP WHERE id = ?", id) > 0;
    }

    @Override
    public boolean unpublishExam(Long id) {
        return jdbcTemplate.update("UPDATE edu_exam SET is_published = 0, updated_at = CURRENT_TIMESTAMP WHERE id = ?", id) > 0;
    }

    @Override
    public List<Map<String, Object>> listPublishedExamsForStudent() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, exam_name, paper_id, duration_min, start_time, end_time, is_published, created_at, updated_at FROM edu_exam WHERE is_published = 1 ORDER BY start_time ASC, updated_at DESC");
        Map<Long, Map<String, Object>> paperMap = buildPaperMap(extractPaperIds(rows));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            result.add(toStudentExamMap(row, paperMap));
        }
        return result;
    }

    @Override
    public Map<String, Object> getStudentExamDetail(Long examId) {
        Map<String, Object> row = getExamRow(examId);
        if (row == null || !toBoolean(row.get("is_published"))) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "考试不存在或未开放");
        }
        Long paperId = toLong(row.get("paper_id"));
        if (paperId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "当前考试未关联试卷");
        }
        Map<Long, Map<String, Object>> paperMap = buildPaperMap(List.of(paperId));
        Map<String, Object> exam = toStudentExamMap(row, paperMap);
        Map<String, Object> paper = new LinkedHashMap<>(eduPaperService.getPaperDetail(paperId));
        Object questionObj = paper.get("questions");
        if (questionObj instanceof List<?>) {
            for (Object item : (List<?>) questionObj) {
                if (item instanceof Map<?, ?> questionMap) {
                    Object inner = questionMap.get("question");
                    if (inner instanceof Map<?, ?> questionDetail) {
                        ((Map<?, ?>) questionDetail).remove("standardAnswer");
                        ((Map<?, ?>) questionDetail).remove("analysis");
                    }
                }
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("exam", exam);
        result.put("paper", paper);
        return result;
    }

    @Override
    public List<Map<String, Object>> listGradingExams() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, exam_name, paper_id, duration_min, start_time, end_time, is_published, created_at, updated_at FROM edu_exam WHERE is_published = 1 ORDER BY updated_at DESC");
        Map<Long, Map<String, Object>> paperMap = buildPaperMap(extractPaperIds(rows));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Long examId = toLong(row.get("id"));
            result.add(toAdminExamMap(row, paperMap, fetchExamProgress(examId)));
        }
        return result;
    }

    @Override
    public Map<String, Object> getAdminExamCard(Long examId) {
        Map<String, Object> row = getExamRow(examId);
        if (row == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "考试不存在");
        }
        Map<Long, Map<String, Object>> paperMap = buildPaperMap(extractPaperIds(List.of(row)));
        return toAdminExamMap(row, paperMap, fetchExamProgress(examId));
    }

    private String buildWhereSql(EduExamQueryRequest queryRequest, List<Object> params) {
        StringBuilder where = new StringBuilder(" WHERE 1 = 1");
        if (queryRequest != null) {
            if (StringUtils.isNotBlank(queryRequest.getExamName())) {
                where.append(" AND e.exam_name LIKE ?");
                params.add("%" + queryRequest.getExamName().trim() + "%");
            }
            if (queryRequest.getIsPublished() != null) {
                where.append(" AND e.is_published = ?");
                params.add(queryRequest.getIsPublished());
            }
        }
        return where.toString();
    }

    private Map<String, Object> getExamRow(Long examId) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, exam_name, paper_id, duration_min, start_time, end_time, is_published, created_at, updated_at FROM edu_exam WHERE id = ? LIMIT 1",
                examId);
        return rows.isEmpty() ? null : rows.get(0);
    }

    private Set<Long> extractPaperIds(Collection<Map<String, Object>> rows) {
        Set<Long> paperIds = new LinkedHashSet<>();
        for (Map<String, Object> row : rows) {
            Long paperId = toLong(row.get("paper_id"));
            if (paperId != null) {
                paperIds.add(paperId);
            }
        }
        return paperIds;
    }

    private Map<Long, Map<String, Object>> buildPaperMap(Collection<Long> paperIds) {
        Map<Long, Map<String, Object>> result = new LinkedHashMap<>();
        if (paperIds == null || paperIds.isEmpty()) {
            return result;
        }
        for (Map<String, Object> paper : eduPaperService.listAllPaperOptions()) {
            Long id = toLong(paper.get("id"));
            if (id != null && paperIds.contains(id)) {
                result.put(id, paper);
            }
        }
        return result;
    }

    private Map<String, Object> toStudentExamMap(Map<String, Object> row, Map<Long, Map<String, Object>> paperMap) {
        Map<String, Object> exam = new LinkedHashMap<>();
        Long paperId = toLong(row.get("paper_id"));
        exam.put("id", toLong(row.get("id")));
        exam.put("examName", row.get("exam_name"));
        exam.put("paperId", paperId);
        exam.put("durationMin", toInteger(row.get("duration_min")));
        exam.put("startTime", formatDate(toDate(row.get("start_time"))));
        exam.put("endTime", formatDate(toDate(row.get("end_time"))));
        exam.put("isPublished", toBoolean(row.get("is_published")));
        exam.put("createdAt", formatDate(toDate(row.get("created_at"))));
        exam.put("updatedAt", formatDate(toDate(row.get("updated_at"))));
        exam.put("paper", paperId == null ? null : paperMap.get(paperId));
        return exam;
    }

    private Map<String, Object> toAdminExamMap(Map<String, Object> row, Map<Long, Map<String, Object>> paperMap, Map<String, Object> progress) {
        Long paperId = toLong(row.get("paper_id"));
        Map<String, Object> paper = paperId == null ? null : paperMap.get(paperId);
        Map<String, Object> exam = toStudentExamMap(row, paperMap);
        exam.put("title", row.get("exam_name"));
        exam.put("topicLabel", paper == null ? "未关联试卷" : paper.get("paperName"));
        exam.put("status", resolveAdminStatus(row));
        exam.put("scope", "全体可见");
        exam.put("schedule", buildScheduleText(toDate(row.get("start_time")), toDate(row.get("end_time")), toInteger(row.get("duration_min"))));
        exam.put("paperTitle", paper == null ? "未关联试卷" : paper.get("paperName"));
        exam.put("candidateCount", progress.get("submittedCount"));
        exam.put("submittedCount", progress.get("submittedCount"));
        exam.put("averageScore", progress.get("averageScore"));
        exam.put("summary", buildAdminSummaryText(progress));
        exam.put("tags", buildAdminTags(resolveAdminStatus(row), paper));
        return exam;
    }

    private Map<String, Object> fetchExamProgress(Long examId) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT student_id, grading_status, score FROM res_exam_record WHERE exam_id = ?",
                examId);
        int submittedCount = 0;
        int pendingCount = 0;
        Map<Long, Integer> statusMap = new LinkedHashMap<>();
        Map<Long, Integer> scoreMap = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            Long studentId = toLong(row.get("student_id"));
            if (studentId == null) {
                continue;
            }
            statusMap.putIfAbsent(studentId, 0);
            Integer gradingStatus = toInteger(row.get("grading_status"));
            if (gradingStatus != null && gradingStatus == 0) {
                statusMap.put(studentId, 1);
            }
            scoreMap.put(studentId, scoreMap.getOrDefault(studentId, 0) + (toInteger(row.get("score")) == null ? 0 : toInteger(row.get("score"))));
        }
        submittedCount = statusMap.size();
        for (Integer state : statusMap.values()) {
            if (state != null && state == 1) {
                pendingCount++;
            }
        }
        int gradedCount = submittedCount - pendingCount;
        double average = 0D;
        if (!scoreMap.isEmpty()) {
            int total = 0;
            for (Integer value : scoreMap.values()) {
                total += value == null ? 0 : value;
            }
            average = total * 1.0 / scoreMap.size();
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("submittedCount", submittedCount);
        result.put("gradedCount", gradedCount);
        result.put("pendingCount", pendingCount);
        result.put("averageScore", formatDecimal(average));
        return result;
    }

    private String buildAdminSummaryText(Map<String, Object> progress) {
        return "已提交 " + progress.get("submittedCount") + " 人，待批改 " + progress.get("pendingCount") + " 人，均分 " + progress.get("averageScore");
    }

    private List<String> buildAdminTags(String status, Map<String, Object> paper) {
        List<String> tags = new ArrayList<>();
        tags.add(status);
        if (paper != null) {
            tags.add("试卷已关联");
        }
        return tags;
    }

    private String resolveAdminStatus(Map<String, Object> row) {
        boolean isPublished = toBoolean(row.get("is_published"));
        if (!isPublished) {
            return "draft";
        }
        Date now = new Date();
        Date startTime = toDate(row.get("start_time"));
        Date endTime = resolveEndTime(startTime, toDate(row.get("end_time")), toInteger(row.get("duration_min")));
        if (startTime != null && now.before(startTime)) {
            return "scheduled";
        }
        if (startTime != null && (endTime == null || !now.after(endTime))) {
            return "running";
        }
        return "closed";
    }

    private String buildScheduleText(Date startTime, Date endTime, Integer durationMin) {
        if (startTime == null) {
            return "开放后可参加";
        }
        Date resolvedEnd = resolveEndTime(startTime, endTime, durationMin);
        if (resolvedEnd == null) {
            return formatDate(startTime);
        }
        return formatDate(startTime) + " - " + formatDate(resolvedEnd);
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
        return null;
    }

    private String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    private String formatDecimal(double value) {
        return String.format("%.1f", value);
    }
}
