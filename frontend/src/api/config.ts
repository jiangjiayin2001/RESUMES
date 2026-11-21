import { http } from '@/api/http'
import { API_ROUTES } from '@/config/api-routes'
import type { ResumeFormConfig } from '@/types/config'

export async function fetchResumeFormConfig(): Promise<ResumeFormConfig> {
  const { data } = await http.get<ResumeFormConfig>(API_ROUTES.CONFIG.RESUME_FORM)
  return data
}
