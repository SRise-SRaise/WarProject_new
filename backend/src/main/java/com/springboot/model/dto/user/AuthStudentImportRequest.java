package com.springboot.model.dto.user;

import java.io.Serializable;
import lombok.Data;

@Data
public class AuthStudentImportRequest implements Serializable {
    /**
     * 学号（唯一）
     */
    private String studentCode;

    /**
     * 姓名
     */
    private String studentName;

    /**
     * 密码（明文），后端会进行盐值 MD5
     */
    private String password;

    /**
     * 班级编号（可为空）
     */
    private String classCode;

    /**
     * 专业/备注（可为空）
     */
    private String remark;

    private static final long serialVersionUID = 1L;
}

