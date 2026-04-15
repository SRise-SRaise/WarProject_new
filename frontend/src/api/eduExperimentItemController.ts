// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 此处后端没有提供注释 POST /experiment/eduExperimentItem/add */
export async function addEduExperimentItem(
  body: API.EduExperimentItemAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/experiment/eduExperimentItem/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /experiment/eduExperimentItem/delete */
export async function deleteEduExperimentItem(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteEduExperimentItemParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>(
    "/experiment/eduExperimentItem/delete",
    {
      method: "POST",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 GET /experiment/eduExperimentItem/get/vo */
export async function getEduExperimentItemVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getEduExperimentItemVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseEduExperimentItemVO>(
    "/experiment/eduExperimentItem/get/vo",
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 POST /experiment/eduExperimentItem/list/page */
export async function listEduExperimentItemByPage(
  body: API.EduExperimentItemQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageEduExperimentItem>(
    "/experiment/eduExperimentItem/list/page",
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

/** 此处后端没有提供注释 POST /experiment/eduExperimentItem/list/page/vo */
export async function listEduExperimentItemVoByPage(
  body: API.EduExperimentItemQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageEduExperimentItemVO>(
    "/experiment/eduExperimentItem/list/page/vo",
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

/** 此处后端没有提供注释 POST /experiment/eduExperimentItem/update */
export async function updateEduExperimentItem(
  body: API.EduExperimentItemUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>(
    "/experiment/eduExperimentItem/update",
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
