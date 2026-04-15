// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 此处后端没有提供注释 POST /exam/eduExam/add */
export async function addEduExam(
  body: API.EduExamAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/eduExam/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /exam/eduExam/delete */
export async function deleteEduExam(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteEduExamParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/eduExam/delete", {
    method: "POST",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /exam/eduExam/get/vo */
export async function getEduExamVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getEduExamVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseEduExamVO>("/exam/eduExam/get/vo", {
    method: "GET",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /exam/eduExam/list/page */
export async function listEduExamByPage(
  body: API.EduExamQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageEduExam>("/exam/eduExam/list/page", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /exam/eduExam/list/page/vo */
export async function listEduExamVoByPage(
  body: API.EduExamQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageEduExamVO>("/exam/eduExam/list/page/vo", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /exam/eduExam/update */
export async function updateEduExam(
  body: API.EduExamUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/eduExam/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
