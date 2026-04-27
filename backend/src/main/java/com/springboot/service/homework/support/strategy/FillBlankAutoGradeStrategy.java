package com.springboot.service.homework.support.strategy;

import cn.hutool.core.collection.CollUtil;
import com.springboot.model.dto.homework.ExerciseSubmitRequest;
import com.springboot.model.entity.homework.EduExerciseItem;

public class FillBlankAutoGradeStrategy implements AutoGradeStrategy {

    @Override
    public boolean supports(Integer questionType) {
        return questionType != null && questionType == 1;
    }

    @Override
    public Integer grade(EduExerciseItem item, ExerciseSubmitRequest.AnswerItem answer) {
        Integer maxScore = item.getMaxScore() != null ? item.getMaxScore() : 0;
        String standardAnswer = item.getStandardAnswer();
        if (standardAnswer == null || standardAnswer.isEmpty()) {
            return null;
        }
        if (CollUtil.isNotEmpty(answer.getFillBlanks())) {
            String[] standardFills = standardAnswer.split("\\|");
            int correctCount = 0;
            for (int i = 0; i < standardFills.length && i < answer.getFillBlanks().size(); i++) {
                String studentFill = answer.getFillBlanks().get(i).getAnswerContent();
                if (studentFill != null && studentFill.trim().equalsIgnoreCase(standardFills[i].trim())) {
                    correctCount++;
                }
            }
            return maxScore * correctCount / standardFills.length;
        }
        return null;
    }
}
