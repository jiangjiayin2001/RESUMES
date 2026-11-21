<template>
  <EditorSection
    eyebrow="03 / CAMPUS"
    title="校园经历"
    description="学生组织、社团、志愿服务与校园项目都可以成为能力的证据。"
  >
    <template #actions>
      <button class="button button--soft" type="button" @click="addItem">
        <Plus :size="17" />
        添加校园经历
      </button>
    </template>

    <div v-if="resume.campusExperiences.length" class="repeat-list">
      <article v-for="(item, index) in resume.campusExperiences" :key="index" class="repeat-card">
        <header class="repeat-card__head">
          <div class="repeat-card__title">
            <span class="repeat-card__index">{{ String(index + 1).padStart(2, '0') }}</span>
            <div>
              <strong>{{ item.organization || '未填写组织' }}</strong>
              <span>{{ item.role || '校园经历' }}</span>
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
              :disabled="index === resume.campusExperiences.length - 1"
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
            <span class="field__label">组织 / 社团</span>
            <input v-model.trim="item.organization" type="text" :maxlength="appConfig.resumeForm?.itemNameMaxLength" placeholder="组织名称" />
          </label>
          <label class="field">
            <span class="field__label">担任角色</span>
            <input v-model.trim="item.role" type="text" :maxlength="appConfig.resumeForm?.itemRoleMaxLength" placeholder="例如：副主席、项目负责人" />
          </label>
          <label class="field">
            <span class="field__label">开始时间</span>
            <input v-model.trim="item.startDate" type="text" :maxlength="appConfig.resumeForm?.dateMaxLength" placeholder="2023.09" />
          </label>
          <label class="field">
            <span class="field__label">结束时间</span>
            <input v-model.trim="item.endDate" type="text" :maxlength="appConfig.resumeForm?.dateMaxLength" placeholder="2024.06 / 至今" />
          </label>
          <label class="field field--full">
            <span class="field__label">职责描述</span>
            <textarea
              v-model="item.description"
              rows="4"
              :maxlength="appConfig.resumeForm?.descriptionMaxLength"
              placeholder="说明你负责了什么、如何协作，以及工作规模。"
            ></textarea>
          </label>
          <label class="field field--full">
            <span class="field__label">代表成果</span>
            <textarea
              v-model="item.achievements"
              rows="3"
              :maxlength="appConfig.resumeForm?.descriptionMaxLength"
              placeholder="每行写一条可量化成果，例如：统筹 12 场活动，累计参与 800+ 人。"
            ></textarea>
          </label>
        </div>
      </article>
    </div>

    <div v-else class="empty-editor">
      <UsersRound :size="28" />
      <strong>还没有校园经历</strong>
      <p>把你组织过、推动过或参与过的事情整理成有结果的故事。</p>
      <button class="button button--primary" type="button" @click="addItem">
        <Plus :size="17" /> 添加第一条
      </button>
    </div>
  </EditorSection>
</template>

<script setup lang="ts">
import { ArrowDown, ArrowUp, Plus, Trash2, UsersRound } from 'lucide-vue-next'
import EditorSection from '@/components/EditorSection.vue'
import { useAppConfigStore } from '@/stores/app-config'
import type { CampusExperience, ResumeData } from '@/types/resume'

const appConfig = useAppConfigStore()
const props = defineProps<{
  resume: ResumeData
}>()

function addItem() {
  props.resume.campusExperiences.push({
    organization: '',
    role: '',
    startDate: '',
    endDate: '',
    description: '',
    achievements: '',
  })
}

function removeItem(index: number) {
  props.resume.campusExperiences.splice(index, 1)
}

function move(index: number, offset: number) {
  const target = index + offset
  if (target < 0 || target >= props.resume.campusExperiences.length) return
  const [item] = props.resume.campusExperiences.splice(index, 1)
  props.resume.campusExperiences.splice(target, 0, item as CampusExperience)
}
</script>

