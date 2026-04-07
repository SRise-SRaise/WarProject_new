package com.springboot.controller.experiment;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.experiment.ResExperimentResultAddRequest;
import com.springboot.model.dto.experiment.ResExperimentResultQueryRequest;
import com.springboot.model.dto.experiment.ResExperimentResultUpdateRequest;
import com.springboot.model.entity.experiment.ResExperimentResult;
import com.springboot.model.vo.experiment.ResExperimentResultVO;
import com.springboot.service.experiment.ResExperimentResultService;
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
@RequestMapping("/experiment/resExperimentResult")
public class ResExperimentResultController {

    @Resource
    private ResExperimentResultService resExperimentResultService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addResExperimentResult(@RequestBody ResExperimentResultAddRequest addRequest) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ResExperimentResult entity = new ResExperimentResult();
        BeanUtils.copyProperties(addRequest, entity);
        resExperimentResultService.validResExperimentResult(entity, true);
        boolean result = resExperimentResultService.save(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteResExperimentResult(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = resExperimentResultService.removeById(id);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateResExperimentResult(@RequestBody ResExperimentResultUpdateRequest updateRequest) {
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ResExperimentResult entity = new ResExperimentResult();
        BeanUtils.copyProperties(updateRequest, entity);
        resExperimentResultService.validResExperimentResult(entity, false);
        boolean result = resExperimentResultService.updateById(entity);
        return ResultUtils.success(result);
    }

    @GetMapping("/get/vo")
    public BaseResponse<ResExperimentResultVO> getResExperimentResultVOById(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ResExperimentResult entity = resExperimentResultService.getById(id);
        ThrowUtils.throwIf(entity == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(resExperimentResultService.getResExperimentResultVO(entity, null));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<ResExperimentResult>> listResExperimentResultByPage(@RequestBody ResExperimentResultQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<ResExperimentResult> page = resExperimentResultService.page(new Page<>(current, size), resExperimentResultService.getQueryWrapper(queryRequest));
        return ResultUtils.success(page);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<ResExperimentResultVO>> listResExperimentResultVOByPage(@RequestBody ResExperimentResultQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<ResExperimentResult> page = resExperimentResultService.page(new Page<>(current, size), resExperimentResultService.getQueryWrapper(queryRequest));
        return ResultUtils.success(resExperimentResultService.getResExperimentResultVOPage(page, null));
    }
}
