package com.springboot.model.vo.user;

import java.io.Serializable;
import lombok.Data;

@Data
public class LoginPrincipal implements Serializable {

    private Long userId;

    private String roleType;

    private String roleCode;

    private String loginAccount;

    private String displayName;

    private static final long serialVersionUID = 1L;
}
