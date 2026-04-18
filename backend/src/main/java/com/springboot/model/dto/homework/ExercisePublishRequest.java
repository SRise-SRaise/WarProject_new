package com.springboot.model.dto.homework;

import java.util.Date;
import java.util.List;
import java.io.Serializable;
import lombok.Data;

/**
 * 作业发布请求DTO
 */
@Data
public class ExercisePublishRequest implements Serializable {

    /**
     * 作业ID
     */
    private Long exerciseId;

    /**
     * 发布班级列表
     */
    private List<String> classCodes;

    /**
     * 开放时间
     */
    private Date startTime;

    /**
     * 截止时间
     */
    private Date endTime;

    /**
     * 是否允许补交
     */
    private Boolean allowLate;

    private static final long serialVersionUID = 1L;
}