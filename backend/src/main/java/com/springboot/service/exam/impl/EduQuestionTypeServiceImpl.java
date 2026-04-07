package com.springboot.service.exam.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.exam.EduQuestionTypeMapper;
import com.springboot.model.dto.exam.EduQuestionTypeQueryRequest;
import com.springboot.model.entity.exam.EduQuestionType;
import com.springboot.model.vo.exam.EduQuestionTypeVO;
import com.springboot.service.exam.EduQuestionTypeService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class EduQuestionTypeServiceImpl extends ServiceImpl<EduQuestionTypeMapper, EduQuestionType> implements EduQuestionTypeService {

    @Override
    public void validEduQuestionType(EduQuestionType eduQuestionType, boolean add) {
        ServiceMethodSupport.validEntity(eduQuestionType);
    }

    @Override
    public QueryWrapper<EduQuestionType> getQueryWrapper(EduQuestionTypeQueryRequest queryRequest) {
        return ServiceMethodSupport.buildQueryWrapper(queryRequest);
    }

    @Override
    public EduQuestionTypeVO getEduQuestionTypeVO(EduQuestionType eduQuestionType, HttpServletRequest request) {
        return EduQuestionTypeVO.objToVo(eduQuestionType);
    }

    @Override
    public Page<EduQuestionTypeVO> getEduQuestionTypeVOPage(Page<EduQuestionType> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, EduQuestionTypeVO::objToVo);
    }
}
