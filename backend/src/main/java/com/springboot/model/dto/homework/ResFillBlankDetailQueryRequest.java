package com.springboot.model.dto.homework;

import com.springboot.common.PageRequest;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResFillBlankDetailQueryRequest extends PageRequest implements Serializable {

    private Long id;

    private Long itemId;

    private Integer blankIndex;

    private String answerContent;

    private String answerHash;

    private Integer submitCount;

    private Boolean isCorrect;

    private Date createdAt;

    private Date updatedAt;

    private static final long serialVersionUID = 1L;
}
