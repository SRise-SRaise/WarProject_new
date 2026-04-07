package com.springboot.model.dto.homework;

import com.springboot.common.PageRequest;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EduExerciseItemQueryRequest extends PageRequest implements Serializable {

    private Long id;

    private Long exerciseId;

    private String question;

    private String optionsText;

    private String standardAnswer;

    private Integer questionType;

    private Date createdAt;

    private Date updatedAt;

    private static final long serialVersionUID = 1L;
}
