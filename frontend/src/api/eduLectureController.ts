// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 此处后端没有提供注释 POST /common/eduLecture/add */
export async function addEduLecture(
  body: API.EduLectureAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/common/eduLecture/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /common/eduLecture/delete */
export async function deleteEduLecture(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteEduLectureParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/common/eduLecture/delete", {
    method: "POST",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /common/eduLecture/get/vo */
export async function getEduLectureVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getEduLectureVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseEduLectureVO>("/common/eduLecture/get/vo", {
    method: "GET",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /common/eduLecture/list/page */
export async function listEduLectureByPage(
  body: API.EduLectureQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageEduLecture>(
    "/common/eduLecture/list/page",
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

/** 此处后端没有提供注释 POST /common/eduLecture/list/page/vo */
export async function listEduLectureVoByPage(
  body: API.EduLectureQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageEduLectureVO>(
    "/common/eduLecture/list/page/vo",
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

/** 此处后端没有提供注释 POST /common/eduLecture/update */
export async function updateEduLecture(
  body: API.EduLectureUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/common/eduLecture/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
