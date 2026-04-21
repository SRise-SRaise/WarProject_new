package com.springboot.model.dto.experiment;

import lombok.Data;

import java.io.Serializable;

/**
 * 学生实验数据分析请求
 */
@Data
public class StudentExperimentAnalysisRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 学生ID
     */
    private Long studentId;
}
