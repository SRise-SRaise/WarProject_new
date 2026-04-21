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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ResExperimentResultServiceImpl extends ServiceImpl<ResExperimentResultMapper, ResExperimentResult> implements ResExperimentResultService {

    @Override
    public void validResExperimentResult(ResExperimentResult resExperimentResult, boolean add) {
        log.debug("[ResExperimentResult] 参数校验: add={}", add);
        ServiceMethodSupport.validEntity(resExperimentResult);
    }

    @Override
    public QueryWrapper<ResExperimentResult> getQueryWrapper(ResExperimentResultQueryRequest queryRequest) {
        if (queryRequest == null) {
            log.debug("[ResExperimentResult] 查询请求为null，返回空Wrapper");
            return ServiceMethodSupport.buildQueryWrapper(null);
        }
        queryRequest.setSortField(mapSortField(queryRequest.getSortField()));
        log.debug("[ResExperimentResult] 构建查询Wrapper: sortField={}", queryRequest.getSortField());
        return ServiceMethodSupport.buildQueryWrapper(queryRequest);
    }

    @Override
    public ResExperimentResultVO getResExperimentResultVO(ResExperimentResult resExperimentResult, HttpServletRequest request) {
        log.debug("[ResExperimentResult] 转换为VO: id={}", resExperimentResult != null ? resExperimentResult.getId() : "null");
        return ResExperimentResultVO.objToVo(resExperimentResult);
    }

    @Override
    public Page<ResExperimentResultVO> getResExperimentResultVOPage(Page<ResExperimentResult> entityPage, HttpServletRequest request) {
        log.debug("[ResExperimentResult] 分页转换为VO: current={}, size={}", entityPage.getCurrent(), entityPage.getSize());
        return ServiceMethodSupport.toVOPage(entityPage, ResExperimentResultVO::objToVo);
    }

    private String mapSortField(String sortField) {
        if (sortField == null) {
            return null;
        }
        String result = switch (sortField) {
            case "id" -> "student_item_id";
            case "studentId" -> "student_id";
            case "itemId" -> "item_id";
            case "subContent" -> "content";
            case "score" -> "score";
            case "submittedAt" -> "fill_time";
            case "gradingStatus" -> "score_flag";
            default -> sortField;
        };
        log.trace("[ResExperimentResult] 映射排序字段: {} -> {}", sortField, result);
        return result;
    }
}
