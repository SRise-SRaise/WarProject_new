package com.springboot.model.entity.experiment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 学生实验提交记录表
 * @TableName res_experiment_result
 */
@TableName(value ="res_experiment_result")
@Data
public class ResExperimentResult implements Serializable {
    /**
     * 提交记录主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学生ID
     */
    @TableField(value = "student_id")
    private Long studentId;

    /**
     * 实验子项目ID
     */
    @TableField(value = "item_id")
    private Long itemId;

    /**
     * 提交的实验内容
     */
    @TableField(value = "sub_content")
    private String subContent;

    /**
     * 评分
     */
    @TableField(value = "score")
    private Integer score;

    /**
     * 提交时间
     */
    @TableField(value = "submitted_at")
    private Date submittedAt;

    /**
     * 评分标记
     */
    @TableField(value = "grading_status")
    private Integer gradingStatus;

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
        ResExperimentResult other = (ResExperimentResult) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getStudentId() == null ? other.getStudentId() == null : this.getStudentId().equals(other.getStudentId()))
            && (this.getItemId() == null ? other.getItemId() == null : this.getItemId().equals(other.getItemId()))
            && (this.getSubContent() == null ? other.getSubContent() == null : this.getSubContent().equals(other.getSubContent()))
            && (this.getScore() == null ? other.getScore() == null : this.getScore().equals(other.getScore()))
            && (this.getSubmittedAt() == null ? other.getSubmittedAt() == null : this.getSubmittedAt().equals(other.getSubmittedAt()))
            && (this.getGradingStatus() == null ? other.getGradingStatus() == null : this.getGradingStatus().equals(other.getGradingStatus()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getStudentId() == null) ? 0 : getStudentId().hashCode());
        result = prime * result + ((getItemId() == null) ? 0 : getItemId().hashCode());
        result = prime * result + ((getSubContent() == null) ? 0 : getSubContent().hashCode());
        result = prime * result + ((getScore() == null) ? 0 : getScore().hashCode());
        result = prime * result + ((getSubmittedAt() == null) ? 0 : getSubmittedAt().hashCode());
        result = prime * result + ((getGradingStatus() == null) ? 0 : getGradingStatus().hashCode());
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
        sb.append(", studentId=").append(studentId);
        sb.append(", itemId=").append(itemId);
        sb.append(", subContent=").append(subContent);
        sb.append(", score=").append(score);
        sb.append(", submittedAt=").append(submittedAt);
        sb.append(", gradingStatus=").append(gradingStatus);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}


