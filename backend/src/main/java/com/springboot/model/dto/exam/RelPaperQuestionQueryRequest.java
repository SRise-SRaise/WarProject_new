package com.springboot.model.dto.exam;

import com.springboot.common.PageRequest;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RelPaperQuestionQueryRequest extends PageRequest implements Serializable {

    private Long id;

    private Long paperId;

    private Long questionId;

    private Integer score;

    private Date createdAt;

    private Date updatedAt;

    private static final long serialVersionUID = 1L;
}
