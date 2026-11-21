<template>
  <article class="resume-paper" aria-label="简历实时预览">
    <header class="resume-hero">
      <div class="resume-hero__meta">
        <span>RESUME / {{ new Date().getFullYear() }}</span>
        <span>PROFILE 01</span>
      </div>

      <div class="resume-hero__identity">
        <div class="resume-hero__name-wrap">
          <h1>{{ resume.fullName || '你的姓名' }}</h1>
          <p>{{ resume.headline || '职业方向 / 一句话介绍自己' }}</p>
        </div>
        <div v-if="safeAvatar" class="resume-avatar">
          <img :src="safeAvatar" alt="个人头像" />
        </div>
        <div v-else class="resume-avatar resume-avatar--initials" aria-hidden="true">
          {{ initials }}
        </div>
      </div>

      <div class="resume-contact">
        <span v-if="resume.email"><b>EMAIL</b>{{ resume.email }}</span>
        <span v-if="resume.phone"><b>PHONE</b>{{ resume.phone }}</span>
        <span v-if="resume.location"><b>LOCATION</b>{{ resume.location }}</span>
        <span v-if="resume.website"><b>WEB</b>{{ compactUrl(resume.website) }}</span>
        <span v-if="resume.github"><b>GITHUB</b>{{ compactUrl(resume.github) }}</span>
      </div>
    </header>

    <div class="resume-body">
      <section v-if="resume.summary.trim()" class="resume-section">
        <div class="resume-section__title"><span>01</span><h2>个人简介</h2></div>
        <p class="resume-summary">{{ resume.summary }}</p>
      </section>

      <section v-if="validEducations.length" class="resume-section">
        <div class="resume-section__title"><span>02</span><h2>教育背景</h2></div>
        <div class="resume-timeline">
          <div v-for="item in validEducations" :key="item.school + item.major" class="resume-entry">
            <div class="resume-entry__head">
              <div>
                <h3>{{ item.school }}</h3>
                <p>{{ [item.major, item.degree].filter(Boolean).join(' · ') }}</p>
              </div>
              <div class="resume-entry__date">{{ dateRange(item.startDate, item.endDate) }}</div>
            </div>
            <p v-if="item.gpa" class="resume-entry__meta">GPA / 排名：{{ item.gpa }}</p>
            <p v-if="item.description" class="resume-entry__text">{{ item.description }}</p>
          </div>
        </div>
      </section>

      <section v-if="validCampus.length" class="resume-section">
        <div class="resume-section__title"><span>03</span><h2>校园经历</h2></div>
        <div class="resume-timeline">
          <div v-for="item in validCampus" :key="item.organization + item.role" class="resume-entry">
            <div class="resume-entry__head">
              <div>
                <h3>{{ item.organization }}</h3>
                <p>{{ item.role }}</p>
              </div>
              <div class="resume-entry__date">{{ dateRange(item.startDate, item.endDate) }}</div>
            </div>
            <p v-if="item.description" class="resume-entry__text">{{ item.description }}</p>
            <ul v-if="lines(item.achievements).length" class="resume-bullets">
              <li v-for="(line, index) in lines(item.achievements)" :key="index">{{ line }}</li>
            </ul>
          </div>
        </div>
      </section>

      <section v-if="validProjects.length" class="resume-section">
        <div class="resume-section__title"><span>04</span><h2>项目经历</h2></div>
        <div class="resume-timeline">
          <div v-for="item in validProjects" :key="item.name + item.role" class="resume-entry">
            <div class="resume-entry__head">
              <div>
                <h3>{{ item.name }}</h3>
                <p>{{ item.role }}</p>
              </div>
              <div class="resume-entry__date">{{ dateRange(item.startDate, item.endDate) }}</div>
            </div>
            <div v-if="techList(item.techStack).length" class="resume-tech">
              <span v-for="tech in techList(item.techStack)" :key="tech">{{ tech }}</span>
            </div>
            <p v-if="item.description" class="resume-entry__text">{{ item.description }}</p>
            <ul v-if="lines(item.highlights).length" class="resume-bullets">
              <li v-for="(line, index) in lines(item.highlights)" :key="index">{{ line }}</li>
            </ul>
            <a
              v-if="safeUrl(item.link)"
              class="resume-link"
              :href="safeUrl(item.link)"
              target="_blank"
              rel="noopener noreferrer"
            >查看项目 ↗</a>
          </div>
        </div>
      </section>

      <div v-if="validSkills.length || validCourses.length" class="resume-duo">
        <section v-if="validSkills.length" class="resume-section resume-section--half">
          <div class="resume-section__title"><span>05</span><h2>专业技能</h2></div>
          <div class="resume-skills">
            <div v-for="group in skillGroups" :key="group.category" class="resume-skill-group">
              <p>{{ group.category || '综合能力' }}</p>
              <div class="resume-skill-tags">
                <span v-for="skill in group.items" :key="skill.name">
                  {{ skill.name }}
                  <i aria-hidden="true">{{ '●'.repeat(skill.level) }}</i>
                </span>
              </div>
            </div>
          </div>
        </section>

        <section v-if="validCourses.length" class="resume-section resume-section--half">
          <div class="resume-section__title"><span>06</span><h2>主修课程</h2></div>
          <div class="resume-courses">
            <div v-for="course in validCourses" :key="course.name" class="resume-course">
              <div>
                <strong>{{ course.name }}</strong>
                <span v-if="course.category">{{ course.category }}</span>
              </div>
              <em v-if="course.score || course.credit">{{ [course.credit && `学分 ${course.credit}`, course.score && `成绩 ${course.score}`].filter(Boolean).join(' · ') }}</em>
            </div>
          </div>
        </section>
      </div>

      <footer class="resume-footer">
        <span>{{ resume.title || '个人简历' }}</span>
        <span>UPDATED {{ updatedLabel }}</span>
      </footer>
    </div>
  </article>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { ResumeData } from '@/types/resume'

