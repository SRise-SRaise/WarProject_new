// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 此处后端没有提供注释 POST /homework/resExerciseRecord/add */
export async function addResExerciseRecord(
  body: API.ResExerciseRecordAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/homework/resExerciseRecord/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /homework/resExerciseRecord/delete */
export async function deleteResExerciseRecord(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteResExerciseRecordParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>(
    "/homework/resExerciseRecord/delete",
    {
      method: "POST",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 GET /homework/resExerciseRecord/get/vo */
export async function getResExerciseRecordVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getResExerciseRecordVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseResExerciseRecordVO>(
    "/homework/resExerciseRecord/get/vo",
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 POST /homework/resExerciseRecord/list/page */
export async function listResExerciseRecordByPage(
  body: API.ResExerciseRecordQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageResExerciseRecord>(
    "/homework/resExerciseRecord/list/page",
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

/** 此处后端没有提供注释 POST /homework/resExerciseRecord/list/page/vo */
export async function listResExerciseRecordVoByPage(
  body: API.ResExerciseRecordQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageResExerciseRecordVO>(
    "/homework/resExerciseRecord/list/page/vo",
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

/** 此处后端没有提供注释 POST /homework/resExerciseRecord/update */
export async function updateResExerciseRecord(
  body: API.ResExerciseRecordUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>(
    "/homework/resExerciseRecord/update",
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
