package com.springboot.model.dto.homework;

import java.io.Serializable;
import lombok.Data;

@Data
public class ResFillBlankDetailAddRequest implements Serializable {

    private Long itemId;

    private Integer blankIndex;

    private String answerContent;

    private String answerHash;

    private Integer submitCount;

    private Boolean isCorrect;

    private static final long serialVersionUID = 1L;
}
