package com.springboot.service.experiment.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.experiment.ResExperimentResultMapper;
import com.springboot.model.dto.experiment.ResExperimentResultQueryRequest;
import com.springboot.model.entity.experiment.ResExperimentResult;
import com.springboot.model.vo.experiment.ResExperimentResultVO;
import com.springboot.service.experiment.ResExperimentResultService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class ResExperimentResultServiceImpl extends ServiceImpl<ResExperimentResultMapper, ResExperimentResult> implements ResExperimentResultService {

    @Override
    public void validResExperimentResult(ResExperimentResult resExperimentResult, boolean add) {
        ServiceMethodSupport.validEntity(resExperimentResult);
    }

    @Override
    public QueryWrapper<ResExperimentResult> getQueryWrapper(ResExperimentResultQueryRequest queryRequest) {
        return ServiceMethodSupport.buildQueryWrapper(queryRequest);
    }

    @Override
    public ResExperimentResultVO getResExperimentResultVO(ResExperimentResult resExperimentResult, HttpServletRequest request) {
        return ResExperimentResultVO.objToVo(resExperimentResult);
    }

    @Override
    public Page<ResExperimentResultVO> getResExperimentResultVOPage(Page<ResExperimentResult> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, ResExperimentResultVO::objToVo);
    }
}
