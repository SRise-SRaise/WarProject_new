package com.springboot.model.vo.experiment;

import lombok.Data;

import java.io.Serializable;

@Data
public class EduExperimentReportListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 报告记录ID
     */
    private Long id;

    /**
     * 实验ID
     */
    private Long experimentId;

    /**
     * 实验名称
     */
    private String experimentName;

    /**
     * 实验序号
     */
    private Integer experimentNo;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 学号
     */
    private String studentNo;

    /**
     * 姓名
     */
    private String studentName;

    /**
     * 班级编号
     */
    private String clazzNo;

    /**
     * 状态: submitted-待批改, reviewed-已批改
     */
    private String status;

    /**
     * 总成绩
     */
    private Integer totalScore;

    /**
     * 提交时间
     */
    private String submittedAt;

    /**
     * 批改时间
     */
    private String reviewedAt;
}
