// @ts-ignore
/* eslint-disable */
import request from "@/request";

/** 此处后端没有提供注释 POST /auth/change/password */
export async function changePassword(
  body: API.AuthChangePasswordRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/auth/change/password", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /auth/get/login */
export async function getLoginUser1(options?: { [key: string]: any }) {
  return request<API.BaseResponseLoginPrincipal>("/auth/get/login", {
    method: "GET",
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /auth/login */
export async function login(
  body: API.AuthLoginRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseAuthLoginVO>("/auth/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /auth/logout */
export async function logout(options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>("/auth/logout", {
    method: "POST",
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /auth/register */
export async function register(
  body: API.AuthRegisterRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseAuthLoginVO>("/auth/register", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
