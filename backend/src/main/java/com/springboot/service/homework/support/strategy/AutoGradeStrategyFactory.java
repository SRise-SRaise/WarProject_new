package com.springboot.service.homework.support.strategy;

import java.util.List;

public class AutoGradeStrategyFactory {

    private final List<AutoGradeStrategy> strategies = List.of(
            new SingleChoiceAutoGradeStrategy(),
            new MultipleChoiceAutoGradeStrategy(),
            new FillBlankAutoGradeStrategy(),
            new ManualReviewAutoGradeStrategy()
    );

    public AutoGradeStrategy resolve(Integer questionType) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(questionType))
                .findFirst()
                .orElseGet(ManualReviewAutoGradeStrategy::new);
    }
}
