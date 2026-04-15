// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 此处后端没有提供注释 POST /common/sysConfig/add */
export async function addSysConfig(
  body: API.SysConfigAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/common/sysConfig/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /common/sysConfig/delete */
export async function deleteSysConfig(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteSysConfigParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/common/sysConfig/delete", {
    method: "POST",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /common/sysConfig/get/vo */
export async function getSysConfigVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getSysConfigVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseSysConfigVO>("/common/sysConfig/get/vo", {
    method: "GET",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /common/sysConfig/list/page */
export async function listSysConfigByPage(
  body: API.SysConfigQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageSysConfig>("/common/sysConfig/list/page", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /common/sysConfig/list/page/vo */
export async function listSysConfigVoByPage(
  body: API.SysConfigQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageSysConfigVO>(
    "/common/sysConfig/list/page/vo",
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

/** 此处后端没有提供注释 POST /common/sysConfig/update */
export async function updateSysConfig(
  body: API.SysConfigUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/common/sysConfig/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
