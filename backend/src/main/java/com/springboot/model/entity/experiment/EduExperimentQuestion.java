package com.springboot.model.entity.experiment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 实验题库表
 * @TableName edu_experiment_question
 */
@TableName(value ="edu_experiment_question")
@Data
public class EduExperimentQuestion implements Serializable {
    /**
     * 题目主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 题目名称/标题
     */
    @TableField(value = "question_name")
    private String questionName;

    /**
     * 题目内容/题干
     */
    @TableField(value = "question_content")
    private String questionContent;

    /**
     * 题目类型：1-选择题，2-填空题，3-编程题，4-简答题
     */
    @TableField(value = "question_type")
    private Integer questionType;

    /**
     * 难度等级：1-简单，2-中等，3-困难
     */
    @TableField(value = "difficulty")
    private Integer difficulty;

    /**
     * 所属知识点/标签
     */
    @TableField(value = "tag")
    private String tag;

    /**
     * 选项内容（JSON格式，适用选择题）
     * 例如：[{"option":"A","content":"选项内容"},{"option":"B","content":"选项内容"}]
     */
    @TableField(value = "options")
    private String options;

    /**
     * 标准答案
     */
    @TableField(value = "standard_answer")
    private String standardAnswer;

    /**
     * 答案解析
     */
    @TableField(value = "answer_analysis")
    private String answerAnalysis;

    /**
     * 参考代码（适用编程题）
     */
    @TableField(value = "reference_code")
    private String referenceCode;

    /**
     * 题目分值
     */
    @TableField(value = "score")
    private Integer score;

    /**
     * 是否启用：0-禁用，1-启用
     */
    @TableField(value = "status")
    private Integer status;

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
        EduExperimentQuestion other = (EduExperimentQuestion) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getQuestionName() == null ? other.getQuestionName() == null : this.getQuestionName().equals(other.getQuestionName()))
            && (this.getQuestionContent() == null ? other.getQuestionContent() == null : this.getQuestionContent().equals(other.getQuestionContent()))
            && (this.getQuestionType() == null ? other.getQuestionType() == null : this.getQuestionType().equals(other.getQuestionType()))
            && (this.getDifficulty() == null ? other.getDifficulty() == null : this.getDifficulty().equals(other.getDifficulty()))
            && (this.getTag() == null ? other.getTag() == null : this.getTag().equals(other.getTag()))
            && (this.getOptions() == null ? other.getOptions() == null : this.getOptions().equals(other.getOptions()))
            && (this.getStandardAnswer() == null ? other.getStandardAnswer() == null : this.getStandardAnswer().equals(other.getStandardAnswer()))
            && (this.getAnswerAnalysis() == null ? other.getAnswerAnalysis() == null : this.getAnswerAnalysis().equals(other.getAnswerAnalysis()))
            && (this.getReferenceCode() == null ? other.getReferenceCode() == null : this.getReferenceCode().equals(other.getReferenceCode()))
            && (this.getScore() == null ? other.getScore() == null : this.getScore().equals(other.getScore()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getQuestionName() == null) ? 0 : getQuestionName().hashCode());
        result = prime * result + ((getQuestionContent() == null) ? 0 : getQuestionContent().hashCode());
        result = prime * result + ((getQuestionType() == null) ? 0 : getQuestionType().hashCode());
        result = prime * result + ((getDifficulty() == null) ? 0 : getDifficulty().hashCode());
        result = prime * result + ((getTag() == null) ? 0 : getTag().hashCode());
        result = prime * result + ((getOptions() == null) ? 0 : getOptions().hashCode());
        result = prime * result + ((getStandardAnswer() == null) ? 0 : getStandardAnswer().hashCode());
        result = prime * result + ((getAnswerAnalysis() == null) ? 0 : getAnswerAnalysis().hashCode());
        result = prime * result + ((getReferenceCode() == null) ? 0 : getReferenceCode().hashCode());
        result = prime * result + ((getScore() == null) ? 0 : getScore().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
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
        sb.append(", questionName=").append(questionName);
        sb.append(", questionContent=").append(questionContent);
        sb.append(", questionType=").append(questionType);
        sb.append(", difficulty=").append(difficulty);
        sb.append(", tag=").append(tag);
        sb.append(", options=").append(options);
        sb.append(", standardAnswer=").append(standardAnswer);
        sb.append(", answerAnalysis=").append(answerAnalysis);
        sb.append(", referenceCode=").append(referenceCode);
        sb.append(", score=").append(score);
        sb.append(", status=").append(status);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
