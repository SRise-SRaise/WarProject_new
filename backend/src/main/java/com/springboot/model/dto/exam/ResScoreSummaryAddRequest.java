package com.springboot.model.dto.exam;

import java.io.Serializable;
import lombok.Data;

@Data
public class ResScoreSummaryAddRequest implements Serializable {

    private Long studentId;

    private Long experimentId;

    private Integer totalScore;

    private static final long serialVersionUID = 1L;
}
