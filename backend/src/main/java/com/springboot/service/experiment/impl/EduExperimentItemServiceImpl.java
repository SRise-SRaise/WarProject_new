package com.springboot.service.experiment.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.experiment.EduExperimentItemMapper;
import com.springboot.model.dto.experiment.EduExperimentItemQueryRequest;
import com.springboot.model.entity.experiment.EduExperimentItem;
import com.springboot.model.vo.experiment.EduExperimentItemVO;
import com.springboot.service.experiment.EduExperimentItemService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class EduExperimentItemServiceImpl extends ServiceImpl<EduExperimentItemMapper, EduExperimentItem> implements EduExperimentItemService {

    @Override
    public void validEduExperimentItem(EduExperimentItem eduExperimentItem, boolean add) {
        ServiceMethodSupport.validEntity(eduExperimentItem);
    }

    @Override
    public QueryWrapper<EduExperimentItem> getQueryWrapper(EduExperimentItemQueryRequest queryRequest) {
        return ServiceMethodSupport.buildQueryWrapper(queryRequest);
    }

    @Override
    public EduExperimentItemVO getEduExperimentItemVO(EduExperimentItem eduExperimentItem, HttpServletRequest request) {
        return EduExperimentItemVO.objToVo(eduExperimentItem);
    }

    @Override
    public Page<EduExperimentItemVO> getEduExperimentItemVOPage(Page<EduExperimentItem> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, EduExperimentItemVO::objToVo);
    }
}
