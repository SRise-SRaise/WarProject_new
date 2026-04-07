package com.springboot.model.dto.homework;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

@Data
public class EduExerciseAddRequest implements Serializable {

    private Integer sortOrder;

    private String taskName;

    private Long relateExpId;

    private Integer interactMode;

    private String description;

    private Date startTime;

    private Date endTime;

    private static final long serialVersionUID = 1L;
}
