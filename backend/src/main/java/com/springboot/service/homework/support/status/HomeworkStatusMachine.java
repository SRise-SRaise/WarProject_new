package com.springboot.service.homework.support.status;

import com.springboot.model.entity.homework.ResExerciseRecord;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class HomeworkStatusMachine {

    private final GradingStatusMachine gradingStatusMachine = new GradingStatusMachine();

    public String resolveStudentStatus(Date endTime, List<ResExerciseRecord> records) {
        if (records == null || records.isEmpty()) {
            if (endTime != null && endTime.before(new Date())) {
                return "overdue";
            }
            return "pending";
        }
        if (gradingStatusMachine.allReviewed(records)) {
            return "reviewed";
        }
        return "submitted";
    }

    public int calculateStudentScore(String status, List<ResExerciseRecord> records) {
        if (records == null || records.isEmpty()) {
            return 0;
        }
        if ("reviewed".equals(status)) {
            return records.stream()
                    .map(ResExerciseRecord::getScore)
                    .filter(Objects::nonNull)
                    .mapToInt(Integer::intValue)
                    .sum();
        }
        if ("submitted".equals(status)) {
            return records.stream()
                    .filter(gradingStatusMachine::isAutoReviewed)
                    .map(ResExerciseRecord::getScore)
                    .filter(Objects::nonNull)
                    .mapToInt(Integer::intValue)
                    .sum();
        }
        return 0;
    }
}
