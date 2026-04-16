package com.springboot.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.constant.CommonConstant;
import com.springboot.mapper.user.AuthClassMapper;
import com.springboot.model.dto.user.AuthClassQueryRequest;
import com.springboot.model.entity.user.AuthClass;
import com.springboot.model.vo.user.AuthClassVO;
import com.springboot.service.user.AuthClassService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.springboot.utils.SqlUtils;

@Service
public class AuthClassServiceImpl extends ServiceImpl<AuthClassMapper, AuthClass> implements AuthClassService {

    @Override
    public void validAuthClass(AuthClass authClass, boolean add) {
        ServiceMethodSupport.validEntity(authClass);
    }

    @Override
    public QueryWrapper<AuthClass> getQueryWrapper(AuthClassQueryRequest queryRequest) {
        QueryWrapper<AuthClass> queryWrapper = ServiceMethodSupport.buildQueryWrapper(queryRequest);
        if (queryRequest == null) {
            return queryWrapper;
        }

        String classCode = StringUtils.trimToNull(queryRequest.getClassCode());
        String headmasterName = StringUtils.trimToNull(queryRequest.getHeadmasterName());
        Integer classStatus = queryRequest.getClassStatus();
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();

        queryWrapper.eq(classCode != null, "class_code", classCode);
        queryWrapper.like(headmasterName != null, "headmaster_name", headmasterName);
        queryWrapper.eq(classStatus != null, "class_status", classStatus);

        // 默认按更新时间、创建时间倒序；若前端传合法排序字段则覆盖追加
        queryWrapper.orderByDesc("updated_at", "created_at");
        boolean asc = CommonConstant.SORT_ORDER_ASC.equals(StringUtils.trimToEmpty(sortOrder));
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), asc, sortField);
        return queryWrapper;
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
