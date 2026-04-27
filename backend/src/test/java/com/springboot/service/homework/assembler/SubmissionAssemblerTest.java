package com.springboot.service.homework.assembler;

import com.springboot.model.entity.homework.EduExercise;
import com.springboot.model.entity.homework.EduExerciseItem;
import com.springboot.model.entity.homework.ResExerciseRecord;
import com.springboot.model.entity.user.AuthStudent;
import com.springboot.model.vo.homework.StudentScoreVO;
import com.springboot.model.vo.homework.SubmissionDetailVO;
import com.springboot.service.homework.support.assembler.StudentScoreAssembler;
import com.springboot.service.homework.support.assembler.SubmissionDetailAssembler;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubmissionAssemblerTest {

    private final StudentScoreAssembler scoreAssembler = new StudentScoreAssembler();
    private final SubmissionDetailAssembler detailAssembler = new SubmissionDetailAssembler();

    @Test
    void shouldAssembleStudentScoreView() {
        EduExercise exercise = new EduExercise();
        exercise.setId(1L);
        exercise.setTaskName("作业A");

        AuthStudent student = new AuthStudent();
        student.setId(2L);
        student.setStudentName("学生甲");

        EduExerciseItem item = new EduExerciseItem();
        item.setId(10L);
        item.setQuestion("题目1");
        item.setQuestionType(2);
        item.setStandardAnswer("A");
        item.setMaxScore(10);

        ResExerciseRecord record = new ResExerciseRecord();
        record.setItemId(10L);
        record.setChoiceAnswer("A");
        record.setScore(10);
        record.setGradingStatus(1);

        StudentScoreVO vo = scoreAssembler.assemble(exercise, student, List.of(item), List.of(record));

        assertEquals(1L, vo.getExerciseId());
        assertEquals("作业A", vo.getExerciseName());
        assertEquals("reviewed", vo.getStatus());
        assertEquals(10, vo.getTotalScore());
        assertEquals(1, vo.getItems().size());
    }

    @Test
    void shouldAssembleSubmissionDetailView() {
        EduExercise exercise = new EduExercise();
        exercise.setId(1L);
        exercise.setTaskName("作业A");

        AuthStudent student = new AuthStudent();
        student.setId(2L);
        student.setStudentName("学生甲");
        student.setClassCode("CS2401");

        EduExerciseItem item = new EduExerciseItem();
        item.setId(11L);
        item.setQuestion("简答题");
        item.setQuestionType(5);
        item.setMaxScore(20);
        item.setStandardAnswer("标准答案");

        ResExerciseRecord record = new ResExerciseRecord();
        record.setId(100L);
        record.setItemId(11L);
        record.setTextContent("学生答案");
        record.setScore(18);
        record.setGradingStatus(2);

        SubmissionDetailVO vo = detailAssembler.assemble(exercise, student, List.of(item), List.of(record));

        assertEquals(1L, vo.getExerciseId());
        assertEquals("学生甲", vo.getStudentName());
        assertEquals(18, vo.getTotalScore());
        assertNotNull(vo.getAnswers());
        assertEquals(1, vo.getAnswers().size());
    }
}
