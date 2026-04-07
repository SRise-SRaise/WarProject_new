package com.springboot.controller.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.common.SysAdminLogAddRequest;
import com.springboot.model.dto.common.SysAdminLogQueryRequest;
import com.springboot.model.dto.common.SysAdminLogUpdateRequest;
import com.springboot.model.entity.common.SysAdminLog;
import com.springboot.model.vo.common.SysAdminLogVO;
import com.springboot.service.common.SysAdminLogService;
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
@RequestMapping("/common/sysAdminLog")
public class SysAdminLogController {

    @Resource
    private SysAdminLogService sysAdminLogService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addSysAdminLog(@RequestBody SysAdminLogAddRequest addRequest) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysAdminLog entity = new SysAdminLog();
        BeanUtils.copyProperties(addRequest, entity);
        sysAdminLogService.validSysAdminLog(entity, true);
        boolean result = sysAdminLogService.save(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSysAdminLog(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = sysAdminLogService.removeById(id);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateSysAdminLog(@RequestBody SysAdminLogUpdateRequest updateRequest) {
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysAdminLog entity = new SysAdminLog();
        BeanUtils.copyProperties(updateRequest, entity);
        sysAdminLogService.validSysAdminLog(entity, false);
        boolean result = sysAdminLogService.updateById(entity);
        return ResultUtils.success(result);
    }

    @GetMapping("/get/vo")
    public BaseResponse<SysAdminLogVO> getSysAdminLogVOById(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysAdminLog entity = sysAdminLogService.getById(id);
        ThrowUtils.throwIf(entity == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(sysAdminLogService.getSysAdminLogVO(entity, null));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<SysAdminLog>> listSysAdminLogByPage(@RequestBody SysAdminLogQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<SysAdminLog> page = sysAdminLogService.page(new Page<>(current, size), sysAdminLogService.getQueryWrapper(queryRequest));
        return ResultUtils.success(page);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<SysAdminLogVO>> listSysAdminLogVOByPage(@RequestBody SysAdminLogQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<SysAdminLog> page = sysAdminLogService.page(new Page<>(current, size), sysAdminLogService.getQueryWrapper(queryRequest));
        return ResultUtils.success(sysAdminLogService.getSysAdminLogVOPage(page, null));
    }
}
