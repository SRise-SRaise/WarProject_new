package com.springboot.service.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.common.SysAdminLogQueryRequest;
import com.springboot.model.entity.common.SysAdminLog;
import com.springboot.model.vo.common.SysAdminLogVO;
import jakarta.servlet.http.HttpServletRequest;

public interface SysAdminLogService extends IService<SysAdminLog> {

    void validSysAdminLog(SysAdminLog sysAdminLog, boolean add);

    QueryWrapper<SysAdminLog> getQueryWrapper(SysAdminLogQueryRequest queryRequest);

    SysAdminLogVO getSysAdminLogVO(SysAdminLog sysAdminLog, HttpServletRequest request);

    Page<SysAdminLogVO> getSysAdminLogVOPage(Page<SysAdminLog> entityPage, HttpServletRequest request);
}
