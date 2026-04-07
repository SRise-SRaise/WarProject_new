package com.springboot.service.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.user.AuthStudentQueryRequest;
import com.springboot.model.entity.user.AuthStudent;
import com.springboot.model.vo.user.AuthStudentVO;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthStudentService extends IService<AuthStudent> {

    void validAuthStudent(AuthStudent authStudent, boolean add);

    QueryWrapper<AuthStudent> getQueryWrapper(AuthStudentQueryRequest queryRequest);

    AuthStudentVO getAuthStudentVO(AuthStudent authStudent, HttpServletRequest request);

    Page<AuthStudentVO> getAuthStudentVOPage(Page<AuthStudent> entityPage, HttpServletRequest request);
}
