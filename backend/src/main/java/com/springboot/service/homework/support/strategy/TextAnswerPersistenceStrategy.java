package com.springboot.service.homework.support.strategy;

import com.springboot.model.dto.homework.ExerciseSubmitRequest;
import com.springboot.model.entity.homework.ResExerciseRecord;

public class TextAnswerPersistenceStrategy implements AnswerPersistenceStrategy {

    @Override
    public boolean supports(Integer questionType) {
        return true;
    }

    @Override
    public void apply(ResExerciseRecord record, ExerciseSubmitRequest.AnswerItem answer) {
        record.setTextContent(answer.getTextContent());
    }
}
