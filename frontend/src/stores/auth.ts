import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { currentUser, login as loginRequest, logout as logoutRequest } from '@/api/auth'
import { ensureCsrfToken } from '@/api/auth'
import type { AuthUser } from '@/types/resume'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<AuthUser | null>(null)
  const initialized = ref(false)
  const loading = ref(false)

  const isAuthenticated = computed(() => user.value !== null)

  async function initialize() {
    if (initialized.value) return
    try {
      await ensureCsrfToken()
      user.value = await currentUser()
    } catch {
      user.value = null
    } finally {
      initialized.value = true
    }
  }

  async function login(username: string, password: string) {
    loading.value = true
    try {
      user.value = await loginRequest({ username, password })
      initialized.value = true
    } finally {
      loading.value = false
    }
  }

  async function logout() {
    try {
      if (user.value) await logoutRequest()
    } finally {
      user.value = null
    }
  }

  function clear() {
    user.value = null
  }

  return {
    user,
    initialized,
    loading,
    isAuthenticated,
    initialize,
    login,
    logout,
    clear,
  }
})
