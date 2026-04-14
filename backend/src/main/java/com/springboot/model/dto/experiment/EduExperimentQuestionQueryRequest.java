package com.springboot.model.dto.experiment;

import com.springboot.common.PageRequest;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 实验题库查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EduExperimentQuestionQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 题目ID
     */
    private Long id;

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
     * 是否启用：0-禁用，1-启用
     */
    private Integer status;
}
