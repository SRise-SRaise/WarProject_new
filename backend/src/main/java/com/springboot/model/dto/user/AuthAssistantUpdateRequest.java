package com.springboot.model.dto.user;

import java.io.Serializable;
import lombok.Data;

@Data
public class AuthAssistantUpdateRequest implements Serializable {

    private Long id;

    private String username;

    private String passwordHash;

    private String bindStudentCode;

    private String realName;

    private String classCode;

    private static final long serialVersionUID = 1L;
}
