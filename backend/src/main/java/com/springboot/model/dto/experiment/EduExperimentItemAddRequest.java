package com.springboot.model.dto.experiment;

import java.io.Serializable;
import lombok.Data;

@Data
public class EduExperimentItemAddRequest implements Serializable {

    private Integer sortOrder;

    private String itemName;

    private Integer questionType;

    private String questionContent;

    private Long experimentId;

    private String standardAnswer;

    private Integer maxScore;

    private Integer itemStatus;

    private static final long serialVersionUID = 1L;
}
