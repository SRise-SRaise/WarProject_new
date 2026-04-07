package com.springboot.service.support;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.common.ErrorCode;
import com.springboot.common.PageRequest;
import com.springboot.constant.CommonConstant;
import com.springboot.exception.BusinessException;
import com.springboot.utils.SqlUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class ServiceMethodSupport {

    private ServiceMethodSupport() {
    }

    public static <T> void validEntity(T entity) {
        if (entity == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
    }

    public static <T> QueryWrapper<T> buildQueryWrapper(PageRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String sortField = request.getSortField();
        String sortOrder = request.getSortOrder();
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                CommonConstant.SORT_ORDER_ASC.equals(sortOrder), sortField);
        return queryWrapper;
    }

    public static <E, V> Page<V> toVOPage(Page<E> page, Function<E, V> mapper) {
        if (page == null) {
            return new Page<>();
        }
        Page<V> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<E> records = page.getRecords();
        if (CollUtil.isEmpty(records)) {
            voPage.setRecords(new ArrayList<>());
            return voPage;
        }
        voPage.setRecords(records.stream().map(mapper).toList());
        return voPage;
    }
}
