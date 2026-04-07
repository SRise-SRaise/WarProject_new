package com.springboot.model.entity.exam;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 试卷表
 * @TableName edu_paper
 */
@TableName(value ="edu_paper")
@Data
public class EduPaper implements Serializable {
    /**
     * 试卷主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 试卷序号
     */
    @TableField(value = "paper_code")
    private Integer paperCode;

    /**
     * 试卷名称
     */
    @TableField(value = "paper_name")
    private String paperName;

    /**
     * 试卷描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 试卷生成时间
     */
    @TableField(value = "generation_time")
    private Date generationTime;

    /**
     * 
     */
    @TableField(value = "created_at")
    private Date createdAt;

    /**
     * 
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
        EduPaper other = (EduPaper) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getPaperCode() == null ? other.getPaperCode() == null : this.getPaperCode().equals(other.getPaperCode()))
            && (this.getPaperName() == null ? other.getPaperName() == null : this.getPaperName().equals(other.getPaperName()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getGenerationTime() == null ? other.getGenerationTime() == null : this.getGenerationTime().equals(other.getGenerationTime()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getPaperCode() == null) ? 0 : getPaperCode().hashCode());
        result = prime * result + ((getPaperName() == null) ? 0 : getPaperName().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getGenerationTime() == null) ? 0 : getGenerationTime().hashCode());
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
        sb.append(", paperCode=").append(paperCode);
        sb.append(", paperName=").append(paperName);
        sb.append(", description=").append(description);
        sb.append(", generationTime=").append(generationTime);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}


