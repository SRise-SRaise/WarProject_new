package com.springboot.service.homework.support.strategy;

import cn.hutool.core.collection.CollUtil;
import com.springboot.model.dto.homework.ExerciseSubmitRequest;
import com.springboot.model.entity.homework.ResExerciseRecord;

import java.util.stream.Collectors;

public class FillBlankAnswerPersistenceStrategy implements AnswerPersistenceStrategy {

    @Override
    public boolean supports(Integer questionType) {
        return questionType != null && questionType == 4;
    }

    @Override
    public void apply(ResExerciseRecord record, ExerciseSubmitRequest.AnswerItem answer) {
        if (CollUtil.isNotEmpty(answer.getFillBlanks())) {
            String fillContent = answer.getFillBlanks().stream()
                    .map(ExerciseSubmitRequest.FillBlankAnswer::getAnswerContent)
                    .collect(Collectors.joining("|"));
            record.setTextContent(fillContent);
        }
    }
}
