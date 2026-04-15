// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 此处后端没有提供注释 POST /homework/resSubmissionLog/add */
export async function addResSubmissionLog(
  body: API.ResSubmissionLogAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/homework/resSubmissionLog/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /homework/resSubmissionLog/delete */
export async function deleteResSubmissionLog(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteResSubmissionLogParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/homework/resSubmissionLog/delete", {
    method: "POST",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /homework/resSubmissionLog/get/vo */
export async function getResSubmissionLogVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getResSubmissionLogVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseResSubmissionLogVO>(
    "/homework/resSubmissionLog/get/vo",
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 POST /homework/resSubmissionLog/list/page */
export async function listResSubmissionLogByPage(
  body: API.ResSubmissionLogQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageResSubmissionLog>(
    "/homework/resSubmissionLog/list/page",
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

/** 此处后端没有提供注释 POST /homework/resSubmissionLog/list/page/vo */
export async function listResSubmissionLogVoByPage(
  body: API.ResSubmissionLogQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageResSubmissionLogVO>(
    "/homework/resSubmissionLog/list/page/vo",
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

/** 此处后端没有提供注释 POST /homework/resSubmissionLog/update */
export async function updateResSubmissionLog(
  body: API.ResSubmissionLogUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/homework/resSubmissionLog/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
