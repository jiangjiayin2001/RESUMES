import axios from 'axios'
import { ApiError } from '@/types/api'

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 12000,
  withCredentials: true,
  xsrfCookieName: 'XSRF-TOKEN',
  xsrfHeaderName: 'X-XSRF-TOKEN',
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json',
  },
})

http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (axios.isAxiosError(error)) {
      const status = error.response?.status ?? 0
      const data = error.response?.data as
        | { detail?: string; message?: string; title?: string }
        | undefined
      const message =
        data?.detail ||
        data?.message ||
        (status === 0 ? '网络连接异常，请稍后重试' : '请求未能完成，请稍后重试')

      if (status === 401 && !error.config?.url?.includes('/auth/')) {
        window.dispatchEvent(new CustomEvent('resume:unauthorized'))
      }

      return Promise.reject(new ApiError(message, status, data))
    }
    return Promise.reject(error)
  },
)
