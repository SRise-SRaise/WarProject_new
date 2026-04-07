package com.springboot.service.exam;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.exam.EduQuestionBankQueryRequest;
import com.springboot.model.entity.exam.EduQuestionBank;
import com.springboot.model.vo.exam.EduQuestionBankVO;
import jakarta.servlet.http.HttpServletRequest;

public interface EduQuestionBankService extends IService<EduQuestionBank> {

    void validEduQuestionBank(EduQuestionBank eduQuestionBank, boolean add);

    QueryWrapper<EduQuestionBank> getQueryWrapper(EduQuestionBankQueryRequest queryRequest);

    EduQuestionBankVO getEduQuestionBankVO(EduQuestionBank eduQuestionBank, HttpServletRequest request);

    Page<EduQuestionBankVO> getEduQuestionBankVOPage(Page<EduQuestionBank> entityPage, HttpServletRequest request);
}
