package com.springboot.service.homework.strategy;

import com.springboot.model.dto.homework.ExerciseSubmitRequest;
import com.springboot.model.entity.homework.EduExerciseItem;
import com.springboot.model.entity.homework.ResExerciseRecord;
import com.springboot.service.homework.support.strategy.AnswerPersistenceStrategyFactory;
import com.springboot.service.homework.support.strategy.AutoGradeStrategyFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HomeworkQuestionStrategyFactoryTest {

    private final AnswerPersistenceStrategyFactory answerFactory = new AnswerPersistenceStrategyFactory();
    private final AutoGradeStrategyFactory gradeFactory = new AutoGradeStrategyFactory();

    @Test
    void shouldPersistChoiceAnswerForChoiceQuestionTypes() {
        ExerciseSubmitRequest.AnswerItem answer = new ExerciseSubmitRequest.AnswerItem();
        answer.setQuestionType(2);
        answer.setChoiceAnswer("A");

        ResExerciseRecord record = new ResExerciseRecord();
        answerFactory.resolve(answer.getQuestionType()).apply(record, answer);

        assertEquals("A", record.getChoiceAnswer());
        assertNull(record.getTextContent());
    }

    @Test
    void shouldPersistFillBlankAnswerAsPipeSeparatedTextForTypeFour() {
        ExerciseSubmitRequest.AnswerItem answer = new ExerciseSubmitRequest.AnswerItem();
        answer.setQuestionType(4);
        answer.setFillBlanks(List.of(fill("Java"), fill("17")));

        ResExerciseRecord record = new ResExerciseRecord();
        answerFactory.resolve(answer.getQuestionType()).apply(record, answer);

        assertEquals("Java|17", record.getTextContent());
        assertNull(record.getChoiceAnswer());
    }

    @Test
    void shouldAutoGradeSingleChoiceIgnoringCaseForTypeTwo() {
        EduExerciseItem item = item(2, "A", 10);
        ExerciseSubmitRequest.AnswerItem answer = new ExerciseSubmitRequest.AnswerItem();
        answer.setChoiceAnswer("a");

        Integer score = gradeFactory.resolve(item.getQuestionType()).grade(item, answer);

        assertEquals(10, score);
    }

    @Test
    void shouldAutoGradeMultipleChoiceIgnoringOrderForTypeThree() {
        EduExerciseItem item = item(3, "ACD", 15);
        ExerciseSubmitRequest.AnswerItem answer = new ExerciseSubmitRequest.AnswerItem();
        answer.setChoiceAnswer("DCA");

        Integer score = gradeFactory.resolve(item.getQuestionType()).grade(item, answer);

        assertEquals(15, score);
    }

    @Test
    void shouldAutoGradeFillBlankByCorrectRatioForTypeOne() {
        EduExerciseItem item = item(1, "封装|继承|多态", 30);
        ExerciseSubmitRequest.AnswerItem answer = new ExerciseSubmitRequest.AnswerItem();
        answer.setFillBlanks(List.of(fill("封装"), fill("继承"), fill("错误答案")));

        Integer score = gradeFactory.resolve(item.getQuestionType()).grade(item, answer);

        assertEquals(20, score);
    }

    @Test
    void shouldReturnNullForManualReviewQuestionTypes() {
        EduExerciseItem item = item(5, "", 20);
        ExerciseSubmitRequest.AnswerItem answer = new ExerciseSubmitRequest.AnswerItem();
        answer.setTextContent("主观题回答");

        Integer score = gradeFactory.resolve(item.getQuestionType()).grade(item, answer);

        assertNull(score);
    }

    private static ExerciseSubmitRequest.FillBlankAnswer fill(String content) {
        ExerciseSubmitRequest.FillBlankAnswer fill = new ExerciseSubmitRequest.FillBlankAnswer();
        fill.setAnswerContent(content);
        return fill;
    }

    private static EduExerciseItem item(Integer type, String standardAnswer, Integer maxScore) {
        EduExerciseItem item = new EduExerciseItem();
        item.setQuestionType(type);
        item.setStandardAnswer(standardAnswer);
        item.setMaxScore(maxScore);
        return item;
    }
}
