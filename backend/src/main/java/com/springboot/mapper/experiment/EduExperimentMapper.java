package com.springboot.mapper.experiment;

import com.springboot.model.entity.experiment.EduExperiment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
* @author l1577
* @description 针对表【t_experiment(实验项目表)】的数据库操作Mapper
* @createDate 2026-04-07 11:33:51
* @Entity com.springboot.model.entity.EduExperiment
*/
@Mapper
public interface EduExperimentMapper extends BaseMapper<EduExperiment> {

    /**
     * 单独更新指导书路径和文件类型（绕过 @TableField(exist=false) 限制）
     */
    @Update("UPDATE t_experiment SET instruction_url = #{url}, instruction_type = #{fileType} WHERE experiment_id = #{experimentId}")
    int updateInstructionUrl(@Param("experimentId") Long experimentId,
                             @Param("url") String url,
                             @Param("fileType") String fileType);
}







