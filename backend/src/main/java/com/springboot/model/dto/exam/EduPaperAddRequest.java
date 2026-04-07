package com.springboot.model.dto.exam;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

@Data
public class EduPaperAddRequest implements Serializable {

    private Integer paperCode;

    private String paperName;

    private String description;

    private Date generationTime;

    private static final long serialVersionUID = 1L;
}
