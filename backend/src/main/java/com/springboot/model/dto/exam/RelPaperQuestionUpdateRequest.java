package com.springboot.model.dto.exam;

import java.io.Serializable;
import lombok.Data;

@Data
public class RelPaperQuestionUpdateRequest implements Serializable {

    private Long id;

    private Long paperId;

    private Long questionId;

    private Integer score;

    private static final long serialVersionUID = 1L;
}
