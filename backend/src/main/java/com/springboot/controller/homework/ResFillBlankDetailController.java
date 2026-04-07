package com.springboot.controller.homework;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.homework.ResFillBlankDetailAddRequest;
import com.springboot.model.dto.homework.ResFillBlankDetailQueryRequest;
import com.springboot.model.dto.homework.ResFillBlankDetailUpdateRequest;
import com.springboot.model.entity.homework.ResFillBlankDetail;
import com.springboot.model.vo.homework.ResFillBlankDetailVO;
import com.springboot.service.homework.ResFillBlankDetailService;
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
@RequestMapping("/homework/resFillBlankDetail")
public class ResFillBlankDetailController {

    @Resource
    private ResFillBlankDetailService resFillBlankDetailService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addResFillBlankDetail(@RequestBody ResFillBlankDetailAddRequest addRequest) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ResFillBlankDetail entity = new ResFillBlankDetail();
        BeanUtils.copyProperties(addRequest, entity);
        resFillBlankDetailService.validResFillBlankDetail(entity, true);
        boolean result = resFillBlankDetailService.save(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteResFillBlankDetail(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = resFillBlankDetailService.removeById(id);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateResFillBlankDetail(@RequestBody ResFillBlankDetailUpdateRequest updateRequest) {
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ResFillBlankDetail entity = new ResFillBlankDetail();
        BeanUtils.copyProperties(updateRequest, entity);
        resFillBlankDetailService.validResFillBlankDetail(entity, false);
        boolean result = resFillBlankDetailService.updateById(entity);
        return ResultUtils.success(result);
    }

    @GetMapping("/get/vo")
    public BaseResponse<ResFillBlankDetailVO> getResFillBlankDetailVOById(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ResFillBlankDetail entity = resFillBlankDetailService.getById(id);
        ThrowUtils.throwIf(entity == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(resFillBlankDetailService.getResFillBlankDetailVO(entity, null));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<ResFillBlankDetail>> listResFillBlankDetailByPage(@RequestBody ResFillBlankDetailQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<ResFillBlankDetail> page = resFillBlankDetailService.page(new Page<>(current, size), resFillBlankDetailService.getQueryWrapper(queryRequest));
        return ResultUtils.success(page);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<ResFillBlankDetailVO>> listResFillBlankDetailVOByPage(@RequestBody ResFillBlankDetailQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<ResFillBlankDetail> page = resFillBlankDetailService.page(new Page<>(current, size), resFillBlankDetailService.getQueryWrapper(queryRequest));
        return ResultUtils.success(resFillBlankDetailService.getResFillBlankDetailVOPage(page, null));
    }
}
