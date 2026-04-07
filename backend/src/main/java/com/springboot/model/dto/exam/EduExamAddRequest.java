package com.springboot.model.dto.exam;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

@Data
public class EduExamAddRequest implements Serializable {

    private String examName;

    private Integer durationMin;

    private Date startTime;

    private Boolean isPublished;

    private static final long serialVersionUID = 1L;
}
