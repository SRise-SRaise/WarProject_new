package com.springboot.model.entity.exam;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 试卷题目关联表
 * @TableName rel_paper_question
 */
@TableName(value ="rel_paper_question")
@Data
public class RelPaperQuestion implements Serializable {
    /**
     * 关联表主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 试卷ID
     */
    @TableField(value = "paper_id")
    private Long paperId;

    /**
     * 题目ID
     */
    @TableField(value = "question_id")
    private Long questionId;

    /**
     * 该题分值
     */
    @TableField(value = "score")
    private Integer score;

    /**
     * 题目在试卷中的顺序号
     */
    @TableField(value = "question_order")
    private Integer questionOrder;

    /**
     * 题目分组名称，如单选题/简答题
     */
    @TableField(value = "section_name")
    private String sectionName;

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
        RelPaperQuestion other = (RelPaperQuestion) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getPaperId() == null ? other.getPaperId() == null : this.getPaperId().equals(other.getPaperId()))
            && (this.getQuestionId() == null ? other.getQuestionId() == null : this.getQuestionId().equals(other.getQuestionId()))
            && (this.getScore() == null ? other.getScore() == null : this.getScore().equals(other.getScore()))
            && (this.getQuestionOrder() == null ? other.getQuestionOrder() == null : this.getQuestionOrder().equals(other.getQuestionOrder()))
            && (this.getSectionName() == null ? other.getSectionName() == null : this.getSectionName().equals(other.getSectionName()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getPaperId() == null) ? 0 : getPaperId().hashCode());
        result = prime * result + ((getQuestionId() == null) ? 0 : getQuestionId().hashCode());
        result = prime * result + ((getScore() == null) ? 0 : getScore().hashCode());
        result = prime * result + ((getQuestionOrder() == null) ? 0 : getQuestionOrder().hashCode());
        result = prime * result + ((getSectionName() == null) ? 0 : getSectionName().hashCode());
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
        sb.append(", paperId=").append(paperId);
        sb.append(", questionId=").append(questionId);
        sb.append(", score=").append(score);
        sb.append(", questionOrder=").append(questionOrder);
        sb.append(", sectionName=").append(sectionName);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
