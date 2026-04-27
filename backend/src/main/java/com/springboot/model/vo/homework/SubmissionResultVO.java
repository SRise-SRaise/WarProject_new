package com.springboot.model.vo.homework;

import java.io.Serializable;
import lombok.Data;

/**
 * 提交结果视图VO
 */
@Data
public class SubmissionResultVO implements Serializable {

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 提交的题目数量
     */
    private Integer submittedCount;

    /**
     * 自动评分结果（客观题）
     */
    private Integer autoScore;

    /**
     * 等待教师批阅的题目数量
     */
    private Integer pendingReviewCount;

    /**
     * 消息
     */
    private String message;

    private static final long serialVersionUID = 1L;
}