package com.springboot.model.entity.homework;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 课内练习表
 * @TableName edu_exercise
 */
@TableName(value ="edu_exercise")
@Data
public class EduExercise implements Serializable {
    /**
     * 练习主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 练习序号
     */
    @TableField(value = "sort_order")
    private Integer sortOrder;

    /**
     * 练习名称
     */
    @TableField(value = "task_name")
    private String taskName;

    /**
     * 关联实验编号
     */
    @TableField(value = "relate_exp_id")
    private Long relateExpId;

    /**
     * 练习类型
     */
    @TableField(value = "interact_mode")
    private Integer interactMode;

    /**
     * 练习描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 开放时间
     */
    @TableField(value = "start_time")
    private Date startTime;

    /**
     * 截止时间
     */
    @TableField(value = "end_time")
    private Date endTime;

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
        EduExercise other = (EduExercise) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSortOrder() == null ? other.getSortOrder() == null : this.getSortOrder().equals(other.getSortOrder()))
            && (this.getTaskName() == null ? other.getTaskName() == null : this.getTaskName().equals(other.getTaskName()))
            && (this.getRelateExpId() == null ? other.getRelateExpId() == null : this.getRelateExpId().equals(other.getRelateExpId()))
            && (this.getInteractMode() == null ? other.getInteractMode() == null : this.getInteractMode().equals(other.getInteractMode()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getStartTime() == null ? other.getStartTime() == null : this.getStartTime().equals(other.getStartTime()))
            && (this.getEndTime() == null ? other.getEndTime() == null : this.getEndTime().equals(other.getEndTime()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSortOrder() == null) ? 0 : getSortOrder().hashCode());
        result = prime * result + ((getTaskName() == null) ? 0 : getTaskName().hashCode());
        result = prime * result + ((getRelateExpId() == null) ? 0 : getRelateExpId().hashCode());
        result = prime * result + ((getInteractMode() == null) ? 0 : getInteractMode().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getStartTime() == null) ? 0 : getStartTime().hashCode());
        result = prime * result + ((getEndTime() == null) ? 0 : getEndTime().hashCode());
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
        sb.append(", taskName=").append(taskName);
        sb.append(", relateExpId=").append(relateExpId);
        sb.append(", interactMode=").append(interactMode);
        sb.append(", description=").append(description);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}


