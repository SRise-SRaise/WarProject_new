package com.springboot.model.dto.exam;

import com.springboot.common.PageRequest;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EduPaperQueryRequest extends PageRequest implements Serializable {

    private Long id;

    private Integer paperCode;

    private String paperName;

    private String description;

    private Date generationTime;

    private Date createdAt;

    private Date updatedAt;

    private static final long serialVersionUID = 1L;
}
