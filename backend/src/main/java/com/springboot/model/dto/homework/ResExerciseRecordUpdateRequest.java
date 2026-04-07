package com.springboot.model.dto.homework;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

@Data
public class ResExerciseRecordUpdateRequest implements Serializable {

    private Long id;

    private Long itemId;

    private Long studentId;

    private String choiceAnswer;

    private String textContent;

    private Integer score;

    private Date submittedAt;

    private static final long serialVersionUID = 1L;
}
