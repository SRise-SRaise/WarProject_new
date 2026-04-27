package com.springboot.model.vo.homework;

import java.util.Date;
import java.util.List;
import java.io.Serializable;
import lombok.Data;

/**
 * 提交详情视图VO（教师批阅用）
 */
@Data
public class SubmissionDetailVO implements Serializable {

    /**
     * 作业ID
     */
    private Long exerciseId;

    /**
     * 作业名称
     */
    private String exerciseName;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 提交时间
     */
    private Date submittedAt;

    /**
     * 总得分
     */
    private Integer totalScore;

    /**
     * 各题答题详情
     */
    private List<AnswerDetailVO> answers;

    /**
     * 单题答题详情
     */
    @Data
    public static class AnswerDetailVO implements Serializable {
        /**
         * 答题记录ID
         */
        private Long recordId;

        /**
         * 题目ID
         */
        private Long itemId;

        /**
         * 题目序号
         */
        private Integer itemOrder;

        /**
         * 题目题干
         */
        private String question;

        /**
         * 题目类型
         */
        private Integer questionType;

        /**
         * 选项文本
         */
        private String optionsText;

        /**
         * 标准答案
         */
        private String standardAnswer;

        /**
         * 该题满分
         */
        private Integer maxScore;

        /**
         * 学生答案
         */
        private String studentAnswer;

        /**
         * 学生得分
         */
        private Integer score;

        /**
         * 批改状态
         */
        private Integer gradingStatus;

        /**
         * 教师评语
         */
        private String comment;

        private static final long serialVersionUID = 1L;
    }

    private static final long serialVersionUID = 1L;
}