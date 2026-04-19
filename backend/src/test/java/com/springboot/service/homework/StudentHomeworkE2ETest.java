package com.springboot.service.homework;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.springboot.mapper.homework.EduExerciseItemMapper;
import com.springboot.mapper.homework.EduExerciseMapper;
import com.springboot.mapper.homework.RelExerciseItemMapper;
import com.springboot.mapper.homework.RelExerciseClassMapper;
import com.springboot.mapper.homework.ResExerciseRecordMapper;
import com.springboot.mapper.user.AuthClassMapper;
import com.springboot.mapper.user.AuthStudentMapper;
import com.springboot.mapper.user.AuthTeacherMapper;
import com.springboot.model.dto.homework.*;
import com.springboot.model.entity.homework.EduExercise;
import com.springboot.model.entity.homework.EduExerciseItem;
import com.springboot.model.entity.homework.RelExerciseClass;
import com.springboot.model.entity.homework.RelExerciseItem;
import com.springboot.model.entity.homework.ResExerciseRecord;
import com.springboot.model.entity.user.AuthClass;
import com.springboot.model.entity.user.AuthStudent;
import com.springboot.model.entity.user.AuthTeacher;
import com.springboot.model.vo.homework.StudentExerciseVO;
import com.springboot.model.vo.homework.StudentProgressVO;
import com.springboot.model.vo.homework.StudentScoreVO;
import com.springboot.model.vo.homework.SubmissionDetailVO;
import com.springboot.model.vo.homework.SubmissionResultVO;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudentHomeworkE2ETest {

    @Resource
    private EduExerciseService eduExerciseService;

    @Resource
    private EduExerciseSubmissionService eduExerciseSubmissionService;

    @Resource
    private EduExerciseMapper eduExerciseMapper;

    @Resource
    private EduExerciseItemMapper eduExerciseItemMapper;

    @Resource
    private RelExerciseClassMapper relExerciseClassMapper;

    @Resource
    private RelExerciseItemMapper relExerciseItemMapper;

    @Resource
    private ResExerciseRecordMapper resExerciseRecordMapper;

    @Resource
    private AuthStudentMapper authStudentMapper;

    @Resource
    private AuthTeacherMapper authTeacherMapper;

    @Resource
    private AuthClassMapper authClassMapper;

    private String testClassCode;
    private String testTeacherUsername;
    private String testStudentCode;
    private static final String TEST_STUDENT_PASSWORD = "test123456";

    private static Long teacherId;
    private static Long studentId;
    private static Long exerciseId;
    private static Long itemIdSingleChoice;
    private static Long itemIdMultipleChoice;
    private static Long itemIdFillBlank;
    private static Long itemIdTrueFalse;

    @AfterAll
    void cleanupAfterAll() {
        if (itemIdTrueFalse != null) {
            try { resExerciseRecordMapper.delete(new QueryWrapper<ResExerciseRecord>().eq("item_id", itemIdTrueFalse)); } catch (Exception e) {}
            try { relExerciseItemMapper.delete(new QueryWrapper<RelExerciseItem>().eq("item_id", itemIdTrueFalse)); } catch (Exception e) {}
            try { eduExerciseItemMapper.deleteById(itemIdTrueFalse); } catch (Exception e) {}
        }
        if (itemIdFillBlank != null) {
            try { resExerciseRecordMapper.delete(new QueryWrapper<ResExerciseRecord>().eq("item_id", itemIdFillBlank)); } catch (Exception e) {}
            try { relExerciseItemMapper.delete(new QueryWrapper<RelExerciseItem>().eq("item_id", itemIdFillBlank)); } catch (Exception e) {}
            try { eduExerciseItemMapper.deleteById(itemIdFillBlank); } catch (Exception e) {}
        }
        if (itemIdMultipleChoice != null) {
            try { resExerciseRecordMapper.delete(new QueryWrapper<ResExerciseRecord>().eq("item_id", itemIdMultipleChoice)); } catch (Exception e) {}
            try { relExerciseItemMapper.delete(new QueryWrapper<RelExerciseItem>().eq("item_id", itemIdMultipleChoice)); } catch (Exception e) {}
            try { eduExerciseItemMapper.deleteById(itemIdMultipleChoice); } catch (Exception e) {}
        }
        if (itemIdSingleChoice != null) {
            try { resExerciseRecordMapper.delete(new QueryWrapper<ResExerciseRecord>().eq("item_id", itemIdSingleChoice)); } catch (Exception e) {}
            try { relExerciseItemMapper.delete(new QueryWrapper<RelExerciseItem>().eq("item_id", itemIdSingleChoice)); } catch (Exception e) {}
            try { eduExerciseItemMapper.deleteById(itemIdSingleChoice); } catch (Exception e) {}
        }
        if (exerciseId != null) {
            try { relExerciseItemMapper.delete(new QueryWrapper<RelExerciseItem>().eq("exercise_id", exerciseId)); } catch (Exception e) {}
            try { relExerciseClassMapper.delete(new QueryWrapper<RelExerciseClass>().eq("exercise_id", exerciseId)); } catch (Exception e) {}
            try { eduExerciseMapper.deleteById(exerciseId); } catch (Exception e) {}
        }
        if (studentId != null) {
            try { authStudentMapper.deleteById(studentId); } catch (Exception e) {}
        }
        if (teacherId != null) {
            try { authTeacherMapper.deleteById(teacherId); } catch (Exception e) {}
        }
        if (testClassCode != null) {
            try { authClassMapper.deleteById(testClassCode); } catch (Exception e) {}
        }
    }

    @Test
    @Order(1)
    void test01_SetupAllTestData() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 4).toUpperCase();
        testClassCode = "TS" + suffix;
        testTeacherUsername = "TT" + suffix;
        testStudentCode = "SS" + suffix;

        AuthClass authClass = new AuthClass();
        authClass.setClassCode(testClassCode);
        authClass.setHeadmasterName("测试班主任");
        authClass.setClassStatus(0);
        authClass.setCreatedAt(new Date());
        authClass.setUpdatedAt(new Date());
        authClassMapper.insert(authClass);
        assertNotNull(authClassMapper.selectById(testClassCode));

        AuthTeacher teacher = new AuthTeacher();
        teacher.setUsername(testTeacherUsername);
        teacher.setRealName("测试教师");
        teacher.setPasswordMd5(DigestUtil.md5Hex(TEST_STUDENT_PASSWORD));
        teacher.setCreatedAt(new Date());
        teacher.setUpdatedAt(new Date());
        authTeacherMapper.insert(teacher);
        teacherId = teacher.getId();
        assertNotNull(teacherId);

        AuthStudent student = new AuthStudent();
        student.setStudentCode(testStudentCode);
        student.setStudentName("测试学生");
        student.setPasswordMd5(DigestUtil.md5Hex(TEST_STUDENT_PASSWORD));
        student.setClassCode(testClassCode);
        student.setAccountStatus(0);
        student.setLoginFailCount(0);
        student.setCreatedAt(new Date());
        student.setUpdatedAt(new Date());
        authStudentMapper.insert(student);
        studentId = student.getId();
        assertNotNull(studentId);

        EduExercise exercise = new EduExercise();
        exercise.setSortOrder(1);
        exercise.setTaskName("Java基础测试");
        exercise.setDescription("这是一套Java基础知识测试题");
        exercise.setInteractMode(1);
        exercise.setTeacherId(teacherId);
        exercise.setPublishStatus(0);
        exercise.setStartTime(new Date());
        exercise.setEndTime(new Date(System.currentTimeMillis() + 7 * 24 * 3600 * 1000L));
        exercise.setCreatedAt(new Date());
        exercise.setUpdatedAt(new Date());
        eduExerciseMapper.insert(exercise);
        exerciseId = exercise.getId();
        assertNotNull(exerciseId);

        List<EduExerciseItem> items = new ArrayList<>();

        EduExerciseItem item1 = new EduExerciseItem();
        item1.setQuestion("Java中super关键字的作用是？");
        item1.setOptionsText("A.调用父类方法;B.调用子类方法;C.调用构造方法;D.无作用");
        item1.setStandardAnswer("A");
        item1.setQuestionType(2);
        item1.setMaxScore(10);
        item1.setCreatedAt(new Date());
        item1.setUpdatedAt(new Date());
        items.add(item1);

        EduExerciseItem item2 = new EduExerciseItem();
        item2.setQuestion("下列哪些是Java的基本数据类型？");
        item2.setOptionsText("A.int;B.String;C.boolean;D.double");
        item2.setStandardAnswer("ACD");
        item2.setQuestionType(3);
        item2.setMaxScore(15);
        item2.setCreatedAt(new Date());
        item2.setUpdatedAt(new Date());
        items.add(item2);

        EduExerciseItem item3 = new EduExerciseItem();
        item3.setQuestion("Java是一门______编程语言。");
        item3.setStandardAnswer("面向对象");
        item3.setQuestionType(1);
        item3.setMaxScore(10);
        item3.setCreatedAt(new Date());
        item3.setUpdatedAt(new Date());
        items.add(item3);

        EduExerciseItem item4 = new EduExerciseItem();
        item4.setQuestion("Java中的main方法必须返回void类型。");
        item4.setStandardAnswer("T");
        item4.setQuestionType(4);
        item4.setMaxScore(5);
        item4.setCreatedAt(new Date());
        item4.setUpdatedAt(new Date());
        items.add(item4);

        for (EduExerciseItem item : items) {
            eduExerciseItemMapper.insert(item);
        }

        List<EduExerciseItem> savedItems = eduExerciseItemMapper.selectBatchIds(
                items.stream().map(EduExerciseItem::getId).toList()
        );
        assertEquals(4, savedItems.size());

        int order = 1;
        for (EduExerciseItem savedItem : savedItems) {
            RelExerciseItem rel = new RelExerciseItem();
            rel.setExerciseId(exerciseId);
            rel.setItemId(savedItem.getId());
            rel.setItemOrder(order++);
            rel.setItemScore(savedItem.getMaxScore());
            relExerciseItemMapper.insert(rel);
        }

        itemIdSingleChoice = savedItems.stream()
                .filter(i -> i.getQuestionType() == 2)
                .findFirst()
                .map(EduExerciseItem::getId)
                .orElse(null);
        itemIdMultipleChoice = savedItems.stream()
                .filter(i -> i.getQuestionType() == 3)
                .findFirst()
                .map(EduExerciseItem::getId)
                .orElse(null);
        itemIdFillBlank = savedItems.stream()
                .filter(i -> i.getQuestionType() == 1)
                .findFirst()
                .map(EduExerciseItem::getId)
                .orElse(null);
        itemIdTrueFalse = savedItems.stream()
                .filter(i -> i.getQuestionType() == 4)
                .findFirst()
                .map(EduExerciseItem::getId)
                .orElse(null);

        assertNotNull(itemIdSingleChoice);
        assertNotNull(itemIdMultipleChoice);
        assertNotNull(itemIdFillBlank);
        assertNotNull(itemIdTrueFalse);

        ExercisePublishRequest request = new ExercisePublishRequest();
        request.setExerciseId(exerciseId);
        request.setClassCodes(Collections.singletonList(testClassCode));
        Boolean publishResult = eduExerciseService.publishExercise(request);
        assertTrue(publishResult);

        EduExercise published = eduExerciseMapper.selectById(exerciseId);
        assertEquals(1, published.getPublishStatus());
    }

    @Test
    @Order(2)
    void test02_StudentListExercises() {
        StudentExerciseQueryRequest queryRequest = new StudentExerciseQueryRequest();
        queryRequest.setStudentId(studentId);

        List<StudentExerciseVO> exercises = eduExerciseService.listForStudent(queryRequest);
        assertNotNull(exercises);
        assertTrue(exercises.size() > 0);

        StudentExerciseVO exercise = exercises.stream()
                .filter(e -> e.getId().equals(exerciseId))
                .findFirst()
                .orElse(null);
        assertNotNull(exercise);
        assertEquals("Java基础测试", exercise.getTaskName());
        assertEquals(1, exercise.getPublishStatus());
    }

    @Test
    @Order(3)
    void test03_GetExerciseProgress() {
        StudentProgressVO progress = eduExerciseSubmissionService.getProgress(exerciseId, studentId);
        assertNotNull(progress);
        assertEquals(exerciseId, progress.getExerciseId());
        assertEquals(4, progress.getTotalCount());
        assertEquals(0, progress.getAnsweredCount());
        assertFalse(progress.getIsSubmitted());
    }

    @Test
    @Order(4)
    void test04_SubmitSingleChoiceCorrect() {
        ExerciseSubmitRequest submitRequest = new ExerciseSubmitRequest();
        submitRequest.setExerciseId(exerciseId);
        submitRequest.setStudentId(studentId);

        ExerciseSubmitRequest.AnswerItem answerItem = new ExerciseSubmitRequest.AnswerItem();
        answerItem.setItemId(itemIdSingleChoice);
        answerItem.setQuestionType(2);
        answerItem.setChoiceAnswer("A");
        submitRequest.setAnswers(Collections.singletonList(answerItem));

        SubmissionResultVO result = eduExerciseSubmissionService.submitAnswers(submitRequest);
        assertNotNull(result);
        assertTrue(result.getSuccess());
    }

    @Test
    @Order(5)
    void test05_SubmitMultipleChoiceCorrect() {
        ExerciseSubmitRequest submitRequest = new ExerciseSubmitRequest();
        submitRequest.setExerciseId(exerciseId);
        submitRequest.setStudentId(studentId);

        ExerciseSubmitRequest.AnswerItem answerItem = new ExerciseSubmitRequest.AnswerItem();
        answerItem.setItemId(itemIdMultipleChoice);
        answerItem.setQuestionType(3);
        answerItem.setChoiceAnswer("ACD");
        submitRequest.setAnswers(Collections.singletonList(answerItem));

        SubmissionResultVO result = eduExerciseSubmissionService.submitAnswers(submitRequest);
        assertNotNull(result);
        assertTrue(result.getSuccess());
    }

    @Test
    @Order(6)
    void test06_SubmitTrueFalseCorrect() {
        ExerciseSubmitRequest submitRequest = new ExerciseSubmitRequest();
        submitRequest.setExerciseId(exerciseId);
        submitRequest.setStudentId(studentId);

        ExerciseSubmitRequest.AnswerItem answerItem = new ExerciseSubmitRequest.AnswerItem();
        answerItem.setItemId(itemIdTrueFalse);
        answerItem.setQuestionType(4);
        answerItem.setChoiceAnswer("T");
        submitRequest.setAnswers(Collections.singletonList(answerItem));

        SubmissionResultVO result = eduExerciseSubmissionService.submitAnswers(submitRequest);
        assertNotNull(result);
        assertTrue(result.getSuccess());
    }

    @Test
    @Order(7)
    void test07_SubmitFillBlankCorrect() {
        ExerciseSubmitRequest submitRequest = new ExerciseSubmitRequest();
        submitRequest.setExerciseId(exerciseId);
        submitRequest.setStudentId(studentId);

        ExerciseSubmitRequest.AnswerItem answerItem = new ExerciseSubmitRequest.AnswerItem();
        answerItem.setItemId(itemIdFillBlank);
        answerItem.setQuestionType(1);
        List<ExerciseSubmitRequest.FillBlankAnswer> fillAnswers = new ArrayList<>();
        ExerciseSubmitRequest.FillBlankAnswer fillBlankAnswer = new ExerciseSubmitRequest.FillBlankAnswer();
        fillBlankAnswer.setBlankIndex(0);
        fillBlankAnswer.setAnswerContent("面向对象");
        fillAnswers.add(fillBlankAnswer);
        answerItem.setFillBlanks(fillAnswers);
        submitRequest.setAnswers(Collections.singletonList(answerItem));

        SubmissionResultVO result = eduExerciseSubmissionService.submitAnswers(submitRequest);
        assertNotNull(result);
        assertTrue(result.getSuccess());
    }

    @Test
    @Order(8)
    void test08_VerifyProgressAfterSubmission() {
        StudentProgressVO progress = eduExerciseSubmissionService.getProgress(exerciseId, studentId);
        assertNotNull(progress);
        assertEquals(4, progress.getAnsweredCount());
        assertTrue(progress.getIsSubmitted());
    }

    @Test
    @Order(9)
    void test09_GetStudentScore() {
        StudentScoreVO score = eduExerciseSubmissionService.getMyScore(exerciseId, studentId);
        assertNotNull(score);
        assertEquals(exerciseId, score.getExerciseId());
        assertTrue(score.getTotalScore() >= 0);
    }

    @Test
    @Order(10)
    void test10_VerifyStudentExerciseListStatus() {
        StudentExerciseQueryRequest queryRequest = new StudentExerciseQueryRequest();
        queryRequest.setStudentId(studentId);

        List<StudentExerciseVO> exercises = eduExerciseService.listForStudent(queryRequest);
        assertNotNull(exercises);

        StudentExerciseVO exercise = exercises.stream()
                .filter(e -> e.getId().equals(exerciseId))
                .findFirst()
                .orElse(null);
        assertNotNull(exercise);
        assertTrue("submitted".equals(exercise.getStatus()) || "reviewed".equals(exercise.getStatus()));
    }

    @Test
    @Order(11)
    void test11_SubmitWrongAnswer() {
        ExerciseSubmitRequest submitRequest = new ExerciseSubmitRequest();
        submitRequest.setExerciseId(exerciseId);
        submitRequest.setStudentId(studentId);

        ExerciseSubmitRequest.AnswerItem answerItem = new ExerciseSubmitRequest.AnswerItem();
        answerItem.setItemId(itemIdSingleChoice);
        answerItem.setQuestionType(2);
        answerItem.setChoiceAnswer("B");
        submitRequest.setAnswers(Collections.singletonList(answerItem));

        SubmissionResultVO result = eduExerciseSubmissionService.submitAnswers(submitRequest);
        assertNotNull(result);
    }

    @Test
    @Order(12)
    void test12_SaveDraftFunctionality() {
        ExerciseSubmitRequest saveRequest = new ExerciseSubmitRequest();
        saveRequest.setExerciseId(exerciseId);
        saveRequest.setStudentId(studentId);

        ExerciseSubmitRequest.AnswerItem answerItem = new ExerciseSubmitRequest.AnswerItem();
        answerItem.setItemId(itemIdFillBlank);
        answerItem.setQuestionType(1);
        answerItem.setChoiceAnswer("这是我保存的草稿答案");
        saveRequest.setAnswers(Collections.singletonList(answerItem));

        Boolean result = eduExerciseSubmissionService.saveDraft(saveRequest);
        assertNotNull(result);
    }

    @Test
    @Order(13)
    void test13_BatchAutoGrading() {
        Integer count = eduExerciseSubmissionService.batchAutoGrade(exerciseId);
        assertNotNull(count);
        assertTrue(count >= 0);
    }

    @Test
    @Order(14)
    void test14_GetSubmissionDetail() {
        SubmissionDetailVO detail = eduExerciseSubmissionService.getSubmissionDetail(exerciseId, studentId);
        assertNotNull(detail);
        assertEquals(exerciseId, detail.getExerciseId());
        assertEquals(studentId, detail.getStudentId());
        assertNotNull(detail.getAnswers());
    }
}
