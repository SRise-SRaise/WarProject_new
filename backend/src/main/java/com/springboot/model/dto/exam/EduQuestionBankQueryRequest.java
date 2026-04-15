package com.springboot.model.dto.exam;

import com.springboot.common.PageRequest;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EduQuestionBankQueryRequest extends PageRequest implements Serializable {

    /**
     * 题目ID
     */
    private Long id;

    /**
     * 题目内容（模糊搜索）
     */
    private String questionContent;

    /**
     * 题目类型
     */
    private Integer questionType;

    /**
     * 难度系数
     */
    private Integer difficulty;

    /**
     * 创建教师ID
     */
    private Long creatorTeacherId;

    private static final long serialVersionUID = 1L;
}
