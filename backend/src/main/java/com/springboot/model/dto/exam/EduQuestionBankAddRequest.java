package com.springboot.model.dto.exam;

import java.io.Serializable;
import lombok.Data;

@Data
public class EduQuestionBankAddRequest implements Serializable {

    private String questionContent;

    private String standardAnswer;

    private Integer questionType;

    private static final long serialVersionUID = 1L;
}
