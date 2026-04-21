package com.springboot.service.homework.support.flow;

import com.springboot.model.dto.homework.ExerciseSubmitRequest;
import com.springboot.model.entity.homework.EduExerciseItem;
import com.springboot.model.entity.homework.ResExerciseRecord;
import com.springboot.service.homework.support.strategy.AnswerPersistenceStrategyFactory;
import com.springboot.service.homework.support.strategy.AutoGradeStrategyFactory;

public class SubmitAnswerFlow extends AbstractSubmissionFlow {

    private final AutoGradeStrategyFactory autoGradeStrategyFactory;

    public SubmitAnswerFlow(AnswerPersistenceStrategyFactory answerPersistenceStrategyFactory,
                            AutoGradeStrategyFactory autoGradeStrategyFactory) {
        super(answerPersistenceStrategyFactory);
        this.autoGradeStrategyFactory = autoGradeStrategyFactory;
    }

    @Override
    protected void beforeScoring(ResExerciseRecord record, SubmissionProcessSummary summary) {
        record.setGradingStatus(0);
    }

    @Override
    protected Integer evaluateScore(EduExerciseItem item, ExerciseSubmitRequest.AnswerItem answer) {
        if (item == null) {
            return null;
        }
        return autoGradeStrategyFactory.resolve(item.getQuestionType()).grade(item, answer);
    }

    @Override
    protected void afterScoring(ResExerciseRecord record, Integer score, SubmissionProcessSummary summary) {
        if (score != null) {
            record.setScore(score);
            record.setGradingStatus(1);
            summary.addAutoScore(score);
            return;
        }
        summary.increasePendingReviewCount();
    }
}
