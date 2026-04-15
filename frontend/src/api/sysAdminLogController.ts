// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 此处后端没有提供注释 POST /common/sysAdminLog/add */
export async function addSysAdminLog(
  body: API.SysAdminLogAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/common/sysAdminLog/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /common/sysAdminLog/delete */
export async function deleteSysAdminLog(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteSysAdminLogParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/common/sysAdminLog/delete", {
    method: "POST",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /common/sysAdminLog/get/vo */
export async function getSysAdminLogVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getSysAdminLogVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseSysAdminLogVO>("/common/sysAdminLog/get/vo", {
    method: "GET",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /common/sysAdminLog/list/page */
export async function listSysAdminLogByPage(
  body: API.SysAdminLogQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageSysAdminLog>(
    "/common/sysAdminLog/list/page",
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

/** 此处后端没有提供注释 POST /common/sysAdminLog/list/page/vo */
export async function listSysAdminLogVoByPage(
  body: API.SysAdminLogQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageSysAdminLogVO>(
    "/common/sysAdminLog/list/page/vo",
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

/** 此处后端没有提供注释 POST /common/sysAdminLog/update */
export async function updateSysAdminLog(
  body: API.SysAdminLogUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/common/sysAdminLog/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
