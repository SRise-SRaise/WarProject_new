package com.springboot.model.dto.homework;

import java.io.Serializable;
import lombok.Data;

@Data
public class EduExerciseItemUpdateRequest implements Serializable {

    private Long id;

    private Long exerciseId;

    private String question;

    private String optionsText;

    private String standardAnswer;

    private Integer questionType;

    private static final long serialVersionUID = 1L;
}
