// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 此处后端没有提供注释 POST /exam/resExamRecord/add */
export async function addResExamRecord(
  body: API.ResExamRecordAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/resExamRecord/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /exam/resExamRecord/delete */
export async function deleteResExamRecord(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteResExamRecordParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/resExamRecord/delete", {
    method: "POST",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /exam/resExamRecord/get/vo */
export async function getResExamRecordVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getResExamRecordVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseResExamRecordVO>(
    "/exam/resExamRecord/get/vo",
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 GET /exam/resExamRecord/grading/cards */
export async function listExamRecordCards(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listExamRecordCardsParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseListMapStringObject>(
    "/exam/resExamRecord/grading/cards",
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 GET /exam/resExamRecord/grading/detail */
export async function getStudentAnswerRecordDetail(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getStudentAnswerRecordDetailParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseMapStringObject>(
    "/exam/resExamRecord/grading/detail",
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 POST /exam/resExamRecord/grading/grade */
export async function gradeAnswer(
  body: Record<string, any>,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/resExamRecord/grading/grade", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /exam/resExamRecord/grading/records */
export async function listStudentAnswerRecords(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listStudentAnswerRecordsParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseListMapStringObject>(
    "/exam/resExamRecord/grading/records",
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 GET /exam/resExamRecord/grading/stats */
export async function getScoreStatistics(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getScoreStatisticsParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseMapStringObject>(
    "/exam/resExamRecord/grading/stats",
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 POST /exam/resExamRecord/list/page */
export async function listResExamRecordByPage(
  body: API.ResExamRecordQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageResExamRecord>(
    "/exam/resExamRecord/list/page",
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

/** 此处后端没有提供注释 POST /exam/resExamRecord/list/page/vo */
export async function listResExamRecordVoByPage(
  body: API.ResExamRecordQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageResExamRecordVO>(
    "/exam/resExamRecord/list/page/vo",
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

/** 此处后端没有提供注释 POST /exam/resExamRecord/student/submit */
export async function submitExam(
  body: Record<string, any>,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseMapStringObject>(
    "/exam/resExamRecord/student/submit",
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

/** 此处后端没有提供注释 POST /exam/resExamRecord/update */
export async function updateResExamRecord(
  body: API.ResExamRecordUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/resExamRecord/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
