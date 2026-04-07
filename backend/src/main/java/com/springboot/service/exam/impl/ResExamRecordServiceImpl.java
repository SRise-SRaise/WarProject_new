package com.springboot.service.exam.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.exam.ResExamRecordMapper;
import com.springboot.model.dto.exam.ResExamRecordQueryRequest;
import com.springboot.model.entity.exam.ResExamRecord;
import com.springboot.model.vo.exam.ResExamRecordVO;
import com.springboot.service.exam.ResExamRecordService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class ResExamRecordServiceImpl extends ServiceImpl<ResExamRecordMapper, ResExamRecord> implements ResExamRecordService {

    @Override
    public void validResExamRecord(ResExamRecord resExamRecord, boolean add) {
        ServiceMethodSupport.validEntity(resExamRecord);
    }

    @Override
    public QueryWrapper<ResExamRecord> getQueryWrapper(ResExamRecordQueryRequest queryRequest) {
        return ServiceMethodSupport.buildQueryWrapper(queryRequest);
    }

    @Override
    public ResExamRecordVO getResExamRecordVO(ResExamRecord resExamRecord, HttpServletRequest request) {
        return ResExamRecordVO.objToVo(resExamRecord);
    }

    @Override
    public Page<ResExamRecordVO> getResExamRecordVOPage(Page<ResExamRecord> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, ResExamRecordVO::objToVo);
    }
}
