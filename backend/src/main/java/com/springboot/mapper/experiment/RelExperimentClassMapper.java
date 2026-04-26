package com.springboot.mapper.experiment;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.springboot.model.entity.experiment.RelExperimentClass;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 实验-班级关联 Mapper
 */
@Mapper
public interface RelExperimentClassMapper extends BaseMapper<RelExperimentClass> {

    /**
     * 根据实验ID查询绑定的班级编号列表
     */
    @Select("SELECT class_code FROM rel_experiment_class WHERE experiment_id = #{experimentId}")
    List<String> selectClassCodesByExperimentId(@Param("experimentId") Long experimentId);

    /**
     * 根据班级编号查询关联的实验ID列表
     */
    @Select("SELECT experiment_id FROM rel_experiment_class WHERE class_code = #{classCode}")
    List<Long> selectExperimentIdsByClassCode(@Param("classCode") String classCode);

    /**
     * 删除某实验的所有班级关联
     */
    @Delete("DELETE FROM rel_experiment_class WHERE experiment_id = #{experimentId}")
    void deleteByExperimentId(@Param("experimentId") Long experimentId);

    /**
     * 批量插入实验-班级关联
     */
    @Insert({
        "<script>",
        "INSERT INTO rel_experiment_class (experiment_id, class_code, created_at) VALUES ",
        "<foreach collection='list' item='item' separator=','>",
        "(#{item.experimentId}, #{item.classCode}, NOW())",
        "</foreach>",
        "</script>"
    })
    void batchInsert(@Param("list") List<RelExperimentClass> records);
}
