package com.springboot.model.dto.exam;

import java.io.Serializable;
import lombok.Data;

@Data
public class ResScoreSummaryUpdateRequest implements Serializable {

    private Long id;

    private Long studentId;

    private Long experimentId;

    private Integer totalScore;

    private static final long serialVersionUID = 1L;
}
