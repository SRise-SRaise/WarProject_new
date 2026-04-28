package com.springboot.model.vo.experiment;

import lombok.Data;

import java.util.List;

/**
 * 实验日志风险分析VO
 */
@Data
public class ExperimentLogRiskVO {
    
    /**
     * 学生账号
     */
    private String account;
    
    /**
     * 学生姓名
     */
    private String studentName;
    
    /**
     * 班级编号
     */
    private String clazzNo;
    
    /**
     * 风险等级：high/medium/low
     */
    private String riskLevel;
    
    /**
     * 风险原因列表
     */
    private List<String> riskReasons;
    
    /**
     * 日志记录数
     */
    private Integer logCount;
    
    /**
     * 总用时（秒）
     */
    private Long totalDuration;
    
    /**
     * 答题数量
     */
    private Integer questionCount;
    
    /**
     * 平均答题间隔（秒）
     */
    private Long avgInterval;
    
    /**
     * 最短答题间隔（秒）
     */
    private Long minInterval;
    
    /**
     * 快速连续答题次数（间隔<10秒）
     */
    private Integer rapidAnswerCount;
    
    /**
     * 粘贴操作次数
     */
    private Integer pasteCount;
}
