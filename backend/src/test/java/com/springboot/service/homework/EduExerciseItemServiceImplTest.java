package com.springboot.service.homework;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.springboot.exception.BusinessException;
import com.springboot.model.dto.homework.EduExerciseItemQueryRequest;
import com.springboot.model.entity.homework.EduExerciseItem;
import com.springboot.service.homework.impl.EduExerciseItemServiceImpl;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EduExerciseItemServiceImplTest {

    private final EduExerciseItemService eduExerciseItemService = new EduExerciseItemServiceImpl();

    @Test
    void shouldBuildFilterConditionsForExerciseItemQuery() {
        EduExerciseItemQueryRequest request = new EduExerciseItemQueryRequest();
        setField(request, "current", 1L);
        setField(request, "pageSize", 10L);
        setField(request, "exerciseId", 123L);
        setField(request, "questionType", 2);
        setField(request, "question", "Java");

        QueryWrapper<EduExerciseItem> queryWrapper = eduExerciseItemService.getQueryWrapper(request);
        String sqlSegment = queryWrapper.getCustomSqlSegment();

        assertTrue(sqlSegment.contains("exercise_id"));
        assertTrue(sqlSegment.contains("question_type"));
        assertTrue(sqlSegment.contains("question"));
    }

    @Test
    void shouldRejectMissingExerciseIdWhenAddingExerciseItem() {
        EduExerciseItem item = new EduExerciseItem();
        setField(item, "question", "Java 的访问修饰符有哪些？");
        setField(item, "questionType", 2);
        setField(item, "standardAnswer", "A");

        assertThrows(BusinessException.class, () -> eduExerciseItemService.validEduExerciseItem(item, true));
    }

    private static void setField(Object target, String fieldName, Object value) {
        Class<?> currentClass = target.getClass();
        while (currentClass != null) {
            try {
                Field field = currentClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
                return;
            } catch (NoSuchFieldException ignored) {
                currentClass = currentClass.getSuperclass();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalArgumentException("Field not found: " + fieldName);
    }
}
