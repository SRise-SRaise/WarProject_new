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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EduExperimentItemServiceImpl extends ServiceImpl<EduExperimentItemMapper, EduExperimentItem> implements EduExperimentItemService {

    @Override
    public void validEduExperimentItem(EduExperimentItem eduExperimentItem, boolean add) {
        log.debug("[EduExperimentItem] 参数校验: add={}", add);
        ServiceMethodSupport.validEntity(eduExperimentItem);
    }

    @Override
    public QueryWrapper<EduExperimentItem> getQueryWrapper(EduExperimentItemQueryRequest queryRequest) {
        if (queryRequest == null) {
            log.debug("[EduExperimentItem] 查询请求为null，返回空Wrapper");
            return new QueryWrapper<>();
        }
        queryRequest.setSortField(mapSortField(queryRequest.getSortField()));
        QueryWrapper<EduExperimentItem> queryWrapper = new QueryWrapper<>();
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();
        if (sortField != null && !sortField.isEmpty()) {
            boolean isAsc = "ascend".equals(sortOrder);
            queryWrapper.orderBy(true, isAsc, sortField);
        }
        // 添加 experimentId 查询条件
        if (queryRequest.getExperimentId() != null) {
            queryWrapper.eq("experiment_id", queryRequest.getExperimentId());
        }
        log.debug("[EduExperimentItem] 构建查询Wrapper: experimentId={}", queryRequest.getExperimentId());
        return queryWrapper;
    }

    @Override
    public EduExperimentItemVO getEduExperimentItemVO(EduExperimentItem eduExperimentItem, HttpServletRequest request) {
        log.debug("[EduExperimentItem] 转换为VO: id={}", eduExperimentItem != null ? eduExperimentItem.getId() : "null");
        return EduExperimentItemVO.objToVo(eduExperimentItem);
    }

    @Override
    public Page<EduExperimentItemVO> getEduExperimentItemVOPage(Page<EduExperimentItem> entityPage, HttpServletRequest request) {
        log.debug("[EduExperimentItem] 分页转换为VO: current={}, size={}", entityPage.getCurrent(), entityPage.getSize());
        return ServiceMethodSupport.toVOPage(entityPage, EduExperimentItemVO::objToVo);
    }

    private String mapSortField(String sortField) {
        if (sortField == null) {
            return null;
        }
        String result = switch (sortField) {
            case "id" -> "experiment_item_id";
            case "sortOrder" -> "experiment_item_no";
            case "itemName" -> "experiment_item_name";
            case "questionType" -> "experiment_item_type";
            case "questionContent" -> "experiment_item_content";
            case "experimentId" -> "experiment_id";
            case "standardAnswer" -> "experiment_item_answer";
            case "maxScore" -> "experiment_item_score";
            case "itemStatus" -> "state";
            default -> sortField;
        };
        log.trace("[EduExperimentItem] 映射排序字段: {} -> {}", sortField, result);
        return result;
    }
}
