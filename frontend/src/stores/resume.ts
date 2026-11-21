import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { fetchCurrentResume, saveCurrentResume } from '@/api/resume'
import type { ResumeData } from '@/types/resume'

export const useResumeStore = defineStore('resume', () => {
  const draft = ref<ResumeData | null>(null)
  const savedSnapshot = ref('')
  const loading = ref(false)
  const saving = ref(false)
  const error = ref('')
  const lastSavedAt = ref<Date | null>(null)

  const isDirty = computed(
    () => draft.value !== null && JSON.stringify(draft.value) !== savedSnapshot.value,
  )

  const completeness = computed(() => {
    if (!draft.value) return 0
    const resume = draft.value
    const checks = [
      Boolean(resume.fullName.trim()),
      Boolean(resume.headline.trim()),
      Boolean(resume.email.trim() || resume.phone.trim()),
      Boolean(resume.summary.trim()),
      resume.educations.some((item) => item.school.trim() && item.major.trim()),
      resume.campusExperiences.some((item) => item.organization.trim()),
      resume.courses.some((item) => item.name.trim()),
      resume.skills.some((item) => item.name.trim()),
      resume.projects.some((item) => item.name.trim()),
    ]
    return Math.round((checks.filter(Boolean).length / checks.length) * 100)
  })

  function capture(resume: ResumeData) {
    draft.value = resume
    savedSnapshot.value = JSON.stringify(resume)
    lastSavedAt.value = resume.updatedAt ? new Date(resume.updatedAt) : null
  }

  async function load() {
    loading.value = true
    error.value = ''
    try {
      capture(await fetchCurrentResume())
    } catch (exception) {
      error.value = exception instanceof Error ? exception.message : '简历加载失败'
      throw exception
    } finally {
      loading.value = false
    }
  }

  async function save() {
    if (!draft.value || saving.value) return
    saving.value = true
    error.value = ''
    try {
      const saved = await saveCurrentResume(draft.value)
      capture(saved)
      lastSavedAt.value = new Date()
    } catch (exception) {
      error.value = exception instanceof Error ? exception.message : '简历保存失败'
      throw exception
    } finally {
      saving.value = false
    }
  }

  function reset() {
    if (savedSnapshot.value) {
      draft.value = JSON.parse(savedSnapshot.value) as ResumeData
    }
  }

  return {
    draft,
    loading,
    saving,
    error,
    lastSavedAt,
    isDirty,
    completeness,
    load,
    save,
    reset,
  }
})
