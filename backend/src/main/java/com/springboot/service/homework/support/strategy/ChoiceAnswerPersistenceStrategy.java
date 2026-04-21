package com.springboot.service.homework.support.strategy;

import com.springboot.model.dto.homework.ExerciseSubmitRequest;
import com.springboot.model.entity.homework.ResExerciseRecord;

public class ChoiceAnswerPersistenceStrategy implements AnswerPersistenceStrategy {

    @Override
    public boolean supports(Integer questionType) {
        return questionType != null && questionType <= 3;
    }

    @Override
    public void apply(ResExerciseRecord record, ExerciseSubmitRequest.AnswerItem answer) {
        record.setChoiceAnswer(answer.getChoiceAnswer());
    }
}
