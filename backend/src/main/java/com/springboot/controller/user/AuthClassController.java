package com.springboot.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.user.AuthClassAddRequest;
import com.springboot.model.dto.user.AuthClassQueryRequest;
import com.springboot.model.dto.user.AuthClassUpdateRequest;
import com.springboot.model.entity.user.AuthClass;
import com.springboot.model.vo.user.AuthClassVO;
import com.springboot.service.user.AuthClassService;
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
@RequestMapping("/user/authClass")
public class AuthClassController {

    @Resource
    private AuthClassService authClassService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addAuthClass(@RequestBody AuthClassAddRequest addRequest) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AuthClass entity = new AuthClass();
        BeanUtils.copyProperties(addRequest, entity);
        authClassService.validAuthClass(entity, true);
        boolean result = authClassService.save(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteAuthClass(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = authClassService.removeById(id);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateAuthClass(@RequestBody AuthClassUpdateRequest updateRequest) {
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AuthClass entity = new AuthClass();
        BeanUtils.copyProperties(updateRequest, entity);
        authClassService.validAuthClass(entity, false);
        boolean result = authClassService.updateById(entity);
        return ResultUtils.success(result);
    }

    @GetMapping("/get/vo")
    public BaseResponse<AuthClassVO> getAuthClassVOById(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AuthClass entity = authClassService.getById(id);
        ThrowUtils.throwIf(entity == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(authClassService.getAuthClassVO(entity, null));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<AuthClass>> listAuthClassByPage(@RequestBody AuthClassQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<AuthClass> page = authClassService.page(new Page<>(current, size), authClassService.getQueryWrapper(queryRequest));
        return ResultUtils.success(page);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<AuthClassVO>> listAuthClassVOByPage(@RequestBody AuthClassQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<AuthClass> page = authClassService.page(new Page<>(current, size), authClassService.getQueryWrapper(queryRequest));
        return ResultUtils.success(authClassService.getAuthClassVOPage(page, null));
    }
}
