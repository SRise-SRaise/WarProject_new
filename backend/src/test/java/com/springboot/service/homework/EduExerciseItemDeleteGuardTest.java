package com.springboot.service.homework;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.springboot.mapper.homework.EduExerciseItemMapper;
import com.springboot.mapper.homework.EduExerciseMapper;
import com.springboot.mapper.homework.ResExerciseRecordMapper;
import com.springboot.mapper.user.AuthClassMapper;
import com.springboot.mapper.user.AuthStudentMapper;
import com.springboot.model.entity.homework.EduExercise;
import com.springboot.model.entity.homework.EduExerciseItem;
import com.springboot.model.entity.homework.ResExerciseRecord;
import com.springboot.model.entity.user.AuthClass;
import com.springboot.model.entity.user.AuthStudent;
import com.springboot.exception.BusinessException;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class EduExerciseItemDeleteGuardTest {

    @Resource
    private EduExerciseItemService eduExerciseItemService;

    @Resource
    private EduExerciseMapper eduExerciseMapper;

    @Resource
    private EduExerciseItemMapper eduExerciseItemMapper;

    @Resource
    private ResExerciseRecordMapper resExerciseRecordMapper;

    @Resource
    private AuthClassMapper authClassMapper;

    @Resource
    private AuthStudentMapper authStudentMapper;

    private Long exerciseId;
    private Long itemId;
    private Long studentId;
    private static final String CLASS_CODE = "TD01";
    private static final String STUDENT_CODE = "TD0001";

    @AfterEach
    void cleanup() {
        if (itemId != null) {
            resExerciseRecordMapper.delete(new QueryWrapper<ResExerciseRecord>().eq("item_id", itemId));
            eduExerciseItemMapper.deleteById(itemId);
        }
        if (exerciseId != null) {
            eduExerciseMapper.deleteById(exerciseId);
        }
        if (studentId != null) {
            authStudentMapper.deleteById(studentId);
        }
        authClassMapper.deleteById(CLASS_CODE);
    }

    @Test
    void shouldRejectDeleteWhenItemHasSubmissionRecords() {
        Date now = new Date();

        AuthClass authClass = new AuthClass();
        authClass.setClassCode(CLASS_CODE);
        authClass.setHeadmasterName("测试班主任");
        authClass.setClassStatus(0);
        authClass.setCreatedAt(now);
        authClass.setUpdatedAt(now);
        authClassMapper.insert(authClass);

        AuthStudent student = new AuthStudent();
        student.setStudentCode(STUDENT_CODE);
        student.setStudentName("测试学生");
        student.setPasswordMd5("md5");
        student.setClassCode(CLASS_CODE);
        student.setAccountStatus(0);
        student.setLoginFailCount(0);
        student.setCreatedAt(now);
        student.setUpdatedAt(now);
        authStudentMapper.insert(student);
        studentId = student.getId();

        EduExercise exercise = new EduExercise();
        exercise.setTaskName("删除保护测试作业");
        exercise.setPublishStatus(1);
        exercise.setCreatedAt(now);
        exercise.setUpdatedAt(now);
        eduExerciseMapper.insert(exercise);
        exerciseId = exercise.getId();

        EduExerciseItem item = new EduExerciseItem();
        item.setQuestion("删除保护测试题目");
        item.setQuestionType(2);
        item.setStandardAnswer("A");
        item.setOptionsText("A.1;B.2");
        item.setMaxScore(5);
        item.setCreatedAt(now);
        item.setUpdatedAt(now);
        eduExerciseItemMapper.insert(item);
        itemId = item.getId();

        ResExerciseRecord record = new ResExerciseRecord();
        record.setExerciseId(exerciseId);
        record.setItemId(itemId);
        record.setStudentId(studentId);
        record.setChoiceAnswer("A");
        record.setScore(5);
        record.setGradingStatus(1);
        record.setSubmittedAt(now);
        record.setCreatedAt(now);
        record.setUpdatedAt(now);
        resExerciseRecordMapper.insert(record);

        BusinessException exception = assertThrows(BusinessException.class, () -> eduExerciseItemService.removeExerciseItemById(itemId));
        assertEquals("该题目已有学生作答，不能移除", exception.getMessage());
    }
}
