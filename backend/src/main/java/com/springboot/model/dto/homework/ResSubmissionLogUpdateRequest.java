package com.springboot.model.dto.homework;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

@Data
public class ResSubmissionLogUpdateRequest implements Serializable {

    private Long id;

    private Long resultId;

    private String contentSnapshot;

    private Date snapshotTime;

    private static final long serialVersionUID = 1L;
}
