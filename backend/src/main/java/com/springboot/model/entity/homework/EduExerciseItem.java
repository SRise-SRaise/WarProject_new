package com.springboot.model.entity.homework;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 练习题目表
 * @TableName edu_exercise_item
 */
@TableName(value ="edu_exercise_item")
@Data
public class EduExerciseItem implements Serializable {
    /**
     * 练习题目主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属练习ID
     */
    @TableField(value = "exercise_id")
    private Long exerciseId;

    /**
     * 题目题干
     */
    @TableField(value = "question")
    private String question;

    /**
     * 选项
     */
    @TableField(value = "options_text")
    private String optionsText;

    /**
     * 标准答案
     */
    @TableField(value = "standard_answer")
    private String standardAnswer;

    /**
     * 题目类型
     */
    @TableField(value = "question_type")
    private Integer questionType;

    /**
     * 该题满分分值
     */
    @TableField(value = "max_score")
    private Integer maxScore;

    /**
     * 来源题库ID
     */
    @TableField(value = "question_bank_id")
    private Long questionBankId;

    /**
     * 
     */
    @TableField(value = "created_at")
    private Date createdAt;

    /**
     * 
     */
    @TableField(value = "updated_at")
    private Date updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        EduExerciseItem other = (EduExerciseItem) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getExerciseId() == null ? other.getExerciseId() == null : this.getExerciseId().equals(other.getExerciseId()))
            && (this.getQuestion() == null ? other.getQuestion() == null : this.getQuestion().equals(other.getQuestion()))
            && (this.getOptionsText() == null ? other.getOptionsText() == null : this.getOptionsText().equals(other.getOptionsText()))
            && (this.getStandardAnswer() == null ? other.getStandardAnswer() == null : this.getStandardAnswer().equals(other.getStandardAnswer()))
            && (this.getQuestionType() == null ? other.getQuestionType() == null : this.getQuestionType().equals(other.getQuestionType()))
            && (this.getMaxScore() == null ? other.getMaxScore() == null : this.getMaxScore().equals(other.getMaxScore()))
            && (this.getQuestionBankId() == null ? other.getQuestionBankId() == null : this.getQuestionBankId().equals(other.getQuestionBankId()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getExerciseId() == null) ? 0 : getExerciseId().hashCode());
        result = prime * result + ((getQuestion() == null) ? 0 : getQuestion().hashCode());
        result = prime * result + ((getOptionsText() == null) ? 0 : getOptionsText().hashCode());
        result = prime * result + ((getStandardAnswer() == null) ? 0 : getStandardAnswer().hashCode());
        result = prime * result + ((getQuestionType() == null) ? 0 : getQuestionType().hashCode());
        result = prime * result + ((getMaxScore() == null) ? 0 : getMaxScore().hashCode());
        result = prime * result + ((getQuestionBankId() == null) ? 0 : getQuestionBankId().hashCode());
        result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
        result = prime * result + ((getUpdatedAt() == null) ? 0 : getUpdatedAt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", exerciseId=").append(exerciseId);
        sb.append(", question=").append(question);
        sb.append(", optionsText=").append(optionsText);
        sb.append(", standardAnswer=").append(standardAnswer);
        sb.append(", questionType=").append(questionType);
        sb.append(", maxScore=").append(maxScore);
        sb.append(", questionBankId=").append(questionBankId);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
