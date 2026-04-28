package com.springboot.model.vo.experiment;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 实验日志详情VO
 */
@Data
public class ExperimentLogDetailVO {
    
    /**
     * 学生账号
     */
    private String account;
    
    /**
     * 实验ID
     */
    private Long experimentId;
    
    /**
     * 风险等级
     */
    private String riskLevel;
    
    /**
     * 风险原因
     */
    private List<String> riskReasons;
    
    /**
     * 总用时（秒）
     */
    private Long totalDuration;
    
    /**
     * 答题数量
     */
    private Integer questionCount;
    
    /**
     * 平均答题间隔
     */
    private Long avgInterval;
    
    /**
     * 最短答题间隔
     */
    private Long minInterval;
    
    /**
     * 快速连续答题次数
     */
    private Integer rapidAnswerCount;
    
    /**
     * 粘贴次数
     */
    private Integer pasteCount;
    
    /**
     * 操作日志列表
     */
    private List<LogItem> logs;
    
    /**
     * 日志项
     */
    @Data
    public static class LogItem {
        /**
         * 操作类型
         */
        private Integer actionType;
        
        /**
         * 操作名称
         */
        private String actionName;
        
        /**
         * 操作时间
         */
        private Date opTime;
        
        /**
         * 客户端IP
         */
        private String clientIp;
        
        /**
         * 题目ID
         */
        private Long questionId;
        
        /**
         * 题目名称
         */
        private String questionName;
        
        /**
         * 额外信息
         */
        private Map<String, Object> extra;
    }
}
