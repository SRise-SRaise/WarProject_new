package com.springboot.model.dto.homework;

import com.springboot.common.PageRequest;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学生作业列表查询请求DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StudentExerciseQueryRequest extends PageRequest implements Serializable {

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 班级编号
     */
    private String classCode;

    /**
     * 作业状态：pending/submitted/reviewed/overdue
     */
    private String status;

    /**
     * 关键词搜索
     */
    private String keyword;

    private static final long serialVersionUID = 1L;
}