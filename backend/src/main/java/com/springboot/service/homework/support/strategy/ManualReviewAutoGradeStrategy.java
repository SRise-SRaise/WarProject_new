package com.springboot.service.homework.support.strategy;

import com.springboot.model.dto.homework.ExerciseSubmitRequest;
import com.springboot.model.entity.homework.EduExerciseItem;

public class ManualReviewAutoGradeStrategy implements AutoGradeStrategy {

    @Override
    public boolean supports(Integer questionType) {
        return true;
    }

    @Override
    public Integer grade(EduExerciseItem item, ExerciseSubmitRequest.AnswerItem answer) {
        return null;
    }
}
