package com.springboot.model.dto.homework;

import java.util.Date;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class EduExerciseUpdateRequest implements Serializable {

    private Long id;

    private Integer sortOrder;

    private String taskName;

    private Long relateExpId;

    private Integer interactMode;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date endTime;

    private static final long serialVersionUID = 1L;
}
