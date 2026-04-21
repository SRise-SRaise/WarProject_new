package com.springboot.service.homework.support.flow;

import com.springboot.model.dto.homework.ExerciseSubmitRequest;
import com.springboot.model.entity.homework.EduExerciseItem;
import com.springboot.model.entity.homework.ResExerciseRecord;
import com.springboot.service.homework.support.strategy.AnswerPersistenceStrategyFactory;

import java.util.Date;

public abstract class AbstractSubmissionFlow {

    private final AnswerPersistenceStrategyFactory answerPersistenceStrategyFactory;

    protected AbstractSubmissionFlow(AnswerPersistenceStrategyFactory answerPersistenceStrategyFactory) {
        this.answerPersistenceStrategyFactory = answerPersistenceStrategyFactory;
    }

    public final void process(ResExerciseRecord record,
                              EduExerciseItem item,
                              ExerciseSubmitRequest.AnswerItem answer,
                              SubmissionProcessSummary summary) {
        answerPersistenceStrategyFactory.resolve(answer.getQuestionType()).apply(record, answer);
        record.setSubmittedAt(new Date());
        beforeScoring(record, summary);
        Integer score = evaluateScore(item, answer);
        afterScoring(record, score, summary);
        summary.increaseSubmittedCount();
    }

    protected abstract void beforeScoring(ResExerciseRecord record, SubmissionProcessSummary summary);

    protected abstract Integer evaluateScore(EduExerciseItem item, ExerciseSubmitRequest.AnswerItem answer);

    protected abstract void afterScoring(ResExerciseRecord record, Integer score, SubmissionProcessSummary summary);
}
