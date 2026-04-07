package com.springboot.model.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 学生操作日志表
 * @TableName sys_student_log
 */
@TableName(value ="sys_student_log")
@Data
public class SysStudentLog implements Serializable {
    /**
     * 日志主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 操作账号
     */
    @TableField(value = "account")
    private String account;

    /**
     * 动作类型
     */
    @TableField(value = "action_type")
    private Integer actionType;

    /**
     * 详情
     */
    @TableField(value = "action_detail")
    private String actionDetail;

    /**
     * 操作时间
     */
    @TableField(value = "op_time")
    private Date opTime;

    /**
     * 客户端IP
     */
    @TableField(value = "client_ip")
    private String clientIp;

    /**
     * 
     */
    @TableField(value = "created_at")
    private Date createdAt;

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
        SysStudentLog other = (SysStudentLog) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getAccount() == null ? other.getAccount() == null : this.getAccount().equals(other.getAccount()))
            && (this.getActionType() == null ? other.getActionType() == null : this.getActionType().equals(other.getActionType()))
            && (this.getActionDetail() == null ? other.getActionDetail() == null : this.getActionDetail().equals(other.getActionDetail()))
            && (this.getOpTime() == null ? other.getOpTime() == null : this.getOpTime().equals(other.getOpTime()))
            && (this.getClientIp() == null ? other.getClientIp() == null : this.getClientIp().equals(other.getClientIp()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getAccount() == null) ? 0 : getAccount().hashCode());
        result = prime * result + ((getActionType() == null) ? 0 : getActionType().hashCode());
        result = prime * result + ((getActionDetail() == null) ? 0 : getActionDetail().hashCode());
        result = prime * result + ((getOpTime() == null) ? 0 : getOpTime().hashCode());
        result = prime * result + ((getClientIp() == null) ? 0 : getClientIp().hashCode());
        result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", account=").append(account);
        sb.append(", actionType=").append(actionType);
        sb.append(", actionDetail=").append(actionDetail);
        sb.append(", opTime=").append(opTime);
        sb.append(", clientIp=").append(clientIp);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}


