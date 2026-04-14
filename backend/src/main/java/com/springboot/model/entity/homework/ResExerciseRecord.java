package com.springboot.model.entity.homework;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 学生练习答题记录表
 * @TableName res_exercise_record
 */
@TableName(value ="res_exercise_record")
@Data
public class ResExerciseRecord implements Serializable {
    /**
     * 答题记录主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属练习ID
     */
    @TableField(value = "exercise_id")
    private Long exerciseId;

    /**
     * 练习题目ID
     */
    @TableField(value = "item_id")
    private Long itemId;

    /**
     * 学生ID
     */
    @TableField(value = "student_id")
    private Long studentId;

    /**
     * 选择/判断题答案
     */
    @TableField(value = "choice_answer")
    private String choiceAnswer;

    /**
     * 编程/简答作答内容
     */
    @TableField(value = "text_content")
    private String textContent;

    /**
     * 得分
     */
    @TableField(value = "score")
    private Integer score;

    /**
     * 提交时间
     */
    @TableField(value = "submitted_at")
    private Date submittedAt;

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
        ResExerciseRecord other = (ResExerciseRecord) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getExerciseId() == null ? other.getExerciseId() == null : this.getExerciseId().equals(other.getExerciseId()))
            && (this.getItemId() == null ? other.getItemId() == null : this.getItemId().equals(other.getItemId()))
            && (this.getStudentId() == null ? other.getStudentId() == null : this.getStudentId().equals(other.getStudentId()))
            && (this.getChoiceAnswer() == null ? other.getChoiceAnswer() == null : this.getChoiceAnswer().equals(other.getChoiceAnswer()))
            && (this.getTextContent() == null ? other.getTextContent() == null : this.getTextContent().equals(other.getTextContent()))
            && (this.getScore() == null ? other.getScore() == null : this.getScore().equals(other.getScore()))
            && (this.getSubmittedAt() == null ? other.getSubmittedAt() == null : this.getSubmittedAt().equals(other.getSubmittedAt()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getExerciseId() == null) ? 0 : getExerciseId().hashCode());
        result = prime * result + ((getItemId() == null) ? 0 : getItemId().hashCode());
        result = prime * result + ((getStudentId() == null) ? 0 : getStudentId().hashCode());
        result = prime * result + ((getChoiceAnswer() == null) ? 0 : getChoiceAnswer().hashCode());
        result = prime * result + ((getTextContent() == null) ? 0 : getTextContent().hashCode());
        result = prime * result + ((getScore() == null) ? 0 : getScore().hashCode());
        result = prime * result + ((getSubmittedAt() == null) ? 0 : getSubmittedAt().hashCode());
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
        sb.append(", exerciseId=").append(exerciseId);
        sb.append(", itemId=").append(itemId);
        sb.append(", studentId=").append(studentId);
        sb.append(", choiceAnswer=").append(choiceAnswer);
        sb.append(", textContent=").append(textContent);
        sb.append(", score=").append(score);
        sb.append(", submittedAt=").append(submittedAt);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
