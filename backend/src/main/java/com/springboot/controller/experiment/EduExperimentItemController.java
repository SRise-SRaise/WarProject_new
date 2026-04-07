package com.springboot.controller.experiment;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.experiment.EduExperimentItemAddRequest;
import com.springboot.model.dto.experiment.EduExperimentItemQueryRequest;
import com.springboot.model.dto.experiment.EduExperimentItemUpdateRequest;
import com.springboot.model.entity.experiment.EduExperimentItem;
import com.springboot.model.vo.experiment.EduExperimentItemVO;
import com.springboot.service.experiment.EduExperimentItemService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/experiment/eduExperimentItem")
public class EduExperimentItemController {

    @Resource
    private EduExperimentItemService eduExperimentItemService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addEduExperimentItem(@RequestBody EduExperimentItemAddRequest addRequest) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduExperimentItem entity = new EduExperimentItem();
        BeanUtils.copyProperties(addRequest, entity);
        eduExperimentItemService.validEduExperimentItem(entity, true);
        boolean result = eduExperimentItemService.save(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteEduExperimentItem(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = eduExperimentItemService.removeById(id);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateEduExperimentItem(@RequestBody EduExperimentItemUpdateRequest updateRequest) {
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduExperimentItem entity = new EduExperimentItem();
        BeanUtils.copyProperties(updateRequest, entity);
        eduExperimentItemService.validEduExperimentItem(entity, false);
        boolean result = eduExperimentItemService.updateById(entity);
        return ResultUtils.success(result);
    }

    @GetMapping("/get/vo")
    public BaseResponse<EduExperimentItemVO> getEduExperimentItemVOById(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        EduExperimentItem entity = eduExperimentItemService.getById(id);
        ThrowUtils.throwIf(entity == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(eduExperimentItemService.getEduExperimentItemVO(entity, null));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<EduExperimentItem>> listEduExperimentItemByPage(@RequestBody EduExperimentItemQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<EduExperimentItem> page = eduExperimentItemService.page(new Page<>(current, size), eduExperimentItemService.getQueryWrapper(queryRequest));
        return ResultUtils.success(page);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<EduExperimentItemVO>> listEduExperimentItemVOByPage(@RequestBody EduExperimentItemQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<EduExperimentItem> page = eduExperimentItemService.page(new Page<>(current, size), eduExperimentItemService.getQueryWrapper(queryRequest));
        return ResultUtils.success(eduExperimentItemService.getEduExperimentItemVOPage(page, null));
    }
}
