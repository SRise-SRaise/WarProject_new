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

/** 上传实验指导书文件 POST /experiment/eduExperiment/upload/instruction */
export async function uploadExperimentInstruction(
  experimentId: number | string,
  file: File,
  options?: { [key: string]: any }
) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('experimentId', String(experimentId))
  return request<API.BaseResponseString>('/experiment/eduExperiment/upload/instruction', {
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

/** 获取实验文档导入模板下载地址（直接返回完整URL供下载使用） */
export function getExperimentTemplateDownloadUrl(): string {
  return '/api/experiment/eduExperiment/template/download'
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
