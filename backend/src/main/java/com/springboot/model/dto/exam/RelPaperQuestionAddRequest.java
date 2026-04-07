package com.springboot.model.dto.exam;

import java.io.Serializable;
import lombok.Data;

@Data
public class RelPaperQuestionAddRequest implements Serializable {

    private Long paperId;

    private Long questionId;

    private Integer score;

    private static final long serialVersionUID = 1L;
}
