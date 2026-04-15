// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 此处后端没有提供注释 POST /user/authClass/add */
export async function addAuthClass(
  body: API.AuthClassAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/user/authClass/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /user/authClass/delete */
export async function deleteAuthClass(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteAuthClassParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/user/authClass/delete", {
    method: "POST",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /user/authClass/get/vo */
export async function getAuthClassVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getAuthClassVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseAuthClassVO>("/user/authClass/get/vo", {
    method: "GET",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /user/authClass/list/page */
export async function listAuthClassByPage(
  body: API.AuthClassQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageAuthClass>("/user/authClass/list/page", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /user/authClass/list/page/vo */
export async function listAuthClassVoByPage(
  body: API.AuthClassQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageAuthClassVO>(
    "/user/authClass/list/page/vo",
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

/** 此处后端没有提供注释 POST /user/authClass/update */
export async function updateAuthClass(
  body: API.AuthClassUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/user/authClass/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
