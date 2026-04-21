package com.springboot.service.homework.support.strategy;

import com.springboot.model.dto.homework.ExerciseSubmitRequest;
import com.springboot.model.entity.homework.EduExerciseItem;

public interface AutoGradeStrategy {

    boolean supports(Integer questionType);

    Integer grade(EduExerciseItem item, ExerciseSubmitRequest.AnswerItem answer);
}
