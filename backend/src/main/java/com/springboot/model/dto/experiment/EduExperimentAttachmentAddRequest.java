package com.springboot.model.dto.experiment;

import java.io.Serializable;
import lombok.Data;
import jakarta.validation.constraints.Size;

@Data
public class EduExperimentAttachmentAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联的实验提交记录ID
     */
    private Long resultId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 原始文件名
     */
    @Size(max = 255, message = "文件名长度不能超过255个字符")
    private String fileName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型（MIME类型）
     */
    @Size(max = 50, message = "文件类型长度不能超过50个字符")
    private String fileType;

    /**
     * 文件后缀
     */
    @Size(max = 20, message = "文件后缀长度不能超过20个字符")
    private String fileSuffix;

    /**
     * OBS存储路径/URL
     */
    @Size(max = 500, message = "OBS路径长度不能超过500个字符")
    private String obsUrl;

    /**
     * OBS桶名称
     */
    @Size(max = 100, message = "桶名称长度不能超过100个字符")
    private String obsBucket;

    /**
     * 上传状态：0失败，1成功
     */
    private Integer uploadStatus;
}
