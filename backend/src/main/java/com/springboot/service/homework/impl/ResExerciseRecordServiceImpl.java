package com.springboot.service.homework.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.homework.ResExerciseRecordMapper;
import com.springboot.model.dto.homework.ResExerciseRecordQueryRequest;
import com.springboot.model.entity.homework.ResExerciseRecord;
import com.springboot.model.vo.homework.ResExerciseRecordVO;
import com.springboot.service.homework.ResExerciseRecordService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class ResExerciseRecordServiceImpl extends ServiceImpl<ResExerciseRecordMapper, ResExerciseRecord> implements ResExerciseRecordService {

    @Override
    public void validResExerciseRecord(ResExerciseRecord resExerciseRecord, boolean add) {
        ServiceMethodSupport.validEntity(resExerciseRecord);
    }

    @Override
    public QueryWrapper<ResExerciseRecord> getQueryWrapper(ResExerciseRecordQueryRequest queryRequest) {
        return ServiceMethodSupport.buildQueryWrapper(queryRequest);
    }

    @Override
    public ResExerciseRecordVO getResExerciseRecordVO(ResExerciseRecord resExerciseRecord, HttpServletRequest request) {
        return ResExerciseRecordVO.objToVo(resExerciseRecord);
    }

    @Override
    public Page<ResExerciseRecordVO> getResExerciseRecordVOPage(Page<ResExerciseRecord> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, ResExerciseRecordVO::objToVo);
    }
}
