package com.springboot.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.user.AuthAssistantMapper;
import com.springboot.model.dto.user.AuthAssistantQueryRequest;
import com.springboot.model.entity.user.AuthAssistant;
import com.springboot.model.vo.user.AuthAssistantVO;
import com.springboot.service.user.AuthAssistantService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthAssistantServiceImpl extends ServiceImpl<AuthAssistantMapper, AuthAssistant> implements AuthAssistantService {

    @Override
    public void validAuthAssistant(AuthAssistant authAssistant, boolean add) {
        ServiceMethodSupport.validEntity(authAssistant);
    }

    @Override
    public QueryWrapper<AuthAssistant> getQueryWrapper(AuthAssistantQueryRequest queryRequest) {
        return ServiceMethodSupport.buildQueryWrapper(queryRequest);
    }

    @Override
    public AuthAssistantVO getAuthAssistantVO(AuthAssistant authAssistant, HttpServletRequest request) {
        return AuthAssistantVO.objToVo(authAssistant);
    }

    @Override
    public Page<AuthAssistantVO> getAuthAssistantVOPage(Page<AuthAssistant> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, AuthAssistantVO::objToVo);
    }
}
