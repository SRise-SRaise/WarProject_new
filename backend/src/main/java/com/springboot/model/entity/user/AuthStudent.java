package com.springboot.model.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 学生信息表
 * @TableName auth_student
 */
@TableName(value ="auth_student")
@Data
public class AuthStudent implements Serializable {
    /**
     * 学生主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学号
     */
    @TableField(value = "student_code")
    private String studentCode;

    /**
     * 学生姓名
     */
    @TableField(value = "student_name")
    private String studentName;

    /**
     * 登录密码
     */
    @TableField(value = "password_md5")
    private String passwordMd5;

    /**
     * 所属班级编号
     */
    @TableField(value = "class_code")
    private String classCode;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 账号状态(0正常)
     */
    @TableField(value = "account_status")
    private Integer accountStatus;

    /**
     * 连续登录失败次数
     */
    @TableField(value = "login_fail_count")
    private Integer loginFailCount;

    /**
     * 最近一次登录IP
     */
    @TableField(value = "last_login_ip")
    private String lastLoginIp;

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
        AuthStudent other = (AuthStudent) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getStudentCode() == null ? other.getStudentCode() == null : this.getStudentCode().equals(other.getStudentCode()))
            && (this.getStudentName() == null ? other.getStudentName() == null : this.getStudentName().equals(other.getStudentName()))
            && (this.getPasswordMd5() == null ? other.getPasswordMd5() == null : this.getPasswordMd5().equals(other.getPasswordMd5()))
            && (this.getClassCode() == null ? other.getClassCode() == null : this.getClassCode().equals(other.getClassCode()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
            && (this.getAccountStatus() == null ? other.getAccountStatus() == null : this.getAccountStatus().equals(other.getAccountStatus()))
            && (this.getLoginFailCount() == null ? other.getLoginFailCount() == null : this.getLoginFailCount().equals(other.getLoginFailCount()))
            && (this.getLastLoginIp() == null ? other.getLastLoginIp() == null : this.getLastLoginIp().equals(other.getLastLoginIp()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getStudentCode() == null) ? 0 : getStudentCode().hashCode());
        result = prime * result + ((getStudentName() == null) ? 0 : getStudentName().hashCode());
        result = prime * result + ((getPasswordMd5() == null) ? 0 : getPasswordMd5().hashCode());
        result = prime * result + ((getClassCode() == null) ? 0 : getClassCode().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        result = prime * result + ((getAccountStatus() == null) ? 0 : getAccountStatus().hashCode());
        result = prime * result + ((getLoginFailCount() == null) ? 0 : getLoginFailCount().hashCode());
        result = prime * result + ((getLastLoginIp() == null) ? 0 : getLastLoginIp().hashCode());
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
        sb.append(", studentCode=").append(studentCode);
        sb.append(", studentName=").append(studentName);
        sb.append(", passwordMd5=").append(passwordMd5);
        sb.append(", classCode=").append(classCode);
        sb.append(", remark=").append(remark);
        sb.append(", accountStatus=").append(accountStatus);
        sb.append(", loginFailCount=").append(loginFailCount);
        sb.append(", lastLoginIp=").append(lastLoginIp);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}


