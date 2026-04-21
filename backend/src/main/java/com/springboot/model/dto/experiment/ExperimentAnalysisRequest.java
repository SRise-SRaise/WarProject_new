package com.springboot.model.dto.experiment;

import lombok.Data;

/**
 * 实验分析请求 DTO
 */
@Data
public class ExperimentAnalysisRequest {
    /**
     * 实验ID（可选，不传则统计所有实验）
     */
    private Long experimentId;

    /**
     * 班级编号（可选）
     */
    private String clazzNo;
}
