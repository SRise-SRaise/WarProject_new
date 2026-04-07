package com.springboot.service.homework.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.homework.EduExerciseMapper;
import com.springboot.model.dto.homework.EduExerciseQueryRequest;
import com.springboot.model.entity.homework.EduExercise;
import com.springboot.model.vo.homework.EduExerciseVO;
import com.springboot.service.homework.EduExerciseService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class EduExerciseServiceImpl extends ServiceImpl<EduExerciseMapper, EduExercise> implements EduExerciseService {

    @Override
    public void validEduExercise(EduExercise eduExercise, boolean add) {
        ServiceMethodSupport.validEntity(eduExercise);
    }

    @Override
    public QueryWrapper<EduExercise> getQueryWrapper(EduExerciseQueryRequest queryRequest) {
        return ServiceMethodSupport.buildQueryWrapper(queryRequest);
    }

    @Override
    public EduExerciseVO getEduExerciseVO(EduExercise eduExercise, HttpServletRequest request) {
        return EduExerciseVO.objToVo(eduExercise);
    }

    @Override
    public Page<EduExerciseVO> getEduExerciseVOPage(Page<EduExercise> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, EduExerciseVO::objToVo);
    }
}
