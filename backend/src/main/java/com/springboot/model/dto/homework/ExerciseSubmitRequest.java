package com.springboot.model.dto.homework;

import java.util.List;
import java.io.Serializable;
import lombok.Data;

/**
 * 作业答题提交请求DTO
 */
@Data
public class ExerciseSubmitRequest implements Serializable {

    /**
     * 作业ID
     */
    private Long exerciseId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 答题列表
     */
    private List<AnswerItem> answers;

    /**
     * 答题项
     */
    @Data
    public static class AnswerItem implements Serializable {
        /**
         * 题目ID
         */
        private Long itemId;

        /**
         * 题目类型：1单选/2多选/3判断/4填空/5简答
         */
        private Integer questionType;

        /**
         * 选择题答案（单选为单个字母，多选为字母组合）
         */
        private String choiceAnswer;

        /**
         * 简答题内容
         */
        private String textContent;

        /**
         * 填空题答案列表
         */
        private List<FillBlankAnswer> fillBlanks;

        private static final long serialVersionUID = 1L;
    }

    /**
     * 填空题答案
     */
    @Data
    public static class FillBlankAnswer implements Serializable {
        /**
         * 填空序号
         */
        private Integer blankIndex;

        /**
         * 答案内容
         */
        private String answerContent;

        private static final long serialVersionUID = 1L;
    }

    private static final long serialVersionUID = 1L;
}