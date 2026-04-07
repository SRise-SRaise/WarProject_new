package com.springboot.model.dto.homework;

import com.springboot.common.PageRequest;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResExerciseRecordQueryRequest extends PageRequest implements Serializable {

    private Long id;

    private Long itemId;

    private Long studentId;

    private String choiceAnswer;

    private String textContent;

    private Integer score;

    private Date submittedAt;

    private Date createdAt;

    private Date updatedAt;

    private static final long serialVersionUID = 1L;
}
