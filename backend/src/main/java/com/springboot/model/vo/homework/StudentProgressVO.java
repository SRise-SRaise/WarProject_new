package com.springboot.model.vo.homework;

import java.util.Date;
import java.util.List;
import java.io.Serializable;
import lombok.Data;

/**
 * 学生答题进度视图VO
 */
@Data
public class StudentProgressVO implements Serializable {

    /**
     * 作业ID
     */
    private Long exerciseId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 已答题数量
     */
    private Integer answeredCount;

    /**
     * 总题目数量
     */
    private Integer totalCount;

    /**
     * 是否已正式提交
     */
    private Boolean isSubmitted;

    /**
     * 最后提交/暂存时间
     */
    private Date lastUpdateTime;

    /**
     * 各题答题状态
     */
    private List<ItemProgressVO> items;

    /**
     * 单题答题进度
     */
    @Data
    public static class ItemProgressVO implements Serializable {
        /**
         * 题目ID
         */
        private Long itemId;

        /**
         * 题目序号
         */
        private Integer itemOrder;

        /**
         * 题目类型
         */
        private Integer questionType;

        /**
         * 是否已答
         */
        private Boolean isAnswered;

        /**
         * 选择题答案（如有）
         */
        private String choiceAnswer;

        /**
         * 简答题内容（如有）
         */
        private String textContent;

        /**
         * 填空题答案数量
         */
        private Integer fillCount;

        private static final long serialVersionUID = 1L;
    }

    private static final long serialVersionUID = 1L;
}