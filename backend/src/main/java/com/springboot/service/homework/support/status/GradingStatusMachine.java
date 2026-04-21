package com.springboot.service.homework.support.status;

import com.springboot.model.entity.homework.ResExerciseRecord;

import java.util.List;

public class GradingStatusMachine {

    public boolean allReviewed(List<ResExerciseRecord> records) {
        return records != null
                && !records.isEmpty()
                && records.stream().allMatch(this::isReviewed);
    }

    public boolean isReviewed(ResExerciseRecord record) {
        return record != null && record.getGradingStatus() != null && record.getGradingStatus() > 0;
    }

    public boolean isAutoReviewed(ResExerciseRecord record) {
        return record != null && record.getGradingStatus() != null && record.getGradingStatus() == 1;
    }

    public long pendingCount(List<ResExerciseRecord> records) {
        if (records == null || records.isEmpty()) {
            return 0;
        }
        return records.stream()
                .filter(record -> record.getGradingStatus() == null || record.getGradingStatus() == 0)
                .count();
    }
}
