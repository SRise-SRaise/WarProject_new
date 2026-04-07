package com.springboot.model.dto.common;

import com.springboot.common.PageRequest;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysConfigQueryRequest extends PageRequest implements Serializable {

    private String configKey;

    private String configValue;

    private Date createdAt;

    private Date updatedAt;

    private static final long serialVersionUID = 1L;
}
