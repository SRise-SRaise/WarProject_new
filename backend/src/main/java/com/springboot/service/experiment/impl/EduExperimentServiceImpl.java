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
        if (queryRequest == null) {
            return ServiceMethodSupport.buildQueryWrapper(null);
        }
        queryRequest.setSortField(mapSortField(queryRequest.getSortField()));
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

    private String mapSortField(String sortField) {
        if (sortField == null) {
            return null;
        }
        return switch (sortField) {
            case "id" -> "experiment_id";
            case "sortOrder" -> "experiment_no";
            case "name" -> "experiment_name";
            case "categoryId" -> "experiment_type";
            case "fileType" -> "instruction_type";
            case "requirement" -> "experiment_requirement";
            case "contentDesc" -> "experiment_content";
            case "publishStatus" -> "state";
            default -> sortField;
        };
    }
}
