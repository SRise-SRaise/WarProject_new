package com.springboot.service.homework.support.assembler;

import com.springboot.model.entity.homework.EduExercise;
import com.springboot.model.entity.homework.EduExerciseItem;
import com.springboot.model.entity.homework.ResExerciseRecord;
import com.springboot.model.entity.user.AuthStudent;
import com.springboot.model.vo.homework.StudentScoreVO;
import com.springboot.service.homework.support.status.GradingStatusMachine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class StudentScoreAssembler {

    private final GradingStatusMachine gradingStatusMachine = new GradingStatusMachine();

    public StudentScoreVO assemble(EduExercise exercise,
                                   AuthStudent student,
                                   List<EduExerciseItem> items,
                                   List<ResExerciseRecord> records) {
        StudentScoreVO scoreVO = new StudentScoreVO();
        Long exerciseId = exercise != null ? exercise.getId() : null;
        Long studentId = student != null ? student.getId() : null;
        scoreVO.setExerciseId(exerciseId);
        scoreVO.setExerciseName(exercise != null ? exercise.getTaskName() : "");
        scoreVO.setStudentId(studentId);
        scoreVO.setStudentName(student != null ? student.getStudentName() : "");

        int totalScore = records.stream()
                .map(ResExerciseRecord::getScore)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
        scoreVO.setTotalScore(totalScore);

        int maxScore = items.stream()
                .map(EduExerciseItem::getMaxScore)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
        scoreVO.setMaxScore(maxScore);
        scoreVO.setStatus(gradingStatusMachine.allReviewed(records) ? "reviewed" : "submitted");

        Map<Long, ResExerciseRecord> recordMap = records.stream()
                .filter(Objects::nonNull)
                .filter(record -> record.getItemId() != null)
                .collect(Collectors.toMap(ResExerciseRecord::getItemId, record -> record, (left, right) -> right));

        List<StudentScoreVO.ScoreItemVO> itemScores = new ArrayList<>();
        int order = 1;
        for (EduExerciseItem item : items) {
            StudentScoreVO.ScoreItemVO itemScore = new StudentScoreVO.ScoreItemVO();
            itemScore.setItemId(item.getId());
            itemScore.setItemOrder(order++);
            itemScore.setQuestion(item.getQuestion());
            itemScore.setQuestionType(item.getQuestionType());
            itemScore.setMaxScore(item.getMaxScore());
            itemScore.setCorrectAnswer(item.getStandardAnswer());

            ResExerciseRecord record = recordMap.get(item.getId());
            if (record != null) {
                itemScore.setStudentScore(record.getScore());
                itemScore.setStudentAnswer(record.getChoiceAnswer() != null ? record.getChoiceAnswer() : record.getTextContent());
                itemScore.setGradingStatus(record.getGradingStatus());
                itemScore.setComment(record.getComment());
            } else {
                itemScore.setStudentScore(0);
                itemScore.setGradingStatus(0);
            }
            itemScores.add(itemScore);
        }
        scoreVO.setItems(itemScores);
        return scoreVO;
    }
}
