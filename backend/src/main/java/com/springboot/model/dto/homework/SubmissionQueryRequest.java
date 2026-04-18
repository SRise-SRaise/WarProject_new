package com.springboot.model.dto.homework;

import com.springboot.common.PageRequest;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 提交记录查询请求DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SubmissionQueryRequest extends PageRequest implements Serializable {

    /**
     * 作业ID
     */
    private Long exerciseId;

    /**
     * 班级编号（可选筛选）
     */
    private String classCode;

    /**
     * 提交状态：draft/submitted/reviewed/late
     */
    private String status;

    /**
     * 学生姓名搜索
     */
    private String studentName;

    private static final long serialVersionUID = 1L;
}