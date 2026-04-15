// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 此处后端没有提供注释 POST /homework/eduExerciseItem/add */
export async function addEduExerciseItem(
  body: API.EduExerciseItemAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/homework/eduExerciseItem/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /homework/eduExerciseItem/delete */
export async function deleteEduExerciseItem(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteEduExerciseItemParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/homework/eduExerciseItem/delete", {
    method: "POST",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /homework/eduExerciseItem/get/vo */
export async function getEduExerciseItemVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getEduExerciseItemVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseEduExerciseItemVO>(
    "/homework/eduExerciseItem/get/vo",
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 POST /homework/eduExerciseItem/list/page */
export async function listEduExerciseItemByPage(
  body: API.EduExerciseItemQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageEduExerciseItem>(
    "/homework/eduExerciseItem/list/page",
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

/** 此处后端没有提供注释 POST /homework/eduExerciseItem/list/page/vo */
export async function listEduExerciseItemVoByPage(
  body: API.EduExerciseItemQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageEduExerciseItemVO>(
    "/homework/eduExerciseItem/list/page/vo",
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

/** 此处后端没有提供注释 POST /homework/eduExerciseItem/update */
export async function updateEduExerciseItem(
  body: API.EduExerciseItemUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/homework/eduExerciseItem/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
