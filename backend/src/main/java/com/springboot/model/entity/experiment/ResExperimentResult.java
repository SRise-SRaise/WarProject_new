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
 * @TableName t_student_item
 */
@TableName(value ="t_student_item")
@Data
public class ResExperimentResult implements Serializable {
    /**
     * 提交记录主键ID
     */
    @TableId(value = "student_item_id", type = IdType.AUTO)
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
    @TableField(value = "content")
    private String subContent;

    /**
     * 评分
     */
    @TableField(value = "score")
    private Integer score;

    /**
     * 提交时间
     */
    @TableField(value = "fill_time")
    private Date fillTime;

    /**
     * 评分标记
     */
    @TableField(value = "score_flag")
    private Integer gradingStatus;

    /**
     * 
     */
    @TableField(exist = false)
    private Date createdAt;

    /**
     * 
     */
    @TableField(exist = false)
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
            && (this.getFillTime() == null ? other.getFillTime() == null : this.getFillTime().equals(other.getFillTime()))
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
        result = prime * result + ((getFillTime() == null) ? 0 : getFillTime().hashCode());
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
        sb.append(", fillTime=").append(fillTime);
        sb.append(", gradingStatus=").append(gradingStatus);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}


