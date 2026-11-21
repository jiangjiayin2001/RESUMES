<template>
  <EditorSection
    eyebrow="01 / PROFILE"
    title="基本信息"
    description="姓名与一句话定位会构成简历的第一印象，建议简洁、具体、可验证。"
  >
    <div class="form-grid">
      <label class="field field--full">
        <span class="field__label">简历名称</span>
        <input v-model.trim="resume.title" type="text" maxlength="120" placeholder="例如：产品经理求职简历" />
      </label>

      <label class="field">
        <span class="field__label">姓名 <em>必填</em></span>
        <input v-model.trim="resume.fullName" type="text" maxlength="100" placeholder="你的姓名" />
      </label>

      <label class="field">
        <span class="field__label">职业定位</span>
        <input
          v-model.trim="resume.headline"
          type="text"
          maxlength="160"
          placeholder="例如：Java 后端开发 / 2026 届"
        />
      </label>

      <label class="field">
        <span class="field__label">邮箱</span>
        <input v-model.trim="resume.email" type="email" maxlength="160" placeholder="name@example.com" />
      </label>

      <label class="field">
        <span class="field__label">手机</span>
        <input v-model.trim="resume.phone" type="tel" maxlength="50" placeholder="+86 138 0000 0000" />
      </label>

      <label class="field">
        <span class="field__label">所在城市</span>
        <input v-model.trim="resume.location" type="text" maxlength="120" placeholder="上海 · 可接受远程" />
      </label>

      <label class="field">
        <span class="field__label">个人网站</span>
        <input v-model.trim="resume.website" type="url" maxlength="300" placeholder="https://your.site" />
      </label>

      <label class="field">
        <span class="field__label">GitHub</span>
        <input v-model.trim="resume.github" type="url" maxlength="300" placeholder="https://github.com/..." />
      </label>

      <div class="field field--full">
        <span class="field__label">个人照片</span>
        <AvatarUploader v-model="resume.avatarUrl" />
      </div>

      <label class="field field--full">
        <span class="field__label">
          个人简介
          <small>{{ resume.summary.length }} / 4000</small>
        </span>
        <textarea
          v-model="resume.summary"
          rows="6"
          maxlength="4000"
          placeholder="用 3–5 句话说明你的专业方向、核心能力和代表性成果。"
        ></textarea>
      </label>
    </div>
  </EditorSection>
</template>

<script setup lang="ts">
import AvatarUploader from '@/components/AvatarUploader.vue'
import EditorSection from '@/components/EditorSection.vue'
import type { ResumeData } from '@/types/resume'

defineProps<{
  resume: ResumeData
}>()
</script>

