// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 此处后端没有提供注释 POST /user/sysStudentLog/add */
export async function addSysStudentLog(
  body: API.SysStudentLogAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/user/sysStudentLog/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /user/sysStudentLog/delete */
export async function deleteSysStudentLog(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteSysStudentLogParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/user/sysStudentLog/delete", {
    method: "POST",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /user/sysStudentLog/get/vo */
export async function getSysStudentLogVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getSysStudentLogVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseSysStudentLogVO>(
    "/user/sysStudentLog/get/vo",
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 POST /user/sysStudentLog/list/page */
export async function listSysStudentLogByPage(
  body: API.SysStudentLogQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageSysStudentLog>(
    "/user/sysStudentLog/list/page",
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

/** 此处后端没有提供注释 POST /user/sysStudentLog/list/page/vo */
export async function listSysStudentLogVoByPage(
  body: API.SysStudentLogQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageSysStudentLogVO>(
    "/user/sysStudentLog/list/page/vo",
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

/** 此处后端没有提供注释 POST /user/sysStudentLog/update */
export async function updateSysStudentLog(
  body: API.SysStudentLogUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/user/sysStudentLog/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
