// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 此处后端没有提供注释 POST /exam/eduQuestionBank/add */
export async function addEduQuestionBank(
  body: API.EduQuestionBankAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/eduQuestionBank/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /exam/eduQuestionBank/delete */
export async function deleteEduQuestionBank(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteEduQuestionBankParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/eduQuestionBank/delete", {
    method: "POST",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /exam/eduQuestionBank/get/vo */
export async function getEduQuestionBankVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getEduQuestionBankVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseEduQuestionBankVO>(
    "/exam/eduQuestionBank/get/vo",
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 GET /exam/eduQuestionBank/list/all */
export async function listAllQuestions(options?: { [key: string]: any }) {
  return request<API.BaseResponseListEduQuestionBank>(
    "/exam/eduQuestionBank/list/all",
    {
      method: "GET",
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 POST /exam/eduQuestionBank/list/page */
export async function listEduQuestionBankByPage(
  body: API.EduQuestionBankQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageEduQuestionBank>(
    "/exam/eduQuestionBank/list/page",
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

/** 此处后端没有提供注释 POST /exam/eduQuestionBank/list/page/vo */
export async function listEduQuestionBankVoByPage(
  body: API.EduQuestionBankQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageEduQuestionBankVO>(
    "/exam/eduQuestionBank/list/page/vo",
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

/** 此处后端没有提供注释 GET /exam/eduQuestionBank/stats */
export async function getQuestionStats(options?: { [key: string]: any }) {
  return request<API.BaseResponseMapStringObject>(
    "/exam/eduQuestionBank/stats",
    {
      method: "GET",
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 POST /exam/eduQuestionBank/update */
export async function updateEduQuestionBank(
  body: API.EduQuestionBankUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/eduQuestionBank/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
