package com.springboot.model.dto.experiment;

import java.io.Serializable;
import lombok.Data;

/**
 * 从docx文档导入实验的请求
 */
@Data
public class DocxImportRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * docx文件URL（已上传到文件服务器的docx文件地址）
     */
    private String fileUrl;

    /**
     * 实验名称（从文档中提取，也可手动指定覆盖）
     */
    private String experimentName;

    /**
     * 实验类型/分类ID
     */
    private Integer categoryId;

    /**
     * 实验要求描述（可选）
     */
    private String requirement;

    /**
     * 是否自动发布实验（默认发布）
     */
    private Boolean autoPublish;

    /**
     * 题目默认难度（1=简单，2=中等，3=困难），未指定时默认2
     */
    private Integer defaultDifficulty;

    /**
     * 题目默认分值，未指定时默认10分
     */
    private Integer defaultScore;
}
