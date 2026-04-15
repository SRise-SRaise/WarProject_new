// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 此处后端没有提供注释 POST /exam/eduPaper/add */
export async function addEduPaper(
  body: API.EduPaperAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/eduPaper/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /exam/eduPaper/delete */
export async function deleteEduPaper(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteEduPaperParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/eduPaper/delete", {
    method: "POST",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /exam/eduPaper/get/vo */
export async function getEduPaperVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getEduPaperVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseEduPaperVO>("/exam/eduPaper/get/vo", {
    method: "GET",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /exam/eduPaper/list/page */
export async function listEduPaperByPage(
  body: API.EduPaperQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageEduPaper>("/exam/eduPaper/list/page", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /exam/eduPaper/list/page/vo */
export async function listEduPaperVoByPage(
  body: API.EduPaperQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageEduPaperVO>(
    "/exam/eduPaper/list/page/vo",
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

/** 此处后端没有提供注释 POST /exam/eduPaper/update */
export async function updateEduPaper(
  body: API.EduPaperUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/eduPaper/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
