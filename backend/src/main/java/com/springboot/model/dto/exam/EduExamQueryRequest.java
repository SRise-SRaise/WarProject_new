package com.springboot.model.dto.exam;

import com.springboot.common.PageRequest;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EduExamQueryRequest extends PageRequest implements Serializable {

    private Long id;

    private String examName;

    private Integer durationMin;

    private Date startTime;

    private Boolean isPublished;

    private Date createdAt;

    private Date updatedAt;

    private static final long serialVersionUID = 1L;
}
