import 'axios'

declare module 'axios' {
  interface AxiosRequestConfig<D = unknown> {
    requestType?: string
  }
}

export {}
