// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 此处后端没有提供注释 POST /exam/eduQuestionType/add */
export async function addEduQuestionType(
  body: API.EduQuestionTypeAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/eduQuestionType/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /exam/eduQuestionType/delete */
export async function deleteEduQuestionType(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteEduQuestionTypeParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/eduQuestionType/delete", {
    method: "POST",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /exam/eduQuestionType/get/vo */
export async function getEduQuestionTypeVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getEduQuestionTypeVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseEduQuestionTypeVO>(
    "/exam/eduQuestionType/get/vo",
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 GET /exam/eduQuestionType/list/all */
export async function listAllEduQuestionTypes(options?: {
  [key: string]: any;
}) {
  return request<API.BaseResponseListMapStringObject>(
    "/exam/eduQuestionType/list/all",
    {
      method: "GET",
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 POST /exam/eduQuestionType/list/page */
export async function listEduQuestionTypeByPage(
  body: API.EduQuestionTypeQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageEduQuestionType>(
    "/exam/eduQuestionType/list/page",
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

/** 此处后端没有提供注释 POST /exam/eduQuestionType/list/page/vo */
export async function listEduQuestionTypeVoByPage(
  body: API.EduQuestionTypeQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageEduQuestionTypeVO>(
    "/exam/eduQuestionType/list/page/vo",
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

/** 此处后端没有提供注释 POST /exam/eduQuestionType/update */
export async function updateEduQuestionType(
  body: API.EduQuestionTypeUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/eduQuestionType/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
