// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 此处后端没有提供注释 POST /exam/eduExam/add */
export async function addEduExam(
  body: API.EduExamAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/eduExam/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /exam/eduExam/admin/get */
export async function getAdminExamCard(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getAdminExamCardParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseMapStringObject>("/exam/eduExam/admin/get", {
    method: "GET",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /exam/eduExam/custom/add */
export async function addExamCustom(
  body: Record<string, any>,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong>("/exam/eduExam/custom/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /exam/eduExam/custom/update */
export async function updateExamCustom(
  body: Record<string, any>,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/eduExam/custom/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /exam/eduExam/delete */
export async function deleteEduExam(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteEduExamParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/eduExam/delete", {
    method: "POST",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /exam/eduExam/get/vo */
export async function getEduExamVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getEduExamVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseEduExamVO>("/exam/eduExam/get/vo", {
    method: "GET",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /exam/eduExam/grading/list */
export async function listGradingExams(options?: { [key: string]: any }) {
  return request<API.BaseResponseListMapStringObject>(
    "/exam/eduExam/grading/list",
    {
      method: "GET",
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 POST /exam/eduExam/list/page */
export async function listEduExamByPage(
  body: API.EduExamQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageEduExam>("/exam/eduExam/list/page", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /exam/eduExam/list/page/full */
export async function listExamPageFull(
  body: API.EduExamQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseMapStringObject>(
    "/exam/eduExam/list/page/full",
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

/** 此处后端没有提供注释 POST /exam/eduExam/list/page/vo */
export async function listEduExamVoByPage(
  body: API.EduExamQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageEduExamVO>("/exam/eduExam/list/page/vo", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /exam/eduExam/papers/options */
export async function listPaperOptions(options?: { [key: string]: any }) {
  return request<API.BaseResponseListMapStringObject>(
    "/exam/eduExam/papers/options",
    {
      method: "GET",
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 POST /exam/eduExam/publish */
export async function publishExam(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.publishExamParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/eduExam/publish", {
    method: "POST",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /exam/eduExam/stats */
export async function getExamStats(options?: { [key: string]: any }) {
  return request<API.BaseResponseMapStringObject>("/exam/eduExam/stats", {
    method: "GET",
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /exam/eduExam/student/detail */
export async function getStudentExamDetail(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getStudentExamDetailParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseMapStringObject>(
    "/exam/eduExam/student/detail",
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 GET /exam/eduExam/student/list */
export async function listPublishedExamsForStudent(options?: {
  [key: string]: any;
}) {
  return request<API.BaseResponseListMapStringObject>(
    "/exam/eduExam/student/list",
    {
      method: "GET",
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 POST /exam/eduExam/unpublish */
export async function unpublishExam(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.unpublishExamParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/eduExam/unpublish", {
    method: "POST",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /exam/eduExam/update */
export async function updateEduExam(
  body: API.EduExamUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/exam/eduExam/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
