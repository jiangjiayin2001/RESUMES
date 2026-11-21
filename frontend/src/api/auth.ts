import { http } from '@/api/http'
import { API_ROUTES } from '@/config/api-routes'
import type { AuthUser } from '@/types/resume'

interface LoginRequest {
  username: string
  password: string
}

export async function ensureCsrfToken(): Promise<void> {
  await http.get(API_ROUTES.AUTH.CSRF)
}

export async function login(payload: LoginRequest): Promise<AuthUser> {
  await ensureCsrfToken()
  const { data } = await http.post<AuthUser>(API_ROUTES.AUTH.LOGIN, payload)
  return data
}

export async function currentUser(): Promise<AuthUser> {
  const { data } = await http.get<AuthUser>(API_ROUTES.AUTH.ME)
  return data
}

export async function logout(): Promise<void> {
  await ensureCsrfToken()
  await http.post(API_ROUTES.AUTH.LOGOUT)
}

export async function changePassword(currentPassword: string, newPassword: string): Promise<void> {
  await ensureCsrfToken()
  await http.post(API_ROUTES.AUTH.PASSWORD, { currentPassword, newPassword })
}

