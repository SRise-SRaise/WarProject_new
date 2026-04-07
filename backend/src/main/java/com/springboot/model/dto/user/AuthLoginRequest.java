package com.springboot.model.dto.user;

import java.io.Serializable;
import lombok.Data;

@Data
public class AuthLoginRequest implements Serializable {

    private String roleType;

    private String loginAccount;

    private String loginPassword;

    private static final long serialVersionUID = 1L;
}
