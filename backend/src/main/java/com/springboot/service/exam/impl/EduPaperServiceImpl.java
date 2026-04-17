package com.springboot.service.exam.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.common.ErrorCode;
import com.springboot.exception.BusinessException;
import com.springboot.mapper.exam.EduPaperMapper;
import com.springboot.model.dto.exam.EduPaperQueryRequest;
import com.springboot.model.entity.exam.EduPaper;
import com.springboot.model.entity.exam.EduQuestionBank;
import com.springboot.model.entity.exam.RelPaperQuestion;
import com.springboot.model.vo.exam.EduPaperVO;
import com.springboot.service.exam.EduPaperService;
import com.springboot.service.exam.EduQuestionBankService;
import com.springboot.service.exam.RelPaperQuestionService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class EduPaperServiceImpl extends ServiceImpl<EduPaperMapper, EduPaper> implements EduPaperService {

    @Resource
    private RelPaperQuestionService relPaperQuestionService;

    @Resource
    private EduQuestionBankService eduQuestionBankService;

    @Override
    public void validEduPaper(EduPaper eduPaper, boolean add) {
        if (eduPaper == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (eduPaper.getPaperCode() == null || eduPaper.getPaperCode() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "试卷编号必须大于 0");
        }
        if (StringUtils.isBlank(eduPaper.getPaperName())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "试卷名称不能为空");
        }
    }

    @Override
    public QueryWrapper<EduPaper> getQueryWrapper(EduPaperQueryRequest queryRequest) {
        QueryWrapper<EduPaper> wrapper = new QueryWrapper<>();
        if (queryRequest == null) {
            return wrapper.orderByDesc("updated_at");
        }
        wrapper.eq(queryRequest.getId() != null, "id", queryRequest.getId());
        wrapper.eq(queryRequest.getPaperCode() != null, "paper_code", queryRequest.getPaperCode());
        wrapper.like(StringUtils.isNotBlank(queryRequest.getPaperName()), "paper_name", queryRequest.getPaperName());
        wrapper.like(StringUtils.isNotBlank(queryRequest.getDescription()), "description", queryRequest.getDescription());
        wrapper.orderByDesc("updated_at");
        return wrapper;
    }

    @Override
    public EduPaperVO getEduPaperVO(EduPaper eduPaper, HttpServletRequest request) {
        return EduPaperVO.objToVo(eduPaper);
    }

    @Override
    public Page<EduPaperVO> getEduPaperVOPage(Page<EduPaper> entityPage, HttpServletRequest request) {
        Page<EduPaperVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(EduPaperVO.objToVo(entityPage.getRecords()));
        return voPage;
    }

    @Override
    public Map<String, Object> listPaperPage(EduPaperQueryRequest queryRequest) {
        long current = queryRequest == null || queryRequest.getCurrent() <= 0 ? 1 : queryRequest.getCurrent();
        long size = queryRequest == null || queryRequest.getPageSize() <= 0 ? 10 : queryRequest.getPageSize();
        Page<EduPaper> page = this.page(new Page<>(current, size), getQueryWrapper(queryRequest));
        Map<Long, Map<String, Object>> summaryMap = buildPaperSummaryMap(page.getRecords());
        List<Map<String, Object>> records = new ArrayList<>();
        for (EduPaper paper : page.getRecords()) {
            records.add(summaryMap.getOrDefault(paper.getId(), buildPaperSummary(paper, List.of())));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("records", records);
        result.put("total", page.getTotal());
        result.put("current", page.getCurrent());
        result.put("size", page.getSize());
        return result;
    }

    @Override
    public Map<String, Object> getPaperDetail(Long paperId) {
        EduPaper paper = this.getById(paperId);
        if (paper == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "试卷不存在");
        }
        List<RelPaperQuestion> relations = new ArrayList<>(relPaperQuestionService.listByPaperId(paperId));
        relations.sort(Comparator.comparing(RelPaperQuestion::getQuestionOrder, Comparator.nullsLast(Integer::compareTo)).thenComparing(RelPaperQuestion::getId));
        Map<Long, EduQuestionBank> questionMap = buildQuestionMap(relations);
        Map<String, Object> detail = buildPaperSummary(paper, relations);
        List<Map<String, Object>> questionItems = new ArrayList<>();
        for (RelPaperQuestion relation : relations) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", relation.getId());
            item.put("paperId", relation.getPaperId());
            item.put("questionId", relation.getQuestionId());
            item.put("score", relation.getScore());
            item.put("questionOrder", relation.getQuestionOrder());
            item.put("sectionName", relation.getSectionName());
            item.put("question", toQuestionMap(questionMap.get(relation.getQuestionId()), true));
            questionItems.add(item);
        }
        detail.put("questions", questionItems);
        return detail;
    }

    @Override
    public List<Map<String, Object>> listAllPaperOptions() {
        List<EduPaper> papers = this.list(new QueryWrapper<EduPaper>().orderByDesc("updated_at"));
        return new ArrayList<>(buildPaperSummaryMap(papers).values());
    }

    private Map<Long, Map<String, Object>> buildPaperSummaryMap(List<EduPaper> papers) {
        Map<Long, Map<String, Object>> result = new LinkedHashMap<>();
        if (papers == null || papers.isEmpty()) {
            return result;
        }
        Set<Long> paperIds = new LinkedHashSet<>();
        for (EduPaper paper : papers) {
            if (paper != null && paper.getId() != null) {
                paperIds.add(paper.getId());
            }
        }
        List<RelPaperQuestion> relations = paperIds.isEmpty() ? List.of() : relPaperQuestionService.list(new QueryWrapper<RelPaperQuestion>().in("paper_id", paperIds));
        Map<Long, List<RelPaperQuestion>> group = new LinkedHashMap<>();
        for (RelPaperQuestion relation : relations) {
            group.computeIfAbsent(relation.getPaperId(), key -> new ArrayList<>()).add(relation);
        }
        for (EduPaper paper : papers) {
            result.put(paper.getId(), buildPaperSummary(paper, group.getOrDefault(paper.getId(), List.of())));
        }
        return result;
    }

    private Map<Long, EduQuestionBank> buildQuestionMap(List<RelPaperQuestion> relations) {
        Set<Long> questionIds = new LinkedHashSet<>();
        for (RelPaperQuestion relation : relations) {
            if (relation.getQuestionId() != null) {
                questionIds.add(relation.getQuestionId());
            }
        }
        List<EduQuestionBank> questions = questionIds.isEmpty() ? List.of() : eduQuestionBankService.listByIds(questionIds);
        Map<Long, EduQuestionBank> map = new LinkedHashMap<>();
        for (EduQuestionBank question : questions) {
            map.put(question.getId(), question);
        }
        return map;
    }

    private Map<String, Object> buildPaperSummary(EduPaper paper, Collection<RelPaperQuestion> relations) {
        int questionCount = 0;
        int totalScore = 0;
        for (RelPaperQuestion relation : relations) {
            questionCount++;
            totalScore += relation.getScore() == null ? 0 : relation.getScore();
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", paper.getId());
        map.put("paperCode", paper.getPaperCode());
        map.put("paperName", paper.getPaperName());
        map.put("description", paper.getDescription());
        map.put("generationTime", formatDate(paper.getGenerationTime()));
        map.put("questionCount", questionCount);
        map.put("totalScore", totalScore);
        map.put("createdAt", formatDate(paper.getCreatedAt()));
        map.put("updatedAt", formatDate(paper.getUpdatedAt()));
        return map;
    }

    private Map<String, Object> toQuestionMap(EduQuestionBank question, boolean includeAnswer) {
        if (question == null) {
            return null;
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", question.getId());
        map.put("questionContent", question.getQuestionContent());
        map.put("questionType", question.getQuestionType());
        map.put("optionsText", question.getOptionsText());
        if (includeAnswer) {
            map.put("standardAnswer", question.getStandardAnswer());
            map.put("analysis", question.getAnalysis());
        }
        map.put("difficulty", question.getDifficulty());
        map.put("creatorTeacherId", question.getCreatorTeacherId());
        map.put("createdAt", formatDate(question.getCreatedAt()));
        map.put("updatedAt", formatDate(question.getUpdatedAt()));
        return map;
    }

    private String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
}
