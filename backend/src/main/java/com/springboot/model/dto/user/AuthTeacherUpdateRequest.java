package com.springboot.model.dto.user;

import java.io.Serializable;
import lombok.Data;

@Data
public class AuthTeacherUpdateRequest implements Serializable {

    private Long id;

    private String username;

    private String passwordMd5;

    private String realName;

    private static final long serialVersionUID = 1L;
}
