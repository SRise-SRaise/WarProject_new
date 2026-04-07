package com.springboot.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.springboot.common.ErrorCode;
import com.springboot.exception.BusinessException;
import com.springboot.model.dto.user.AuthRegisterRequest;
import com.springboot.model.entity.user.AuthStudent;
import com.springboot.model.vo.user.AuthLoginVO;
import com.springboot.service.user.AuthAssistantService;
import com.springboot.service.user.AuthStudentService;
import com.springboot.service.user.AuthTeacherService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthLoginServiceImplRegisterTest {

    @Mock
    private AuthStudentService authStudentService;

    @Mock
    private AuthTeacherService authTeacherService;

    @Mock
    private AuthAssistantService authAssistantService;

    @InjectMocks
    private AuthLoginServiceImpl authLoginService;

    @Test
    void registerStudentShouldSaveAndLogin() {
        AuthRegisterRequest request = new AuthRegisterRequest();
        request.setRoleType("student");
        request.setLoginAccount("20260001");
        request.setDisplayName("张三");
        request.setClassCode("A101");
        request.setLoginPassword("12345678");
        request.setCheckPassword("12345678");

        when(authStudentService.count(any(QueryWrapper.class))).thenReturn(0L);
        when(authStudentService.save(any(AuthStudent.class))).thenReturn(true);

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        AuthLoginVO authLoginVO = authLoginService.register(request, httpServletRequest);

        Assertions.assertNotNull(authLoginVO);
        Assertions.assertNotNull(authLoginVO.getLoginPrincipal());
        Assertions.assertEquals("student", authLoginVO.getLoginPrincipal().getRoleType());
        Assertions.assertEquals("20260001", authLoginVO.getLoginPrincipal().getLoginAccount());
        ArgumentCaptor<AuthStudent> captor = ArgumentCaptor.forClass(AuthStudent.class);
        verify(authStudentService).save(captor.capture());
        Assertions.assertEquals("20260001", captor.getValue().getStudentCode());
    }

    @Test
    void registerTeacherShouldThrowForbidden() {
        AuthRegisterRequest request = new AuthRegisterRequest();
        request.setRoleType("teacher");
        request.setLoginAccount("T001");
        request.setDisplayName("李老师");
        request.setLoginPassword("12345678");
        request.setCheckPassword("12345678");

        HttpServletRequest httpServletRequest = new MockHttpServletRequest();
        BusinessException businessException = Assertions.assertThrows(BusinessException.class,
                () -> authLoginService.register(request, httpServletRequest));
        Assertions.assertEquals(ErrorCode.NO_AUTH_ERROR.getCode(), businessException.getCode());
    }
}
