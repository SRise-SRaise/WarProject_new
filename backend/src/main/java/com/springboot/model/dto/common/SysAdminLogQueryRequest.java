package com.springboot.model.dto.common;

import com.springboot.common.PageRequest;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysAdminLogQueryRequest extends PageRequest implements Serializable {

    private Long id;

    private String account;

    private Integer actionType;

    private String actionDetail;

    private Date opTime;

    private String clientIp;

    private Date createdAt;

    private static final long serialVersionUID = 1L;
}
