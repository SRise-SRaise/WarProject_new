package com.springboot.model.dto.homework;

import com.springboot.common.PageRequest;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 成绩查询请求DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ScoreQueryRequest extends PageRequest implements Serializable {

    /**
     * 作业ID
     */
    private Long exerciseId;

    /**
     * 班级编号
     */
    private String classCode;

    /**
     * 学生姓名搜索
     */
    private String studentName;

    private static final long serialVersionUID = 1L;
}