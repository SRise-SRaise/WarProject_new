package com.springboot.service.exam.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.exam.RelPaperQuestionMapper;
import com.springboot.model.dto.exam.RelPaperQuestionQueryRequest;
import com.springboot.model.entity.exam.RelPaperQuestion;
import com.springboot.model.vo.exam.RelPaperQuestionVO;
import com.springboot.service.exam.RelPaperQuestionService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class RelPaperQuestionServiceImpl extends ServiceImpl<RelPaperQuestionMapper, RelPaperQuestion> implements RelPaperQuestionService {

    @Override
    public void validRelPaperQuestion(RelPaperQuestion relPaperQuestion, boolean add) {
        ServiceMethodSupport.validEntity(relPaperQuestion);
    }

    @Override
    public QueryWrapper<RelPaperQuestion> getQueryWrapper(RelPaperQuestionQueryRequest queryRequest) {
        return ServiceMethodSupport.buildQueryWrapper(queryRequest);
    }

    @Override
    public RelPaperQuestionVO getRelPaperQuestionVO(RelPaperQuestion relPaperQuestion, HttpServletRequest request) {
        return RelPaperQuestionVO.objToVo(relPaperQuestion);
    }

    @Override
    public Page<RelPaperQuestionVO> getRelPaperQuestionVOPage(Page<RelPaperQuestion> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, RelPaperQuestionVO::objToVo);
    }
}
