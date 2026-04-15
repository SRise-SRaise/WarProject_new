// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 此处后端没有提供注释 POST /exam/relPaperQuestion/add */
export async function addRelPaperQuestion(
  body: API.RelPaperQuestionAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/relPaperQuestion/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /exam/relPaperQuestion/delete */
export async function deleteRelPaperQuestion(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteRelPaperQuestionParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/relPaperQuestion/delete", {
    method: "POST",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /exam/relPaperQuestion/get/vo */
export async function getRelPaperQuestionVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getRelPaperQuestionVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseRelPaperQuestionVO>(
    "/exam/relPaperQuestion/get/vo",
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 POST /exam/relPaperQuestion/list/page */
export async function listRelPaperQuestionByPage(
  body: API.RelPaperQuestionQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageRelPaperQuestion>(
    "/exam/relPaperQuestion/list/page",
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

/** 此处后端没有提供注释 POST /exam/relPaperQuestion/list/page/vo */
export async function listRelPaperQuestionVoByPage(
  body: API.RelPaperQuestionQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageRelPaperQuestionVO>(
    "/exam/relPaperQuestion/list/page/vo",
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

/** 此处后端没有提供注释 POST /exam/relPaperQuestion/update */
export async function updateRelPaperQuestion(
  body: API.RelPaperQuestionUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/relPaperQuestion/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
