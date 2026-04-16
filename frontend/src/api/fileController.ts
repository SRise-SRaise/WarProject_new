// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 此处后端没有提供注释 POST /file/upload */
export async function uploadFile(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.uploadFileParams,
  file: File,
  options?: { [key: string]: any }
) {
  const formData = new FormData()
  formData.append('file', file)

  return request<API.BaseResponseString>("/file/upload", {
    method: "POST",
    params: {
      ...params,
    },
    data: formData,
    ...(options || {}),
  })
}
