package com.springboot.service.exam.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.exam.EduExamMapper;
import com.springboot.model.dto.exam.EduExamQueryRequest;
import com.springboot.model.entity.exam.EduExam;
import com.springboot.model.vo.exam.EduExamVO;
import com.springboot.service.exam.EduExamService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class EduExamServiceImpl extends ServiceImpl<EduExamMapper, EduExam> implements EduExamService {

    @Override
    public void validEduExam(EduExam eduExam, boolean add) {
        ServiceMethodSupport.validEntity(eduExam);
    }

    @Override
    public QueryWrapper<EduExam> getQueryWrapper(EduExamQueryRequest queryRequest) {
        return ServiceMethodSupport.buildQueryWrapper(queryRequest);
    }

    @Override
    public EduExamVO getEduExamVO(EduExam eduExam, HttpServletRequest request) {
        return EduExamVO.objToVo(eduExam);
    }

    @Override
    public Page<EduExamVO> getEduExamVOPage(Page<EduExam> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, EduExamVO::objToVo);
    }
}
