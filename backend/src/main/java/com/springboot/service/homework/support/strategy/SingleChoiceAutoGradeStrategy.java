package com.springboot.service.homework.support.strategy;

import com.springboot.model.dto.homework.ExerciseSubmitRequest;
import com.springboot.model.entity.homework.EduExerciseItem;

public class SingleChoiceAutoGradeStrategy implements AutoGradeStrategy {

    @Override
    public boolean supports(Integer questionType) {
        return questionType != null && (questionType == 2 || questionType == 4);
    }

    @Override
    public Integer grade(EduExerciseItem item, ExerciseSubmitRequest.AnswerItem answer) {
        Integer maxScore = item.getMaxScore() != null ? item.getMaxScore() : 0;
        String standardAnswer = item.getStandardAnswer();
        if (standardAnswer == null || standardAnswer.isEmpty()) {
            return null;
        }
        if (answer.getChoiceAnswer() != null
                && answer.getChoiceAnswer().equalsIgnoreCase(standardAnswer.trim())) {
            return maxScore;
        }
        return 0;
    }
}
