package com.springboot.service.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.user.AuthClassQueryRequest;
import com.springboot.model.entity.user.AuthClass;
import com.springboot.model.vo.user.AuthClassVO;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthClassService extends IService<AuthClass> {

    void validAuthClass(AuthClass authClass, boolean add);

    QueryWrapper<AuthClass> getQueryWrapper(AuthClassQueryRequest queryRequest);

    AuthClassVO getAuthClassVO(AuthClass authClass, HttpServletRequest request);

    Page<AuthClassVO> getAuthClassVOPage(Page<AuthClass> entityPage, HttpServletRequest request);
}
