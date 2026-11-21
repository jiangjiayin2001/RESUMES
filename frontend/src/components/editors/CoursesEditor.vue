<template>
  <EditorSection
    eyebrow="04 / COURSES"
    title="主修课程"
    description="挑选与目标岗位最相关的课程，不必罗列全部课表。"
  >
    <template #actions>
      <button class="button button--soft" type="button" @click="addItem">
        <Plus :size="17" />
        添加课程
      </button>
    </template>

    <div v-if="resume.courses.length" class="course-list">
      <article v-for="(item, index) in resume.courses" :key="index" class="course-row">
        <span class="course-row__number">{{ String(index + 1).padStart(2, '0') }}</span>
        <label class="field">
          <span class="field__label">课程名称</span>
          <input v-model.trim="item.name" type="text" :maxlength="appConfig.resumeForm?.courseNameMaxLength" placeholder="例如：数据结构与算法" />
        </label>
        <label class="field">
          <span class="field__label">课程类别</span>
          <input v-model.trim="item.category" type="text" :maxlength="appConfig.resumeForm?.categoryMaxLength" placeholder="专业核心 / 选修" />
        </label>
        <label class="field field--compact">
          <span class="field__label">学分</span>
          <input v-model.trim="item.credit" type="text" :maxlength="appConfig.resumeForm?.creditMaxLength" placeholder="4" />
        </label>
        <label class="field field--compact">
          <span class="field__label">成绩</span>
          <input v-model.trim="item.score" type="text" :maxlength="appConfig.resumeForm?.scoreMaxLength" placeholder="92" />
        </label>
        <button class="icon-button icon-button--danger" type="button" aria-label="删除课程" @click="removeItem(index)">
          <Trash2 :size="16" />
        </button>
      </article>
    </div>

    <div v-else class="empty-editor empty-editor--compact">
      <BookOpen :size="27" />
      <strong>还没有课程信息</strong>
      <p>列出 4–8 门最能证明专业基础的课程即可。</p>
      <button class="button button--primary" type="button" @click="addItem">
        <Plus :size="17" /> 添加第一门课程
      </button>
    </div>
  </EditorSection>
</template>

<script setup lang="ts">
import { BookOpen, Plus, Trash2 } from 'lucide-vue-next'
import EditorSection from '@/components/EditorSection.vue'
import { useAppConfigStore } from '@/stores/app-config'
import type { ResumeData } from '@/types/resume'

const appConfig = useAppConfigStore()
const props = defineProps<{
  resume: ResumeData
}>()

function addItem() {
  props.resume.courses.push({
    name: '',
    category: '',
    credit: '',
    score: '',
  })
}

function removeItem(index: number) {
  props.resume.courses.splice(index, 1)
}
</script>


