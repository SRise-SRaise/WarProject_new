package com.springboot.model.dto.experiment;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

/**
 * 步骤（题目）得分分析 DTO
 * 供 MyBatis Mapper 直接映射使用（独立顶层类，避免静态内部类映射失败问题）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StepScoreAnalysisDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 步骤序号 */
    private Integer sortOrder;

    /** 步骤/题目名称 */
    private String itemName;

    /** 题目类型（1单选 2填空 3编程 4简答 7实验小结） */
    private Integer questionType;

    /** 满分 */
    private Integer maxScore;

    /** 平均得分 */
    private Double avgScore;

    /** 最高得分 */
    private Integer highScore;

    /** 最低得分 */
    private Integer lowScore;

    /** 得分率（平均分/满分，百分比，由 Service 层计算填充） */
    private Double scoreRate;

    /** 作答人数（已被批改的记录数） */
    private Integer answeredCount;
}
