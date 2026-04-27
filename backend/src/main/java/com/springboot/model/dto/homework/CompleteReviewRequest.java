package com.springboot.model.dto.homework;

import java.io.Serializable;
import lombok.Data;

/**
 * 教师完成整体批阅请求DTO
 */
@Data
public class CompleteReviewRequest implements Serializable {

    /**
     * 作业ID
     */
    private Long exerciseId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 总分
     */
    private Integer totalScore;

    /**
     * 整体评语
     */
    private String overallComment;

    private static final long serialVersionUID = 1L;
}