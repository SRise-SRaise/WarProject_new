package com.springboot.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.user.AuthTeacherAddRequest;
import com.springboot.model.dto.user.AuthTeacherQueryRequest;
import com.springboot.model.dto.user.AuthTeacherUpdateRequest;
import com.springboot.model.entity.user.AuthTeacher;
import com.springboot.model.vo.user.AuthTeacherVO;
import com.springboot.service.user.AuthTeacherService;
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
@RequestMapping("/user/authTeacher")
public class AuthTeacherController {

    @Resource
    private AuthTeacherService authTeacherService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addAuthTeacher(@RequestBody AuthTeacherAddRequest addRequest) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AuthTeacher entity = new AuthTeacher();
        BeanUtils.copyProperties(addRequest, entity);
        authTeacherService.validAuthTeacher(entity, true);
        boolean result = authTeacherService.save(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteAuthTeacher(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = authTeacherService.removeById(id);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateAuthTeacher(@RequestBody AuthTeacherUpdateRequest updateRequest) {
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AuthTeacher entity = new AuthTeacher();
        BeanUtils.copyProperties(updateRequest, entity);
        authTeacherService.validAuthTeacher(entity, false);
        boolean result = authTeacherService.updateById(entity);
        return ResultUtils.success(result);
    }

    @GetMapping("/get/vo")
    public BaseResponse<AuthTeacherVO> getAuthTeacherVOById(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AuthTeacher entity = authTeacherService.getById(id);
        ThrowUtils.throwIf(entity == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(authTeacherService.getAuthTeacherVO(entity, null));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<AuthTeacher>> listAuthTeacherByPage(@RequestBody AuthTeacherQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<AuthTeacher> page = authTeacherService.page(new Page<>(current, size), authTeacherService.getQueryWrapper(queryRequest));
        return ResultUtils.success(page);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<AuthTeacherVO>> listAuthTeacherVOByPage(@RequestBody AuthTeacherQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<AuthTeacher> page = authTeacherService.page(new Page<>(current, size), authTeacherService.getQueryWrapper(queryRequest));
        return ResultUtils.success(authTeacherService.getAuthTeacherVOPage(page, null));
    }
}
