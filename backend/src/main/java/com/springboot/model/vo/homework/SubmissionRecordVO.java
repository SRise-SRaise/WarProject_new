package com.springboot.model.vo.homework;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * 提交记录视图VO
 */
@Data
public class SubmissionRecordVO implements Serializable {

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 作业ID
     */
    private Long exerciseId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 班级编号
     */
    private String classCode;

    /**
     * 提交状态：draft/submitted/reviewed/late
     */
    private String status;

    /**
     * 提交时间
     */
    private Date submittedAt;

    /**
     * 总得分
     */
    private Integer totalScore;

    /**
     * 自动评分（客观题）
     */
    private Integer autoScore;

    /**
     * 教师评分（主观题）
     */
    private Integer manualScore;

    /**
     * 批改状态汇总
     */
    private String gradingSummary;

    private static final long serialVersionUID = 1L;
}