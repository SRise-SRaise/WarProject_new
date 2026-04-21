package com.springboot.service.homework.support.strategy;

import com.springboot.model.dto.homework.ExerciseSubmitRequest;
import com.springboot.model.entity.homework.ResExerciseRecord;

public interface AnswerPersistenceStrategy {

    boolean supports(Integer questionType);

    void apply(ResExerciseRecord record, ExerciseSubmitRequest.AnswerItem answer);
}
