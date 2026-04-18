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
        if (queryRequest == null) {
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
        return queryWrapper;
    }

    @Override
    public EduExperimentItemVO getEduExperimentItemVO(EduExperimentItem eduExperimentItem, HttpServletRequest request) {
        return EduExperimentItemVO.objToVo(eduExperimentItem);
    }

    @Override
    public Page<EduExperimentItemVO> getEduExperimentItemVOPage(Page<EduExperimentItem> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, EduExperimentItemVO::objToVo);
    }

    private String mapSortField(String sortField) {
        if (sortField == null) {
            return null;
        }
        return switch (sortField) {
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
    }
}
