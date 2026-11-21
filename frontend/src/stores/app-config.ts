import { defineStore } from 'pinia'
import { ref } from 'vue'
import { fetchResumeFormConfig } from '@/api/config'
import type { ResumeFormConfig } from '@/types/config'

export const useAppConfigStore = defineStore('app-config', () => {
  const resumeForm = ref<ResumeFormConfig | null>(null)
  const loading = ref(false)

  async function loadResumeFormConfig() {
    if (resumeForm.value || loading.value) return
    loading.value = true
    try {
      resumeForm.value = await fetchResumeFormConfig()
    } finally {
      loading.value = false
    }
  }

  return {
    resumeForm,
    loading,
    loadResumeFormConfig,
  }
})
