package com.springboot.service.exam.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.exam.EduQuestionBankMapper;
import com.springboot.model.dto.exam.EduQuestionBankQueryRequest;
import com.springboot.model.entity.exam.EduQuestionBank;
import com.springboot.model.vo.exam.EduQuestionBankVO;
import com.springboot.service.exam.EduQuestionBankService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class EduQuestionBankServiceImpl extends ServiceImpl<EduQuestionBankMapper, EduQuestionBank> implements EduQuestionBankService {

    @Override
    public void validEduQuestionBank(EduQuestionBank eduQuestionBank, boolean add) {
        ServiceMethodSupport.validEntity(eduQuestionBank);
    }

    @Override
    public QueryWrapper<EduQuestionBank> getQueryWrapper(EduQuestionBankQueryRequest queryRequest) {
        return ServiceMethodSupport.buildQueryWrapper(queryRequest);
    }

    @Override
    public EduQuestionBankVO getEduQuestionBankVO(EduQuestionBank eduQuestionBank, HttpServletRequest request) {
        return EduQuestionBankVO.objToVo(eduQuestionBank);
    }

    @Override
    public Page<EduQuestionBankVO> getEduQuestionBankVOPage(Page<EduQuestionBank> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, EduQuestionBankVO::objToVo);
    }
}
