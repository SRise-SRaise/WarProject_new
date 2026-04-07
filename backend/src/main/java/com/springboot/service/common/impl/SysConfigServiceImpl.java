package com.springboot.service.common.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.common.SysConfigMapper;
import com.springboot.model.dto.common.SysConfigQueryRequest;
import com.springboot.model.entity.common.SysConfig;
import com.springboot.model.vo.common.SysConfigVO;
import com.springboot.service.common.SysConfigService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Override
    public void validSysConfig(SysConfig sysConfig, boolean add) {
        ServiceMethodSupport.validEntity(sysConfig);
    }

    @Override
    public QueryWrapper<SysConfig> getQueryWrapper(SysConfigQueryRequest queryRequest) {
        return ServiceMethodSupport.buildQueryWrapper(queryRequest);
    }

    @Override
    public SysConfigVO getSysConfigVO(SysConfig sysConfig, HttpServletRequest request) {
        return SysConfigVO.objToVo(sysConfig);
    }

    @Override
    public Page<SysConfigVO> getSysConfigVOPage(Page<SysConfig> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, SysConfigVO::objToVo);
    }
}
