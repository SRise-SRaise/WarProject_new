package com.springboot.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.user.AuthTeacherMapper;
import com.springboot.model.dto.user.AuthTeacherQueryRequest;
import com.springboot.model.entity.user.AuthTeacher;
import com.springboot.model.vo.user.AuthTeacherVO;
import com.springboot.service.user.AuthTeacherService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthTeacherServiceImpl extends ServiceImpl<AuthTeacherMapper, AuthTeacher> implements AuthTeacherService {

    @Override
    public void validAuthTeacher(AuthTeacher authTeacher, boolean add) {
        ServiceMethodSupport.validEntity(authTeacher);
    }

    @Override
    public QueryWrapper<AuthTeacher> getQueryWrapper(AuthTeacherQueryRequest queryRequest) {
        return ServiceMethodSupport.buildQueryWrapper(queryRequest);
    }

    @Override
    public AuthTeacherVO getAuthTeacherVO(AuthTeacher authTeacher, HttpServletRequest request) {
        return AuthTeacherVO.objToVo(authTeacher);
    }

    @Override
    public Page<AuthTeacherVO> getAuthTeacherVOPage(Page<AuthTeacher> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, AuthTeacherVO::objToVo);
    }
}
