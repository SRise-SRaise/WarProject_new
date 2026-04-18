package com.springboot.model.dto.homework;

import java.io.Serializable;
import lombok.Data;

/**
 * 教师批阅单题请求DTO
 */
@Data
public class ReviewItemRequest implements Serializable {

    /**
     * 答题记录ID
     */
    private Long recordId;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 教师评语
     */
    private String comment;

    /**
     * 批改状态：2=教师已批改
     */
    private Integer gradingStatus;

    private static final long serialVersionUID = 1L;
}