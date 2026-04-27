package com.springboot.model.dto.experiment;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class EduExperimentUpdateRequest implements Serializable {

    private Long id;

    private Integer sortOrder;

    private String name;

    private Integer categoryId;

    private String fileType;

    private String instructionUrl;

    private String requirement;

    private String contentDesc;

    private Integer publishStatus;

    /**
     * 发布的班级编号列表（必须非空，发布时必须选班级）
     */
    private List<String> classCodes;

    private static final long serialVersionUID = 1L;
}
