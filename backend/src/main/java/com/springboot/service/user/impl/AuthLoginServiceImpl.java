package com.springboot.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.springboot.common.ErrorCode;
import com.springboot.constant.UserConstant;
import com.springboot.exception.BusinessException;
import com.springboot.model.dto.user.AuthLoginRequest;
import com.springboot.model.dto.user.AuthRegisterRequest;
import com.springboot.model.dto.user.AuthChangePasswordRequest;
import com.springboot.model.entity.user.AuthAssistant;
import com.springboot.model.entity.user.AuthStudent;
import com.springboot.model.entity.user.AuthTeacher;
import com.springboot.model.vo.user.AuthLoginVO;
import com.springboot.model.vo.user.LoginPrincipal;
import com.springboot.service.user.AuthAssistantService;
import com.springboot.service.user.AuthClassService;
import com.springboot.service.user.AuthLoginService;
import com.springboot.service.user.AuthStudentService;
import com.springboot.service.user.AuthTeacherService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class AuthLoginServiceImpl implements AuthLoginService {

    private static final String SALT = "springboot";
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_ACCOUNT_LENGTH = 32;
    private static final int MAX_DISPLAY_NAME_LENGTH = 32;
    private static final int MAX_CLASS_CODE_LENGTH = 32;
    private static final ConcurrentMap<String, String> ACCOUNT_SESSION_TOKEN_MAP = new ConcurrentHashMap<>();

    @Resource
    private AuthStudentService authStudentService;

    @Resource
    private AuthTeacherService authTeacherService;

    @Resource
    private AuthAssistantService authAssistantService;

    @Resource
    private AuthClassService authClassService;

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
                loginPrincipal = studentLogin(loginAccount, loginPassword, request);
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
        String accountSessionKey = buildAccountSessionKey(loginPrincipal);
        if (ACCOUNT_SESSION_TOKEN_MAP.containsKey(accountSessionKey)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "该账号已在其他浏览器登录，请先退出后再试");
        }
        String sessionToken = UUID.randomUUID().toString();
        ACCOUNT_SESSION_TOKEN_MAP.put(accountSessionKey, sessionToken);
        request.getSession().setAttribute(UserConstant.AUTH_LOGIN_STATE, loginPrincipal);
        request.getSession().setAttribute(UserConstant.AUTH_LOGIN_SESSION_TOKEN, sessionToken);
        return buildLoginVO(request, loginPrincipal);
    }

    @Override
    public AuthLoginVO register(AuthRegisterRequest authRegisterRequest, HttpServletRequest request) {
        if (authRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求上下文为空");
        }
        String roleType = StringUtils.trimToEmpty(authRegisterRequest.getRoleType()).toLowerCase();
        String loginAccount = StringUtils.trimToEmpty(authRegisterRequest.getLoginAccount());
        String displayName = StringUtils.trimToEmpty(authRegisterRequest.getDisplayName());
        String classCode = StringUtils.trimToNull(authRegisterRequest.getClassCode());
        String loginPassword = StringUtils.trimToEmpty(authRegisterRequest.getLoginPassword());
        String checkPassword = StringUtils.trimToEmpty(authRegisterRequest.getCheckPassword());
        if (StringUtils.isAnyBlank(roleType, loginAccount, displayName, loginPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册参数不完整");
        }
        if (!loginPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入密码不一致");
        }
        if (loginPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能小于8位");
        }
        if (loginAccount.length() > MAX_ACCOUNT_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度不能超过32位");
        }
        if (displayName.length() > MAX_DISPLAY_NAME_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "昵称长度不能超过32位");
        }
        if (classCode != null && classCode.length() > MAX_CLASS_CODE_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "班级编号长度不能超过32位");
        }
        LoginPrincipal loginPrincipal;
        if (UserConstant.ROLE_TYPE_STUDENT.equals(roleType)) {
            if (classCode != null && authClassService.getById(classCode) == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "班级编号不存在");
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
            loginPrincipal = buildStudentPrincipal(authStudent);
        } else if (UserConstant.ROLE_TYPE_TEACHER.equals(roleType)) {
            QueryWrapper<AuthTeacher> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", loginAccount);
            long count = authTeacherService.count(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "教师工号已存在");
            }
            AuthTeacher authTeacher = new AuthTeacher();
            authTeacher.setUsername(loginAccount);
            authTeacher.setRealName(displayName);
            authTeacher.setPasswordMd5(DigestUtils.md5DigestAsHex((SALT + loginPassword).getBytes()));
            boolean saveResult = authTeacherService.save(authTeacher);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败");
            }
            loginPrincipal = buildTeacherPrincipal(authTeacher);
        } else {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "仅支持学生或教师注册");
        }
        String accountSessionKey = buildAccountSessionKey(loginPrincipal);
        String sessionToken = UUID.randomUUID().toString();
        ACCOUNT_SESSION_TOKEN_MAP.put(accountSessionKey, sessionToken);
        request.getSession().setAttribute(UserConstant.AUTH_LOGIN_STATE, loginPrincipal);
        request.getSession().setAttribute(UserConstant.AUTH_LOGIN_SESSION_TOKEN, sessionToken);
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
        if (getLoginPrincipalPermitNull(request) == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "当前未登录");
        }
        Object loginObj = request.getSession().getAttribute(UserConstant.AUTH_LOGIN_STATE);
        if (loginObj instanceof LoginPrincipal) {
            LoginPrincipal loginPrincipal = (LoginPrincipal) loginObj;
            String accountSessionKey = buildAccountSessionKey(loginPrincipal);
            String sessionToken = String.valueOf(request.getSession().getAttribute(UserConstant.AUTH_LOGIN_SESSION_TOKEN));
            ACCOUNT_SESSION_TOKEN_MAP.computeIfPresent(accountSessionKey,
                    (key, activeToken) -> activeToken.equals(sessionToken) ? null : activeToken);
        }
        request.getSession().removeAttribute(UserConstant.AUTH_LOGIN_STATE);
        request.getSession().removeAttribute(UserConstant.AUTH_LOGIN_SESSION_TOKEN);
        return true;
    }

    @Override
    public boolean changePassword(AuthChangePasswordRequest changePasswordRequest, HttpServletRequest request) {
        if (changePasswordRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }
        String oldPassword = StringUtils.trimToEmpty(changePasswordRequest.getOldPassword());
        String newPassword = StringUtils.trimToEmpty(changePasswordRequest.getNewPassword());
        String confirmPassword = StringUtils.trimToEmpty(changePasswordRequest.getConfirmPassword());
        if (StringUtils.isAnyBlank(oldPassword, newPassword, confirmPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码参数不能为空");
        }
        if (newPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新密码长度不能小于8位");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的新密码不一致");
        }
        if (oldPassword.equals(newPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新密码不能与旧密码相同");
        }
        LoginPrincipal loginPrincipal = getLoginPrincipal(request);
        String roleType = StringUtils.trimToEmpty(loginPrincipal.getRoleType()).toLowerCase();
        String loginAccount = StringUtils.trimToEmpty(loginPrincipal.getLoginAccount());
        if (StringUtils.isBlank(roleType) || StringUtils.isBlank(loginAccount)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "登录态无效，请重新登录");
        }
        if (UserConstant.ROLE_TYPE_STUDENT.equals(roleType)) {
            QueryWrapper<AuthStudent> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("student_code", loginAccount);
            queryWrapper.orderByDesc("updated_at", "id");
            queryWrapper.last("limit 1");
            AuthStudent authStudent = authStudentService.getOne(queryWrapper, false);
            if (authStudent == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "学生账号不存在");
            }
            if (!passwordMatched(oldPassword, authStudent.getPasswordMd5())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "旧密码错误");
            }
            authStudent.setPasswordMd5(DigestUtils.md5DigestAsHex((SALT + newPassword).getBytes()));
            return authStudentService.updateById(authStudent);
        }
        if (UserConstant.ROLE_TYPE_TEACHER.equals(roleType)) {
            QueryWrapper<AuthTeacher> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", loginAccount);
            queryWrapper.orderByDesc("updated_at", "id");
            queryWrapper.last("limit 1");
            AuthTeacher authTeacher = authTeacherService.getOne(queryWrapper, false);
            if (authTeacher == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "教师账号不存在");
            }
            if (!passwordMatched(oldPassword, authTeacher.getPasswordMd5())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "旧密码错误");
            }
            authTeacher.setPasswordMd5(DigestUtils.md5DigestAsHex((SALT + newPassword).getBytes()));
            return authTeacherService.updateById(authTeacher);
        }
        throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "当前角色不支持修改密码");
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
        LoginPrincipal loginPrincipal = (LoginPrincipal) loginObj;
        String accountSessionKey = buildAccountSessionKey(loginPrincipal);
        String activeToken = ACCOUNT_SESSION_TOKEN_MAP.get(accountSessionKey);
        Object sessionTokenObj = request.getSession().getAttribute(UserConstant.AUTH_LOGIN_SESSION_TOKEN);
        String sessionToken = sessionTokenObj == null ? null : String.valueOf(sessionTokenObj);
        if (StringUtils.isAnyBlank(activeToken, sessionToken) || !StringUtils.equals(activeToken, sessionToken)) {
            request.getSession().removeAttribute(UserConstant.AUTH_LOGIN_STATE);
            request.getSession().removeAttribute(UserConstant.AUTH_LOGIN_SESSION_TOKEN);
            return null;
        }
        // 兼容旧会话：历史 session 中可能没有 classCode/major，这里按当前数据库补齐一次
        enrichStudentPrincipal(loginPrincipal);
        request.getSession().setAttribute(UserConstant.AUTH_LOGIN_STATE, loginPrincipal);
        return loginPrincipal;
    }

    private String buildAccountSessionKey(LoginPrincipal loginPrincipal) {
        String roleType = StringUtils.trimToEmpty(loginPrincipal == null ? null : loginPrincipal.getRoleType());
        String loginAccount = StringUtils.trimToEmpty(loginPrincipal == null ? null : loginPrincipal.getLoginAccount());
        return roleType + "::" + loginAccount;
    }

    private LoginPrincipal studentLogin(String loginAccount, String loginPassword, HttpServletRequest request) {
        QueryWrapper<AuthStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_code", loginAccount);
        queryWrapper.orderByDesc("updated_at", "id");
        queryWrapper.last("limit 1");
        AuthStudent authStudent = authStudentService.getOne(queryWrapper, false);
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
        loginPrincipal.setMajor(authStudent.getRemark());
        loginPrincipal.setClassCode(authStudent.getClassCode());
        String currentLoginIp = resolveClientIp(request);
        if (StringUtils.isNotBlank(currentLoginIp)) {
            authStudent.setLastLoginIp(currentLoginIp);
            authStudentService.updateById(authStudent);
            loginPrincipal.setLastLoginIp(currentLoginIp);
        } else {
            loginPrincipal.setLastLoginIp(authStudent.getLastLoginIp());
        }
        return loginPrincipal;
    }

    private LoginPrincipal teacherLogin(String loginAccount, String loginPassword) {
        QueryWrapper<AuthTeacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", loginAccount);
        AuthTeacher authTeacher = authTeacherService.getOne(queryWrapper, false);
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
        AuthAssistant authAssistant = authAssistantService.getOne(queryWrapper, false);
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

    private LoginPrincipal buildStudentPrincipal(AuthStudent authStudent) {
        LoginPrincipal loginPrincipal = new LoginPrincipal();
        loginPrincipal.setUserId(authStudent.getId());
        loginPrincipal.setRoleType(UserConstant.ROLE_TYPE_STUDENT);
        loginPrincipal.setRoleCode(UserConstant.DEFAULT_ROLE);
        loginPrincipal.setLoginAccount(authStudent.getStudentCode());
        loginPrincipal.setDisplayName(authStudent.getStudentName());
        loginPrincipal.setMajor(authStudent.getRemark());
        loginPrincipal.setClassCode(authStudent.getClassCode());
        loginPrincipal.setLastLoginIp(authStudent.getLastLoginIp());
        return loginPrincipal;
    }

    private LoginPrincipal buildTeacherPrincipal(AuthTeacher authTeacher) {
        LoginPrincipal loginPrincipal = new LoginPrincipal();
        loginPrincipal.setUserId(authTeacher.getId());
        loginPrincipal.setRoleType(UserConstant.ROLE_TYPE_TEACHER);
        loginPrincipal.setRoleCode(UserConstant.ADMIN_ROLE);
        loginPrincipal.setLoginAccount(authTeacher.getUsername());
        loginPrincipal.setDisplayName(authTeacher.getRealName());
        return loginPrincipal;
    }

    /**
     * 按学生账号回填登录态中的扩展字段（专业/班级编号）。
     */
    private void enrichStudentPrincipal(LoginPrincipal loginPrincipal) {
        if (loginPrincipal == null) {
            return;
        }
        if (!UserConstant.ROLE_TYPE_STUDENT.equalsIgnoreCase(StringUtils.trimToEmpty(loginPrincipal.getRoleType()))) {
            return;
        }
        AuthStudent authStudent = null;
        if (loginPrincipal.getUserId() != null) {
            authStudent = authStudentService.getById(loginPrincipal.getUserId());
        }
        if (authStudent == null) {
            String loginAccount = StringUtils.trimToEmpty(loginPrincipal.getLoginAccount());
            if (StringUtils.isBlank(loginAccount)) {
                return;
            }
            QueryWrapper<AuthStudent> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("student_code", loginAccount);
            queryWrapper.orderByDesc("updated_at", "id");
            queryWrapper.last("limit 1");
            authStudent = authStudentService.getOne(queryWrapper, false);
        }
        if (authStudent == null) {
            return;
        }
        if (StringUtils.isBlank(loginPrincipal.getDisplayName())) {
            loginPrincipal.setDisplayName(authStudent.getStudentName());
        }
        if (StringUtils.isBlank(loginPrincipal.getLoginAccount())) {
            loginPrincipal.setLoginAccount(authStudent.getStudentCode());
        }
        loginPrincipal.setClassCode(authStudent.getClassCode());
        loginPrincipal.setMajor(authStudent.getRemark());
        loginPrincipal.setLastLoginIp(authStudent.getLastLoginIp());
    }

    /**
     * 获取本次请求来源 IP（优先代理头）。
     */
    private String resolveClientIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String forwarded = StringUtils.trimToNull(request.getHeader("X-Forwarded-For"));
        if (forwarded != null) {
            return StringUtils.trimToNull(forwarded.split(",")[0]);
        }
        String realIp = StringUtils.trimToNull(request.getHeader("X-Real-IP"));
        if (realIp != null) {
            return realIp;
        }
        return StringUtils.trimToNull(request.getRemoteAddr());
    }
}
