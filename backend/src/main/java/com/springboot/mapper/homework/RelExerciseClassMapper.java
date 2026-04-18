package com.springboot.mapper.homework;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.springboot.model.entity.homework.RelExerciseClass;
import org.apache.ibatis.annotations.Mapper;

/**
 * 作业班级关联Mapper
 */
@Mapper
public interface RelExerciseClassMapper extends BaseMapper<RelExerciseClass> {
}