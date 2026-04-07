package com.springboot.controller.user;

import com.springboot.common.BaseResponse;
import com.springboot.common.ResultUtils;
import com.springboot.model.dto.user.AuthLoginRequest;
import com.springboot.model.dto.user.AuthRegisterRequest;
import com.springboot.model.vo.user.AuthLoginVO;
import com.springboot.model.vo.user.LoginPrincipal;
import com.springboot.service.user.AuthLoginService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
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

    @PostMapping("/login")
    public BaseResponse<AuthLoginVO> login(@RequestBody AuthLoginRequest authLoginRequest, HttpServletRequest request) {
        return ResultUtils.success(authLoginService.login(authLoginRequest, request));
    }

    @PostMapping("/register")
    public BaseResponse<AuthLoginVO> register(@RequestBody AuthRegisterRequest authRegisterRequest,
            HttpServletRequest request) {
        return ResultUtils.success(authLoginService.register(authRegisterRequest, request));
    }

    @PostMapping("/logout")
    public BaseResponse<Boolean> logout(HttpServletRequest request) {
        return ResultUtils.success(authLoginService.logout(request));
    }

    @GetMapping("/get/login")
    public BaseResponse<LoginPrincipal> getLoginUser(HttpServletRequest request) {
        return ResultUtils.success(authLoginService.getLoginPrincipal(request));
    }
}
