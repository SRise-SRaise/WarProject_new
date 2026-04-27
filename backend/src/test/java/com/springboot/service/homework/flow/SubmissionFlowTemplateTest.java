package com.springboot.service.homework.flow;

import com.springboot.model.dto.homework.ExerciseSubmitRequest;
import com.springboot.model.entity.homework.EduExerciseItem;
import com.springboot.model.entity.homework.ResExerciseRecord;
import com.springboot.service.homework.support.flow.SaveDraftFlow;
import com.springboot.service.homework.support.flow.SubmissionProcessSummary;
import com.springboot.service.homework.support.flow.SubmitAnswerFlow;
import com.springboot.service.homework.support.strategy.AnswerPersistenceStrategyFactory;
import com.springboot.service.homework.support.strategy.AutoGradeStrategyFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class SubmissionFlowTemplateTest {

    private final SubmitAnswerFlow submitFlow = new SubmitAnswerFlow(new AnswerPersistenceStrategyFactory(), new AutoGradeStrategyFactory());
    private final SaveDraftFlow draftFlow = new SaveDraftFlow(new AnswerPersistenceStrategyFactory());

    @Test
    void shouldSetAutoScoreAndReviewedStatusWhenSubmitObjectiveAnswer() {
        EduExerciseItem item = new EduExerciseItem();
        item.setQuestionType(2);
        item.setStandardAnswer("A");
        item.setMaxScore(10);

        ExerciseSubmitRequest.AnswerItem answer = new ExerciseSubmitRequest.AnswerItem();
        answer.setQuestionType(2);
        answer.setChoiceAnswer("A");

        ResExerciseRecord record = new ResExerciseRecord();
        SubmissionProcessSummary summary = new SubmissionProcessSummary();

        submitFlow.process(record, item, answer, summary);

        assertEquals(10, record.getScore());
        assertEquals(1, record.getGradingStatus());
        assertEquals(10, summary.getAutoScore());
        assertEquals(0, summary.getPendingReviewCount());
        assertNotNull(record.getSubmittedAt());
    }

    @Test
    void shouldIncreasePendingCountWhenSubmitEssayAnswer() {
        EduExerciseItem item = new EduExerciseItem();
        item.setQuestionType(5);
        item.setMaxScore(20);

        ExerciseSubmitRequest.AnswerItem answer = new ExerciseSubmitRequest.AnswerItem();
        answer.setQuestionType(5);
        answer.setTextContent("主观题作答");

        ResExerciseRecord record = new ResExerciseRecord();
        SubmissionProcessSummary summary = new SubmissionProcessSummary();

        submitFlow.process(record, item, answer, summary);

        assertNull(record.getScore());
        assertEquals(0, record.getGradingStatus());
        assertEquals(1, summary.getPendingReviewCount());
        assertEquals(0, summary.getAutoScore());
    }

    @Test
    void shouldKeepScoreAndGradingStatusUnchangedWhenSaveDraft() {
        ExerciseSubmitRequest.AnswerItem answer = new ExerciseSubmitRequest.AnswerItem();
        answer.setQuestionType(2);
        answer.setChoiceAnswer("B");

        ResExerciseRecord record = new ResExerciseRecord();
        record.setScore(7);
        record.setGradingStatus(2);
        SubmissionProcessSummary summary = new SubmissionProcessSummary();

        draftFlow.process(record, null, answer, summary);

        assertEquals("B", record.getChoiceAnswer());
        assertEquals(7, record.getScore());
        assertEquals(2, record.getGradingStatus());
        assertNotNull(record.getSubmittedAt());
    }
}
