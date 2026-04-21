package com.springboot.service.homework.support.flow;

import com.springboot.model.dto.homework.ExerciseSubmitRequest;
import com.springboot.model.entity.homework.EduExerciseItem;
import com.springboot.model.entity.homework.ResExerciseRecord;
import com.springboot.service.homework.support.strategy.AnswerPersistenceStrategyFactory;

public class SaveDraftFlow extends AbstractSubmissionFlow {

    public SaveDraftFlow(AnswerPersistenceStrategyFactory answerPersistenceStrategyFactory) {
        super(answerPersistenceStrategyFactory);
    }

    @Override
    protected void beforeScoring(ResExerciseRecord record, SubmissionProcessSummary summary) {
        // draft flow keeps existing grading fields unchanged
    }

    @Override
    protected Integer evaluateScore(EduExerciseItem item, ExerciseSubmitRequest.AnswerItem answer) {
        return null;
    }

    @Override
    protected void afterScoring(ResExerciseRecord record, Integer score, SubmissionProcessSummary summary) {
        // draft flow does not score
    }
}
