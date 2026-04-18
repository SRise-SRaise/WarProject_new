package com.springboot.model.dto.experiment;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class EduExperimentReportGradeRequest implements Serializable {

    private Long experimentId;

    private Long studentId;

    private List<QuestionScore> scores;

    private Integer totalScore;

    private String feedback;

    @Data
    public static class QuestionScore implements Serializable {
        private Long itemId;
        private Integer score;
        private String comment;
    }

    private static final long serialVersionUID = 1L;
}
