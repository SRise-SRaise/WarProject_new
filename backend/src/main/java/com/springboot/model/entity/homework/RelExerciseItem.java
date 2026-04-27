package com.springboot.model.entity.homework;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 作业题目关联表
 */
@TableName(value = "rel_exercise_item")
@Data
public class RelExerciseItem implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "exercise_id")
    private Long exerciseId;

    @TableField(value = "item_id")
    private Long itemId;

    @TableField(value = "item_order")
    private Integer itemOrder;

    @TableField(value = "item_score")
    private Integer itemScore;

    @TableField(value = "created_at")
    private Date createdAt;

    @TableField(value = "updated_at")
    private Date updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
