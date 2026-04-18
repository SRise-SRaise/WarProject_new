package com.springboot.model.vo.homework;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * 学生作业视图VO
 */
@Data
public class StudentExerciseVO implements Serializable {

    /**
     * 作业ID
     */
    private Long id;

    /**
     * 作业名称
     */
    private String taskName;

    /**
     * 作业描述
     */
    private String description;

    /**
     * 开放时间
     */
    private Date startTime;

    /**
     * 截止时间
     */
    private Date endTime;

    /**
     * 作业状态：pending/submitted/reviewed/overdue
     */
    private String status;

    /**
     * 教师姓名
     */
    private String teacherName;

    /**
     * 作业总分
     */
    private Integer totalScore;

    /**
     * 学生得分
     */
    private Integer studentScore;

    /**
     * 题目数量
     */
    private Integer itemCount;

    /**
     * 发布状态：0草稿/1已发布/2已关闭
     */
    private Integer publishStatus;

    private static final long serialVersionUID = 1L;
}