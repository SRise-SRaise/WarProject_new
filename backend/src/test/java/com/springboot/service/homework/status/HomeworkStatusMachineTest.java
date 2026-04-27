package com.springboot.service.homework.status;

import com.springboot.model.entity.homework.ResExerciseRecord;
import com.springboot.service.homework.support.status.HomeworkStatusMachine;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HomeworkStatusMachineTest {

    private final HomeworkStatusMachine machine = new HomeworkStatusMachine();

    @Test
    void shouldReturnPendingWhenNoRecordsAndNotOverdue() {
        String status = machine.resolveStudentStatus(new Date(System.currentTimeMillis() + 3600_000L), List.of());
        assertEquals("pending", status);
    }

    @Test
    void shouldReturnOverdueWhenNoRecordsAndEndTimePassed() {
        String status = machine.resolveStudentStatus(new Date(System.currentTimeMillis() - 3600_000L), List.of());
        assertEquals("overdue", status);
    }

    @Test
    void shouldReturnSubmittedWhenNotAllReviewed() {
        ResExerciseRecord auto = record(1, 10);
        ResExerciseRecord manualPending = record(0, null);

        String status = machine.resolveStudentStatus(null, List.of(auto, manualPending));

        assertEquals("submitted", status);
        assertEquals(10, machine.calculateStudentScore(status, List.of(auto, manualPending)));
    }

    @Test
    void shouldReturnReviewedWhenAllReviewed() {
        ResExerciseRecord a = record(1, 10);
        ResExerciseRecord b = record(2, 18);

        String status = machine.resolveStudentStatus(null, List.of(a, b));

        assertEquals("reviewed", status);
        assertEquals(28, machine.calculateStudentScore(status, List.of(a, b)));
    }

    private static ResExerciseRecord record(Integer gradingStatus, Integer score) {
        ResExerciseRecord record = new ResExerciseRecord();
        record.setGradingStatus(gradingStatus);
        record.setScore(score);
        return record;
    }
}
