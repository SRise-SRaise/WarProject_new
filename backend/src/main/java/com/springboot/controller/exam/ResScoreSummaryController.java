package com.springboot.controller.exam;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.exam.ResScoreSummaryAddRequest;
import com.springboot.model.dto.exam.ResScoreSummaryQueryRequest;
import com.springboot.model.dto.exam.ResScoreSummaryUpdateRequest;
import com.springboot.model.entity.exam.ResScoreSummary;
import com.springboot.model.vo.exam.ResScoreSummaryVO;
import com.springboot.service.exam.ResScoreSummaryService;
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
@RequestMapping("/exam/resScoreSummary")
public class ResScoreSummaryController {

    @Resource
    private ResScoreSummaryService resScoreSummaryService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addResScoreSummary(@RequestBody ResScoreSummaryAddRequest addRequest) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ResScoreSummary entity = new ResScoreSummary();
        BeanUtils.copyProperties(addRequest, entity);
        resScoreSummaryService.validResScoreSummary(entity, true);
        boolean result = resScoreSummaryService.save(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteResScoreSummary(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = resScoreSummaryService.removeById(id);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateResScoreSummary(@RequestBody ResScoreSummaryUpdateRequest updateRequest) {
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ResScoreSummary entity = new ResScoreSummary();
        BeanUtils.copyProperties(updateRequest, entity);
        resScoreSummaryService.validResScoreSummary(entity, false);
        boolean result = resScoreSummaryService.updateById(entity);
        return ResultUtils.success(result);
    }

    @GetMapping("/get/vo")
    public BaseResponse<ResScoreSummaryVO> getResScoreSummaryVOById(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ResScoreSummary entity = resScoreSummaryService.getById(id);
        ThrowUtils.throwIf(entity == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(resScoreSummaryService.getResScoreSummaryVO(entity, null));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<ResScoreSummary>> listResScoreSummaryByPage(@RequestBody ResScoreSummaryQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<ResScoreSummary> page = resScoreSummaryService.page(new Page<>(current, size), resScoreSummaryService.getQueryWrapper(queryRequest));
        return ResultUtils.success(page);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<ResScoreSummaryVO>> listResScoreSummaryVOByPage(@RequestBody ResScoreSummaryQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<ResScoreSummary> page = resScoreSummaryService.page(new Page<>(current, size), resScoreSummaryService.getQueryWrapper(queryRequest));
        return ResultUtils.success(resScoreSummaryService.getResScoreSummaryVOPage(page, null));
    }
}
