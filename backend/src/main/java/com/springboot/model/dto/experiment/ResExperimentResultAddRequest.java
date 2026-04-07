package com.springboot.model.dto.experiment;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

@Data
public class ResExperimentResultAddRequest implements Serializable {

    private Long studentId;

    private Long itemId;

    private String subContent;

    private Integer score;

    private Date submittedAt;

    private Integer gradingStatus;

    private static final long serialVersionUID = 1L;
}
