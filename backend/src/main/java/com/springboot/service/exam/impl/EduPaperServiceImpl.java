package com.springboot.service.exam.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.exam.EduPaperMapper;
import com.springboot.model.dto.exam.EduPaperQueryRequest;
import com.springboot.model.entity.exam.EduPaper;
import com.springboot.model.vo.exam.EduPaperVO;
import com.springboot.service.exam.EduPaperService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class EduPaperServiceImpl extends ServiceImpl<EduPaperMapper, EduPaper> implements EduPaperService {

    @Override
    public void validEduPaper(EduPaper eduPaper, boolean add) {
        ServiceMethodSupport.validEntity(eduPaper);
    }

    @Override
    public QueryWrapper<EduPaper> getQueryWrapper(EduPaperQueryRequest queryRequest) {
        return ServiceMethodSupport.buildQueryWrapper(queryRequest);
    }

    @Override
    public EduPaperVO getEduPaperVO(EduPaper eduPaper, HttpServletRequest request) {
        return EduPaperVO.objToVo(eduPaper);
    }

    @Override
    public Page<EduPaperVO> getEduPaperVOPage(Page<EduPaper> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, EduPaperVO::objToVo);
    }
}
