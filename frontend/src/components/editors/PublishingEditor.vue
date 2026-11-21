<template>
  <EditorSection
    eyebrow="07 / PUBLISH"
    title="公开主页"
    description="雇主看到的是独立的一页式个人主页；后台编辑内容不会直接公开，保存并开启发布后才会展示。"
  >
    <div class="publish-layout">
      <div class="publish-status" :class="{ 'publish-status--live': resume.published }">
        <div class="publish-status__icon">
          <Globe2 :size="25" />
        </div>
        <div class="publish-status__copy">
          <p class="eyebrow eyebrow--dark">{{ resume.published ? 'PUBLIC PAGE IS LIVE' : 'PRIVATE WORKSPACE' }}</p>
          <h3>{{ resume.published ? '公开主页已上线' : '当前仅后台可见' }}</h3>
          <p>
            {{ resume.published
              ? '访客无需登录即可访问你的个人主页，并查看最新保存的信息。'
              : '填写并保存个人信息后，打开右侧开关即可让雇主访问。' }}
          </p>
        </div>
        <label class="publish-switch">
          <input v-model="resume.published" type="checkbox" />
          <span class="publish-switch__track"><i></i></span>
          <b>{{ resume.published ? '已发布' : '未发布' }}</b>
        </label>
      </div>

      <div class="publish-grid">
        <article class="publish-card">
          <span class="publish-card__icon"><Eye :size="19" /></span>
          <div>
            <strong>公开地址</strong>
            <p>{{ publicUrl }}</p>
            <a :href="publicUrl" target="_blank" rel="noopener noreferrer">
              查看公开主页 <ExternalLink :size="13" />
            </a>
          </div>
        </article>

        <article class="publish-card">
          <span class="publish-card__icon"><ShieldCheck :size="19" /></span>
          <div>
            <strong>展示范围</strong>
            <p>姓名、联系方式、简介、教育、校园经历、课程、技能和项目。留空的模块不会出现在公开页。</p>
          </div>
        </article>

        <article class="publish-card publish-card--wide">
          <span class="publish-card__icon"><Rocket :size="19" /></span>
          <div>
            <strong>上线到自己的域名</strong>
            <p>生产部署时配置域名与 HTTPS 后，公开地址会自动变为你的正式域名。完整部署步骤见项目中的部署文档。</p>
          </div>
        </article>
      </div>

      <p class="publish-tip">
        <Info :size="16" />
        修改公开页内容后，请点击右上角“保存”。未保存的修改不会出现在公开主页。
      </p>
    </div>
  </EditorSection>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ExternalLink, Eye, Globe2, Info, Rocket, ShieldCheck } from 'lucide-vue-next'
import EditorSection from '@/components/EditorSection.vue'
import type { ResumeData } from '@/types/resume'

defineProps<{
  resume: ResumeData
}>()

const publicUrl = computed(() => `${window.location.origin}/`)
</script>

