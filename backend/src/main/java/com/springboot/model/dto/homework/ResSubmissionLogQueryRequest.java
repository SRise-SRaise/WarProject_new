package com.springboot.model.dto.homework;

import com.springboot.common.PageRequest;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResSubmissionLogQueryRequest extends PageRequest implements Serializable {

    private Long id;

    private Long resultId;

    private String contentSnapshot;

    private Date snapshotTime;

    private Date createdAt;

    private static final long serialVersionUID = 1L;
}
