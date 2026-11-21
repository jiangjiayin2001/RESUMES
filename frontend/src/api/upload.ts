import { http } from '@/api/http'

export interface ImageUploadResponse {
  url: string
  provider: 'local' | 'oss'
}

export async function uploadImage(file: File): Promise<ImageUploadResponse> {
  const formData = new FormData()
  formData.append('file', file)
  const { data } = await http.post<ImageUploadResponse>('/uploads/images', formData, {
    timeout: 30000,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
  return data
}

