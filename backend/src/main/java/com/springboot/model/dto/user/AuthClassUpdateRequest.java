package com.springboot.model.dto.user;

import java.io.Serializable;
import lombok.Data;

@Data
public class AuthClassUpdateRequest implements Serializable {

    private String classCode;

    private String headmasterName;

    private Integer classStatus;

    private static final long serialVersionUID = 1L;
}
