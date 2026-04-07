package com.springboot.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.user.AuthStudentMapper;
import com.springboot.model.dto.user.AuthStudentQueryRequest;
import com.springboot.model.entity.user.AuthStudent;
import com.springboot.model.vo.user.AuthStudentVO;
import com.springboot.service.user.AuthStudentService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthStudentServiceImpl extends ServiceImpl<AuthStudentMapper, AuthStudent> implements AuthStudentService {

    @Override
    public void validAuthStudent(AuthStudent authStudent, boolean add) {
        ServiceMethodSupport.validEntity(authStudent);
    }

    @Override
    public QueryWrapper<AuthStudent> getQueryWrapper(AuthStudentQueryRequest queryRequest) {
        return ServiceMethodSupport.buildQueryWrapper(queryRequest);
    }

    @Override
    public AuthStudentVO getAuthStudentVO(AuthStudent authStudent, HttpServletRequest request) {
        return AuthStudentVO.objToVo(authStudent);
    }

    @Override
    public Page<AuthStudentVO> getAuthStudentVOPage(Page<AuthStudent> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, AuthStudentVO::objToVo);
    }
}
