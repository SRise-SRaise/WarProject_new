package com.springboot.model.dto.user;

import com.springboot.common.PageRequest;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AuthTeacherQueryRequest extends PageRequest implements Serializable {

    private Long id;

    private String username;

    private String passwordMd5;

    private String realName;

    private Date createdAt;

    private Date updatedAt;

    private static final long serialVersionUID = 1L;
}
