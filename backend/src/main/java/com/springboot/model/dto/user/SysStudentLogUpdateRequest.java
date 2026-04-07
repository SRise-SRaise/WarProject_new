package com.springboot.model.dto.user;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

@Data
public class SysStudentLogUpdateRequest implements Serializable {

    private Long id;

    private String account;

    private Integer actionType;

    private String actionDetail;

    private Date opTime;

    private String clientIp;

    private static final long serialVersionUID = 1L;
}
