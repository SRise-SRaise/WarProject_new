package com.springboot.model.entity.experiment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 实验-班级关联表
 * 用于控制实验按班级发布，一个实验可关联多个班级
 * @TableName rel_experiment_class
 */
@TableName(value = "rel_experiment_class")
@Data
public class RelExperimentClass implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 实验ID
     */
    @TableField(value = "experiment_id")
    private Long experimentId;

    /**
     * 班级编号
     */
    @TableField(value = "class_code")
    private String classCode;

    /**
     * 创建时间
     */
    @TableField(value = "created_at")
    private Date createdAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
