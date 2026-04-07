package com.springboot.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.springboot.common.ErrorCode;
import com.springboot.constant.UserConstant;
import com.springboot.exception.BusinessException;
import com.springboot.model.dto.user.AuthLoginRequest;
import com.springboot.model.dto.user.AuthRegisterRequest;
import com.springboot.model.entity.user.AuthAssistant;
import com.springboot.model.entity.user.AuthStudent;
import com.springboot.model.entity.user.AuthTeacher;
import com.springboot.model.vo.user.AuthLoginVO;
import com.springboot.model.vo.user.LoginPrincipal;
import com.springboot.service.user.AuthAssistantService;
import com.springboot.service.user.AuthLoginService;
import com.springboot.service.user.AuthStudentService;
import com.springboot.service.user.AuthTeacherService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class AuthLoginServiceImpl implements AuthLoginService {

    private static final String SALT = "springboot";

    @Resource
    private AuthStudentService authStudentService;

    @Resource
    private AuthTeacherService authTeacherService;

    @Resource
    private AuthAssistantService authAssistantService;

    @Override
    public AuthLoginVO login(AuthLoginRequest authLoginRequest, HttpServletRequest request) {
        if (authLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String roleType = StringUtils.trimToEmpty(authLoginRequest.getRoleType()).toLowerCase();
        String loginAccount = StringUtils.trimToEmpty(authLoginRequest.getLoginAccount());
        String loginPassword = StringUtils.trimToEmpty(authLoginRequest.getLoginPassword());
        if (StringUtils.isAnyBlank(roleType, loginAccount, loginPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号、密码或角色不能为空");
        }
        LoginPrincipal loginPrincipal;
        switch (roleType) {
            case UserConstant.ROLE_TYPE_STUDENT:
                loginPrincipal = studentLogin(loginAccount, loginPassword);
                break;
            case UserConstant.ROLE_TYPE_TEACHER:
                loginPrincipal = teacherLogin(loginAccount, loginPassword);
                break;
            case UserConstant.ROLE_TYPE_ASSISTANT:
                loginPrincipal = assistantLogin(loginAccount, loginPassword);
                break;
            default:
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "角色类型不支持");
        }
        request.getSession().setAttribute(UserConstant.AUTH_LOGIN_STATE, loginPrincipal);
        return buildLoginVO(request, loginPrincipal);
    }

    @Override
    public AuthLoginVO register(AuthRegisterRequest authRegisterRequest, HttpServletRequest request) {
        if (authRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String roleType = StringUtils.trimToEmpty(authRegisterRequest.getRoleType()).toLowerCase();
        String loginAccount = StringUtils.trimToEmpty(authRegisterRequest.getLoginAccount());
        String displayName = StringUtils.trimToEmpty(authRegisterRequest.getDisplayName());
        String classCode = StringUtils.trimToEmpty(authRegisterRequest.getClassCode());
        String loginPassword = StringUtils.trimToEmpty(authRegisterRequest.getLoginPassword());
        String checkPassword = StringUtils.trimToEmpty(authRegisterRequest.getCheckPassword());
        if (StringUtils.isAnyBlank(roleType, loginAccount, displayName, loginPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册参数不完整");
        }
        if (!loginPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入密码不一致");
        }
        if (loginPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能小于8位");
        }
        if (!UserConstant.ROLE_TYPE_STUDENT.equals(roleType)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "仅支持学生自助注册");
        }
        QueryWrapper<AuthStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_code", loginAccount);
        long count = authStudentService.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
        }
        AuthStudent authStudent = new AuthStudent();
        authStudent.setStudentCode(loginAccount);
        authStudent.setStudentName(displayName);
        authStudent.setClassCode(classCode);
        authStudent.setPasswordMd5(DigestUtils.md5DigestAsHex((SALT + loginPassword).getBytes()));
        authStudent.setAccountStatus(0);
        authStudent.setLoginFailCount(0);
        boolean saveResult = authStudentService.save(authStudent);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败");
        }
        LoginPrincipal loginPrincipal = new LoginPrincipal();
        loginPrincipal.setUserId(authStudent.getId());
        loginPrincipal.setRoleType(UserConstant.ROLE_TYPE_STUDENT);
        loginPrincipal.setRoleCode(UserConstant.DEFAULT_ROLE);
        loginPrincipal.setLoginAccount(authStudent.getStudentCode());
        loginPrincipal.setDisplayName(authStudent.getStudentName());
        request.getSession().setAttribute(UserConstant.AUTH_LOGIN_STATE, loginPrincipal);
        return buildLoginVO(request, loginPrincipal);
    }

    private AuthLoginVO buildLoginVO(HttpServletRequest request, LoginPrincipal loginPrincipal) {
        AuthLoginVO authLoginVO = new AuthLoginVO();
        authLoginVO.setSessionId(request.getSession().getId());
        authLoginVO.setLoginPrincipal(loginPrincipal);
        return authLoginVO;
    }

    @Override
    public boolean logout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        request.getSession().removeAttribute(UserConstant.AUTH_LOGIN_STATE);
        return true;
    }

    @Override
    public LoginPrincipal getLoginPrincipal(HttpServletRequest request) {
        LoginPrincipal loginPrincipal = getLoginPrincipalPermitNull(request);
        if (loginPrincipal == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return loginPrincipal;
    }

    @Override
    public LoginPrincipal getLoginPrincipalPermitNull(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object loginObj = request.getSession().getAttribute(UserConstant.AUTH_LOGIN_STATE);
        if (!(loginObj instanceof LoginPrincipal)) {
            return null;
        }
        return (LoginPrincipal) loginObj;
    }

    private LoginPrincipal studentLogin(String loginAccount, String loginPassword) {
        QueryWrapper<AuthStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_code", loginAccount);
        AuthStudent authStudent = authStudentService.getOne(queryWrapper);
        if (authStudent == null || !passwordMatched(loginPassword, authStudent.getPasswordMd5())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }
        if (authStudent.getAccountStatus() != null && authStudent.getAccountStatus() != 0) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "账号状态异常，禁止登录");
        }
        LoginPrincipal loginPrincipal = new LoginPrincipal();
        loginPrincipal.setUserId(authStudent.getId());
        loginPrincipal.setRoleType(UserConstant.ROLE_TYPE_STUDENT);
        loginPrincipal.setRoleCode(UserConstant.DEFAULT_ROLE);
        loginPrincipal.setLoginAccount(authStudent.getStudentCode());
        loginPrincipal.setDisplayName(authStudent.getStudentName());
        return loginPrincipal;
    }

    private LoginPrincipal teacherLogin(String loginAccount, String loginPassword) {
        QueryWrapper<AuthTeacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", loginAccount);
        AuthTeacher authTeacher = authTeacherService.getOne(queryWrapper);
        if (authTeacher == null || !passwordMatched(loginPassword, authTeacher.getPasswordMd5())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }
        LoginPrincipal loginPrincipal = new LoginPrincipal();
        loginPrincipal.setUserId(authTeacher.getId());
        loginPrincipal.setRoleType(UserConstant.ROLE_TYPE_TEACHER);
        loginPrincipal.setRoleCode(UserConstant.ADMIN_ROLE);
        loginPrincipal.setLoginAccount(authTeacher.getUsername());
        loginPrincipal.setDisplayName(authTeacher.getRealName());
        return loginPrincipal;
    }

    private LoginPrincipal assistantLogin(String loginAccount, String loginPassword) {
        QueryWrapper<AuthAssistant> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", loginAccount);
        AuthAssistant authAssistant = authAssistantService.getOne(queryWrapper);
        if (authAssistant == null || !passwordMatched(loginPassword, authAssistant.getPasswordHash())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }
        LoginPrincipal loginPrincipal = new LoginPrincipal();
        loginPrincipal.setUserId(authAssistant.getId());
        loginPrincipal.setRoleType(UserConstant.ROLE_TYPE_ASSISTANT);
        loginPrincipal.setRoleCode(UserConstant.DEFAULT_ROLE);
        loginPrincipal.setLoginAccount(authAssistant.getUsername());
        loginPrincipal.setDisplayName(authAssistant.getRealName());
        return loginPrincipal;
    }

    private boolean passwordMatched(String rawPassword, String dbPassword) {
        if (StringUtils.isBlank(dbPassword)) {
            return false;
        }
        String md5Raw = DigestUtils.md5DigestAsHex(rawPassword.getBytes());
        String md5Salt = DigestUtils.md5DigestAsHex((SALT + rawPassword).getBytes());
        return dbPassword.equals(rawPassword) || dbPassword.equals(md5Raw) || dbPassword.equals(md5Salt);
    }
}
