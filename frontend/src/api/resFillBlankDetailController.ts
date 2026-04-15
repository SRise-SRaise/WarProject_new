// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 此处后端没有提供注释 POST /homework/resFillBlankDetail/add */
export async function addResFillBlankDetail(
  body: API.ResFillBlankDetailAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/homework/resFillBlankDetail/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /homework/resFillBlankDetail/delete */
export async function deleteResFillBlankDetail(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteResFillBlankDetailParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>(
    "/homework/resFillBlankDetail/delete",
    {
      method: "POST",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 GET /homework/resFillBlankDetail/get/vo */
export async function getResFillBlankDetailVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getResFillBlankDetailVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseResFillBlankDetailVO>(
    "/homework/resFillBlankDetail/get/vo",
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 POST /homework/resFillBlankDetail/list/page */
export async function listResFillBlankDetailByPage(
  body: API.ResFillBlankDetailQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageResFillBlankDetail>(
    "/homework/resFillBlankDetail/list/page",
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

/** 此处后端没有提供注释 POST /homework/resFillBlankDetail/list/page/vo */
export async function listResFillBlankDetailVoByPage(
  body: API.ResFillBlankDetailQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageResFillBlankDetailVO>(
    "/homework/resFillBlankDetail/list/page/vo",
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

/** 此处后端没有提供注释 POST /homework/resFillBlankDetail/update */
export async function updateResFillBlankDetail(
  body: API.ResFillBlankDetailUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>(
    "/homework/resFillBlankDetail/update",
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
