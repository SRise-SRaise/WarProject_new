// @ts-ignore
/* eslint-disable */
import request from "@/request";

/**
 * 获取学生的实验报告（动态拼接）
 */
export async function getStudentReport(params: {
  experimentId: string | number;
  studentId: string | number;
}) {
  return request<API.EduExperimentReportVO>("/experiment/report/get", {
    method: "GET",
    params,
  });
}

/**
 * 批改实验报告
 */
export async function gradeReport(data: {
  experimentId: string | number;
  studentId: string | number;
  scores: Array<{
    itemId: string | number;
    score: number;
    comment?: string;
  }>;
  totalScore: number;
  feedback?: string;
}) {
  return request<API.BaseResponse_boolean_>("/experiment/report/grade", {
    method: "POST",
    data,
  });
}

/**
 * 获取教师端实验报告列表（分页）
 */
export async function getTeacherReportListPage(params: {
  experimentId: string | number;
  clazzNo?: string;
  status?: string;
  current?: number;
  pageSize?: number;
}) {
  return request<API.Page_EduExperimentReportListVO_>("/experiment/report/list/teacher/page", {
    method: "GET",
    params,
  });
}

/**
 * 获取实验下的所有班级列表
 */
export async function getExperimentClasses(experimentId: string | number) {
  return request<API.BaseResponseList_string_>("/experiment/eduExperiment/classes", {
    method: "GET",
    params: { experimentId },
  });
}
