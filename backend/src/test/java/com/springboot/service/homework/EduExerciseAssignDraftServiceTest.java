package com.springboot.service.homework;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.springboot.mapper.homework.EduExerciseMapper;
import com.springboot.mapper.homework.RelExerciseClassMapper;
import com.springboot.mapper.user.AuthClassMapper;
import com.springboot.model.dto.homework.ExercisePublishRequest;
import com.springboot.model.entity.homework.EduExercise;
import com.springboot.model.entity.homework.RelExerciseClass;
import com.springboot.model.entity.user.AuthClass;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class EduExerciseAssignDraftServiceTest {

    @Resource
    private EduExerciseService eduExerciseService;

    @Resource
    private EduExerciseMapper eduExerciseMapper;

    @Resource
    private RelExerciseClassMapper relExerciseClassMapper;

    @Resource
    private AuthClassMapper authClassMapper;

    private Long createdExerciseId;
    private static final String CLASS_CODE = "TST01";

    @AfterEach
    void cleanup() {
        if (createdExerciseId != null) {
            relExerciseClassMapper.delete(new QueryWrapper<RelExerciseClass>().eq("exercise_id", createdExerciseId));
            eduExerciseMapper.deleteById(createdExerciseId);
        }
        authClassMapper.deleteById(CLASS_CODE);
    }

    @Test
    void shouldSaveAssignDraftWithoutPublishing() {
        EduExercise exercise = new EduExercise();
        exercise.setTaskName("草稿布置保存测试");
        exercise.setPublishStatus(0);
        exercise.setCreatedAt(new Date());
        exercise.setUpdatedAt(new Date());
        eduExerciseMapper.insert(exercise);
        assertNotNull(exercise.getId());
        createdExerciseId = exercise.getId();

        AuthClass authClass = new AuthClass();
        authClass.setClassCode(CLASS_CODE);
        authClass.setClassStatus(0);
        authClass.setHeadmasterName("测试班主任");
        authClass.setCreatedAt(new Date());
        authClass.setUpdatedAt(new Date());
        authClassMapper.insert(authClass);

        Date endTime = new Date(System.currentTimeMillis() + 24 * 3600 * 1000L);
        ExercisePublishRequest request = new ExercisePublishRequest();
        request.setExerciseId(createdExerciseId);
        request.setClassCodes(Collections.singletonList(CLASS_CODE));
        request.setEndTime(endTime);

        Boolean result = eduExerciseService.saveDraftAssign(request);
        assertTrue(result);

        EduExercise savedExercise = eduExerciseMapper.selectById(createdExerciseId);
        assertNotNull(savedExercise);
        assertEquals(0, savedExercise.getPublishStatus());
        assertNotNull(savedExercise.getEndTime());
        long delta = Math.abs(savedExercise.getEndTime().getTime() - endTime.getTime());
        assertTrue(delta < 1000);

        Long relCount = relExerciseClassMapper.selectCount(new QueryWrapper<RelExerciseClass>().eq("exercise_id", createdExerciseId));
        assertEquals(1L, relCount);
    }
}
