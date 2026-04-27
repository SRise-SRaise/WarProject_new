// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 此处后端没有提供注释 POST /homework/eduExercise/add */
export async function addEduExercise(
  body: API.EduExerciseAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/homework/eduExercise/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /homework/eduExercise/delete */
export async function deleteEduExercise(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteEduExerciseParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/homework/eduExercise/delete", {
    method: "POST",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /homework/eduExercise/get/vo */
export async function getEduExerciseVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getEduExerciseVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseEduExerciseVO>(
    "/homework/eduExercise/get/vo",
    {
      method: "GET",
      params: {
        ...params,
      },
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 POST /homework/eduExercise/list/page */
export async function listEduExerciseByPage(
  body: API.EduExerciseQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageEduExercise>(
    "/homework/eduExercise/list/page",
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

/** 此处后端没有提供注释 POST /homework/eduExercise/list/page/vo */
export async function listEduExerciseVoByPage(
  body: API.EduExerciseQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageEduExerciseVO>(
    "/homework/eduExercise/list/page/vo",
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

/** 此处后端没有提供注释 POST /homework/eduExercise/update */
export async function updateEduExercise(
  body: API.EduExerciseUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/homework/eduExercise/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 发布作业到指定班级 POST /homework/eduExercise/publish */
export async function publishExercise(
  body: API.ExercisePublishRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/homework/eduExercise/publish", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 关闭作业 POST /homework/eduExercise/close */
export async function closeExercise(
  params: { exerciseId: number },
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/homework/eduExercise/close", {
    method: "POST",
    params,
    ...(options || {}),
  });
}

/** 学生查询可见作业列表 POST /homework/eduExercise/listForStudent */
export async function listExerciseForStudent(
  body: API.StudentExerciseQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseListStudentExerciseVO>(
    "/homework/eduExercise/listForStudent",
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
