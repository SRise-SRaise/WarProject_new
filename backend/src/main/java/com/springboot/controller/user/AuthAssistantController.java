package com.springboot.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.user.AuthAssistantAddRequest;
import com.springboot.model.dto.user.AuthAssistantQueryRequest;
import com.springboot.model.dto.user.AuthAssistantUpdateRequest;
import com.springboot.model.entity.user.AuthAssistant;
import com.springboot.model.vo.user.AuthAssistantVO;
import com.springboot.service.user.AuthAssistantService;
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
@RequestMapping("/user/authAssistant")
public class AuthAssistantController {

    @Resource
    private AuthAssistantService authAssistantService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addAuthAssistant(@RequestBody AuthAssistantAddRequest addRequest) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AuthAssistant entity = new AuthAssistant();
        BeanUtils.copyProperties(addRequest, entity);
        authAssistantService.validAuthAssistant(entity, true);
        boolean result = authAssistantService.save(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteAuthAssistant(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = authAssistantService.removeById(id);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateAuthAssistant(@RequestBody AuthAssistantUpdateRequest updateRequest) {
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AuthAssistant entity = new AuthAssistant();
        BeanUtils.copyProperties(updateRequest, entity);
        authAssistantService.validAuthAssistant(entity, false);
        boolean result = authAssistantService.updateById(entity);
        return ResultUtils.success(result);
    }

    @GetMapping("/get/vo")
    public BaseResponse<AuthAssistantVO> getAuthAssistantVOById(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AuthAssistant entity = authAssistantService.getById(id);
        ThrowUtils.throwIf(entity == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(authAssistantService.getAuthAssistantVO(entity, null));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<AuthAssistant>> listAuthAssistantByPage(@RequestBody AuthAssistantQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<AuthAssistant> page = authAssistantService.page(new Page<>(current, size), authAssistantService.getQueryWrapper(queryRequest));
        return ResultUtils.success(page);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<AuthAssistantVO>> listAuthAssistantVOByPage(@RequestBody AuthAssistantQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<AuthAssistant> page = authAssistantService.page(new Page<>(current, size), authAssistantService.getQueryWrapper(queryRequest));
        return ResultUtils.success(authAssistantService.getAuthAssistantVOPage(page, null));
    }
}
