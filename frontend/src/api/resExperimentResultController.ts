// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 此处后端没有提供注释 POST /experiment/resExperimentResult/add */
export async function addResExperimentResult(
  body: API.ResExperimentResultAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>(
    "/experiment/resExperimentResult/add",
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

/** 此处后端没有提供注释 POST /experiment/resExperimentResult/delete */
export async function deleteResExperimentResult(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteResExperimentResultParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>(
    "/experiment/resExperimentResult/delete",
    {
      method: "POST",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 GET /experiment/resExperimentResult/get/vo */
export async function getResExperimentResultVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getResExperimentResultVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseResExperimentResultVO>(
    "/experiment/resExperimentResult/get/vo",
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 POST /experiment/resExperimentResult/list/page */
export async function listResExperimentResultByPage(
  body: API.ResExperimentResultQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageResExperimentResult>(
    "/experiment/resExperimentResult/list/page",
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

/** 此处后端没有提供注释 POST /experiment/resExperimentResult/list/page/vo */
export async function listResExperimentResultVoByPage(
  body: API.ResExperimentResultQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageResExperimentResultVO>(
    "/experiment/resExperimentResult/list/page/vo",
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

/** 此处后端没有提供注释 POST /experiment/resExperimentResult/update */
export async function updateResExperimentResult(
  body: API.ResExperimentResultUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>(
    "/experiment/resExperimentResult/update",
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
