package com.springboot.service.exam.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.common.ErrorCode;
import com.springboot.exception.BusinessException;
import com.springboot.mapper.exam.EduQuestionBankMapper;
import com.springboot.model.dto.exam.EduQuestionBankQueryRequest;
import com.springboot.model.entity.exam.EduQuestionBank;
import com.springboot.model.vo.exam.EduQuestionBankVO;
import com.springboot.service.exam.EduQuestionBankService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class EduQuestionBankServiceImpl extends ServiceImpl<EduQuestionBankMapper, EduQuestionBank> implements EduQuestionBankService {

    @Override
    public void validEduQuestionBank(EduQuestionBank eduQuestionBank, boolean add) {
        if (eduQuestionBank == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isBlank(eduQuestionBank.getQuestionContent())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目内容不能为空");
        }
        Integer questionType = eduQuestionBank.getQuestionType();
        if (questionType == null || questionType < 1 || questionType > 7) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目类型非法");
        }
        if (eduQuestionBank.getDifficulty() == null || eduQuestionBank.getDifficulty() < 1 || eduQuestionBank.getDifficulty() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "难度系数必须在 1 到 5 之间");
        }
        if (StringUtils.isBlank(eduQuestionBank.getStandardAnswer())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标准答案不能为空");
        }
        if ((questionType == 2 || questionType == 3) && StringUtils.isBlank(eduQuestionBank.getOptionsText())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "选择题必须配置选项");
        }
    }

    @Override
    public QueryWrapper<EduQuestionBank> getQueryWrapper(EduQuestionBankQueryRequest queryRequest) {
        QueryWrapper<EduQuestionBank> wrapper = new QueryWrapper<>();
        if (queryRequest == null) {
            return wrapper.orderByDesc("updated_at");
        }
        wrapper.eq(queryRequest.getId() != null, "id", queryRequest.getId());
        wrapper.like(StringUtils.isNotBlank(queryRequest.getQuestionContent()), "question_content", queryRequest.getQuestionContent());
        wrapper.eq(queryRequest.getQuestionType() != null, "question_type", queryRequest.getQuestionType());
        wrapper.eq(queryRequest.getDifficulty() != null, "difficulty", queryRequest.getDifficulty());
        wrapper.eq(queryRequest.getCreatorTeacherId() != null, "creator_teacher_id", queryRequest.getCreatorTeacherId());
        wrapper.orderByDesc("updated_at");
        return wrapper;
    }

    @Override
    public EduQuestionBankVO getEduQuestionBankVO(EduQuestionBank eduQuestionBank, HttpServletRequest request) {
        return EduQuestionBankVO.objToVo(eduQuestionBank);
    }

    @Override
    public Page<EduQuestionBankVO> getEduQuestionBankVOPage(Page<EduQuestionBank> entityPage, HttpServletRequest request) {
        Page<EduQuestionBankVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(EduQuestionBankVO.objToVo(entityPage.getRecords()));
        return voPage;
    }

    @Override
    public List<EduQuestionBank> listAllQuestions() {
        return this.list(new QueryWrapper<EduQuestionBank>().orderByDesc("updated_at"));
    }

    @Override
    public Map<String, Object> getQuestionStats() {
        List<EduQuestionBank> questions = listAllQuestions();
        Map<Integer, Integer> byType = new LinkedHashMap<>();
        Map<Integer, Integer> byDifficulty = new LinkedHashMap<>();
        for (EduQuestionBank question : questions) {
            if (question.getQuestionType() != null) {
                byType.put(question.getQuestionType(), byType.getOrDefault(question.getQuestionType(), 0) + 1);
            }
            if (question.getDifficulty() != null) {
                byDifficulty.put(question.getDifficulty(), byDifficulty.getOrDefault(question.getDifficulty(), 0) + 1);
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", questions.size());
        result.put("byType", byType);
        result.put("byDifficulty", byDifficulty);
        return result;
    }
}
