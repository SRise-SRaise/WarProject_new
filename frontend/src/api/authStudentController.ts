// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 此处后端没有提供注释 POST /user/authStudent/add */
export async function addAuthStudent(
  body: API.AuthStudentAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/user/authStudent/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /user/authStudent/delete */
export async function deleteAuthStudent(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteAuthStudentParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/user/authStudent/delete", {
    method: "POST",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /user/authStudent/get/vo */
export async function getAuthStudentVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getAuthStudentVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseAuthStudentVO>("/user/authStudent/get/vo", {
    method: "GET",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /user/authStudent/list/page */
export async function listAuthStudentByPage(
  body: API.AuthStudentQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageAuthStudent>(
    "/user/authStudent/list/page",
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

/** 此处后端没有提供注释 POST /user/authStudent/list/page/vo */
export async function listAuthStudentVoByPage(
  body: API.AuthStudentQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageAuthStudentVO>(
    "/user/authStudent/list/page/vo",
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

/** 此处后端没有提供注释 POST /user/authStudent/update */
export async function updateAuthStudent(
  body: API.AuthStudentUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/user/authStudent/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
