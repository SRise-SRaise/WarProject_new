package com.springboot.model.dto.user;

import com.springboot.common.PageRequest;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AuthStudentQueryRequest extends PageRequest implements Serializable {

    private Long id;

    private String studentCode;

    private String studentName;

    private String passwordMd5;

    private String classCode;

    private String remark;

    private Integer accountStatus;

    private Integer loginFailCount;

    private String lastLoginIp;

    private Date createdAt;

    private Date updatedAt;

    private static final long serialVersionUID = 1L;
}
