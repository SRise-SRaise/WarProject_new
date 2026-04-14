package com.springboot.model.entity.exam;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 学生考试答题表
 * @TableName res_exam_record
 */
@TableName(value ="res_exam_record")
@Data
public class ResExamRecord implements Serializable {
    /**
     * 考试答题主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 考试ID
     */
    @TableField(value = "exam_id")
    private Long examId;

    /**
     * 试卷ID
     */
    @TableField(value = "paper_id")
    private Long paperId;

    /**
     * 试卷题目关联ID
     */
    @TableField(value = "paper_question_id")
    private Long paperQuestionId;

    /**
     * 学生ID
     */
    @TableField(value = "student_id")
    private Long studentId;

    /**
     * 题目ID
     */
    @TableField(value = "question_id")
    private Long questionId;

    /**
     * 填写的答案
     */
    @TableField(value = "student_answer")
    private String studentAnswer;

    /**
     * 本题得分
     */
    @TableField(value = "score")
    private Integer score;

    /**
     * 批改状态：0=未批改, 1=系统自动判分, 2=教师已批改
     */
    @TableField(value = "grading_status")
    private Integer gradingStatus;

    /**
     * 教师评语
     */
    @TableField(value = "comment")
    private String comment;

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
        ResExamRecord other = (ResExamRecord) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getExamId() == null ? other.getExamId() == null : this.getExamId().equals(other.getExamId()))
            && (this.getPaperId() == null ? other.getPaperId() == null : this.getPaperId().equals(other.getPaperId()))
            && (this.getPaperQuestionId() == null ? other.getPaperQuestionId() == null : this.getPaperQuestionId().equals(other.getPaperQuestionId()))
            && (this.getStudentId() == null ? other.getStudentId() == null : this.getStudentId().equals(other.getStudentId()))
            && (this.getQuestionId() == null ? other.getQuestionId() == null : this.getQuestionId().equals(other.getQuestionId()))
            && (this.getStudentAnswer() == null ? other.getStudentAnswer() == null : this.getStudentAnswer().equals(other.getStudentAnswer()))
            && (this.getScore() == null ? other.getScore() == null : this.getScore().equals(other.getScore()))
            && (this.getGradingStatus() == null ? other.getGradingStatus() == null : this.getGradingStatus().equals(other.getGradingStatus()))
            && (this.getComment() == null ? other.getComment() == null : this.getComment().equals(other.getComment()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getExamId() == null) ? 0 : getExamId().hashCode());
        result = prime * result + ((getPaperId() == null) ? 0 : getPaperId().hashCode());
        result = prime * result + ((getPaperQuestionId() == null) ? 0 : getPaperQuestionId().hashCode());
        result = prime * result + ((getStudentId() == null) ? 0 : getStudentId().hashCode());
        result = prime * result + ((getQuestionId() == null) ? 0 : getQuestionId().hashCode());
        result = prime * result + ((getStudentAnswer() == null) ? 0 : getStudentAnswer().hashCode());
        result = prime * result + ((getScore() == null) ? 0 : getScore().hashCode());
        result = prime * result + ((getGradingStatus() == null) ? 0 : getGradingStatus().hashCode());
        result = prime * result + ((getComment() == null) ? 0 : getComment().hashCode());
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
        sb.append(", examId=").append(examId);
        sb.append(", paperId=").append(paperId);
        sb.append(", paperQuestionId=").append(paperQuestionId);
        sb.append(", studentId=").append(studentId);
        sb.append(", questionId=").append(questionId);
        sb.append(", studentAnswer=").append(studentAnswer);
        sb.append(", score=").append(score);
        sb.append(", gradingStatus=").append(gradingStatus);
        sb.append(", comment=").append(comment);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
