package com.springboot.model.entity.experiment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 实验子项目评分项表
 * @TableName edu_experiment_item
 */
@TableName(value ="edu_experiment_item")
@Data
public class EduExperimentItem implements Serializable {
    /**
     * 子项目主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 子项目序号
     */
    @TableField(value = "sort_order")
    private Integer sortOrder;

    /**
     * 子项目名称
     */
    @TableField(value = "item_name")
    private String itemName;

    /**
     * 子项目类型
     */
    @TableField(value = "question_type")
    private Integer questionType;

    /**
     * 题目要求
     */
    @TableField(value = "question_content")
    private String questionContent;

    /**
     * 所属实验ID
     */
    @TableField(value = "experiment_id")
    private Long experimentId;

    /**
     * 标准答案
     */
    @TableField(value = "standard_answer")
    private String standardAnswer;

    /**
     * 该项满分
     */
    @TableField(value = "max_score")
    private Integer maxScore;

    /**
     * 状态
     */
    @TableField(value = "item_status")
    private Integer itemStatus;

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
        EduExperimentItem other = (EduExperimentItem) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSortOrder() == null ? other.getSortOrder() == null : this.getSortOrder().equals(other.getSortOrder()))
            && (this.getItemName() == null ? other.getItemName() == null : this.getItemName().equals(other.getItemName()))
            && (this.getQuestionType() == null ? other.getQuestionType() == null : this.getQuestionType().equals(other.getQuestionType()))
            && (this.getQuestionContent() == null ? other.getQuestionContent() == null : this.getQuestionContent().equals(other.getQuestionContent()))
            && (this.getExperimentId() == null ? other.getExperimentId() == null : this.getExperimentId().equals(other.getExperimentId()))
            && (this.getStandardAnswer() == null ? other.getStandardAnswer() == null : this.getStandardAnswer().equals(other.getStandardAnswer()))
            && (this.getMaxScore() == null ? other.getMaxScore() == null : this.getMaxScore().equals(other.getMaxScore()))
            && (this.getItemStatus() == null ? other.getItemStatus() == null : this.getItemStatus().equals(other.getItemStatus()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSortOrder() == null) ? 0 : getSortOrder().hashCode());
        result = prime * result + ((getItemName() == null) ? 0 : getItemName().hashCode());
        result = prime * result + ((getQuestionType() == null) ? 0 : getQuestionType().hashCode());
        result = prime * result + ((getQuestionContent() == null) ? 0 : getQuestionContent().hashCode());
        result = prime * result + ((getExperimentId() == null) ? 0 : getExperimentId().hashCode());
        result = prime * result + ((getStandardAnswer() == null) ? 0 : getStandardAnswer().hashCode());
        result = prime * result + ((getMaxScore() == null) ? 0 : getMaxScore().hashCode());
        result = prime * result + ((getItemStatus() == null) ? 0 : getItemStatus().hashCode());
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
        sb.append(", sortOrder=").append(sortOrder);
        sb.append(", itemName=").append(itemName);
        sb.append(", questionType=").append(questionType);
        sb.append(", questionContent=").append(questionContent);
        sb.append(", experimentId=").append(experimentId);
        sb.append(", standardAnswer=").append(standardAnswer);
        sb.append(", maxScore=").append(maxScore);
        sb.append(", itemStatus=").append(itemStatus);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}


