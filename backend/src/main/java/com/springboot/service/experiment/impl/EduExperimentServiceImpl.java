package com.springboot.service.experiment.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.experiment.EduExperimentMapper;
import com.springboot.model.dto.experiment.EduExperimentQueryRequest;
import com.springboot.model.entity.experiment.EduExperiment;
import com.springboot.model.vo.experiment.EduExperimentVO;
import com.springboot.service.experiment.EduExperimentService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class EduExperimentServiceImpl extends ServiceImpl<EduExperimentMapper, EduExperiment> implements EduExperimentService {

    @Override
    public void validEduExperiment(EduExperiment eduExperiment, boolean add) {
        ServiceMethodSupport.validEntity(eduExperiment);
    }

    @Override
    public QueryWrapper<EduExperiment> getQueryWrapper(EduExperimentQueryRequest queryRequest) {
        return ServiceMethodSupport.buildQueryWrapper(queryRequest);
    }

    @Override
    public EduExperimentVO getEduExperimentVO(EduExperiment eduExperiment, HttpServletRequest request) {
        return EduExperimentVO.objToVo(eduExperiment);
    }

    @Override
    public Page<EduExperimentVO> getEduExperimentVOPage(Page<EduExperiment> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, EduExperimentVO::objToVo);
    }
}
