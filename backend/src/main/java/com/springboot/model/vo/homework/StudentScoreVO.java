package com.springboot.model.vo.homework;

import java.util.List;
import java.io.Serializable;
import lombok.Data;

/**
 * 学生成绩视图VO
 */
@Data
public class StudentScoreVO implements Serializable {

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
     * 学生总得分
     */
    private Integer totalScore;

    /**
     * 作业满分
     */
    private Integer maxScore;

    /**
     * 批改状态汇总
     */
    private String status;

    /**
     * 各题得分详情
     */
    private List<ScoreItemVO> items;

    /**
     * 教师整体评语
     */
    private String overallComment;

    /**
     * 单题得分详情
     */
    @Data
    public static class ScoreItemVO implements Serializable {
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
         * 该题满分
         */
        private Integer maxScore;

        /**
         * 学生得分
         */
        private Integer studentScore;

        /**
         * 学生答案
         */
        private String studentAnswer;

        /**
         * 标准答案
         */
        private String correctAnswer;

        /**
         * 批改状态：0未批改/1自动判分/2教师批改
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