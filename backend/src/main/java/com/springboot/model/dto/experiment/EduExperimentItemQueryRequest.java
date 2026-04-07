package com.springboot.model.dto.experiment;

import com.springboot.common.PageRequest;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EduExperimentItemQueryRequest extends PageRequest implements Serializable {

    private Long id;

    private String itemName;

    private Integer questionType;

    private String questionContent;

    private Long experimentId;

    private String standardAnswer;

    private Integer maxScore;

    private Integer itemStatus;

    private Date createdAt;

    private Date updatedAt;

    private static final long serialVersionUID = 1L;
}
