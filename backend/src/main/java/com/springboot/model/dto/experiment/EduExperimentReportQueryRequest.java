package com.springboot.model.dto.experiment;

import lombok.Data;

import java.io.Serializable;

@Data
public class EduExperimentReportQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 实验ID
     */
    private Long experimentId;

    /**
     * 班级编号
     */
    private String clazzNo;

    /**
     * 批改状态筛选: submitted-待批改, reviewed-已批改, all-全部
     */
    private String status;

    /**
     * 当前页
     */
    private Long current = 1L;

    /**
     * 每页大小
     */
    private Long pageSize = 10L;
}
