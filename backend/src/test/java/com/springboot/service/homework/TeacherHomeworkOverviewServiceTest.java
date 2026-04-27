package com.springboot.service.homework;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.springboot.mapper.homework.EduExerciseItemMapper;
import com.springboot.mapper.homework.EduExerciseMapper;
import com.springboot.mapper.homework.RelExerciseClassMapper;
import com.springboot.mapper.homework.RelExerciseItemMapper;
import com.springboot.mapper.homework.ResExerciseRecordMapper;
import com.springboot.mapper.user.AuthClassMapper;
import com.springboot.mapper.user.AuthStudentMapper;
import com.springboot.mapper.user.AuthTeacherMapper;
import com.springboot.model.dto.homework.ExercisePublishRequest;
import com.springboot.model.dto.homework.ExerciseSubmitRequest;
import com.springboot.model.dto.homework.ReviewItemRequest;
import com.springboot.model.entity.homework.EduExercise;
import com.springboot.model.entity.homework.EduExerciseItem;
import com.springboot.model.entity.homework.RelExerciseClass;
import com.springboot.model.entity.homework.RelExerciseItem;
import com.springboot.model.entity.homework.ResExerciseRecord;
import com.springboot.model.entity.user.AuthClass;
import com.springboot.model.entity.user.AuthStudent;
import com.springboot.model.entity.user.AuthTeacher;
import com.springboot.model.vo.homework.EduExerciseVO;
import com.springboot.model.vo.homework.SubmissionDetailVO;
import com.springboot.model.vo.homework.SubmissionRecordVO;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class TeacherHomeworkOverviewServiceTest {

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
    private AuthClassMapper authClassMapper;

    @Resource
    private AuthStudentMapper authStudentMapper;

    @Resource
    private AuthTeacherMapper authTeacherMapper;

    private String classCode;
    private Long teacherId;
    private Long studentId;
    private Long exerciseId;
    private Long objectiveItemId;
    private Long essayItemId;

    @AfterEach
    void cleanup() {
        if (exerciseId != null) {
            resExerciseRecordMapper.delete(new QueryWrapper<ResExerciseRecord>().eq("exercise_id", exerciseId));
            relExerciseItemMapper.delete(new QueryWrapper<RelExerciseItem>().eq("exercise_id", exerciseId));
            relExerciseClassMapper.delete(new QueryWrapper<RelExerciseClass>().eq("exercise_id", exerciseId));
        }
        if (objectiveItemId != null) {
            relExerciseItemMapper.delete(new QueryWrapper<RelExerciseItem>().eq("item_id", objectiveItemId));
            eduExerciseItemMapper.deleteById(objectiveItemId);
        }
        if (essayItemId != null) {
            relExerciseItemMapper.delete(new QueryWrapper<RelExerciseItem>().eq("item_id", essayItemId));
            eduExerciseItemMapper.deleteById(essayItemId);
        }
        if (exerciseId != null) {
            eduExerciseMapper.deleteById(exerciseId);
        }
        if (studentId != null) {
            authStudentMapper.deleteById(studentId);
        }
        if (teacherId != null) {
            authTeacherMapper.deleteById(teacherId);
        }
        if (classCode != null) {
            authClassMapper.deleteById(classCode);
        }
    }

    @Test
    void shouldExposeClassCodesAndSubmissionProgressForTeacherOverview() throws Exception {
        setupScenario();

        Page<EduExercise> page = eduExerciseService.page(
                new Page<>(1, 10),
                new QueryWrapper<EduExercise>().eq("id", exerciseId)
        );

        Page<EduExerciseVO> overviewPage = eduExerciseService.getEduExerciseVOPage(page, null);
        assertEquals(1, overviewPage.getRecords().size());

        EduExerciseVO overview = overviewPage.getRecords().get(0);
        assertEquals(Collections.singletonList(classCode), readField(overview, "classCodes"));
        assertEquals(1, ((Number) readField(overview, "submissionCount")).intValue());
        assertEquals(0, ((Number) readField(overview, "reviewedCount")).intValue());
    }

    @Test
    void shouldPopulateExerciseNameInSubmissionDetail() {
        setupScenario();

        SubmissionDetailVO detail = eduExerciseSubmissionService.getSubmissionDetail(exerciseId, studentId);

        assertNotNull(detail);
        assertEquals(exerciseId, detail.getExerciseId());
        assertEquals("教师端作业视图测试", detail.getExerciseName());
        assertEquals("测试学生", detail.getStudentName());
        assertEquals(classCode, detail.getClassName());
    }

    @Test
    void shouldIncreaseReviewedCountAfterManualReview() throws Exception {
        setupScenario();

        ResExerciseRecord essayRecord = resExerciseRecordMapper.selectOne(
                new QueryWrapper<ResExerciseRecord>()
                        .eq("exercise_id", exerciseId)
                        .eq("student_id", studentId)
                        .eq("item_id", essayItemId)
        );
        assertNotNull(essayRecord);

        ReviewItemRequest request = new ReviewItemRequest();
        request.setRecordId(essayRecord.getId());
        request.setScore(18);
        request.setComment("观点完整");
        request.setGradingStatus(2);
        assertTrue(eduExerciseSubmissionService.reviewItem(request));

        EduExercise exercise = eduExerciseMapper.selectById(exerciseId);
        EduExerciseVO overview = eduExerciseService.getEduExerciseVO(exercise, null);
        assertEquals(1, ((Number) readField(overview, "reviewedCount")).intValue());
    }

    @Test
    void shouldListSubmissionRecordsWithoutClassRelation() {
        setupScenario();
        relExerciseClassMapper.delete(new QueryWrapper<RelExerciseClass>().eq("exercise_id", exerciseId));

        List<SubmissionRecordVO> records = eduExerciseSubmissionService.listSubmissionRecords(exerciseId);

        assertFalse(records.isEmpty());
        SubmissionRecordVO record = records.get(0);
        assertEquals(studentId, record.getStudentId());
        assertEquals("测试学生", record.getStudentName());
        assertEquals(classCode, record.getClassCode());
        assertNotNull(record.getSubmittedAt());
    }

    private void setupScenario() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 4).toUpperCase();
        classCode = "TH" + suffix;
        String teacherUsername = "T" + suffix;
        String studentCode = "S" + suffix;

        AuthClass authClass = new AuthClass();
        authClass.setClassCode(classCode);
        authClass.setHeadmasterName("测试班主任");
        authClass.setClassStatus(0);
        authClass.setCreatedAt(new Date());
        authClass.setUpdatedAt(new Date());
        authClassMapper.insert(authClass);

        AuthTeacher teacher = new AuthTeacher();
        teacher.setUsername(teacherUsername);
        teacher.setRealName("测试教师");
        teacher.setPasswordMd5(DigestUtil.md5Hex("test123456"));
        teacher.setCreatedAt(new Date());
        teacher.setUpdatedAt(new Date());
        authTeacherMapper.insert(teacher);
        teacherId = teacher.getId();

        AuthStudent student = new AuthStudent();
        student.setStudentCode(studentCode);
        student.setStudentName("测试学生");
        student.setPasswordMd5(DigestUtil.md5Hex("test123456"));
        student.setClassCode(classCode);
        student.setAccountStatus(0);
        student.setLoginFailCount(0);
        student.setCreatedAt(new Date());
        student.setUpdatedAt(new Date());
        authStudentMapper.insert(student);
        studentId = student.getId();

        EduExercise exercise = new EduExercise();
        exercise.setTaskName("教师端作业视图测试");
        exercise.setDescription("验证教师端作业概览与批阅详情");
        exercise.setTeacherId(teacherId);
        exercise.setPublishStatus(0);
        exercise.setStartTime(new Date());
        exercise.setEndTime(new Date(System.currentTimeMillis() + 24 * 3600 * 1000L));
        exercise.setCreatedAt(new Date());
        exercise.setUpdatedAt(new Date());
        eduExerciseMapper.insert(exercise);
        exerciseId = exercise.getId();

        EduExerciseItem objectiveItem = new EduExerciseItem();
        objectiveItem.setQuestion("Java 支持面向对象编程。\nA. 正确\nB. 错误");
        objectiveItem.setOptionsText("正确,错误");
        objectiveItem.setStandardAnswer("A");
        objectiveItem.setQuestionType(2);
        objectiveItem.setMaxScore(10);
        objectiveItem.setCreatedAt(new Date());
        objectiveItem.setUpdatedAt(new Date());
        eduExerciseItemMapper.insert(objectiveItem);
        objectiveItemId = objectiveItem.getId();

        RelExerciseItem relObjectiveItem = new RelExerciseItem();
        relObjectiveItem.setExerciseId(exerciseId);
        relObjectiveItem.setItemId(objectiveItemId);
        relObjectiveItem.setItemOrder(1);
        relObjectiveItem.setItemScore(objectiveItem.getMaxScore());
        relExerciseItemMapper.insert(relObjectiveItem);

        EduExerciseItem essayItem = new EduExerciseItem();
        essayItem.setQuestion("请简述面向对象的三个核心特性。");
        essayItem.setQuestionType(5);
        essayItem.setMaxScore(20);
        essayItem.setCreatedAt(new Date());
        essayItem.setUpdatedAt(new Date());
        eduExerciseItemMapper.insert(essayItem);
        essayItemId = essayItem.getId();

        RelExerciseItem relEssayItem = new RelExerciseItem();
        relEssayItem.setExerciseId(exerciseId);
        relEssayItem.setItemId(essayItemId);
        relEssayItem.setItemOrder(2);
        relEssayItem.setItemScore(essayItem.getMaxScore());
        relExerciseItemMapper.insert(relEssayItem);

        ExercisePublishRequest publishRequest = new ExercisePublishRequest();
        publishRequest.setExerciseId(exerciseId);
        publishRequest.setClassCodes(Collections.singletonList(classCode));
        assertTrue(eduExerciseService.publishExercise(publishRequest));

        ExerciseSubmitRequest submitRequest = new ExerciseSubmitRequest();
        submitRequest.setExerciseId(exerciseId);
        submitRequest.setStudentId(studentId);
        submitRequest.setAnswers(List.of(
                singleChoiceAnswer(objectiveItemId, "A"),
                essayAnswer(essayItemId, "封装、继承、多态")
        ));

        assertTrue(eduExerciseSubmissionService.submitAnswers(submitRequest).getSuccess());
    }

    private ExerciseSubmitRequest.AnswerItem singleChoiceAnswer(Long itemId, String answer) {
        ExerciseSubmitRequest.AnswerItem item = new ExerciseSubmitRequest.AnswerItem();
        item.setItemId(itemId);
        item.setQuestionType(2);
        item.setChoiceAnswer(answer);
        return item;
    }

    private ExerciseSubmitRequest.AnswerItem essayAnswer(Long itemId, String answer) {
        ExerciseSubmitRequest.AnswerItem item = new ExerciseSubmitRequest.AnswerItem();
        item.setItemId(itemId);
        item.setQuestionType(5);
        item.setTextContent(answer);
        return item;
    }

    @SuppressWarnings("unchecked")
    private <T> T readField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(target);
    }
}
