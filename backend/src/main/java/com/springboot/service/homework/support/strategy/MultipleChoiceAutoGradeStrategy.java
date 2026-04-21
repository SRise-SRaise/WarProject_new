package com.springboot.service.homework.support.strategy;

import com.springboot.model.dto.homework.ExerciseSubmitRequest;
import com.springboot.model.entity.homework.EduExerciseItem;

public class MultipleChoiceAutoGradeStrategy implements AutoGradeStrategy {

    @Override
    public boolean supports(Integer questionType) {
        return questionType != null && questionType == 3;
    }

    @Override
    public Integer grade(EduExerciseItem item, ExerciseSubmitRequest.AnswerItem answer) {
        Integer maxScore = item.getMaxScore() != null ? item.getMaxScore() : 0;
        String standardAnswer = item.getStandardAnswer();
        if (standardAnswer == null || standardAnswer.isEmpty()) {
            return null;
        }
        if (answer.getChoiceAnswer() != null) {
            String sortedAnswer = answer.getChoiceAnswer().chars()
                    .sorted()
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            String sortedStandard = standardAnswer.trim().chars()
                    .sorted()
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            if (sortedAnswer.equalsIgnoreCase(sortedStandard)) {
                return maxScore;
            }
        }
        return 0;
    }
}
