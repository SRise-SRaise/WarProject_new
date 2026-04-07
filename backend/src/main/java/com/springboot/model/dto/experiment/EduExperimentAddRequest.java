package com.springboot.model.dto.experiment;

import java.io.Serializable;
import lombok.Data;

@Data
public class EduExperimentAddRequest implements Serializable {

    private Integer sortOrder;

    private String name;

    private Integer categoryId;

    private String fileType;

    private String requirement;

    private String contentDesc;

    private Integer publishStatus;

    private static final long serialVersionUID = 1L;
}
