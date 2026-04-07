package com.springboot.service.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.user.AuthTeacherQueryRequest;
import com.springboot.model.entity.user.AuthTeacher;
import com.springboot.model.vo.user.AuthTeacherVO;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthTeacherService extends IService<AuthTeacher> {

    void validAuthTeacher(AuthTeacher authTeacher, boolean add);

    QueryWrapper<AuthTeacher> getQueryWrapper(AuthTeacherQueryRequest queryRequest);

    AuthTeacherVO getAuthTeacherVO(AuthTeacher authTeacher, HttpServletRequest request);

    Page<AuthTeacherVO> getAuthTeacherVOPage(Page<AuthTeacher> entityPage, HttpServletRequest request);
}
