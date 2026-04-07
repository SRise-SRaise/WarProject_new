package com.springboot.model.dto.exam;

import com.springboot.common.PageRequest;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EduQuestionTypeQueryRequest extends PageRequest implements Serializable {

    private Integer typeId;

    private String typeName;

    private Date createdAt;

    private static final long serialVersionUID = 1L;
}
