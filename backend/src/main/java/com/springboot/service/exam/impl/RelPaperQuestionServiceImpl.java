package com.springboot.service.exam.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.common.ErrorCode;
import com.springboot.exception.BusinessException;
import com.springboot.mapper.exam.RelPaperQuestionMapper;
import com.springboot.model.dto.exam.RelPaperQuestionQueryRequest;
import com.springboot.model.entity.exam.RelPaperQuestion;
import com.springboot.model.vo.exam.RelPaperQuestionVO;
import com.springboot.service.exam.RelPaperQuestionService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RelPaperQuestionServiceImpl extends ServiceImpl<RelPaperQuestionMapper, RelPaperQuestion> implements RelPaperQuestionService {

    @Override
    public void validRelPaperQuestion(RelPaperQuestion relPaperQuestion, boolean add) {
        if (relPaperQuestion == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (relPaperQuestion.getPaperId() == null || relPaperQuestion.getQuestionId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "试卷和题目不能为空");
        }
        if (relPaperQuestion.getScore() == null || relPaperQuestion.getScore() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目分值必须大于 0");
        }
    }

    @Override
    public QueryWrapper<RelPaperQuestion> getQueryWrapper(RelPaperQuestionQueryRequest queryRequest) {
        QueryWrapper<RelPaperQuestion> wrapper = new QueryWrapper<>();
        if (queryRequest == null) {
            return wrapper.orderByAsc("paper_id").orderByAsc("question_order").orderByAsc("id");
        }
        wrapper.eq(queryRequest.getId() != null, "id", queryRequest.getId());
        wrapper.eq(queryRequest.getPaperId() != null, "paper_id", queryRequest.getPaperId());
        wrapper.eq(queryRequest.getQuestionId() != null, "question_id", queryRequest.getQuestionId());
        wrapper.eq(queryRequest.getScore() != null, "score", queryRequest.getScore());
        wrapper.orderByAsc("paper_id").orderByAsc("question_order").orderByAsc("id");
        return wrapper;
    }

    @Override
    public RelPaperQuestionVO getRelPaperQuestionVO(RelPaperQuestion relPaperQuestion, HttpServletRequest request) {
        return RelPaperQuestionVO.objToVo(relPaperQuestion);
    }

    @Override
    public Page<RelPaperQuestionVO> getRelPaperQuestionVOPage(Page<RelPaperQuestion> entityPage, HttpServletRequest request) {
        Page<RelPaperQuestionVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(RelPaperQuestionVO.objToVo(entityPage.getRecords()));
        return voPage;
    }

    @Override
    public List<RelPaperQuestion> listByPaperId(Long paperId) {
        if (paperId == null) {
            return List.of();
        }
        return this.list(new QueryWrapper<RelPaperQuestion>()
                .eq("paper_id", paperId)
                .orderByAsc("question_order")
                .orderByAsc("id"));
    }

    @Override
    public boolean addQuestionToPaper(Long paperId, Long questionId, Integer score, Integer questionOrder, String sectionName) {
        RelPaperQuestion entity = new RelPaperQuestion();
        entity.setPaperId(paperId);
        entity.setQuestionId(questionId);
        entity.setScore(score);
        validRelPaperQuestion(entity, true);
        long exists = this.count(new QueryWrapper<RelPaperQuestion>()
                .eq("paper_id", paperId)
                .eq("question_id", questionId));
        if (exists > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该题目已存在于当前试卷");
        }
        List<RelPaperQuestion> current = listByPaperId(paperId);
        entity.setQuestionOrder(questionOrder == null || questionOrder <= 0 ? current.size() + 1 : questionOrder);
        entity.setSectionName(sectionName);
        return this.save(entity);
    }

    @Override
    public boolean updatePaperQuestion(Long id, Integer score, String sectionName) {
        RelPaperQuestion entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "试卷题目不存在");
        }
        if (score != null) {
            entity.setScore(score);
        }
        if (sectionName != null) {
            entity.setSectionName(sectionName);
        }
        validRelPaperQuestion(entity, false);
        return this.updateById(entity);
    }

    @Override
    public boolean removeQuestionFromPaper(Long id) {
        RelPaperQuestion entity = this.getById(id);
        if (entity == null) {
            return false;
        }
        Long paperId = entity.getPaperId();
        Integer removedOrder = entity.getQuestionOrder();
        boolean removed = this.removeById(id);
        if (!removed || paperId == null || removedOrder == null) {
            return removed;
        }
        List<RelPaperQuestion> remain = new ArrayList<>(listByPaperId(paperId));
        boolean changed = false;
        for (RelPaperQuestion item : remain) {
            if (item.getQuestionOrder() != null && item.getQuestionOrder() > removedOrder) {
                item.setQuestionOrder(item.getQuestionOrder() - 1);
                changed = true;
            }
        }
        if (changed) {
            this.updateBatchById(remain);
        }
        return true;
    }

    @Override
    public boolean reorderPaperQuestions(Long paperId, List<Long> questionIds) {
        if (paperId == null || questionIds == null || questionIds.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "排序参数不能为空");
        }
        List<RelPaperQuestion> current = new ArrayList<>(listByPaperId(paperId));
        current.sort(Comparator.comparing(RelPaperQuestion::getQuestionOrder, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(RelPaperQuestion::getId));
        for (int i = 0; i < questionIds.size(); i++) {
            Long questionId = questionIds.get(i);
            for (RelPaperQuestion item : current) {
                if (questionId != null && questionId.equals(item.getQuestionId())) {
                    item.setQuestionOrder(i + 1);
                    break;
                }
            }
        }
        return this.updateBatchById(current);
    }
}
