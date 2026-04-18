package com.springboot.model.dto.experiment;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class EduExperimentReportSubmitRequest implements Serializable {

    private Long experimentId;

    private Long studentId;

    private List<QuestionAnswer> answers;

    @Data
    public static class QuestionAnswer implements Serializable {
        private Long itemId;
        private String answer;
        private List<String> filledBlanks;
    }

    private static final long serialVersionUID = 1L;
}
