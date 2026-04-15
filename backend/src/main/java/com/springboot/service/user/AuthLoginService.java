package com.springboot.service.user;

import com.springboot.model.dto.user.AuthLoginRequest;
import com.springboot.model.dto.user.AuthRegisterRequest;
import com.springboot.model.vo.user.AuthLoginVO;
import com.springboot.model.vo.user.LoginPrincipal;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthLoginService {

    AuthLoginVO login(AuthLoginRequest authLoginRequest, HttpServletRequest request);

    /**
     * 用户注册（当前仅支持学生自助注册）。
     */
    AuthLoginVO register(AuthRegisterRequest authRegisterRequest, HttpServletRequest request);

    boolean logout(HttpServletRequest request);

    LoginPrincipal getLoginPrincipal(HttpServletRequest request);

    LoginPrincipal getLoginPrincipalPermitNull(HttpServletRequest request);
}
