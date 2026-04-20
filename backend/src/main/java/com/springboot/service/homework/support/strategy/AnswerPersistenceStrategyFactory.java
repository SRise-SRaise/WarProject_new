package com.springboot.service.homework.support.strategy;

import java.util.List;

public class AnswerPersistenceStrategyFactory {

    private final List<AnswerPersistenceStrategy> strategies = List.of(
            new ChoiceAnswerPersistenceStrategy(),
            new FillBlankAnswerPersistenceStrategy(),
            new TextAnswerPersistenceStrategy()
    );

    public AnswerPersistenceStrategy resolve(Integer questionType) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(questionType))
                .findFirst()
                .orElseGet(TextAnswerPersistenceStrategy::new);
    }
}
