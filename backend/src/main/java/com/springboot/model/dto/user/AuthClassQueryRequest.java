package com.springboot.model.dto.user;

import com.springboot.common.PageRequest;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AuthClassQueryRequest extends PageRequest implements Serializable {

    private String classCode;

    private String headmasterName;

    private Integer classStatus;

    private Date createdAt;

    private Date updatedAt;

    private static final long serialVersionUID = 1L;
}
