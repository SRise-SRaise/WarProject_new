package com.springboot.model.dto.experiment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 实验得分分布 DTO（Mapper 查询结果）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperimentScoreDistributionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 学生得分（实验总分）
     */
    private Integer score;
}
