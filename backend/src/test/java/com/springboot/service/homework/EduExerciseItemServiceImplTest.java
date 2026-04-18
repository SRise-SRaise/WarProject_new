package com.springboot.service.homework;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.springboot.model.dto.homework.EduExerciseItemQueryRequest;
import com.springboot.model.entity.homework.EduExerciseItem;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class EduExerciseItemServiceImplTest {

    @Resource
    private EduExerciseItemService eduExerciseItemService;

    @Test
    void shouldBuildFilterConditionsForExerciseItemQuery() {
        EduExerciseItemQueryRequest request = new EduExerciseItemQueryRequest();
        request.setCurrent(1);
        request.setPageSize(10);
        request.setExerciseId(123L);
        request.setQuestionType(2);
        request.setQuestion("Java");

        QueryWrapper<EduExerciseItem> queryWrapper = eduExerciseItemService.getQueryWrapper(request);
        String sqlSegment = queryWrapper.getCustomSqlSegment();

        assertTrue(sqlSegment.contains("exercise_id"));
        assertTrue(sqlSegment.contains("question_type"));
        assertTrue(sqlSegment.contains("question"));
    }
}
