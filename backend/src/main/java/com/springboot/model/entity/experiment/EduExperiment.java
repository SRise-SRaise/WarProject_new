package com.springboot.model.entity.experiment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 实验项目表
 * @TableName t_experiment
 */
@TableName(value ="t_experiment")
@Data
public class EduExperiment implements Serializable {
    /**
     * 实验项目主键ID
     */
    @TableId(value = "experiment_id", type = IdType.AUTO)
    private Long id;

    /**
     * 实验序号(排序)
     */
    @TableField(value = "experiment_no")
    private Integer sortOrder;

    /**
     * 实验名称
     */
    @TableField(value = "experiment_name")
    private String name;

    /**
     * 实验类型
     */
    @TableField(value = "experiment_type")
    private Integer categoryId;

    /**
     * 指导书文件类型
     */
    @TableField(value = "instruction_type")
    private String fileType;

    /**
     * 实验要求
     */
    @TableField(value = "experiment_requirement")
    private String requirement;

    /**
     * 实验内容描述
     */
    @TableField(value = "experiment_content")
    private String contentDesc;

    /**
     * 发布状态
     */
    @TableField(value = "state")
    private Integer publishStatus;

    /**
     * 指导书/附件文件访问路径
     * 注意：需要先执行 V003__add_instruction_url_column.sql 迁移脚本
     */
    @TableField(value = "instruction_url")
    private String instructionUrl;

    /**
     * 
     */
    @TableField(exist = false)
    private Date createdAt;

    /**
     * 
     */
    @TableField(exist = false)
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
        EduExperiment other = (EduExperiment) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSortOrder() == null ? other.getSortOrder() == null : this.getSortOrder().equals(other.getSortOrder()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getCategoryId() == null ? other.getCategoryId() == null : this.getCategoryId().equals(other.getCategoryId()))
            && (this.getFileType() == null ? other.getFileType() == null : this.getFileType().equals(other.getFileType()))
            && (this.getRequirement() == null ? other.getRequirement() == null : this.getRequirement().equals(other.getRequirement()))
            && (this.getContentDesc() == null ? other.getContentDesc() == null : this.getContentDesc().equals(other.getContentDesc()))
            && (this.getPublishStatus() == null ? other.getPublishStatus() == null : this.getPublishStatus().equals(other.getPublishStatus()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSortOrder() == null) ? 0 : getSortOrder().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getCategoryId() == null) ? 0 : getCategoryId().hashCode());
        result = prime * result + ((getFileType() == null) ? 0 : getFileType().hashCode());
        result = prime * result + ((getRequirement() == null) ? 0 : getRequirement().hashCode());
        result = prime * result + ((getContentDesc() == null) ? 0 : getContentDesc().hashCode());
        result = prime * result + ((getPublishStatus() == null) ? 0 : getPublishStatus().hashCode());
        result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
        result = prime * result + ((getUpdatedAt() == null) ? 0 : getUpdatedAt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sortOrder=").append(sortOrder);
        sb.append(", name=").append(name);
        sb.append(", categoryId=").append(categoryId);
        sb.append(", fileType=").append(fileType);
        sb.append(", requirement=").append(requirement);
        sb.append(", contentDesc=").append(contentDesc);
        sb.append(", publishStatus=").append(publishStatus);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}


