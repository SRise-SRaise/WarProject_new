package com.springboot.service.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.common.SysConfigQueryRequest;
import com.springboot.model.entity.common.SysConfig;
import com.springboot.model.vo.common.SysConfigVO;
import jakarta.servlet.http.HttpServletRequest;

public interface SysConfigService extends IService<SysConfig> {

    void validSysConfig(SysConfig sysConfig, boolean add);

    QueryWrapper<SysConfig> getQueryWrapper(SysConfigQueryRequest queryRequest);

    SysConfigVO getSysConfigVO(SysConfig sysConfig, HttpServletRequest request);

    Page<SysConfigVO> getSysConfigVOPage(Page<SysConfig> entityPage, HttpServletRequest request);
}
