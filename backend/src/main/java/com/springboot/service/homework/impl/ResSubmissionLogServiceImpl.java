package com.springboot.service.homework.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.homework.ResSubmissionLogMapper;
import com.springboot.model.dto.homework.ResSubmissionLogQueryRequest;
import com.springboot.model.entity.homework.ResSubmissionLog;
import com.springboot.model.vo.homework.ResSubmissionLogVO;
import com.springboot.service.homework.ResSubmissionLogService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class ResSubmissionLogServiceImpl extends ServiceImpl<ResSubmissionLogMapper, ResSubmissionLog> implements ResSubmissionLogService {

    @Override
    public void validResSubmissionLog(ResSubmissionLog resSubmissionLog, boolean add) {
        ServiceMethodSupport.validEntity(resSubmissionLog);
    }

    @Override
    public QueryWrapper<ResSubmissionLog> getQueryWrapper(ResSubmissionLogQueryRequest queryRequest) {
        return ServiceMethodSupport.buildQueryWrapper(queryRequest);
    }

    @Override
    public ResSubmissionLogVO getResSubmissionLogVO(ResSubmissionLog resSubmissionLog, HttpServletRequest request) {
        return ResSubmissionLogVO.objToVo(resSubmissionLog);
    }

    @Override
    public Page<ResSubmissionLogVO> getResSubmissionLogVOPage(Page<ResSubmissionLog> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, ResSubmissionLogVO::objToVo);
    }
}
