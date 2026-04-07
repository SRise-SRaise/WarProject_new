package com.springboot.model.dto.experiment;

import com.springboot.common.PageRequest;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResExperimentResultQueryRequest extends PageRequest implements Serializable {

    private Long id;

    private Long studentId;

    private Long itemId;

    private String subContent;

    private Integer score;

    private Date submittedAt;

    private Integer gradingStatus;

    private Date createdAt;

    private Date updatedAt;

    private static final long serialVersionUID = 1L;
}
