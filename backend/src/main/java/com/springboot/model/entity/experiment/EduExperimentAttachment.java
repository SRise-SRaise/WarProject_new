package com.springboot.model.entity.experiment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 实验附件表
 * @TableName edu_experiment_attachment
 */
@TableName(value = "edu_experiment_attachment")
@Data
public class EduExperimentAttachment implements Serializable {

    /**
     * 附件主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联的实验提交记录ID
     */
    @TableField(value = "result_id")
    private Long resultId;

    /**
     * 学生ID
     */
    @TableField(value = "student_id")
    private Long studentId;

    /**
     * 原始文件名
     */
    @TableField(value = "file_name")
    private String fileName;

    /**
     * 文件大小（字节）
     */
    @TableField(value = "file_size")
    private Long fileSize;

    /**
     * 文件类型（MIME类型）
     */
    @TableField(value = "file_type")
    private String fileType;

    /**
     * 文件后缀
     */
    @TableField(value = "file_suffix")
    private String fileSuffix;

    /**
     * OBS存储路径/URL
     */
    @TableField(value = "obs_url")
    private String obsUrl;

    /**
     * OBS桶名称
     */
    @TableField(value = "obs_bucket")
    private String obsBucket;

    /**
     * 上传状态：0失败，1成功
     */
    @TableField(value = "upload_status")
    private Integer uploadStatus;

    /**
     * 逻辑删除：0未删除，1已删除
     */
    @TableField(value = "is_deleted")
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @TableField(value = "created_at")
    private Date createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at")
    private Date updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        EduExperimentAttachment other = (EduExperimentAttachment) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getResultId() == null ? other.getResultId() == null : this.getResultId().equals(other.getResultId()))
            && (this.getStudentId() == null ? other.getStudentId() == null : this.getStudentId().equals(other.getStudentId()))
            && (this.getFileName() == null ? other.getFileName() == null : this.getFileName().equals(other.getFileName()))
            && (this.getObsUrl() == null ? other.getObsUrl() == null : this.getObsUrl().equals(other.getObsUrl()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getResultId() == null) ? 0 : getResultId().hashCode());
        result = prime * result + ((getStudentId() == null) ? 0 : getStudentId().hashCode());
        result = prime * result + ((getFileName() == null) ? 0 : getFileName().hashCode());
        result = prime * result + ((getObsUrl() == null) ? 0 : getObsUrl().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", resultId=").append(resultId);
        sb.append(", studentId=").append(studentId);
        sb.append(", fileName=").append(fileName);
        sb.append(", fileSize=").append(fileSize);
        sb.append(", obsUrl=").append(obsUrl);
        sb.append(", uploadStatus=").append(uploadStatus);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
