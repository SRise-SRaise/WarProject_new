package com.springboot.service.homework.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springboot.mapper.homework.EduExerciseItemMapper;
import com.springboot.model.dto.homework.EduExerciseItemQueryRequest;
import com.springboot.model.entity.homework.EduExerciseItem;
import com.springboot.model.vo.homework.EduExerciseItemVO;
import com.springboot.service.homework.EduExerciseItemService;
import com.springboot.service.support.ServiceMethodSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class EduExerciseItemServiceImpl extends ServiceImpl<EduExerciseItemMapper, EduExerciseItem> implements EduExerciseItemService {

    @Override
    public void validEduExerciseItem(EduExerciseItem eduExerciseItem, boolean add) {
        ServiceMethodSupport.validEntity(eduExerciseItem);
    }

    @Override
    public QueryWrapper<EduExerciseItem> getQueryWrapper(EduExerciseItemQueryRequest queryRequest) {
        return ServiceMethodSupport.buildQueryWrapper(queryRequest);
    }

    @Override
    public EduExerciseItemVO getEduExerciseItemVO(EduExerciseItem eduExerciseItem, HttpServletRequest request) {
        return EduExerciseItemVO.objToVo(eduExerciseItem);
    }

    @Override
    public Page<EduExerciseItemVO> getEduExerciseItemVOPage(Page<EduExerciseItem> entityPage, HttpServletRequest request) {
        return ServiceMethodSupport.toVOPage(entityPage, EduExerciseItemVO::objToVo);
    }
}
