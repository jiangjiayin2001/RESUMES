<template>
  <EditorSection
    eyebrow="06 / PROJECTS"
    title="项目经历"
    description="用「背景—行动—结果」组织项目，优先呈现你的职责和可量化影响。"
  >
    <template #actions>
      <button class="button button--soft" type="button" @click="addItem">
        <Plus :size="17" />
        添加项目
      </button>
    </template>

    <div v-if="resume.projects.length" class="repeat-list">
      <article v-for="(item, index) in resume.projects" :key="index" class="repeat-card repeat-card--project">
        <header class="repeat-card__head">
          <div class="repeat-card__title">
            <span class="repeat-card__index">{{ String(index + 1).padStart(2, '0') }}</span>
            <div>
              <strong>{{ item.name || '未命名项目' }}</strong>
              <span>{{ item.role || '项目经历' }}</span>
            </div>
          </div>
          <div class="row-actions">
            <button class="icon-button" type="button" aria-label="上移" :disabled="index === 0" @click="move(index, -1)">
              <ArrowUp :size="16" />
            </button>
            <button
              class="icon-button"
              type="button"
              aria-label="下移"
              :disabled="index === resume.projects.length - 1"
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
            <span class="field__label">项目名称</span>
            <input v-model.trim="item.name" type="text" :maxlength="appConfig.resumeForm?.itemNameMaxLength" placeholder="项目名称" />
          </label>
          <label class="field">
            <span class="field__label">担任角色</span>
            <input v-model.trim="item.role" type="text" :maxlength="appConfig.resumeForm?.itemRoleMaxLength" placeholder="例如：核心开发、项目负责人" />
          </label>
          <label class="field">
            <span class="field__label">开始时间</span>
            <input v-model.trim="item.startDate" type="text" :maxlength="appConfig.resumeForm?.dateMaxLength" placeholder="2024.03" />
          </label>
          <label class="field">
            <span class="field__label">结束时间</span>
            <input v-model.trim="item.endDate" type="text" :maxlength="appConfig.resumeForm?.dateMaxLength" placeholder="2024.09 / 至今" />
          </label>
          <label class="field field--full">
            <span class="field__label">技术栈 / 使用工具</span>
            <input
              v-model.trim="item.techStack"
              type="text"
              :maxlength="appConfig.resumeForm?.techStackMaxLength"
              placeholder="Java · Spring Boot · MySQL · Redis · Vue 3"
            />
          </label>
          <label class="field field--full">
            <span class="field__label">项目描述</span>
            <textarea
              v-model="item.description"
              rows="4"
              :maxlength="appConfig.resumeForm?.projectDescriptionMaxLength"
              placeholder="说明项目目标、业务背景和你承担的核心工作。"
            ></textarea>
          </label>
          <label class="field field--full">
            <span class="field__label">成果亮点</span>
            <textarea
              v-model="item.highlights"
              rows="4"
              :maxlength="appConfig.resumeForm?.projectDescriptionMaxLength"
              placeholder="每行写一条成果，尽量包含指标，例如：接口 P95 延迟由 420ms 降至 90ms。"
            ></textarea>
          </label>
          <label class="field field--full">
            <span class="field__label">项目链接</span>
            <input v-model.trim="item.link" type="url" :maxlength="appConfig.resumeForm?.linkMaxLength" placeholder="https://github.com/..." />
          </label>
        </div>
      </article>
    </div>

    <div v-else class="empty-editor">
      <FolderKanban :size="28" />
      <strong>还没有项目经历</strong>
      <p>课程设计、竞赛作品、开源项目与实习项目都可以成为有力证明。</p>
      <button class="button button--primary" type="button" @click="addItem">
        <Plus :size="17" /> 添加第一个项目
      </button>
    </div>
  </EditorSection>
</template>

<script setup lang="ts">
import { ArrowDown, ArrowUp, FolderKanban, Plus, Trash2 } from 'lucide-vue-next'
import EditorSection from '@/components/EditorSection.vue'
import { useAppConfigStore } from '@/stores/app-config'
import type { Project, ResumeData } from '@/types/resume'

const appConfig = useAppConfigStore()

const props = defineProps<{
  resume: ResumeData
}>()

function addItem() {
  props.resume.projects.push({
    name: '',
    role: '',
    startDate: '',
    endDate: '',
    techStack: '',
    description: '',
    highlights: '',
    link: '',
  })
}

function removeItem(index: number) {
  props.resume.projects.splice(index, 1)
}

function move(index: number, offset: number) {
  const target = index + offset
  if (target < 0 || target >= props.resume.projects.length) return
  const [item] = props.resume.projects.splice(index, 1)
  props.resume.projects.splice(target, 0, item as Project)
}
</script>

