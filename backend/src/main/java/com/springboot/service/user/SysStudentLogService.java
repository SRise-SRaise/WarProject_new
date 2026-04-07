package com.springboot.service.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.user.SysStudentLogQueryRequest;
import com.springboot.model.entity.user.SysStudentLog;
import com.springboot.model.vo.user.SysStudentLogVO;
import jakarta.servlet.http.HttpServletRequest;

public interface SysStudentLogService extends IService<SysStudentLog> {

    void validSysStudentLog(SysStudentLog sysStudentLog, boolean add);

    QueryWrapper<SysStudentLog> getQueryWrapper(SysStudentLogQueryRequest queryRequest);

    SysStudentLogVO getSysStudentLogVO(SysStudentLog sysStudentLog, HttpServletRequest request);

    Page<SysStudentLogVO> getSysStudentLogVOPage(Page<SysStudentLog> entityPage, HttpServletRequest request);
}
