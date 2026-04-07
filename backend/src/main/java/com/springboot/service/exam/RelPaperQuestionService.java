package com.springboot.service.exam;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.exam.RelPaperQuestionQueryRequest;
import com.springboot.model.entity.exam.RelPaperQuestion;
import com.springboot.model.vo.exam.RelPaperQuestionVO;
import jakarta.servlet.http.HttpServletRequest;

public interface RelPaperQuestionService extends IService<RelPaperQuestion> {

    void validRelPaperQuestion(RelPaperQuestion relPaperQuestion, boolean add);

    QueryWrapper<RelPaperQuestion> getQueryWrapper(RelPaperQuestionQueryRequest queryRequest);

    RelPaperQuestionVO getRelPaperQuestionVO(RelPaperQuestion relPaperQuestion, HttpServletRequest request);

    Page<RelPaperQuestionVO> getRelPaperQuestionVOPage(Page<RelPaperQuestion> entityPage, HttpServletRequest request);
}
