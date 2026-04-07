package com.springboot.model.dto.exam;

import java.io.Serializable;
import lombok.Data;

@Data
public class EduQuestionTypeAddRequest implements Serializable {

    private Integer typeId;

    private String typeName;

    private static final long serialVersionUID = 1L;
}
