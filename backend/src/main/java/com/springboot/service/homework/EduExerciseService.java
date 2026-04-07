package com.springboot.service.homework;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.homework.EduExerciseQueryRequest;
import com.springboot.model.entity.homework.EduExercise;
import com.springboot.model.vo.homework.EduExerciseVO;
import jakarta.servlet.http.HttpServletRequest;

public interface EduExerciseService extends IService<EduExercise> {

    void validEduExercise(EduExercise eduExercise, boolean add);

    QueryWrapper<EduExercise> getQueryWrapper(EduExerciseQueryRequest queryRequest);

    EduExerciseVO getEduExerciseVO(EduExercise eduExercise, HttpServletRequest request);

    Page<EduExerciseVO> getEduExerciseVOPage(Page<EduExercise> entityPage, HttpServletRequest request);
}
