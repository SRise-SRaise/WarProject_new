package com.springboot.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.user.AuthClassMapper;
import com.springboot.model.dto.user.AuthClassQueryRequest;
import com.springboot.model.entity.user.AuthClass;
import com.springboot.model.vo.user.AuthClassVO;
import com.springboot.service.user.AuthClassService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthClassServiceImpl extends ServiceImpl<AuthClassMapper, AuthClass> implements AuthClassService {

    @Override
    public void validAuthClass(AuthClass authClass, boolean add) {
        ServiceMethodSupport.validEntity(authClass);
    }

    @Override
    public QueryWrapper<AuthClass> getQueryWrapper(AuthClassQueryRequest queryRequest) {
        return ServiceMethodSupport.buildQueryWrapper(queryRequest);
    }

    @Override
    public AuthClassVO getAuthClassVO(AuthClass authClass, HttpServletRequest request) {
        return AuthClassVO.objToVo(authClass);
    }

    @Override
    public Page<AuthClassVO> getAuthClassVOPage(Page<AuthClass> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, AuthClassVO::objToVo);
    }
}
