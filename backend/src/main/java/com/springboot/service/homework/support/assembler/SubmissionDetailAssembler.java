package com.springboot.service.homework.support.assembler;

import com.springboot.model.entity.homework.EduExercise;
import com.springboot.model.entity.homework.EduExerciseItem;
import com.springboot.model.entity.homework.ResExerciseRecord;
import com.springboot.model.entity.user.AuthStudent;
import com.springboot.model.vo.homework.SubmissionDetailVO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SubmissionDetailAssembler {

    public SubmissionDetailVO assemble(EduExercise exercise,
                                       AuthStudent student,
                                       List<EduExerciseItem> items,
                                       List<ResExerciseRecord> records) {
        SubmissionDetailVO detailVO = new SubmissionDetailVO();
        detailVO.setExerciseId(exercise != null ? exercise.getId() : null);
        detailVO.setExerciseName(exercise != null ? exercise.getTaskName() : "");
        detailVO.setStudentId(student != null ? student.getId() : null);
        detailVO.setStudentName(student != null ? student.getStudentName() : "");
        detailVO.setClassName(student != null ? student.getClassCode() : "");

        int totalScore = records.stream()
                .map(ResExerciseRecord::getScore)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
        detailVO.setTotalScore(totalScore);

        Date submittedAt = records.stream()
                .map(ResExerciseRecord::getSubmittedAt)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
        detailVO.setSubmittedAt(submittedAt);

        Map<Long, ResExerciseRecord> recordMap = records.stream()
                .filter(Objects::nonNull)
                .filter(record -> record.getItemId() != null)
                .collect(Collectors.toMap(ResExerciseRecord::getItemId, record -> record, (left, right) -> right));

        List<SubmissionDetailVO.AnswerDetailVO> answerDetails = new ArrayList<>();
        int order = 1;
        for (EduExerciseItem item : items) {
            SubmissionDetailVO.AnswerDetailVO answerDetail = new SubmissionDetailVO.AnswerDetailVO();
            answerDetail.setItemId(item.getId());
            answerDetail.setItemOrder(order++);
            answerDetail.setQuestion(item.getQuestion());
            answerDetail.setQuestionType(item.getQuestionType());
            answerDetail.setOptionsText(item.getOptionsText());
            answerDetail.setStandardAnswer(item.getStandardAnswer());
            answerDetail.setMaxScore(item.getMaxScore());

            ResExerciseRecord record = recordMap.get(item.getId());
            if (record != null) {
                answerDetail.setRecordId(record.getId());
                answerDetail.setStudentAnswer(record.getChoiceAnswer() != null ? record.getChoiceAnswer() : record.getTextContent());
                answerDetail.setScore(record.getScore());
                answerDetail.setGradingStatus(record.getGradingStatus());
                answerDetail.setComment(record.getComment());
            }
            answerDetails.add(answerDetail);
        }
        detailVO.setAnswers(answerDetails);
        return detailVO;
    }
}
