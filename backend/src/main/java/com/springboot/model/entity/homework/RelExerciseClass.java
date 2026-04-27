package com.springboot.model.entity.homework;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 作业班级关联表
 * @TableName rel_exercise_class
 */
@TableName(value = "rel_exercise_class")
@Data
public class RelExerciseClass implements Serializable {

    /**
     * 关联主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 作业ID
     */
    @TableField(value = "exercise_id")
    private Long exerciseId;

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

    /**
     * 更新时间
     */
    @TableField(value = "updated_at")
    private Date updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}