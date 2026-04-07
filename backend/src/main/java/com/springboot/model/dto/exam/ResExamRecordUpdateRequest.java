package com.springboot.model.dto.exam;

import java.io.Serializable;
import lombok.Data;

@Data
public class ResExamRecordUpdateRequest implements Serializable {

    private Long id;

    private Long studentId;

    private Long questionId;

    private String studentAnswer;

    private static final long serialVersionUID = 1L;
}
