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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EduExperimentServiceImpl extends ServiceImpl<EduExperimentMapper, EduExperiment> implements EduExperimentService {

    @Override
    public void validEduExperiment(EduExperiment eduExperiment, boolean add) {
        log.debug("[EduExperiment] 参数校验: add={}", add);
        ServiceMethodSupport.validEntity(eduExperiment);
    }

    @Override
    public QueryWrapper<EduExperiment> getQueryWrapper(EduExperimentQueryRequest queryRequest) {
        if (queryRequest == null) {
            log.debug("[EduExperiment] 查询请求为null，返回空Wrapper");
            return new QueryWrapper<>();
        }
        queryRequest.setSortField(mapSortField(queryRequest.getSortField()));
        QueryWrapper<EduExperiment> queryWrapper = new QueryWrapper<>();
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();
        if (sortField != null && !sortField.isEmpty()) {
            boolean isAsc = "ascend".equals(sortOrder);
            queryWrapper.orderBy(true, isAsc, sortField);
        }
        log.debug("[EduExperiment] 构建查询Wrapper: sortField={}, sortOrder={}", sortField, sortOrder);
        return queryWrapper;
    }

    @Override
    public EduExperimentVO getEduExperimentVO(EduExperiment eduExperiment, HttpServletRequest request) {
        log.debug("[EduExperiment] 转换为VO: id={}", eduExperiment != null ? eduExperiment.getId() : "null");
        return EduExperimentVO.objToVo(eduExperiment);
    }

    @Override
    public Page<EduExperimentVO> getEduExperimentVOPage(Page<EduExperiment> entityPage, HttpServletRequest request) {
        log.debug("[EduExperiment] 分页转换为VO: current={}, size={}", entityPage.getCurrent(), entityPage.getSize());
        return ServiceMethodSupport.toVOPage(entityPage, EduExperimentVO::objToVo);
    }

    private String mapSortField(String sortField) {
        if (sortField == null) {
            return null;
        }
        String result = switch (sortField) {
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
        log.trace("[EduExperiment] 映射排序字段: {} -> {}", sortField, result);
        return result;
    }
}
