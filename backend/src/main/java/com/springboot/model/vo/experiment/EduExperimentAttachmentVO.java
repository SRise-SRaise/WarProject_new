package com.springboot.model.vo.experiment;

import com.springboot.model.entity.experiment.EduExperimentAttachment;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class EduExperimentAttachmentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 附件主键ID
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
     * 原始文件名
     */
    private String fileName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型（MIME类型）
     */
    private String fileType;

    /**
     * 文件后缀
     */
    private String fileSuffix;

    /**
     * OBS存储路径/URL
     */
    private String obsUrl;

    /**
     * OBS桶名称
     */
    private String obsBucket;

    /**
     * 上传状态：0失败，1成功
     */
    private Integer uploadStatus;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 格式化后的文件大小
     */
    private String formattedFileSize;

    public static EduExperimentAttachmentVO objToVo(EduExperimentAttachment entity) {
        if (entity == null) {
            return null;
        }
        EduExperimentAttachmentVO vo = new EduExperimentAttachmentVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setFormattedFileSize(formatFileSize(entity.getFileSize()));
        return vo;
    }

    private static String formatFileSize(Long fileSize) {
        if (fileSize == null || fileSize <= 0) {
            return "0 B";
        }
        final String[] units = {"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(fileSize) / Math.log10(1024));
        digitGroups = Math.min(digitGroups, units.length - 1);
        return String.format("%.2f %s", fileSize / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    public static List<EduExperimentAttachmentVO> objToVo(List<EduExperimentAttachment> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(EduExperimentAttachmentVO::objToVo).collect(Collectors.toList());
    }
}
