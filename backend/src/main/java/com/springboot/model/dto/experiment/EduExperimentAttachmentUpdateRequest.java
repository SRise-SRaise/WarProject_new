package com.springboot.model.dto.experiment;

import java.io.Serializable;
import lombok.Data;

@Data
public class EduExperimentAttachmentUpdateRequest implements Serializable {

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
     * OBS存储路径/URL
     */
    private String obsUrl;

    /**
     * 上传状态：0失败，1成功
     */
    private Integer uploadStatus;

    /**
     * 是否删除
     */
    private Integer isDeleted;
}
