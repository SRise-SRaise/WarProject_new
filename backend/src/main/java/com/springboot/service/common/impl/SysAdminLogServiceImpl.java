package com.springboot.service.common.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.common.SysAdminLogMapper;
import com.springboot.model.dto.common.SysAdminLogQueryRequest;
import com.springboot.model.entity.common.SysAdminLog;
import com.springboot.model.vo.common.SysAdminLogVO;
import com.springboot.service.common.SysAdminLogService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class SysAdminLogServiceImpl extends ServiceImpl<SysAdminLogMapper, SysAdminLog> implements SysAdminLogService {

    @Override
    public void validSysAdminLog(SysAdminLog sysAdminLog, boolean add) {
        ServiceMethodSupport.validEntity(sysAdminLog);
    }

    @Override
    public QueryWrapper<SysAdminLog> getQueryWrapper(SysAdminLogQueryRequest queryRequest) {
        return ServiceMethodSupport.buildQueryWrapper(queryRequest);
    }

    @Override
    public SysAdminLogVO getSysAdminLogVO(SysAdminLog sysAdminLog, HttpServletRequest request) {
        return SysAdminLogVO.objToVo(sysAdminLog);
    }

    @Override
    public Page<SysAdminLogVO> getSysAdminLogVOPage(Page<SysAdminLog> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, SysAdminLogVO::objToVo);
    }
}
