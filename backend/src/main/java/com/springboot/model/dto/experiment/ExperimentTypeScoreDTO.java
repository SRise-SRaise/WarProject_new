package com.springboot.model.dto.experiment;

import com.springboot.service.experiment.adapter.ScoreDataProvider.HasTypeCodeAndScore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 实验类型维度得分统计DTO（Mapper查询结果）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperimentTypeScoreDTO
        implements Serializable, HasTypeCodeAndScore {

    private static final long serialVersionUID = 1L;

    /**
     * 实验类型编码（1-6）
     */
    private Integer typeCode;

    /**
     * 参与该类型实验的数量
     */
    private Integer experimentCount;

    /**
     * 该类型实验的总得分
     */
    private Integer totalScore;
}
