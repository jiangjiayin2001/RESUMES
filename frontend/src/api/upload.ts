import { http } from '@/api/http'
import { API_ROUTES } from '@/config/api-routes'

export interface ImageUploadResponse {
  url: string
  provider: 'local' | 'oss'
}

export interface ImageUploadConfig {
  maxImageBytes: number
  maxImageDimension: number
  allowedFormats: string[]
  allowedContentTypes: string[]
  provider: 'local' | 'oss'
}

export async function fetchImageUploadConfig(): Promise<ImageUploadConfig> {
  const { data } = await http.get<ImageUploadConfig>(API_ROUTES.UPLOADS.CONFIG)
  return data
}

export async function uploadImage(file: File): Promise<ImageUploadResponse> {
  const formData = new FormData()
  formData.append('file', file)
  const { data } = await http.post<ImageUploadResponse>(API_ROUTES.UPLOADS.IMAGES, formData, {
    timeout: 30000,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
  return data
}
