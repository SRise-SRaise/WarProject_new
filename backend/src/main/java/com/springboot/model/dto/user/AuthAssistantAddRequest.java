package com.springboot.model.dto.user;

import java.io.Serializable;
import lombok.Data;

@Data
public class AuthAssistantAddRequest implements Serializable {

    private String username;

    private String passwordHash;

    private String bindStudentCode;

    private String realName;

    private String classCode;

    private static final long serialVersionUID = 1L;
}
