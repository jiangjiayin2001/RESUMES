<template>
  <EditorSection
    eyebrow="02 / EDUCATION"
    title="教育经历"
    description="按时间倒序排列。学校、专业与学位会自动组合进预览区。"
  >
    <template #actions>
      <button class="button button--soft" type="button" @click="addItem">
        <Plus :size="17" />
        添加教育经历
      </button>
    </template>

    <div v-if="resume.educations.length" class="repeat-list">
      <article v-for="(item, index) in resume.educations" :key="index" class="repeat-card">
        <header class="repeat-card__head">
          <div class="repeat-card__title">
            <span class="repeat-card__index">{{ String(index + 1).padStart(2, '0') }}</span>
            <div>
              <strong>{{ item.school || '未填写学校' }}</strong>
              <span>{{ [item.major, item.degree].filter(Boolean).join(' · ') || '教育经历' }}</span>
            </div>
          </div>
          <div class="row-actions">
            <button
              class="icon-button"
              type="button"
              aria-label="上移"
              :disabled="index === 0"
              @click="move(index, -1)"
            >
              <ArrowUp :size="16" />
            </button>
            <button
              class="icon-button"
              type="button"
              aria-label="下移"
              :disabled="index === resume.educations.length - 1"
              @click="move(index, 1)"
            >
              <ArrowDown :size="16" />
            </button>
            <button class="icon-button icon-button--danger" type="button" aria-label="删除" @click="removeItem(index)">
              <Trash2 :size="16" />
            </button>
          </div>
        </header>

        <div class="form-grid">
          <label class="field">
            <span class="field__label">学校</span>
            <input v-model.trim="item.school" type="text" maxlength="160" placeholder="学校名称" />
          </label>
          <label class="field">
            <span class="field__label">专业</span>
            <input v-model.trim="item.major" type="text" maxlength="160" placeholder="专业名称" />
          </label>
          <label class="field">
            <span class="field__label">学历 / 学位</span>
            <select v-model="item.degree">
              <option value="">请选择</option>
              <option v-for="degree in degrees" :key="degree" :value="degree">{{ degree }}</option>
            </select>
          </label>
          <label class="field">
            <span class="field__label">GPA / 排名</span>
            <input v-model.trim="item.gpa" type="text" maxlength="40" placeholder="例如：3.8 / 4.0，专业前 10%" />
          </label>
          <label class="field">
            <span class="field__label">开始时间</span>
            <input v-model.trim="item.startDate" type="text" maxlength="20" placeholder="2022.09" />
          </label>
          <label class="field">
            <span class="field__label">结束时间</span>
            <input v-model.trim="item.endDate" type="text" maxlength="20" placeholder="2026.06 / 至今" />
          </label>
          <label class="field field--full">
            <span class="field__label">教育描述</span>
            <textarea
              v-model="item.description"
              rows="4"
              maxlength="2000"
              placeholder="可填写主修方向、荣誉奖项、学术成果或校园角色。"
            ></textarea>
          </label>
        </div>
      </article>
    </div>

    <div v-else class="empty-editor">
      <GraduationCap :size="28" />
      <strong>还没有教育经历</strong>
      <p>添加学校与专业信息，让预览区形成完整的学习轨迹。</p>
      <button class="button button--primary" type="button" @click="addItem">
        <Plus :size="17" /> 添加第一条
      </button>
    </div>
  </EditorSection>
</template>

<script setup lang="ts">
import { ArrowDown, ArrowUp, GraduationCap, Plus, Trash2 } from 'lucide-vue-next'
import EditorSection from '@/components/EditorSection.vue'
import type { Education, ResumeData } from '@/types/resume'

const props = defineProps<{
  resume: ResumeData
}>()

const degrees = ['大专', '本科', '硕士', '博士', '其他']

function addItem() {
  props.resume.educations.push({
    school: '',
    major: '',
    degree: '本科',
    startDate: '',
    endDate: '',
    gpa: '',
    description: '',
  })
}

function removeItem(index: number) {
  props.resume.educations.splice(index, 1)
}

function move(index: number, offset: number) {
  const target = index + offset
  if (target < 0 || target >= props.resume.educations.length) return
  const [item] = props.resume.educations.splice(index, 1)
  props.resume.educations.splice(target, 0, item as Education)
}
</script>
