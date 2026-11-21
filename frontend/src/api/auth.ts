import { http } from '@/api/http'
import type { AuthUser } from '@/types/resume'

interface LoginRequest {
  username: string
  password: string
}

export async function ensureCsrfToken(): Promise<void> {
  await http.get('/auth/csrf')
}

export async function login(payload: LoginRequest): Promise<AuthUser> {
  await ensureCsrfToken()
  const { data } = await http.post<AuthUser>('/auth/login', payload)
  return data
}

export async function currentUser(): Promise<AuthUser> {
  const { data } = await http.get<AuthUser>('/auth/me')
  return data
}

export async function logout(): Promise<void> {
  await ensureCsrfToken()
  await http.post('/auth/logout')
}

export async function changePassword(currentPassword: string, newPassword: string): Promise<void> {
  await ensureCsrfToken()
  await http.post('/auth/password', { currentPassword, newPassword })
}
