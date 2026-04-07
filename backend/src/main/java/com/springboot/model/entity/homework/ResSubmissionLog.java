package com.springboot.model.entity.homework;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 学生实验提交日志表
 * @TableName res_submission_log
 */
@TableName(value ="res_submission_log")
@Data
public class ResSubmissionLog implements Serializable {
    /**
     * 日志主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 提交记录ID
     */
    @TableField(value = "result_id")
    private Long resultId;

    /**
     * 快照内容
     */
    @TableField(value = "content_snapshot")
    private String contentSnapshot;

    /**
     * 快照时间
     */
    @TableField(value = "snapshot_time")
    private Date snapshotTime;

    /**
     * 
     */
    @TableField(value = "created_at")
    private Date createdAt;

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
        ResSubmissionLog other = (ResSubmissionLog) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getResultId() == null ? other.getResultId() == null : this.getResultId().equals(other.getResultId()))
            && (this.getContentSnapshot() == null ? other.getContentSnapshot() == null : this.getContentSnapshot().equals(other.getContentSnapshot()))
            && (this.getSnapshotTime() == null ? other.getSnapshotTime() == null : this.getSnapshotTime().equals(other.getSnapshotTime()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getResultId() == null) ? 0 : getResultId().hashCode());
        result = prime * result + ((getContentSnapshot() == null) ? 0 : getContentSnapshot().hashCode());
        result = prime * result + ((getSnapshotTime() == null) ? 0 : getSnapshotTime().hashCode());
        result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
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
        sb.append(", contentSnapshot=").append(contentSnapshot);
        sb.append(", snapshotTime=").append(snapshotTime);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}


