package com.springboot.model.vo.user;

import java.io.Serializable;
import lombok.Data;

@Data
public class AuthLoginVO implements Serializable {

    private String sessionId;

    private LoginPrincipal loginPrincipal;

    private static final long serialVersionUID = 1L;
}
