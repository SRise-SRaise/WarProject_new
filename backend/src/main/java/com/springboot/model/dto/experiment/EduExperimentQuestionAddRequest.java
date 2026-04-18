package com.springboot.model.dto.experiment;

import java.io.Serializable;
import lombok.Data;

/**
 * 添加实验题库请求
 */
@Data
public class EduExperimentQuestionAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 题目名称/标题
     */
    private String questionName;

    /**
     * 题目内容/题干
     */
    private String questionContent;

    /**
     * 题目类型：1-选择题，2-填空题，3-编程题，4-简答题
     */
    private Integer questionType;

    /**
     * 难度等级：1-简单，2-中等，3-困难
     */
    private Integer difficulty;

    /**
     * 所属知识点/标签
     */
    private String tag;

    /**
     * 选项内容（JSON格式，适用选择题）
     */
    private String options;

    /**
     * 标准答案
     */
    private String standardAnswer;

    /**
     * 答案解析
     */
    private String answerAnalysis;

    /**
     * 参考代码（适用编程题）
     */
    private String referenceCode;

    /**
     * 题目分值
     */
    private Integer score;

    /**
     * 是否启用：0-禁用，1-启用
     */
    private Integer status;
}
