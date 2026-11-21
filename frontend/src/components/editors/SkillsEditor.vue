<template>
  <EditorSection
    eyebrow="05 / SKILLS"
    title="专业技能"
    description="用分类和五级熟练度构成清晰的能力地图。建议优先填写与目标岗位最相关的技能。"
  >
    <template #actions>
      <button class="button button--soft" type="button" @click="addItem">
        <Plus :size="17" />
        添加技能
      </button>
    </template>

    <datalist id="skill-category-options">
      <option value="后端开发" />
      <option value="前端开发" />
      <option value="数据库" />
      <option value="工程工具" />
      <option value="设计能力" />
      <option value="通用能力" />
    </datalist>

    <div v-if="resume.skills.length" class="skill-editor-list">
      <article v-for="(item, index) in resume.skills" :key="index" class="skill-editor-card">
        <div class="skill-editor-card__top">
          <span class="skill-editor-card__index">{{ String(index + 1).padStart(2, '0') }}</span>
          <label class="field">
            <span class="field__label">技能名称</span>
            <input v-model.trim="item.name" type="text" maxlength="80" placeholder="例如：Java / Spring Boot" />
          </label>
          <label class="field">
            <span class="field__label">技能分类</span>
            <input
              v-model.trim="item.category"
              type="text"
              maxlength="80"
              list="skill-category-options"
              placeholder="例如：后端开发"
            />
          </label>
          <button class="icon-button icon-button--danger" type="button" aria-label="删除技能" @click="removeItem(index)">
            <Trash2 :size="16" />
          </button>
        </div>

        <div class="skill-level-row">
          <div class="skill-level-row__label">
            <span>熟练度</span>
            <strong>{{ levelLabel(item.level) }}</strong>
          </div>
          <div class="level-selector" role="group" :aria-label="`${item.name || '技能'}熟练度`">
            <button
              v-for="option in levelOptions"
              :key="option.value"
              type="button"
              :class="{ active: item.level === option.value, reached: option.value <= item.level }"
              :aria-pressed="item.level === option.value"
              @click="item.level = option.value"
            >
              <b>{{ option.value }}</b>
              <small>{{ option.label }}</small>
            </button>
          </div>
        </div>
      </article>
    </div>

    <div v-else class="empty-editor empty-editor--compact">
      <Sparkles :size="27" />
      <strong>还没有技能标签</strong>
      <p>从语言、框架、数据库、工具和通用能力中提炼你的优势。</p>
      <button class="button button--primary" type="button" @click="addItem">
        <Plus :size="17" /> 添加第一项技能
      </button>
    </div>
  </EditorSection>
</template>

<script setup lang="ts">
import { Plus, Sparkles, Trash2 } from 'lucide-vue-next'
import EditorSection from '@/components/EditorSection.vue'
import type { ResumeData } from '@/types/resume'

const props = defineProps<{
  resume: ResumeData
}>()

const levelOptions = [
  { value: 1, label: '了解' },
  { value: 2, label: '基础' },
  { value: 3, label: '熟练' },
  { value: 4, label: '精通' },
  { value: 5, label: '专家' },
]

function levelLabel(level: number): string {
  return levelOptions.find((option) => option.value === level)?.label ?? '熟练'
}

function addItem() {
  props.resume.skills.push({
    name: '',
    category: '',
    level: 3,
  })
}

function removeItem(index: number) {
  props.resume.skills.splice(index, 1)
}
</script>
