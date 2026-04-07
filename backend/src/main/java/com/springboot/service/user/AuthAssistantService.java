package com.springboot.service.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.user.AuthAssistantQueryRequest;
import com.springboot.model.entity.user.AuthAssistant;
import com.springboot.model.vo.user.AuthAssistantVO;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthAssistantService extends IService<AuthAssistant> {

    void validAuthAssistant(AuthAssistant authAssistant, boolean add);

    QueryWrapper<AuthAssistant> getQueryWrapper(AuthAssistantQueryRequest queryRequest);

    AuthAssistantVO getAuthAssistantVO(AuthAssistant authAssistant, HttpServletRequest request);

    Page<AuthAssistantVO> getAuthAssistantVOPage(Page<AuthAssistant> entityPage, HttpServletRequest request);
}
