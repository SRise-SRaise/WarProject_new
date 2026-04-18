package com.springboot.model.dto.homework;

import com.springboot.common.PageRequest;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EduExerciseQueryRequest extends PageRequest implements Serializable {

    private Long id;

    private String taskName;

    private Long relateExpId;

    private Integer interactMode;

    private String description;

    private Date startTime;

    private Date endTime;

    private Date createdAt;

    private Date updatedAt;

    private Long teacherId;

    private Integer publishStatus;

    private static final long serialVersionUID = 1L;
}
