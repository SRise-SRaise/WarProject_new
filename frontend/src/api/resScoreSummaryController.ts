// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 此处后端没有提供注释 POST /exam/resScoreSummary/add */
export async function addResScoreSummary(
  body: API.ResScoreSummaryAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/resScoreSummary/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /exam/resScoreSummary/delete */
export async function deleteResScoreSummary(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteResScoreSummaryParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/resScoreSummary/delete", {
    method: "POST",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /exam/resScoreSummary/get/vo */
export async function getResScoreSummaryVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getResScoreSummaryVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseResScoreSummaryVO>(
    "/exam/resScoreSummary/get/vo",
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 POST /exam/resScoreSummary/list/page */
export async function listResScoreSummaryByPage(
  body: API.ResScoreSummaryQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageResScoreSummary>(
    "/exam/resScoreSummary/list/page",
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

/** 此处后端没有提供注释 POST /exam/resScoreSummary/list/page/vo */
export async function listResScoreSummaryVoByPage(
  body: API.ResScoreSummaryQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageResScoreSummaryVO>(
    "/exam/resScoreSummary/list/page/vo",
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

/** 此处后端没有提供注释 POST /exam/resScoreSummary/update */
export async function updateResScoreSummary(
  body: API.ResScoreSummaryUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/resScoreSummary/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
