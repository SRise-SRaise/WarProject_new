// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 此处后端没有提供注释 POST /experiment/eduExperiment/add */
export async function addEduExperiment(
  body: API.EduExperimentAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/experiment/eduExperiment/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /experiment/eduExperiment/delete */
export async function deleteEduExperiment(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteEduExperimentParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/experiment/eduExperiment/delete", {
    method: "POST",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /experiment/eduExperiment/get/vo */
export async function getEduExperimentVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getEduExperimentVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseEduExperimentVO>(
    "/experiment/eduExperiment/get/vo",
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 POST /experiment/eduExperiment/list/page */
export async function listEduExperimentByPage(
  body: API.EduExperimentQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageEduExperiment>(
    "/experiment/eduExperiment/list/page",
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

/** 此处后端没有提供注释 POST /experiment/eduExperiment/list/page/vo */
export async function listEduExperimentVoByPage(
  body: API.EduExperimentQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageEduExperimentVO>(
    "/experiment/eduExperiment/list/page/vo",
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

/**
 * 上传实验指导书文件（复用 attachment 上传接口，resultId=0/studentId=0 标识教师端上传）
 * POST /experiment/attachment/upload
 */
export async function uploadExperimentInstruction(
  experimentId: number | string,
  file: File,
  options?: { [key: string]: any }
) {
  const formData = new FormData()
  formData.append('file', file)
  // 教师端指导书：用 experimentId 作为 resultId，studentId=0 标识非学生上传
  formData.append('resultId', String(experimentId))
  formData.append('studentId', '0')
  return request<any>('/experiment/attachment/upload', {
    method: 'POST',
    data: formData,
    ...(options || {})
  })
}

/** 从 docx 文件导入实验步骤题目 POST /experiment/eduExperiment/import/docx */
export async function importExperimentFromDocx(
  experimentId: number | string,
  file: File,
  options?: { [key: string]: any }
) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('experimentId', String(experimentId))
  return request<any>('/experiment/eduExperiment/import/docx', {
    method: 'POST',
    data: formData,
    ...(options || {})
  })
}

/** 触发实验文档导入模板下载（通过 axios blob 方式接收二进制流，再构造 a 标签下载） */
export async function downloadExperimentTemplate(): Promise<void> {
  const response = await request('/experiment/eduExperiment/template/download', {
    method: 'GET',
    responseType: 'blob',
  } as any)
  const blob = new Blob([(response as any).data], {
    type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
  })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = '实验文档导入模板.docx'
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(url)
}

/** 此处后端没有提供注释 POST /experiment/eduExperiment/update */
export async function updateEduExperiment(
  body: API.EduExperimentUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/experiment/eduExperiment/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
