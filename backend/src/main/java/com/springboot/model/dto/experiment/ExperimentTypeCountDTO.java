package com.springboot.model.dto.experiment;

import com.springboot.service.experiment.adapter.ScoreDataProvider.HasTypeCode;
import com.springboot.service.experiment.adapter.ScoreDataProvider.HasTypeCodeEx;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 实验类型统计 DTO（Mapper 查询结果）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperimentTypeCountDTO
        implements Serializable, HasTypeCode, HasTypeCodeEx {

    private static final long serialVersionUID = 1L;

    /**
     * 实验类型编码
     */
    private Integer typeCode;

    /**
     * 实验数量
     */
    private Integer experimentCount;

    /**
     * 已提交实验数量
     */
    private Integer submittedCount;

    /**
     * 已批改实验数量
     */
    private Integer gradedCount;

    /**
     * 总提交次数（学生数）
     */
    private Integer totalSubmissions;

    /**
     * 平均分
     */
    private Double averageScore;
}
