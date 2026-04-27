package com.springboot.model.entity.homework;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 练习题目表
 */
@TableName(value = "edu_exercise_item")
@Data
public class EduExerciseItem implements Serializable {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField(value = "question")
    private String question;

    @TableField(value = "options_text")
    private String optionsText;

    @TableField(value = "standard_answer")
    private String standardAnswer;

    @TableField(value = "question_type")
    private Integer questionType;

    @TableField(value = "max_score")
    private Integer maxScore;

    @TableField(value = "question_bank_id")
    private Long questionBankId;

    @TableField(value = "analysis")
    private String analysis;

    @TableField(value = "difficulty")
    private Integer difficulty;

    @TableField(value = "created_at")
    private Date createdAt;

    @TableField(value = "updated_at")
    private Date updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
