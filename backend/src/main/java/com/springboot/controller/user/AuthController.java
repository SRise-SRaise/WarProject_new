package com.springboot.controller.user;

import com.springboot.common.BaseResponse;
import com.springboot.common.ErrorCode;
import com.springboot.common.ResultUtils;
import com.springboot.exception.BusinessException;
import com.springboot.model.dto.user.AuthLoginRequest;
import com.springboot.model.dto.user.AuthRegisterRequest;
import com.springboot.model.dto.user.AuthChangePasswordRequest;
import com.springboot.model.vo.user.AuthLoginVO;
import com.springboot.model.vo.user.LoginPrincipal;
import com.springboot.service.user.AuthLoginService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private AuthLoginService authLoginService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public BaseResponse<AuthLoginVO> login(@RequestBody AuthLoginRequest authLoginRequest, HttpServletRequest request) {
        if (authLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String roleType = authLoginRequest.getRoleType();
        String loginAccount = authLoginRequest.getLoginAccount();
        String loginPassword = authLoginRequest.getLoginPassword();
        if (StringUtils.isAnyBlank(roleType, loginAccount, loginPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(authLoginService.login(authLoginRequest, request));
    }

    /**
     * 用户注册（当前支持学生、教师注册）。
     */
    @PostMapping("/register")
    public BaseResponse<AuthLoginVO> register(@RequestBody AuthRegisterRequest authRegisterRequest,
            HttpServletRequest request) {
        if (authRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String roleType = authRegisterRequest.getRoleType();
        String loginAccount = authRegisterRequest.getLoginAccount();
        String displayName = authRegisterRequest.getDisplayName();
        String loginPassword = authRegisterRequest.getLoginPassword();
        String checkPassword = authRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(roleType, loginAccount, displayName, loginPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(authLoginService.register(authRegisterRequest, request));
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> logout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(authLoginService.logout(request));
    }

    /**
     * 修改当前登录用户密码
     */
    @PostMapping("/change/password")
    public BaseResponse<Boolean> changePassword(@RequestBody AuthChangePasswordRequest changePasswordRequest,
            HttpServletRequest request) {
        if (changePasswordRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(authLoginService.changePassword(changePasswordRequest, request));
    }

    /**
     * 获取当前登录用户
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginPrincipal> getLoginUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(authLoginService.getLoginPrincipal(request));
    }
}
