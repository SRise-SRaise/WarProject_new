package com.springboot.model.dto.common;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

@Data
public class SysAdminLogAddRequest implements Serializable {

    private String account;

    private Integer actionType;

    private String actionDetail;

    private Date opTime;

    private String clientIp;

    private static final long serialVersionUID = 1L;
}
