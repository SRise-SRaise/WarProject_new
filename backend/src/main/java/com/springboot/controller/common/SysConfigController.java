package com.springboot.controller.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.common.SysConfigAddRequest;
import com.springboot.model.dto.common.SysConfigQueryRequest;
import com.springboot.model.dto.common.SysConfigUpdateRequest;
import com.springboot.model.entity.common.SysConfig;
import com.springboot.model.vo.common.SysConfigVO;
import com.springboot.service.common.SysConfigService;
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
@RequestMapping("/common/sysConfig")
public class SysConfigController {

    @Resource
    private SysConfigService sysConfigService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addSysConfig(@RequestBody SysConfigAddRequest addRequest) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysConfig entity = new SysConfig();
        BeanUtils.copyProperties(addRequest, entity);
        sysConfigService.validSysConfig(entity, true);
        boolean result = sysConfigService.save(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSysConfig(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = sysConfigService.removeById(id);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateSysConfig(@RequestBody SysConfigUpdateRequest updateRequest) {
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysConfig entity = new SysConfig();
        BeanUtils.copyProperties(updateRequest, entity);
        sysConfigService.validSysConfig(entity, false);
        boolean result = sysConfigService.updateById(entity);
        return ResultUtils.success(result);
    }

    @GetMapping("/get/vo")
    public BaseResponse<SysConfigVO> getSysConfigVOById(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysConfig entity = sysConfigService.getById(id);
        ThrowUtils.throwIf(entity == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(sysConfigService.getSysConfigVO(entity, null));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<SysConfig>> listSysConfigByPage(@RequestBody SysConfigQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<SysConfig> page = sysConfigService.page(new Page<>(current, size), sysConfigService.getQueryWrapper(queryRequest));
        return ResultUtils.success(page);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<SysConfigVO>> listSysConfigVOByPage(@RequestBody SysConfigQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<SysConfig> page = sysConfigService.page(new Page<>(current, size), sysConfigService.getQueryWrapper(queryRequest));
        return ResultUtils.success(sysConfigService.getSysConfigVOPage(page, null));
    }
}
