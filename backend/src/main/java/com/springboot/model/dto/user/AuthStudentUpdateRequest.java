package com.springboot.model.dto.user;

import java.io.Serializable;
import lombok.Data;

@Data
public class AuthStudentUpdateRequest implements Serializable {

    private Long id;

    private String studentCode;

    private String studentName;

    private String passwordMd5;

    private String classCode;

    private String remark;

    private Integer accountStatus;

    private Integer loginFailCount;

    private String lastLoginIp;

    private static final long serialVersionUID = 1L;
}
