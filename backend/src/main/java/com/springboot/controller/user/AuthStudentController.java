package com.springboot.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.exception.ThrowUtils;
import com.springboot.model.dto.user.AuthStudentAddRequest;
import com.springboot.model.dto.user.AuthStudentQueryRequest;
import com.springboot.model.dto.user.AuthStudentUpdateRequest;
import com.springboot.model.entity.user.AuthStudent;
import com.springboot.model.vo.user.AuthStudentVO;
import com.springboot.service.user.AuthStudentService;
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
@RequestMapping("/user/authStudent")
public class AuthStudentController {

    @Resource
    private AuthStudentService authStudentService;

    @PostMapping("/add")
    public BaseResponse<Boolean> addAuthStudent(@RequestBody AuthStudentAddRequest addRequest) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AuthStudent entity = new AuthStudent();
        BeanUtils.copyProperties(addRequest, entity);
        authStudentService.validAuthStudent(entity, true);
        boolean result = authStudentService.save(entity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteAuthStudent(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = authStudentService.removeById(id);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateAuthStudent(@RequestBody AuthStudentUpdateRequest updateRequest) {
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AuthStudent entity = new AuthStudent();
        BeanUtils.copyProperties(updateRequest, entity);
        authStudentService.validAuthStudent(entity, false);
        boolean result = authStudentService.updateById(entity);
        return ResultUtils.success(result);
    }

    @GetMapping("/get/vo")
    public BaseResponse<AuthStudentVO> getAuthStudentVOById(@RequestParam String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AuthStudent entity = authStudentService.getById(id);
        ThrowUtils.throwIf(entity == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(authStudentService.getAuthStudentVO(entity, null));
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<AuthStudent>> listAuthStudentByPage(@RequestBody AuthStudentQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        Page<AuthStudent> page = authStudentService.page(new Page<>(current, size), authStudentService.getQueryWrapper(queryRequest));
        return ResultUtils.success(page);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<AuthStudentVO>> listAuthStudentVOByPage(@RequestBody AuthStudentQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        ThrowUtils.throwIf(size > 50, ErrorCode.PARAMS_ERROR);
        Page<AuthStudent> page = authStudentService.page(new Page<>(current, size), authStudentService.getQueryWrapper(queryRequest));
        return ResultUtils.success(authStudentService.getAuthStudentVOPage(page, null));
    }
}
