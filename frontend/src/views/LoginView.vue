<template>
  <main class="login-page">
    <section class="login-story" aria-label="产品介绍">
      <div class="story-grid" aria-hidden="true"></div>
      <div class="story-top">
        <div class="brand-mark brand-mark--light">履</div>
        <span class="brand-word brand-word--light">履迹 · Resume Studio</span>
      </div>

      <div class="story-copy">
        <p class="eyebrow">CRAFT YOUR STORY</p>
        <h1>把经历，<br />写成你的<span>下一章。</span></h1>
        <p class="story-lead">
          一处编辑，实时预览。用清晰、克制而专业的版式，让每一段积累都被看见。
        </p>
      </div>

      <div class="story-paper" aria-hidden="true">
        <div class="story-paper__head">
          <span>RESUME / 2026</span>
          <span>01</span>
        </div>
        <div class="story-paper__name">LIN<br />ZHOU</div>
        <div class="story-paper__rule"></div>
        <div class="story-paper__line story-paper__line--long"></div>
        <div class="story-paper__line"></div>
        <div class="story-paper__line story-paper__line--short"></div>
        <div class="story-paper__chips">
          <span>策略</span><span>设计</span><span>技术</span>
        </div>
      </div>

      <p class="story-foot">数据存储于私有 MySQL · Redis 安全会话 · 自动版本校验</p>
    </section>

    <section class="login-panel">
      <div class="login-box">
        <div class="login-mobile-brand">
          <div class="brand-mark">履</div>
          <span class="brand-word">履迹 · Resume Studio</span>
        </div>

        <RouterLink class="login-back" to="/">
          <ArrowLeft :size="15" /> 返回公开主页
        </RouterLink>

        <p class="eyebrow eyebrow--dark">WELCOME BACK</p>
        <h2>登录工作台</h2>
        <p class="login-subtitle">继续完善你的个人履历与项目档案。</p>

        <form class="login-form" @submit.prevent="submit">
          <label class="field">
            <span class="field__label">管理员账号</span>
            <span class="field__control">
              <UserRound :size="18" aria-hidden="true" />
              <input
                v-model.trim="username"
                type="text"
                autocomplete="username"
                placeholder="请输入账号"
                maxlength="80"
                required
                autofocus
              />
            </span>
          </label>

          <label class="field">
            <span class="field__label">登录密码</span>
            <span class="field__control">
              <LockKeyhole :size="18" aria-hidden="true" />
              <input
                v-model="password"
                :type="showPassword ? 'text' : 'password'"
                autocomplete="current-password"
                placeholder="请输入密码"
                maxlength="200"
                required
              />
              <button
                class="icon-button icon-button--plain"
                type="button"
                :aria-label="showPassword ? '隐藏密码' : '显示密码'"
                @click="showPassword = !showPassword"
              >
                <EyeOff v-if="showPassword" :size="18" />
                <Eye v-else :size="18" />
              </button>
            </span>
          </label>

          <p v-if="errorMessage" class="form-error" role="alert">{{ errorMessage }}</p>

          <button class="button button--primary login-submit" type="submit" :disabled="auth.loading">
            <LoaderCircle v-if="auth.loading" class="spin" :size="19" />
            <span>{{ auth.loading ? '正在验证…' : '进入工作台' }}</span>
            <ArrowRight v-if="!auth.loading" :size="19" />
          </button>
        </form>

        <div class="login-note">
          <ShieldCheck :size="18" aria-hidden="true" />
          <p>账号由部署环境变量初始化。首次登录后请立即在服务端更换高强度密码。</p>
        </div>
      </div>

      <p class="login-copyright">© {{ year }} Resume Studio · 为职业叙事而设计</p>
    </section>
  </main>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ArrowRight,
  Eye,
  EyeOff,
  LoaderCircle,
  LockKeyhole,
  ShieldCheck,
  UserRound,
} from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()

const username = ref('')
const password = ref('')
const showPassword = ref(false)
const errorMessage = ref('')
const year = computed(() => new Date().getFullYear())

async function submit() {
  errorMessage.value = ''
  if (!username.value || !password.value) {
    errorMessage.value = '请输入账号和密码'
    return
  }

  try {
    await auth.login(username.value, password.value)
    const requested = typeof route.query.redirect === 'string' ? route.query.redirect : ''
    const redirect = requested.startsWith('/admin') ? requested : '/admin'
    await router.replace(redirect)
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '登录失败，请稍后重试'
  }
}
</script>





