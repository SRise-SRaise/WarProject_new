package com.springboot.model.dto.user;

import java.io.Serializable;
import lombok.Data;

@Data
public class AuthRegisterRequest implements Serializable {

    private String roleType;

    private String loginAccount;

    private String displayName;

    private String classCode;

    private String loginPassword;

    private String checkPassword;

    private static final long serialVersionUID = 1L;
}
