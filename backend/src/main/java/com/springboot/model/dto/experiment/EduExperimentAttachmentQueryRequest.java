package com.springboot.model.dto.experiment;

import java.io.Serializable;
import lombok.Data;

@Data
public class EduExperimentAttachmentQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 附件ID
     */
    private Long id;

    /**
     * 关联的实验提交记录ID
     */
    private Long resultId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 文件名（模糊搜索）
     */
    private String fileName;

    /**
     * 文件后缀
     */
    private String fileSuffix;

    /**
     * 上传状态
     */
    private Integer uploadStatus;

    /**
     * 当前页
     */
    private Long current;

    /**
     * 页面大小
     */
    private Long pageSize;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序
     */
    private String sortOrder;
}
