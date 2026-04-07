package com.springboot.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.user.SysStudentLogMapper;
import com.springboot.model.dto.user.SysStudentLogQueryRequest;
import com.springboot.model.entity.user.SysStudentLog;
import com.springboot.model.vo.user.SysStudentLogVO;
import com.springboot.service.user.SysStudentLogService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class SysStudentLogServiceImpl extends ServiceImpl<SysStudentLogMapper, SysStudentLog> implements SysStudentLogService {

    @Override
    public void validSysStudentLog(SysStudentLog sysStudentLog, boolean add) {
        ServiceMethodSupport.validEntity(sysStudentLog);
    }

    @Override
    public QueryWrapper<SysStudentLog> getQueryWrapper(SysStudentLogQueryRequest queryRequest) {
        return ServiceMethodSupport.buildQueryWrapper(queryRequest);
    }

    @Override
    public SysStudentLogVO getSysStudentLogVO(SysStudentLog sysStudentLog, HttpServletRequest request) {
        return SysStudentLogVO.objToVo(sysStudentLog);
    }

    @Override
    public Page<SysStudentLogVO> getSysStudentLogVOPage(Page<SysStudentLog> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, SysStudentLogVO::objToVo);
    }
}
