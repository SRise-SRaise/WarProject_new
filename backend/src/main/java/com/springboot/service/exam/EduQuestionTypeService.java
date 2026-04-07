package com.springboot.service.exam;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.exam.EduQuestionTypeQueryRequest;
import com.springboot.model.entity.exam.EduQuestionType;
import com.springboot.model.vo.exam.EduQuestionTypeVO;
import jakarta.servlet.http.HttpServletRequest;

public interface EduQuestionTypeService extends IService<EduQuestionType> {

    void validEduQuestionType(EduQuestionType eduQuestionType, boolean add);

    QueryWrapper<EduQuestionType> getQueryWrapper(EduQuestionTypeQueryRequest queryRequest);

    EduQuestionTypeVO getEduQuestionTypeVO(EduQuestionType eduQuestionType, HttpServletRequest request);

    Page<EduQuestionTypeVO> getEduQuestionTypeVOPage(Page<EduQuestionType> entityPage, HttpServletRequest request);
}
