// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 批量提交作业答案 POST /homework/submission/submit */
export async function submitExerciseAnswers(
  body: API.ExerciseSubmitRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseSubmissionResultVO>(
    "/homework/submission/submit",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      data: body,
      ...(options || {}),
    }
  );
}

/** 暂存答案 POST /homework/submission/saveDraft */
export async function saveExerciseDraft(
  body: API.ExerciseSubmitRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/homework/submission/saveDraft", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 获取答题进度 GET /homework/submission/getProgress */
export async function getExerciseProgress(
  params: { exerciseId: number; studentId: number },
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseStudentProgressVO>(
    "/homework/submission/getProgress",
    {
      method: "GET",
      params,
      ...(options || {}),
    }
  );
}

/** 自动评分 POST /homework/submission/autoGrade */
export async function autoGradeExercise(
  params: { exerciseId: number; studentId: number },
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseNumber>("/homework/submission/autoGrade", {
    method: "POST",
    params,
    ...(options || {}),
  });
}

/** 批量自动评分 POST /homework/submission/batchAutoGrade */
export async function batchAutoGradeExercise(
  params: { exerciseId: number },
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseNumber>(
    "/homework/submission/batchAutoGrade",
    {
      method: "POST",
      params,
      ...(options || {}),
    }
  );
}

/** 教师批阅单题 POST /homework/submission/reviewItem */
export async function reviewExerciseItem(
  body: API.ReviewItemRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/homework/submission/reviewItem", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 教师完成整体批阅 POST /homework/submission/completeReview */
export async function completeExerciseReview(
  body: API.CompleteReviewRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>(
    "/homework/submission/completeReview",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      data: body,
      ...(options || {}),
    }
  );
}

/** 学生查询个人成绩 GET /homework/submission/getMyScore */
export async function getMyExerciseScore(
  params: { exerciseId: number; studentId: number },
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseStudentScoreVO>(
    "/homework/submission/getMyScore",
    {
      method: "GET",
      params,
      ...(options || {}),
    }
  );
}

/** 获取提交详情（教师批阅用）GET /homework/submission/getDetail */
export async function getSubmissionDetail(
  params: { exerciseId: number; studentId: number },
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseSubmissionDetailVO>(
    "/homework/submission/getDetail",
    {
      method: "GET",
      params,
      ...(options || {}),
    }
  );
}