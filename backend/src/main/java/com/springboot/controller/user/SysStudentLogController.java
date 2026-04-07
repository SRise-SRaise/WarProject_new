package com.springboot.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.user.SysStudentLogAddRequest;
import com.springboot.model.dto.user.SysStudentLogQueryRequest;
import com.springboot.model.dto.user.SysStudentLogUpdateRequest;
import com.springboot.model.entity.user.SysStudentLog;
import com.springboot.model.vo.user.SysStudentLogVO;
import com.springboot.service.user.SysStudentLogService;
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
@RequestMapping("/user/sysStudentLog")
public class SysStudentLogController {

    @Resource
    private SysStudentLogService sysStudentLogService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addSysStudentLog(@RequestBody SysStudentLogAddRequest addRequest) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysStudentLog entity = new SysStudentLog();
        BeanUtils.copyProperties(addRequest, entity);
        sysStudentLogService.validSysStudentLog(entity, true);
        boolean result = sysStudentLogService.save(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSysStudentLog(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = sysStudentLogService.removeById(id);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateSysStudentLog(@RequestBody SysStudentLogUpdateRequest updateRequest) {
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysStudentLog entity = new SysStudentLog();
        BeanUtils.copyProperties(updateRequest, entity);
        sysStudentLogService.validSysStudentLog(entity, false);
        boolean result = sysStudentLogService.updateById(entity);
        return ResultUtils.success(result);
    }

    @GetMapping("/get/vo")
    public BaseResponse<SysStudentLogVO> getSysStudentLogVOById(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysStudentLog entity = sysStudentLogService.getById(id);
        ThrowUtils.throwIf(entity == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(sysStudentLogService.getSysStudentLogVO(entity, null));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<SysStudentLog>> listSysStudentLogByPage(@RequestBody SysStudentLogQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<SysStudentLog> page = sysStudentLogService.page(new Page<>(current, size), sysStudentLogService.getQueryWrapper(queryRequest));
        return ResultUtils.success(page);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<SysStudentLogVO>> listSysStudentLogVOByPage(@RequestBody SysStudentLogQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<SysStudentLog> page = sysStudentLogService.page(new Page<>(current, size), sysStudentLogService.getQueryWrapper(queryRequest));
        return ResultUtils.success(sysStudentLogService.getSysStudentLogVOPage(page, null));
    }
}
