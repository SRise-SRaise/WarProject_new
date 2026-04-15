package com.springboot.model.dto.exam;

import java.io.Serializable;
import lombok.Data;

@Data
public class EduQuestionBankUpdateRequest implements Serializable {

    /**
     * 题目ID
     */
    private Long id;

    /**
     * 题目题干
     */
    private String questionContent;

    /**
     * 题目类型
     */
    private Integer questionType;

    /**
     * 选项内容（用于单选/多选/判断）
     */
    private String optionsText;

    /**
     * 标准答案
     */
    private String standardAnswer;

    /**
     * 题目解析
     */
    private String analysis;

    /**
     * 难度系数 1-5
     */
    private Integer difficulty;

    private static final long serialVersionUID = 1L;
}
