package com.springboot.model.entity.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 班级信息表
 * @TableName auth_class
 */
@TableName(value ="auth_class")
@Data
public class AuthClass implements Serializable {
    /**
     * 班级编号
     */
    @TableId(value = "class_code")
    private String classCode;

    /**
     * 班主任姓名/备注
     */
    @TableField(value = "headmaster_name")
    private String headmasterName;

    /**
     * 班级状态：0正常, 1归档
     */
    @TableField(value = "class_status")
    private Integer classStatus;

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
        AuthClass other = (AuthClass) that;
        return (this.getClassCode() == null ? other.getClassCode() == null : this.getClassCode().equals(other.getClassCode()))
            && (this.getHeadmasterName() == null ? other.getHeadmasterName() == null : this.getHeadmasterName().equals(other.getHeadmasterName()))
            && (this.getClassStatus() == null ? other.getClassStatus() == null : this.getClassStatus().equals(other.getClassStatus()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getClassCode() == null) ? 0 : getClassCode().hashCode());
        result = prime * result + ((getHeadmasterName() == null) ? 0 : getHeadmasterName().hashCode());
        result = prime * result + ((getClassStatus() == null) ? 0 : getClassStatus().hashCode());
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
        sb.append(", classCode=").append(classCode);
        sb.append(", headmasterName=").append(headmasterName);
        sb.append(", classStatus=").append(classStatus);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}


