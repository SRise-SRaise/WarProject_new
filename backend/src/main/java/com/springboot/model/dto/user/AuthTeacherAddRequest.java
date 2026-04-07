package com.springboot.model.dto.user;

import java.io.Serializable;
import lombok.Data;

@Data
public class AuthTeacherAddRequest implements Serializable {

    private String username;

    private String passwordMd5;

    private String realName;

    private static final long serialVersionUID = 1L;
}
