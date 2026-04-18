package com.springboot.service.homework;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.springboot.model.dto.homework.EduExerciseItemQueryRequest;
import com.springboot.model.entity.homework.EduExerciseItem;
import com.springboot.model.vo.homework.EduExerciseItemVO;
import jakarta.servlet.http.HttpServletRequest;

public interface EduExerciseItemService extends IService<EduExerciseItem> {

    void validEduExerciseItem(EduExerciseItem eduExerciseItem, boolean add);

    QueryWrapper<EduExerciseItem> getQueryWrapper(EduExerciseItemQueryRequest queryRequest);

    EduExerciseItemVO getEduExerciseItemVO(EduExerciseItem eduExerciseItem, HttpServletRequest request);

    Page<EduExerciseItemVO> getEduExerciseItemVOPage(Page<EduExerciseItem> entityPage, HttpServletRequest request);

    /**
     * 删除作业题目（已有作答记录时禁止删除）
     */
    Boolean removeExerciseItemById(Long id);
}
