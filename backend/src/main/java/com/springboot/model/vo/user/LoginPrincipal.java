package com.springboot.model.vo.user;

import java.io.Serializable;
import lombok.Data;

@Data
public class LoginPrincipal implements Serializable {

    /**
     * id
     */
    private Long userId;

    /**
     * 角色类型
     */
    private String roleType;

    /**
     * 角色编号
     */
    private String roleCode;

    /**
     * 登录账号
     */
    private String loginAccount;

    /**
     * 显示名称
     */
    private String displayName;

    /**
     * 专业名称（学生：auth_student.remark）
     */
    private String major;

    /**
     * 班级编号（学生）
     */
    private String classCode;

    /**
     * 最近一次登录 IP（学生）
     */
    private String lastLoginIp;

    private static final long serialVersionUID = 1L;
}
