// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 此处后端没有提供注释 POST /user/authAssistant/add */
export async function addAuthAssistant(
  body: API.AuthAssistantAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/user/authAssistant/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /user/authAssistant/delete */
export async function deleteAuthAssistant(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteAuthAssistantParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/user/authAssistant/delete", {
    method: "POST",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /user/authAssistant/get/vo */
export async function getAuthAssistantVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getAuthAssistantVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseAuthAssistantVO>(
    "/user/authAssistant/get/vo",
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 POST /user/authAssistant/list/page */
export async function listAuthAssistantByPage(
  body: API.AuthAssistantQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageAuthAssistant>(
    "/user/authAssistant/list/page",
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

/** 此处后端没有提供注释 POST /user/authAssistant/list/page/vo */
export async function listAuthAssistantVoByPage(
  body: API.AuthAssistantQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageAuthAssistantVO>(
    "/user/authAssistant/list/page/vo",
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

/** 此处后端没有提供注释 POST /user/authAssistant/update */
export async function updateAuthAssistant(
  body: API.AuthAssistantUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/user/authAssistant/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
