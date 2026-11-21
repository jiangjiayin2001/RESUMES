<template>
  <div class="modal-backdrop" role="presentation" @mousedown.self="$emit('close')">
    <section class="password-modal" role="dialog" aria-modal="true" aria-labelledby="password-title">
      <header class="password-modal__head">
        <div>
          <p class="eyebrow eyebrow--dark">ACCOUNT SECURITY</p>
          <h2 id="password-title">修改登录密码</h2>
        </div>
        <button class="icon-button" type="button" aria-label="关闭" @click="$emit('close')">
          <X :size="18" />
        </button>
      </header>

      <form class="password-modal__body" @submit.prevent="submit">
        <label class="field">
          <span class="field__label">当前密码</span>
          <input v-model="currentPassword" type="password" autocomplete="current-password" maxlength="200" required />
        </label>
        <label class="field">
          <span class="field__label">新密码 <small>12–72 位</small></span>
          <input v-model="newPassword" type="password" autocomplete="new-password" minlength="12" maxlength="72" required />
        </label>
        <label class="field">
          <span class="field__label">确认新密码</span>
          <input v-model="confirmation" type="password" autocomplete="new-password" minlength="12" maxlength="72" required />
        </label>

        <p v-if="errorMessage" class="form-error" role="alert">{{ errorMessage }}</p>

        <div class="password-modal__footer">
          <button class="button button--soft" type="button" @click="$emit('close')">取消</button>
          <button class="button button--primary" type="submit" :disabled="submitting">
            <LoaderCircle v-if="submitting" class="spin" :size="17" />
            <ShieldCheck v-else :size="17" />
            {{ submitting ? '正在更新' : '更新密码' }}
          </button>
        </div>
      </form>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { LoaderCircle, ShieldCheck, X } from 'lucide-vue-next'
import { changePassword } from '@/api/auth'

const emit = defineEmits<{
  close: []
  success: []
}>()

const currentPassword = ref('')
const newPassword = ref('')
const confirmation = ref('')
const errorMessage = ref('')
const submitting = ref(false)

async function submit() {
  errorMessage.value = ''
  if (newPassword.value.length < 12) {
    errorMessage.value = '新密码至少需要 12 个字符'
    return
  }
  if (newPassword.value !== confirmation.value) {
    errorMessage.value = '两次输入的新密码不一致'
    return
  }
  if (newPassword.value === currentPassword.value) {
    errorMessage.value = '新密码不能与当前密码相同'
    return
  }

  submitting.value = true
  try {
    await changePassword(currentPassword.value, newPassword.value)
    emit('success')
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '密码更新失败'
  } finally {
    submitting.value = false
  }
}
</script>
