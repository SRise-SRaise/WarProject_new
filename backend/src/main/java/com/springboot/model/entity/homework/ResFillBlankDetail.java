package com.springboot.model.entity.homework;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 学生填空答案表
 * @TableName res_fill_blank_detail
 */
@TableName(value ="res_fill_blank_detail")
@Data
public class ResFillBlankDetail implements Serializable {
    /**
     * 答案记录主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 实验子项目ID
     */
    @TableField(value = "item_id")
    private Long itemId;

    /**
     * 填空号
     */
    @TableField(value = "blank_index")
    private Integer blankIndex;

    /**
     * 填写的答案内容
     */
    @TableField(value = "answer_content")
    private String answerContent;

    /**
     * 答案MD5
     */
    @TableField(value = "answer_hash")
    private String answerHash;

    /**
     * 提交次数
     */
    @TableField(value = "submit_count")
    private Integer submitCount;

    /**
     * 是否正确
     */
    @TableField(value = "is_correct")
    private Boolean isCorrect;

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
        ResFillBlankDetail other = (ResFillBlankDetail) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getItemId() == null ? other.getItemId() == null : this.getItemId().equals(other.getItemId()))
            && (this.getBlankIndex() == null ? other.getBlankIndex() == null : this.getBlankIndex().equals(other.getBlankIndex()))
            && (this.getAnswerContent() == null ? other.getAnswerContent() == null : this.getAnswerContent().equals(other.getAnswerContent()))
            && (this.getAnswerHash() == null ? other.getAnswerHash() == null : this.getAnswerHash().equals(other.getAnswerHash()))
            && (this.getSubmitCount() == null ? other.getSubmitCount() == null : this.getSubmitCount().equals(other.getSubmitCount()))
            && (this.getIsCorrect() == null ? other.getIsCorrect() == null : this.getIsCorrect().equals(other.getIsCorrect()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getItemId() == null) ? 0 : getItemId().hashCode());
        result = prime * result + ((getBlankIndex() == null) ? 0 : getBlankIndex().hashCode());
        result = prime * result + ((getAnswerContent() == null) ? 0 : getAnswerContent().hashCode());
        result = prime * result + ((getAnswerHash() == null) ? 0 : getAnswerHash().hashCode());
        result = prime * result + ((getSubmitCount() == null) ? 0 : getSubmitCount().hashCode());
        result = prime * result + ((getIsCorrect() == null) ? 0 : getIsCorrect().hashCode());
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
        sb.append(", itemId=").append(itemId);
        sb.append(", blankIndex=").append(blankIndex);
        sb.append(", answerContent=").append(answerContent);
        sb.append(", answerHash=").append(answerHash);
        sb.append(", submitCount=").append(submitCount);
        sb.append(", isCorrect=").append(isCorrect);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}


