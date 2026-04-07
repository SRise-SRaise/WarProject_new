package com.springboot.model.dto.experiment;

import com.springboot.common.PageRequest;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EduExperimentQueryRequest extends PageRequest implements Serializable {

    private Long id;

    private String name;

    private Integer categoryId;

    private String fileType;

    private String requirement;

    private String contentDesc;

    private Integer publishStatus;

    private Date createdAt;

    private Date updatedAt;

    private static final long serialVersionUID = 1L;
}
