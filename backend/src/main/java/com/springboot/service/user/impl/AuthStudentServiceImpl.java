package com.springboot.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.common.ErrorCode;
import com.springboot.constant.CommonConstant;
import com.springboot.exception.BusinessException;
import com.springboot.mapper.user.AuthStudentMapper;
import com.springboot.model.dto.user.AuthStudentQueryRequest;
import com.springboot.model.entity.user.AuthStudent;
import com.springboot.model.vo.user.AuthStudentVO;
import com.springboot.service.user.AuthStudentService;
import com.springboot.service.support.ServiceMethodSupport;
import com.springboot.utils.SqlUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class AuthStudentServiceImpl extends ServiceImpl<AuthStudentMapper, AuthStudent> implements AuthStudentService {

    @Override
    public void validAuthStudent(AuthStudent authStudent, boolean add) {
        ServiceMethodSupport.validEntity(authStudent);
    }

    @Override
    public QueryWrapper<AuthStudent> getQueryWrapper(AuthStudentQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = queryRequest.getId();
        String studentCode = StringUtils.trimToNull(queryRequest.getStudentCode());
        String studentName = StringUtils.trimToNull(queryRequest.getStudentName());
        String classCode = StringUtils.trimToNull(queryRequest.getClassCode());
        Integer accountStatus = queryRequest.getAccountStatus();
        Integer loginFailCount = queryRequest.getLoginFailCount();
        String lastLoginIp = StringUtils.trimToNull(queryRequest.getLastLoginIp());
        Date createdAt = queryRequest.getCreatedAt();
        Date updatedAt = queryRequest.getUpdatedAt();
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();

        // 表字段名与库一致（mybatis-plus map-underscore-to-camel-case: false）
        QueryWrapper<AuthStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null && id > 0, "id", id);
        queryWrapper.eq(studentCode != null, "student_code", studentCode);
        queryWrapper.like(studentName != null, "student_name", studentName);
        queryWrapper.eq(classCode != null, "class_code", classCode);
        queryWrapper.eq(accountStatus != null, "account_status", accountStatus);
        queryWrapper.eq(loginFailCount != null, "login_fail_count", loginFailCount);
        queryWrapper.eq(lastLoginIp != null, "last_login_ip", lastLoginIp);
        queryWrapper.eq(createdAt != null, "created_at", createdAt);
        queryWrapper.eq(updatedAt != null, "updated_at", updatedAt);
        boolean asc = CommonConstant.SORT_ORDER_ASC.equals(StringUtils.trimToEmpty(sortOrder));
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), asc, sortField);
        return queryWrapper;
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
