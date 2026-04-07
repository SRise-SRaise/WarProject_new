package com.springboot.model.dto.user;

import com.springboot.common.PageRequest;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AuthAssistantQueryRequest extends PageRequest implements Serializable {

    private Long id;

    private String username;

    private String passwordHash;

    private String bindStudentCode;

    private String realName;

    private String classCode;

    private Date createdAt;

    private Date updatedAt;

    private static final long serialVersionUID = 1L;
}
