package com.springboot.model.entity.exam;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 题目表/题库
 * @TableName edu_question_bank
 */
@TableName(value ="edu_question_bank")
@Data
public class EduQuestionBank implements Serializable {
    /**
     * 题目主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 题目题干
     */
    @TableField(value = "question_content")
    private String questionContent;

    /**
     * 题目类型
     */
    @TableField(value = "question_type")
    private Integer questionType;

    /**
     * 选项内容（主要用于单选/多选/判断题）
     */
    @TableField(value = "options_text")
    private String optionsText;

    /**
     * 标准答案
     */
    @TableField(value = "standard_answer")
    private String standardAnswer;

    /**
     * 题目解析说明
     */
    @TableField(value = "analysis")
    private String analysis;

    /**
     * 难度系数：1-5
     */
    @TableField(value = "difficulty")
    private Integer difficulty;

    /**
     * 创建该题目的教师ID
     */
    @TableField(value = "creator_teacher_id")
    private Long creatorTeacherId;

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
        EduQuestionBank other = (EduQuestionBank) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getQuestionContent() == null ? other.getQuestionContent() == null : this.getQuestionContent().equals(other.getQuestionContent()))
            && (this.getQuestionType() == null ? other.getQuestionType() == null : this.getQuestionType().equals(other.getQuestionType()))
            && (this.getOptionsText() == null ? other.getOptionsText() == null : this.getOptionsText().equals(other.getOptionsText()))
            && (this.getStandardAnswer() == null ? other.getStandardAnswer() == null : this.getStandardAnswer().equals(other.getStandardAnswer()))
            && (this.getAnalysis() == null ? other.getAnalysis() == null : this.getAnalysis().equals(other.getAnalysis()))
            && (this.getDifficulty() == null ? other.getDifficulty() == null : this.getDifficulty().equals(other.getDifficulty()))
            && (this.getCreatorTeacherId() == null ? other.getCreatorTeacherId() == null : this.getCreatorTeacherId().equals(other.getCreatorTeacherId()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getQuestionContent() == null) ? 0 : getQuestionContent().hashCode());
        result = prime * result + ((getQuestionType() == null) ? 0 : getQuestionType().hashCode());
        result = prime * result + ((getOptionsText() == null) ? 0 : getOptionsText().hashCode());
        result = prime * result + ((getStandardAnswer() == null) ? 0 : getStandardAnswer().hashCode());
        result = prime * result + ((getAnalysis() == null) ? 0 : getAnalysis().hashCode());
        result = prime * result + ((getDifficulty() == null) ? 0 : getDifficulty().hashCode());
        result = prime * result + ((getCreatorTeacherId() == null) ? 0 : getCreatorTeacherId().hashCode());
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
        sb.append(", questionContent=").append(questionContent);
        sb.append(", questionType=").append(questionType);
        sb.append(", optionsText=").append(optionsText);
        sb.append(", standardAnswer=").append(standardAnswer);
        sb.append(", analysis=").append(analysis);
        sb.append(", difficulty=").append(difficulty);
        sb.append(", creatorTeacherId=").append(creatorTeacherId);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