const props = defineProps<{
  resume: ResumeData
}>()

const validEducations = computed(() => props.resume.educations.filter((item) => item.school.trim()))
const validCampus = computed(() => props.resume.campusExperiences.filter((item) => item.organization.trim()))
const validProjects = computed(() => props.resume.projects.filter((item) => item.name.trim()))
const validSkills = computed(() => props.resume.skills.filter((item) => item.name.trim()))
const validCourses = computed(() => props.resume.courses.filter((item) => item.name.trim()))

const initials = computed(() => {
  const name = props.resume.fullName.trim()
  return name ? name.slice(0, 2) : '简历'
})

const safeAvatar = computed(() => safeImageUrl(props.resume.avatarUrl))

const skillGroups = computed(() => {
  const groups = new Map<string, typeof props.resume.skills>()
  for (const skill of validSkills.value) {
    const category = skill.category.trim()
    groups.set(category, [...(groups.get(category) ?? []), skill])
  }
  return Array.from(groups, ([category, items]) => ({ category, items }))
})

const updatedLabel = computed(() => {
  const date = props.resume.updatedAt ? new Date(props.resume.updatedAt) : new Date()
  return new Intl.DateTimeFormat('zh-CN', { year: 'numeric', month: '2-digit' }).format(date)
})

function safeImageUrl(value: string): string {
  if (value.startsWith('/api/public/uploads/')) return value
  return safeUrl(value)
}

function safeUrl(value: string): string {
  if (!value) return ''
  try {
    const parsed = new URL(value)
    return parsed.protocol === 'http:' || parsed.protocol === 'https:' ? parsed.toString() : ''
  } catch {
    return ''
  }
}

function compactUrl(value: string): string {
  return value.replace(/^https?:\/\//, '').replace(/\/$/, '')
}

function dateRange(start: string, end: string): string {
  if (start && end) return `${start} — ${end}`
  return start || end
}

function lines(value: string): string[] {
  return value
    .split(/\r?\n/)
    .map((item) => item.trim())
    .filter(Boolean)
}

function techList(value: string): string[] {
  return value
    .split(/[·,，/|]/)
    .map((item) => item.trim())
    .filter(Boolean)
}
</script>

