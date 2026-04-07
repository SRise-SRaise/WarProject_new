package com.springboot.model.dto.common;

import java.io.Serializable;
import lombok.Data;

@Data
public class SysConfigAddRequest implements Serializable {

    private String configKey;

    private String configValue;

    private static final long serialVersionUID = 1L;
}
