// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 此处后端没有提供注释 POST /user/authTeacher/add */
export async function addAuthTeacher(
  body: API.AuthTeacherAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/user/authTeacher/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /user/authTeacher/delete */
export async function deleteAuthTeacher(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteAuthTeacherParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/user/authTeacher/delete", {
    method: "POST",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /user/authTeacher/get/vo */
export async function getAuthTeacherVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getAuthTeacherVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseAuthTeacherVO>("/user/authTeacher/get/vo", {
    method: "GET",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /user/authTeacher/list/page */
export async function listAuthTeacherByPage(
  body: API.AuthTeacherQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageAuthTeacher>(
    "/user/authTeacher/list/page",
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

/** 此处后端没有提供注释 POST /user/authTeacher/list/page/vo */
export async function listAuthTeacherVoByPage(
  body: API.AuthTeacherQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageAuthTeacherVO>(
    "/user/authTeacher/list/page/vo",
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

/** 此处后端没有提供注释 POST /user/authTeacher/update */
export async function updateAuthTeacher(
  body: API.AuthTeacherUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/user/authTeacher/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
