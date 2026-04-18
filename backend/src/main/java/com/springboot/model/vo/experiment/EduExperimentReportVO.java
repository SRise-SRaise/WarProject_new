package com.springboot.model.vo.experiment;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class EduExperimentReportVO implements Serializable {

    private Long experimentId;

    private String experimentName;

    private String courseName;

    private String schedule;

    private Long studentId;

    private String studentNo;

    private String studentName;

    private String clazzNo;

    private String summaryNote;

    private String status;

    private String submittedAt;

    private String teacherScore;

    private String teacherFeedback;

    private String reviewedAt;

    private String objective;

    private String content;

    private List<ReportQuestionVO> questions;

    @Data
    public static class ReportQuestionVO implements Serializable {
        private String id;
        private String experimentItemId;
        private Integer stepNo;
        private String title;
        private Integer type;
        private String content;
        private Integer score;
        private String standardAnswer;
        private String studentAnswer;
        private String filledBlanks;
        private Integer teacherScore;
        private String teacherComment;
    }

    private static final long serialVersionUID = 1L;
}
