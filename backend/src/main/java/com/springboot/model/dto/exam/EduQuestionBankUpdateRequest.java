package com.springboot.model.dto.exam;

import java.io.Serializable;
import lombok.Data;

@Data
public class EduQuestionBankUpdateRequest implements Serializable {

    private Long id;

    private String questionContent;

    private String standardAnswer;

    private Integer questionType;

    private static final long serialVersionUID = 1L;
}
