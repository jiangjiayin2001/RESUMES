<template>
  <div class="portfolio-page">
    <div v-if="loading" class="portfolio-loading">
      <div class="portfolio-loading__mark">履</div>
      <p>正在整理个人主页…</p>
    </div>

    <main v-else-if="unavailable" class="portfolio-private">
      <div class="portfolio-private__grid" aria-hidden="true"></div>
      <div class="portfolio-private__mark">履</div>
      <p class="portfolio-kicker">PRIVATE PORTFOLIO</p>
      <h1>一份新的职业故事<br />正在准备中。</h1>
      <p>主页尚未公开发布。请稍后再来，或联系页面所有者获取最新简历。</p>
      <RouterLink class="portfolio-private__link" to="/login">所有者入口</RouterLink>
    </main>

    <div v-else-if="loadError" class="portfolio-private">
      <div class="portfolio-private__mark">!</div>
      <h1>主页暂时无法载入</h1>
      <p>{{ loadError }}</p>
      <button class="portfolio-private__link" type="button" @click="loadResume">重新加载</button>
    </div>

    <template v-else-if="resume">
      <header class="portfolio-nav" :class="{ 'portfolio-nav--scrolled': scrolled }">
        <RouterLink class="portfolio-brand" to="/" aria-label="返回首页">
          <span>{{ initials }}</span>
          <b>{{ resume.fullName || '个人履历' }}</b>
        </RouterLink>

        <nav class="portfolio-desktop-nav" aria-label="主页导航">
          <RouterLink to="/" exact-active-class="router-link-active">首页</RouterLink>
          <RouterLink to="/education">教育经历</RouterLink>
          <RouterLink to="/campus">校园经历</RouterLink>
          <RouterLink to="/projects">项目经历</RouterLink>
          <RouterLink to="/skills">专业技能</RouterLink>
          <RouterLink to="/courses">主修课程</RouterLink>
          <RouterLink to="/contact">联系我</RouterLink>
        </nav>

        <div class="portfolio-nav__actions">
          <RouterLink class="portfolio-nav__admin" to="/login">
            <Settings2 :size="14" /> 管理后台
          </RouterLink>
          <a v-if="resume.email" class="portfolio-nav__contact" :href="`mailto:${resume.email}`">
            联系我 <ArrowUpRight :size="15" />
          </a>
          <button class="portfolio-nav__toggle" type="button" aria-label="打开导航菜单" @click="mobileMenuOpen = !mobileMenuOpen">
            <X v-if="mobileMenuOpen" :size="20" />
            <Menu v-else :size="20" />
          </button>
        </div>

        <Transition name="portfolio-mobile-menu">
          <nav v-if="mobileMenuOpen" class="portfolio-mobile-menu" aria-label="移动端导航" @click="mobileMenuOpen = false">
            <RouterLink to="/">首页</RouterLink>
            <RouterLink to="/education">教育经历</RouterLink>
            <RouterLink to="/campus">校园经历</RouterLink>
            <RouterLink to="/projects">项目经历</RouterLink>
            <RouterLink to="/skills">专业技能</RouterLink>
            <RouterLink to="/courses">主修课程</RouterLink>
            <RouterLink to="/contact">联系我</RouterLink>
            <RouterLink to="/login">管理后台</RouterLink>
          </nav>
        </Transition>
      </header>

      <main id="top">
        <section v-if="page === 'home'" class="portfolio-hero">
          <div class="portfolio-hero__grid" aria-hidden="true"></div>
          <div class="portfolio-hero__orb" aria-hidden="true"></div>

          <div class="portfolio-hero__content">
            <div class="portfolio-hero__intro">
              <p class="portfolio-kicker">PORTFOLIO / {{ currentYear }}</p>
              <h1>{{ resume.fullName || '未命名主页' }}</h1>
              <p class="portfolio-hero__headline">{{ resume.headline || resume.title }}</p>
              <p v-if="resume.summary" class="portfolio-hero__summary">{{ resume.summary }}</p>

              <div class="portfolio-hero__actions">
                <a v-if="resume.email" class="portfolio-button portfolio-button--light" :href="`mailto:${resume.email}`">
                  发起联系 <ArrowRight :size="17" />
                </a>
                <RouterLink class="portfolio-button portfolio-button--ghost" to="/projects">
                  查看项目 <ArrowUpRight :size="16" />
                </RouterLink>
              </div>

              <div class="portfolio-contact-line">
                <span v-if="resume.location"><MapPin :size="14" />{{ resume.location }}</span>
                <span v-if="resume.phone"><Phone :size="14" />{{ resume.phone }}</span>
                <span v-if="resume.email"><Mail :size="14" />{{ resume.email }}</span>
              </div>
            </div>

            <aside class="portfolio-profile-card">
              <div class="portfolio-profile-card__top">
                <span>PROFILE / 01</span>
                <span class="portfolio-profile-card__status"><i></i> PUBLIC</span>
              </div>
              <div class="portfolio-avatar">
                <img v-if="safeAvatar" :src="safeAvatar" :alt="`${resume.fullName}的头像`" />
                <span v-else>{{ initials }}</span>
              </div>
              <div class="portfolio-profile-card__name">
                <strong>{{ resume.fullName }}</strong>
                <span>{{ resume.headline }}</span>
              </div>
              <div class="portfolio-profile-card__stats">
                <div><b>{{ validProjects.length.toString().padStart(2, '0') }}</b><span>Projects</span></div>
                <div><b>{{ validSkills.length.toString().padStart(2, '0') }}</b><span>Skills</span></div>
                <div><b>{{ validCourses.length.toString().padStart(2, '0') }}</b><span>Courses</span></div>
              </div>
              <div v-if="safeWebsite || safeGithub" class="portfolio-profile-card__links">
                <a v-if="safeWebsite" :href="safeWebsite" target="_blank" rel="noopener noreferrer">Website ↗</a>
                <a v-if="safeGithub" :href="safeGithub" target="_blank" rel="noopener noreferrer">GitHub ↗</a>
              </div>
            </aside>
          </div>

          <div class="portfolio-hero__footer">
            <span>EXPLORE THE STORY</span>
            <span class="portfolio-hero__scroll-line"></span>
            <span>{{ resume.location || 'CHINA' }}</span>
          </div>
        </section>

        <section v-else class="portfolio-subhero">
          <div class="portfolio-subhero__grid" aria-hidden="true"></div>
          <div class="portfolio-subhero__inner">
            <div>
              <p class="portfolio-kicker">{{ pageInfo.eyebrow }}</p>
              <h1>{{ pageInfo.title }}</h1>
              <p>{{ pageInfo.description }}</p>
            </div>
            <span class="portfolio-subhero__number">{{ pageInfo.number }}</span>
          </div>
        </section>

        <div v-if="page === 'home' && marqueeSkills.length" class="portfolio-marquee" aria-hidden="true">
          <div class="portfolio-marquee__track">
            <span v-for="(skill, index) in marqueeSkills" :key="`${skill.name}-${index}`">
              {{ skill.name }} <i>✦</i>
            </span>
          </div>
        </div>

        <template v-if="page === 'home'">
          <section class="portfolio-home-about">
            <div class="portfolio-home-about__intro">
              <p class="portfolio-kicker">01 / ABOUT</p>
              <h2>保持好奇，<br /><em>把想法做成结果。</em></h2>
            </div>
            <div class="portfolio-home-about__body">
              <p>{{ resume.summary || '个人简介尚未填写。' }}</p>
              <div class="portfolio-home-about__facts">
                <div v-if="validEducations.length"><span>教育背景</span><strong>{{ validEducations[0]?.school }}</strong></div>
                <div v-if="validEducations[0]?.major"><span>专业方向</span><strong>{{ validEducations[0]?.major }}</strong></div>
                <div v-if="resume.location"><span>所在城市</span><strong>{{ resume.location }}</strong></div>
              </div>
            </div>
          </section>

          <section class="portfolio-home-index">
            <header class="portfolio-home-index__head">
              <div><p class="portfolio-kicker">02 / EXPLORE</p><h2>浏览我的履历</h2></div>
              <p>每一个分类都有独立页面，方便你快速找到需要的信息。</p>
            </header>
            <div class="portfolio-home-grid">
              <RouterLink v-for="item in homeCards" :key="item.path" :to="item.path" class="portfolio-home-card">
                <span>{{ item.number }}</span>
                <div><h3>{{ item.title }}</h3><p>{{ item.description }}</p></div>
                <ArrowUpRight :size="18" />
              </RouterLink>
            </div>
          </section>
        </template>

        <section v-else-if="page === 'education'" class="portfolio-page-section">
          <div class="portfolio-page-section__label"><span>02</span><p>EDUCATION</p></div>
          <div class="portfolio-page-section__body">
            <div v-if="validEducations.length" class="portfolio-timeline">
              <article v-for="(item, index) in validEducations" :key="`edu-${index}`" class="portfolio-timeline__item">
                <span class="portfolio-timeline__index">{{ String(index + 1).padStart(2, '0') }}</span>
                <div class="portfolio-timeline__main">
                  <div class="portfolio-timeline__head">
                    <div><h3>{{ item.school }}</h3><p>{{ [item.major, item.degree].filter(Boolean).join(' · ') }}</p></div>
                    <time>{{ dateRange(item.startDate, item.endDate) }}</time>
                  </div>
                  <p v-if="item.gpa" class="portfolio-meta">GPA / 排名：{{ item.gpa }}</p>
                  <p v-if="item.description" class="portfolio-body-text">{{ item.description }}</p>
                </div>
              </article>
            </div>
            <PortfolioEmpty v-else title="教育经历正在整理" description="教育背景内容将在后台完善后展示。" />
          </div>
        </section>

        <section v-else-if="page === 'campus'" class="portfolio-page-section">
          <div class="portfolio-page-section__label"><span>03</span><p>CAMPUS</p></div>
          <div class="portfolio-page-section__body">
            <div v-if="validCampus.length" class="portfolio-timeline">
              <article v-for="(item, index) in validCampus" :key="`campus-${index}`" class="portfolio-timeline__item">
                <span class="portfolio-timeline__index">{{ String(index + 1).padStart(2, '0') }}</span>
                <div class="portfolio-timeline__main">
                  <div class="portfolio-timeline__head">
                    <div><h3>{{ item.organization }}</h3><p>{{ item.role }}</p></div>
                    <time>{{ dateRange(item.startDate, item.endDate) }}</time>
                  </div>
                  <p v-if="item.description" class="portfolio-body-text">{{ item.description }}</p>
                  <ul v-if="lines(item.achievements).length" class="portfolio-list">
                    <li v-for="(line, lineIndex) in lines(item.achievements)" :key="lineIndex">{{ line }}</li>
                  </ul>
                </div>
              </article>
            </div>
            <PortfolioEmpty v-else title="校园经历正在整理" description="学生组织、社团和校园项目将在后台完善后展示。" />
          </div>
        </section>

        <section v-else-if="page === 'projects'" class="portfolio-page-section">
          <div class="portfolio-page-section__label"><span>04</span><p>SELECTED WORK</p></div>
          <div class="portfolio-page-section__body">
            <div v-if="validProjects.length" class="portfolio-project-list portfolio-project-list--tab">
              <article v-for="(item, index) in validProjects" :key="`project-${index}`" class="portfolio-project">
                <div class="portfolio-project__number">{{ String(index + 1).padStart(2, '0') }}</div>
                <div class="portfolio-project__content">
                  <div class="portfolio-project__head">
                    <div><p>{{ item.role }}</p><h3>{{ item.name }}</h3></div>
                    <time>{{ dateRange(item.startDate, item.endDate) }}</time>
                  </div>
                  <div v-if="techList(item.techStack).length" class="portfolio-tech">
                    <span v-for="tech in techList(item.techStack)" :key="tech">{{ tech }}</span>
                  </div>
                  <p v-if="item.description" class="portfolio-project__description">{{ item.description }}</p>
                  <ul v-if="lines(item.highlights).length" class="portfolio-list portfolio-list--light">
                    <li v-for="(line, lineIndex) in lines(item.highlights)" :key="lineIndex">{{ line }}</li>
                  </ul>
                  <a v-if="safeUrl(item.link)" class="portfolio-project__link" :href="safeUrl(item.link)" target="_blank" rel="noopener noreferrer">
                    查看项目 <ArrowUpRight :size="16" />
                  </a>
                </div>
              </article>
            </div>
            <PortfolioEmpty v-else title="项目经历正在整理" description="代表项目将在后台完善后展示。" dark />
          </div>
        </section>

        <section v-else-if="page === 'skills'" class="portfolio-page-section">
          <div class="portfolio-page-section__label"><span>05</span><p>SKILLS</p></div>
          <div class="portfolio-page-section__body">
            <div v-if="validSkills.length" class="portfolio-skills portfolio-skills--grid">
              <div v-for="group in skillGroups" :key="group.category" class="portfolio-skill-group">
                <div class="portfolio-skill-group__head">
                  <span>{{ group.category || '综合能力' }}</span>
                  <small>{{ group.items.length.toString().padStart(2, '0') }}</small>
                </div>
                <div class="portfolio-skill-group__tags">
                  <span v-for="skill in group.items" :key="skill.name" class="portfolio-skill-item">
                    <b>{{ skill.name }}</b>
                    <span class="portfolio-skill-level" :aria-label="`${skill.level} 级熟练度`">
                      <i v-for="dot in 5" :key="dot" :class="{ active: dot <= skill.level }"></i>
                    </span>
                  </span>
                </div>
              </div>
            </div>
            <PortfolioEmpty v-else title="专业技能正在整理" description="技能分类和熟练度将在后台完善后展示。" />
          </div>
        </section>

        <section v-else-if="page === 'courses'" class="portfolio-page-section">
          <div class="portfolio-page-section__label"><span>06</span><p>COURSES</p></div>
          <div class="portfolio-page-section__body">
            <div v-if="validCourses.length" class="portfolio-course-list">
              <article v-for="course in validCourses" :key="course.name" class="portfolio-course-card">
                <div><span>{{ course.category || '主修课程' }}</span><strong>{{ course.name }}</strong></div>
                <p><em v-if="course.credit">学分 {{ course.credit }}</em><em v-if="course.score">成绩 {{ course.score }}</em></p>
              </article>
            </div>
            <PortfolioEmpty v-else title="课程信息正在整理" description="主修课程将在后台完善后展示。" />
          </div>
        </section>

        <section v-else-if="page === 'contact'" class="portfolio-contact portfolio-contact--page">
          <div class="portfolio-contact__inner">
            <p class="portfolio-kicker">LET'S CONNECT</p>
            <h2>期待与你交流，<br /><em>也欢迎新的机会。</em></h2>
            <a v-if="resume.email" class="portfolio-contact__mail" :href="`mailto:${resume.email}`">
              {{ resume.email }} <ArrowUpRight :size="28" />
            </a>
            <div class="portfolio-contact__details">
              <span v-if="resume.phone">{{ resume.phone }}</span>
              <span v-if="resume.location">{{ resume.location }}</span>
              <a v-if="safeWebsite" :href="safeWebsite" target="_blank" rel="noopener noreferrer">{{ compactUrl(resume.website) }}</a>
            </div>
          </div>
        </section>

        <footer class="portfolio-site-footer">
          <span>© {{ currentYear }} {{ resume.fullName }}</span>
          <span>{{ resume.title }}</span>
          <RouterLink to="/login">管理入口</RouterLink>
        </footer>
      </main>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import { ArrowRight, ArrowUpRight, Mail, MapPin, Menu, Phone, Settings2, X } from 'lucide-vue-next'
import PortfolioEmpty from '@/components/PortfolioEmpty.vue'
import { fetchPublicResume } from '@/api/resume'
import { ApiError } from '@/types/api'
import type { ResumeData } from '@/types/resume'

type PublicPage = 'home' | 'education' | 'campus' | 'projects' | 'skills' | 'courses' | 'contact'

const route = useRoute()
const resume = ref<ResumeData | null>(null)
const loading = ref(true)
const unavailable = ref(false)
const loadError = ref('')
const scrolled = ref(false)
const mobileMenuOpen = ref(false)
const currentYear = new Date().getFullYear()

const page = computed<PublicPage>(() => (route.meta.publicPage as PublicPage | undefined) ?? 'home')

const pageDefinitions: Record<PublicPage, { title: string; eyebrow: string; description: string; number: string }> = {
  home: { title: '首页', eyebrow: 'HOME', description: '个人介绍与履历概览。', number: '00' },
  education: { title: '教育经历', eyebrow: 'EDUCATION', description: '学校、专业、学习方向与学术成果。', number: '02' },
  campus: { title: '校园经历', eyebrow: 'CAMPUS', description: '学生组织、社团、志愿服务与校园项目。', number: '03' },
  projects: { title: '项目经历', eyebrow: 'SELECTED WORK', description: '从真实问题出发，呈现职责、方法与可量化成果。', number: '04' },
  skills: { title: '专业技能', eyebrow: 'EXPERTISE', description: '按能力领域整理的工具、技术与专业优势。', number: '05' },
  courses: { title: '主修课程', eyebrow: 'COURSES', description: '与职业方向相关的核心课程与学习成果。', number: '06' },
  contact: { title: '联系我', eyebrow: 'CONTACT', description: '欢迎交流工作机会、项目合作或行业想法。', number: '07' },
}

const pageInfo = computed(() => pageDefinitions[page.value])

const homeCards = [
  { path: '/education', number: '01', title: '教育经历', description: '教育背景、专业方向与学习成果' },
  { path: '/campus', number: '02', title: '校园经历', description: '组织协作、校园项目与代表成果' },
  { path: '/projects', number: '03', title: '项目经历', description: '核心职责、技术方案与项目结果' },
  { path: '/skills', number: '04', title: '专业技能', description: '按领域整理的技术与专业能力' },
  { path: '/courses', number: '05', title: '主修课程', description: '与目标方向相关的核心课程' },
  { path: '/contact', number: '06', title: '联系我', description: '邮箱、电话与其他联系方式' },
]

const validEducations = computed(() => (resume.value?.educations ?? []).filter((item) => item.school.trim()))
const validCampus = computed(() => (resume.value?.campusExperiences ?? []).filter((item) => item.organization.trim()))
const validProjects = computed(() => (resume.value?.projects ?? []).filter((item) => item.name.trim()))
const validSkills = computed(() => (resume.value?.skills ?? []).filter((item) => item.name.trim()))
const validCourses = computed(() => (resume.value?.courses ?? []).filter((item) => item.name.trim()))

const initials = computed(() => {
  const name = resume.value?.fullName?.trim()
  return name ? name.slice(0, 2) : '履'
})

const safeAvatar = computed(() => safeImageUrl(resume.value?.avatarUrl ?? ''))
const safeWebsite = computed(() => safeUrl(resume.value?.website ?? ''))
const safeGithub = computed(() => safeUrl(resume.value?.github ?? ''))

const marqueeSkills = computed(() => [...validSkills.value.slice(0, 8), ...validSkills.value.slice(0, 8)])

const skillGroups = computed(() => {
  const groups = new Map<string, typeof validSkills.value>()
  for (const skill of validSkills.value) {
    const category = skill.category.trim()
    groups.set(category, [...(groups.get(category) ?? []), skill])
  }
  return Array.from(groups, ([category, items]) => ({ category, items }))
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
  return value.split(/\r?\n/).map((item) => item.trim()).filter(Boolean)
}

function techList(value: string): string[] {
  return value.split(/[·,，/|]/).map((item) => item.trim()).filter(Boolean)
}

function syncMeta() {
  if (!resume.value) return
  const titlePrefix = page.value === 'home' ? '' : `${pageInfo.value.title} · `
  document.title = `${titlePrefix}${resume.value.fullName} · ${resume.value.headline || '个人主页'}`
  const description = document.querySelector('meta[name="description"]')
  if (description) {
    description.setAttribute('content', resume.value.summary.slice(0, 160) || `${resume.value.fullName}的个人主页`)
  }
}

async function loadResume() {
  loading.value = true
  unavailable.value = false
  loadError.value = ''
  try {
    resume.value = await fetchPublicResume()
    syncMeta()
  } catch (error) {
    if (error instanceof ApiError && error.status === 404) {
      unavailable.value = true
    } else {
      loadError.value = error instanceof Error ? error.message : '请稍后重试'
    }
  } finally {
    loading.value = false
  }
}

function onScroll() {
  scrolled.value = window.scrollY > 24
}

watch(
  () => route.path,
  () => {
    mobileMenuOpen.value = false
    window.scrollTo({ top: 0, behavior: 'smooth' })
    syncMeta()
  },
)

onMounted(() => {
  void loadResume()
  window.addEventListener('scroll', onScroll, { passive: true })
})

onBeforeUnmount(() => {
  window.removeEventListener('scroll', onScroll)
})
</script>
<style scoped>
.portfolio-page {
  --p-ink: #17221d;
  --p-ink-soft: #2d3a33;
  --p-cream: #f4efe6;
  --p-paper: #fbf8f2;
  --p-terra: #c56d48;
  --p-sage: #8da294;
  --p-line: rgb(23 34 29 / 14%);
  min-height: 100vh;
  color: var(--p-ink);
  background: var(--p-cream);
  font-family: 'Inter', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.portfolio-page * {
  box-sizing: border-box;
}

.portfolio-page h1,
.portfolio-page h2,
.portfolio-page h3,
.portfolio-page p {
  margin: 0;
}

.portfolio-page a {
  color: inherit;
}

.portfolio-kicker {
  color: var(--p-terra);
  font-size: 10px;
  font-weight: 750;
  letter-spacing: 0.22em;
}

.portfolio-loading,
.portfolio-private {
  display: flex;
  min-height: 100vh;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 40px 24px;
  text-align: center;
  background:
    radial-gradient(circle at 70% 15%, rgb(197 109 72 / 15%), transparent 28%),
    linear-gradient(135deg, #16211c, #24352d);
  color: #fff;
}

.portfolio-loading__mark,
.portfolio-private__mark {
  display: grid;
  width: 66px;
  height: 66px;
  place-items: center;
  margin-bottom: 24px;
  color: var(--p-ink);
  border-radius: 22px 22px 22px 6px;
  background: #e9d8c8;
  font-family: Georgia, 'Songti SC', serif;
  font-size: 31px;
}

.portfolio-loading p {
  color: rgb(255 255 255 / 55%);
  font-size: 12px;
  letter-spacing: 0.12em;
}

.portfolio-private {
  position: relative;
  overflow: hidden;
}

.portfolio-private__grid {
  position: absolute;
  inset: 0;
  opacity: 0.12;
  background-image: linear-gradient(rgb(255 255 255 / 20%) 1px, transparent 1px),
    linear-gradient(90deg, rgb(255 255 255 / 20%) 1px, transparent 1px);
  background-size: 64px 64px;
  mask-image: radial-gradient(circle at center, #000, transparent 70%);
}

.portfolio-private > *:not(.portfolio-private__grid) {
  position: relative;
  z-index: 1;
}

.portfolio-private h1 {
  margin: 19px 0;
  font-family: Georgia, 'Songti SC', serif;
  font-size: clamp(42px, 7vw, 86px);
  font-weight: 400;
  letter-spacing: -0.055em;
  line-height: 1.04;
}

.portfolio-private > p:not(.portfolio-kicker) {
  max-width: 520px;
  color: rgb(255 255 255 / 55%);
  font-size: 13px;
  line-height: 1.9;
}

.portfolio-private__link {
  margin-top: 30px;
  padding: 12px 20px;
  color: #fff;
  border: 1px solid rgb(255 255 255 / 20%);
  border-radius: 99px;
  background: rgb(255 255 255 / 6%);
  font-size: 11px;
  text-decoration: none;
  cursor: pointer;
}

.portfolio-nav {
  position: fixed;
  z-index: 30;
  top: 0;
  right: 0;
  left: 0;
  display: flex;
  height: 78px;
  align-items: center;
  justify-content: space-between;
  padding: 0 clamp(24px, 5vw, 80px);
  color: #fff;
  transition: 260ms ease;
  transition-property: background, color, box-shadow, height;
}

.portfolio-nav--scrolled {
  height: 66px;
  color: var(--p-ink);
  background: rgb(248 245 238 / 92%);
  box-shadow: 0 1px 0 var(--p-line);
  backdrop-filter: blur(18px);
}

.portfolio-brand {
  display: flex;
  align-items: center;
  gap: 11px;
  color: inherit;
  text-decoration: none;
}

.portfolio-brand span {
  display: grid;
  width: 34px;
  height: 34px;
  place-items: center;
  border: 1px solid currentColor;
  border-radius: 50%;
  font-family: Georgia, 'Songti SC', serif;
  font-size: 12px;
}

.portfolio-brand b {
  font-size: 12px;
  letter-spacing: 0.06em;
}

.portfolio-nav nav {
  display: flex;
  gap: 34px;
}

.portfolio-nav nav a {
  color: inherit;
  font-size: 11px;
  letter-spacing: 0.08em;
  text-decoration: none;
  opacity: 0.72;
  transition: opacity 150ms ease;
}

.portfolio-nav nav a:hover {
  opacity: 1;
}

.portfolio-nav__actions {
  display: flex;
  align-items: center;
  gap: 9px;
}

.portfolio-nav__admin {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 9px 13px;
  color: inherit;
  border: 1px solid rgb(255 255 255 / 16%);
  border-radius: 99px;
  background: rgb(255 255 255 / 5%);
  font-size: 9px;
  letter-spacing: 0.04em;
  text-decoration: none;
  opacity: 0.72;
  transition: 160ms ease;
  transition-property: opacity, background, border-color;
}

.portfolio-nav__admin:hover {
  opacity: 1;
  background: rgb(255 255 255 / 10%);
}

.portfolio-nav--scrolled .portfolio-nav__admin {
  border-color: var(--p-line);
  background: rgb(23 34 29 / 3%);
}

.portfolio-nav__contact {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  padding: 9px 14px;
  color: inherit;
  border: 1px solid currentColor;
  border-radius: 99px;
  font-size: 10px;
  text-decoration: none;
}

.portfolio-hero {
  position: relative;
  display: flex;
  min-height: 100vh;
  overflow: hidden;
  flex-direction: column;
  padding: 130px clamp(24px, 6vw, 96px) 38px;
  color: #fff;
  background:
    radial-gradient(circle at 86% 18%, rgb(197 109 72 / 30%), transparent 26%),
    radial-gradient(circle at 2% 90%, rgb(141 162 148 / 16%), transparent 28%),
    linear-gradient(135deg, #15201b 0%, #1d2c25 55%, #26372e 100%);
}

.portfolio-hero__grid {
  position: absolute;
  inset: 0;
  opacity: 0.15;
  background-image: linear-gradient(rgb(255 255 255 / 12%) 1px, transparent 1px),
    linear-gradient(90deg, rgb(255 255 255 / 12%) 1px, transparent 1px);
  background-size: 72px 72px;
  mask-image: linear-gradient(120deg, #000, transparent 70%);
}

.portfolio-hero__orb {
  position: absolute;
  top: 18%;
  right: -15vw;
  width: 54vw;
  height: 54vw;
  border: 1px solid rgb(255 255 255 / 8%);
  border-radius: 50%;
  box-shadow: 0 0 0 9vw rgb(255 255 255 / 2%), 0 0 0 18vw rgb(255 255 255 / 1%);
}

.portfolio-hero__content {
  position: relative;
  z-index: 2;
  display: grid;
  width: 100%;
  max-width: 1440px;
  flex: 1;
  align-items: center;
  grid-template-columns: minmax(0, 1fr) minmax(300px, 390px);
  gap: clamp(50px, 9vw, 150px);
  margin: 0 auto;
}

.portfolio-hero__intro {
  max-width: 800px;
}

.portfolio-hero__intro h1 {
  margin: 20px 0 8px;
  font-family: Georgia, 'Songti SC', 'STSong', serif;
  font-size: clamp(68px, 11vw, 170px);
  font-weight: 400;
  letter-spacing: -0.075em;
  line-height: 0.84;
  white-space: nowrap;
}

.portfolio-hero__headline {
  margin-top: 27px;
  color: #e6ad8f;
  font-family: Georgia, 'Songti SC', serif;
  font-size: clamp(19px, 2vw, 29px);
  font-style: italic;
  letter-spacing: -0.02em;
}

.portfolio-hero__summary {
  max-width: 650px;
  margin-top: 28px;
  color: rgb(255 255 255 / 58%);
  font-size: 13px;
  line-height: 1.95;
  white-space: pre-line;
}

.portfolio-hero__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 34px;
}

.portfolio-button {
  display: inline-flex;
  min-height: 46px;
  align-items: center;
  gap: 12px;
  padding: 0 19px;
  border-radius: 99px;
  font-size: 11px;
  font-weight: 650;
  text-decoration: none;
  transition: 180ms ease;
  transition-property: transform, background, border-color;
}

.portfolio-button:hover {
  transform: translateY(-2px);
}

.portfolio-button--light {
  color: var(--p-ink);
  background: #f3e9dc;
}

.portfolio-button--ghost {
  color: #fff;
  border: 1px solid rgb(255 255 255 / 22%);
  background: rgb(255 255 255 / 4%);
}

.portfolio-contact-line {
  display: flex;
  flex-wrap: wrap;
  gap: 11px 22px;
  margin-top: 34px;
  padding-top: 20px;
  border-top: 1px solid rgb(255 255 255 / 10%);
  color: rgb(255 255 255 / 46%);
  font-size: 9px;
}

.portfolio-contact-line span {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.portfolio-profile-card {
  position: relative;
  padding: 20px;
  color: var(--p-ink);
  border: 1px solid rgb(255 255 255 / 14%);
  border-radius: 4px;
  background: #eee3d4;
  box-shadow: 0 34px 80px rgb(0 0 0 / 28%);
  transform: rotate(1.5deg);
}

.portfolio-profile-card::before {
  position: absolute;
  inset: 8px;
  border: 1px solid rgb(23 34 29 / 12%);
  content: '';
  pointer-events: none;
}

.portfolio-profile-card__top {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  color: #746c61;
  font-size: 7px;
  font-weight: 700;
  letter-spacing: 0.15em;
}

.portfolio-profile-card__status {
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.portfolio-profile-card__status i {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: #5f856e;
  box-shadow: 0 0 0 3px rgb(95 133 110 / 14%);
}

.portfolio-avatar {
  position: relative;
  z-index: 1;
  display: grid;
  width: 100%;
  aspect-ratio: 1.15;
  place-items: center;
  overflow: hidden;
  margin-top: 18px;
  color: #665445;
  border-radius: 150px 150px 3px 3px;
  background:
    radial-gradient(circle at 60% 30%, rgb(255 255 255 / 45%), transparent 30%),
    linear-gradient(150deg, #d6c3ad, #bba58d);
  font-family: Georgia, 'Songti SC', serif;
  font-size: clamp(55px, 6vw, 88px);
}

.portfolio-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.portfolio-profile-card__name {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  gap: 5px;
  margin-top: 18px;
}

.portfolio-profile-card__name strong {
  font-family: Georgia, 'Songti SC', serif;
  font-size: 27px;
  font-weight: 400;
  letter-spacing: -0.03em;
}

.portfolio-profile-card__name span {
  color: #746c61;
  font-size: 9px;
}

.portfolio-profile-card__stats {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  margin-top: 18px;
  padding-top: 15px;
  border-top: 1px solid rgb(23 34 29 / 14%);
}

.portfolio-profile-card__stats div {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.portfolio-profile-card__stats b {
  font-family: Georgia, 'Songti SC', serif;
  font-size: 22px;
  font-weight: 400;
}

.portfolio-profile-card__stats span {
  color: #81796e;
  font-size: 7px;
  letter-spacing: 0.1em;
  text-transform: uppercase;
}

.portfolio-profile-card__links {
  position: relative;
  z-index: 1;
  display: flex;
  gap: 15px;
  margin-top: 17px;
  padding-top: 13px;
  border-top: 1px solid rgb(23 34 29 / 14%);
}

.portfolio-profile-card__links a {
  color: #665445;
  font-size: 8px;
  text-decoration: none;
}

.portfolio-hero__footer {
  position: relative;
  z-index: 2;
  display: flex;
  width: 100%;
  max-width: 1440px;
  align-items: center;
  gap: 16px;
  margin: 40px auto 0;
  color: rgb(255 255 255 / 35%);
  font-size: 7px;
  letter-spacing: 0.15em;
}

.portfolio-hero__scroll-line {
  width: 52px;
  height: 1px;
  background: rgb(255 255 255 / 22%);
}

.portfolio-marquee {
  position: relative;
  overflow: hidden;
  color: #f7efe4;
  border-top: 1px solid rgb(255 255 255 / 8%);
  border-bottom: 1px solid rgb(255 255 255 / 10%);
  background: #c56d48;
}

.portfolio-marquee__track {
  display: flex;
  width: max-content;
  align-items: center;
  gap: 34px;
  padding: 16px 0;
  animation: portfolio-marquee 28s linear infinite;
}

.portfolio-marquee__track span {
  display: inline-flex;
  align-items: center;
  gap: 34px;
  font-family: Georgia, 'Songti SC', serif;
  font-size: clamp(18px, 2.1vw, 28px);
  font-style: italic;
  white-space: nowrap;
}

.portfolio-marquee__track i {
  color: #f1c4aa;
  font-size: 11px;
  font-style: normal;
}

.portfolio-section {
  display: grid;
  max-width: 1440px;
  grid-template-columns: 150px minmax(0, 1fr);
  gap: clamp(30px, 5vw, 80px);
  margin: 0 auto;
  padding: clamp(90px, 10vw, 150px) clamp(24px, 6vw, 96px);
}

.portfolio-section__label {
  display: flex;
  align-items: flex-start;
  gap: 13px;
  padding-top: 10px;
  color: #8b8d85;
  font-size: 8px;
  letter-spacing: 0.16em;
}

.portfolio-section__label span {
  color: var(--p-terra);
  font-family: Georgia, 'Songti SC', serif;
  font-size: 13px;
}

.portfolio-section__heading {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 30px;
  margin-bottom: clamp(42px, 5vw, 70px);
}

.portfolio-section__heading h2 {
  font-family: Georgia, 'Songti SC', serif;
  font-size: clamp(40px, 5vw, 70px);
  font-weight: 400;
  letter-spacing: -0.05em;
  line-height: 1;
}

.portfolio-section__heading > p {
  max-width: 330px;
  color: #777c76;
  font-size: 11px;
  line-height: 1.8;
}

.portfolio-about {
  background: var(--p-paper);
}

.portfolio-about__content h2 {
  max-width: 900px;
  font-family: Georgia, 'Songti SC', serif;
  font-size: clamp(42px, 6vw, 88px);
  font-weight: 400;
  letter-spacing: -0.06em;
  line-height: 1.08;
}

.portfolio-about__content h2 em {
  color: var(--p-terra);
  font-weight: 400;
}

.portfolio-about__content > p {
  max-width: 780px;
  margin-top: 36px;
  color: #5d655f;
  font-size: 14px;
  line-height: 2;
  white-space: pre-line;
}

.portfolio-about__facts {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 20px;
  margin-top: 54px;
  padding-top: 26px;
  border-top: 1px solid var(--p-line);
}

.portfolio-about__facts div {
  display: flex;
  flex-direction: column;
  gap: 7px;
}

.portfolio-about__facts span {
  color: #8c8f88;
  font-size: 8px;
  letter-spacing: 0.14em;
}

.portfolio-about__facts strong {
  font-family: Georgia, 'Songti SC', serif;
  font-size: 18px;
  font-weight: 400;
}

.portfolio-timeline {
  border-top: 1px solid var(--p-line);
}

.portfolio-timeline__item {
  display: grid;
  grid-template-columns: 56px minmax(0, 1fr);
  gap: 20px;
  padding: 30px 0;
  border-bottom: 1px solid var(--p-line);
}

.portfolio-timeline__index {
  color: var(--p-terra);
  font-family: Georgia, 'Songti SC', serif;
  font-size: 13px;
}

.portfolio-timeline__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 30px;
}

.portfolio-timeline__head h3 {
  font-family: Georgia, 'Songti SC', serif;
  font-size: clamp(23px, 2.2vw, 31px);
  font-weight: 400;
  letter-spacing: -0.025em;
}

.portfolio-timeline__head p {
  margin-top: 5px;
  color: #737a74;
  font-size: 10px;
}

.portfolio-timeline__head time {
  flex: 0 0 auto;
  color: #91948e;
  font-size: 8px;
  letter-spacing: 0.04em;
}

.portfolio-meta {
  display: inline-block;
  margin-top: 10px;
  padding: 5px 9px;
  color: #7a5b48;
  border-radius: 99px;
  background: #efe1d5;
  font-size: 8px;
}

.portfolio-body-text {
  max-width: 780px;
  margin-top: 13px;
  color: #626a64;
  font-size: 11px;
  line-height: 1.85;
  white-space: pre-line;
}

.portfolio-list {
  display: flex;
  flex-direction: column;
  gap: 7px;
  margin: 14px 0 0;
  padding: 0;
  list-style: none;
}

.portfolio-list li {
  position: relative;
  padding-left: 15px;
  color: #525c56;
  font-size: 10px;
  line-height: 1.7;
}

.portfolio-list li::before {
  position: absolute;
  top: 0.72em;
  left: 0;
  width: 5px;
  height: 1px;
  background: var(--p-terra);
  content: '';
}

.portfolio-projects {
  color: #fff;
  background:
    radial-gradient(circle at 88% 8%, rgb(197 109 72 / 20%), transparent 25%),
    #17221d;
}

.portfolio-projects .portfolio-section__label {
  color: rgb(255 255 255 / 40%);
}

.portfolio-section__heading--dark > p {
  color: rgb(255 255 255 / 45%);
}

.portfolio-project-list {
  border-top: 1px solid rgb(255 255 255 / 14%);
}

.portfolio-project {
  display: grid;
  grid-template-columns: 56px minmax(0, 1fr);
  gap: 20px;
  padding: 38px 18px;
  border-bottom: 1px solid rgb(255 255 255 / 14%);
  border-radius: 4px;
  transition: 220ms ease;
  transition-property: background, transform;
}

.portfolio-project:hover {
  background: rgb(255 255 255 / 4%);
  transform: translateX(5px);
}

.portfolio-project__number {
  color: var(--p-terra);
  font-family: Georgia, 'Songti SC', serif;
  font-size: 13px;
}

.portfolio-project__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 30px;
}

.portfolio-project__head p {
  margin-bottom: 8px;
  color: var(--p-terra);
  font-size: 8px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.portfolio-project__head h3 {
  font-family: Georgia, 'Songti SC', serif;
  font-size: clamp(30px, 3.7vw, 54px);
  font-weight: 400;
  letter-spacing: -0.04em;
  line-height: 1;
}

.portfolio-project__head time {
  flex: 0 0 auto;
  color: rgb(255 255 255 / 38%);
  font-size: 8px;
}

.portfolio-tech {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 18px;
}

.portfolio-tech span {
  padding: 5px 9px;
  color: rgb(255 255 255 / 58%);
  border: 1px solid rgb(255 255 255 / 12%);
  border-radius: 99px;
  font-size: 7px;
}

.portfolio-project__description {
  max-width: 790px;
  margin-top: 20px;
  color: rgb(255 255 255 / 55%);
  font-size: 11px;
  line-height: 1.9;
  white-space: pre-line;
}

.portfolio-list--light li {
  color: rgb(255 255 255 / 70%);
}

.portfolio-project__link {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  margin-top: 18px;
  color: #e7a785;
  font-size: 9px;
  text-decoration: none;
}

.portfolio-expertise {
  background: var(--p-paper);
}

.portfolio-expertise__grid {
  display: grid;
  grid-template-columns: minmax(0, 1.12fr) minmax(280px, 0.88fr);
  gap: clamp(40px, 7vw, 100px);
}

.portfolio-skill-group {
  padding: 20px 0;
  border-top: 1px solid var(--p-line);
}

.portfolio-skill-group__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 15px;
}

.portfolio-skill-group__head span {
  color: #777d77;
  font-size: 8px;
  font-weight: 700;
  letter-spacing: 0.14em;
  text-transform: uppercase;
}

.portfolio-skill-group__head small {
  color: #a2a39d;
  font-size: 8px;
}

.portfolio-skill-group__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.portfolio-skill-group__tags > span {
  position: relative;
  overflow: hidden;
  padding: 9px 12px 12px;
  border: 1px solid var(--p-line);
  border-radius: 3px;
  font-size: 10px;
}

.portfolio-skill-group__tags i {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 2px;
  background: var(--p-terra);
}

.portfolio-courses {
  align-self: start;
  padding: 22px;
  background: #f0e9df;
}

.portfolio-courses__head {
  display: flex;
  justify-content: space-between;
  padding-bottom: 14px;
  color: #89867f;
  border-bottom: 1px solid rgb(23 34 29 / 12%);
  font-size: 7px;
  letter-spacing: 0.13em;
}

.portfolio-course {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 14px;
  padding: 14px 0;
  border-bottom: 1px solid rgb(23 34 29 / 10%);
}

.portfolio-course:last-child {
  border-bottom: 0;
}

.portfolio-course > div {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.portfolio-course strong {
  font-family: Georgia, 'Songti SC', serif;
  font-size: 14px;
  font-weight: 400;
}

.portfolio-course span,
.portfolio-course em {
  color: #8a877f;
  font-size: 7px;
  font-style: normal;
}

.portfolio-contact {
  color: #fff;
  background: #c56d48;
}

.portfolio-contact__inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: clamp(90px, 11vw, 160px) clamp(24px, 6vw, 80px) 80px;
  text-align: center;
}

.portfolio-contact .portfolio-kicker {
  color: #f4d6c5;
}

.portfolio-contact h2 {
  margin-top: 20px;
  font-family: Georgia, 'Songti SC', serif;
  font-size: clamp(40px, 6vw, 84px);
  font-weight: 400;
  letter-spacing: -0.055em;
  line-height: 1.08;
}

.portfolio-contact h2 em {
  color: #f5dfd2;
  font-weight: 400;
}

.portfolio-contact__mail {
  display: inline-flex;
  align-items: center;
  gap: 14px;
  margin-top: 42px;
  padding-bottom: 8px;
  color: #fff;
  border-bottom: 1px solid rgb(255 255 255 / 45%);
  font-family: Georgia, 'Songti SC', serif;
  font-size: clamp(18px, 2.4vw, 31px);
  text-decoration: none;
}

.portfolio-contact__details {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 14px 28px;
  margin-top: 35px;
  color: rgb(255 255 255 / 62%);
  font-size: 9px;
}

.portfolio-contact__details a {
  color: inherit;
  text-decoration: none;
}

.portfolio-footer {
  display: flex;
  max-width: 1440px;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  margin: 0 auto;
  padding: 24px clamp(24px, 6vw, 80px);
  color: rgb(255 255 255 / 48%);
  border-top: 1px solid rgb(255 255 255 / 18%);
  font-size: 8px;
  letter-spacing: 0.06em;
}

.portfolio-footer a {
  color: rgb(255 255 255 / 55%);
  text-decoration: none;
}

@media (max-width: 980px) {
  .portfolio-hero {
    min-height: auto;
    padding-top: 115px;
  }

  .portfolio-hero__content {
    grid-template-columns: 1fr;
  }

  .portfolio-profile-card {
    width: min(360px, 82vw);
    justify-self: end;
    margin-top: 20px;
  }

  .portfolio-hero__intro h1 {
    white-space: normal;
  }

  .portfolio-section {
    grid-template-columns: 1fr;
    gap: 26px;
  }

  .portfolio-section__label {
    padding-top: 0;
  }

  .portfolio-section__heading {
    align-items: flex-start;
    flex-direction: column;
  }

  .portfolio-expertise__grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 720px) {
  .portfolio-nav {
    height: 64px;
    padding: 0 18px;
  }

  .portfolio-nav nav {
    display: none;
  }

  .portfolio-brand b {
    display: none;
  }

  .portfolio-nav__contact {
    padding: 8px 12px;
  }

  .portfolio-hero {
    padding: 100px 20px 26px;
  }

  .portfolio-hero__intro h1 {
    margin-top: 16px;
    font-size: clamp(58px, 20vw, 100px);
    line-height: 0.88;
  }

  .portfolio-hero__headline {
    margin-top: 21px;
    font-size: 20px;
  }

  .portfolio-hero__summary {
    margin-top: 21px;
    font-size: 12px;
  }

  .portfolio-profile-card {
    width: 88vw;
    justify-self: center;
  }

  .portfolio-hero__footer {
    margin-top: 55px;
  }

  .portfolio-section {
    padding: 80px 20px;
  }

  .portfolio-about__content h2 {
    font-size: 43px;
  }

  .portfolio-about__content > p {
    margin-top: 24px;
    font-size: 12px;
  }

  .portfolio-about__facts {
    grid-template-columns: 1fr;
    gap: 15px;
    margin-top: 35px;
  }

  .portfolio-timeline__item,
  .portfolio-project {
    grid-template-columns: 30px minmax(0, 1fr);
    gap: 12px;
    padding: 24px 0;
  }

  .portfolio-timeline__head,
  .portfolio-project__head {
    flex-direction: column;
    gap: 8px;
  }

  .portfolio-project__head h3 {
    font-size: 32px;
  }

  .portfolio-project__description {
    font-size: 10px;
  }

  .portfolio-contact__inner {
    padding: 90px 20px 55px;
  }

  .portfolio-contact h2 {
    font-size: 41px;
  }

  .portfolio-contact__mail {
    max-width: 100%;
    margin-top: 30px;
    font-size: 18px;
    word-break: break-all;
  }

  .portfolio-footer {
    align-items: flex-start;
    flex-direction: column;
    gap: 8px;
    padding: 20px;
  }
}

@media (prefers-reduced-motion: no-preference) {
  .portfolio-hero__intro h1 {
    animation: portfolio-reveal 0.8s cubic-bezier(0.2, 0.8, 0.2, 1) both;
  }

  .portfolio-hero__headline,
  .portfolio-hero__summary,
  .portfolio-hero__actions,
  .portfolio-contact-line {
    animation: portfolio-rise 0.75s 0.12s ease both;
  }

  .portfolio-profile-card {
    animation: portfolio-card 0.9s 0.1s cubic-bezier(0.2, 0.8, 0.2, 1) both;
  }
}

@keyframes portfolio-reveal {
  from { opacity: 0; transform: translateY(24px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes portfolio-rise {
  from { opacity: 0; transform: translateY(14px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes portfolio-marquee {
  to { transform: translateX(-50%); }
}

@keyframes portfolio-card {
  from { opacity: 0; transform: translateY(24px) rotate(4deg); }
  to { opacity: 1; transform: translateY(0) rotate(1.5deg); }
}
/* Multi-page public site */
.portfolio-nav nav a.router-link-active {
  opacity: 1;
}

.portfolio-nav nav a.router-link-active::after {
  display: block;
  width: 15px;
  height: 1px;
  margin-top: 4px;
  background: currentColor;
  content: '';
}

.portfolio-nav__toggle {
  display: none;
  width: 36px;
  height: 36px;
  place-items: center;
  padding: 0;
  color: inherit;
  border: 1px solid currentColor;
  border-radius: 50%;
  background: transparent;
  cursor: pointer;
}

.portfolio-mobile-menu {
  display: none;
}

.portfolio-subhero {
  position: relative;
  display: flex;
  min-height: 58vh;
  overflow: hidden;
  align-items: flex-end;
  padding: 130px clamp(24px, 7vw, 110px) clamp(55px, 7vw, 90px);
  color: #fff;
  background:
    radial-gradient(circle at 82% 12%, rgb(197 109 72 / 23%), transparent 28%),
    linear-gradient(138deg, #15201b, #23342b);
}

.portfolio-subhero__grid {
  position: absolute;
  inset: 0;
  opacity: 0.12;
  background-image: linear-gradient(rgb(255 255 255 / 14%) 1px, transparent 1px),
    linear-gradient(90deg, rgb(255 255 255 / 14%) 1px, transparent 1px);
  background-size: 68px 68px;
  mask-image: linear-gradient(110deg, #000, transparent 76%);
}

.portfolio-subhero__inner {
  position: relative;
  z-index: 1;
  display: flex;
  width: 100%;
  max-width: 1440px;
  align-items: flex-end;
  justify-content: space-between;
  gap: 50px;
  margin: 0 auto;
}

.portfolio-subhero__inner h1 {
  margin-top: 19px;
  font-family: Georgia, 'Songti SC', serif;
  font-size: clamp(58px, 9vw, 126px);
  font-weight: 400;
  letter-spacing: -0.07em;
  line-height: 0.92;
}

.portfolio-subhero__inner > div > p:last-child {
  max-width: 540px;
  margin-top: 24px;
  color: rgb(255 255 255 / 55%);
  font-size: 12px;
  line-height: 1.85;
}

.portfolio-subhero__number {
  color: rgb(255 255 255 / 8%);
  font-family: Georgia, 'Songti SC', serif;
  font-size: clamp(110px, 17vw, 240px);
  line-height: 0.76;
}

.portfolio-home-about {
  display: grid;
  max-width: 1440px;
  grid-template-columns: minmax(260px, 0.9fr) minmax(0, 1.1fr);
  gap: clamp(40px, 8vw, 130px);
  margin: 0 auto;
  padding: clamp(90px, 10vw, 145px) clamp(24px, 6vw, 96px);
  background: var(--p-paper);
}

.portfolio-home-about__intro h2 {
  margin-top: 18px;
  font-family: Georgia, 'Songti SC', serif;
  font-size: clamp(43px, 5vw, 76px);
  font-weight: 400;
  letter-spacing: -0.055em;
  line-height: 1.08;
}

.portfolio-home-about__intro h2 em {
  color: var(--p-terra);
  font-weight: 400;
}

.portfolio-home-about__body > p {
  max-width: 760px;
  color: #58615b;
  font-size: 14px;
  line-height: 2;
  white-space: pre-line;
}

.portfolio-home-about__facts {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 20px;
  margin-top: 44px;
  padding-top: 25px;
  border-top: 1px solid var(--p-line);
}

.portfolio-home-about__facts div {
  display: flex;
  min-width: 0;
  flex-direction: column;
  gap: 6px;
}

.portfolio-home-about__facts span {
  color: #8b8f89;
  font-size: 8px;
  letter-spacing: 0.13em;
}

.portfolio-home-about__facts strong {
  overflow-wrap: anywhere;
  font-family: Georgia, 'Songti SC', serif;
  font-size: 17px;
  font-weight: 400;
}

.portfolio-home-index {
  padding: clamp(90px, 10vw, 145px) clamp(24px, 6vw, 96px);
}

.portfolio-home-index__head {
  display: flex;
  max-width: 1440px;
  align-items: flex-end;
  justify-content: space-between;
  gap: 40px;
  margin: 0 auto 45px;
}

.portfolio-home-index__head h2 {
  margin-top: 15px;
  font-family: Georgia, 'Songti SC', serif;
  font-size: clamp(42px, 5.5vw, 78px);
  font-weight: 400;
  letter-spacing: -0.055em;
}

.portfolio-home-index__head > p {
  max-width: 320px;
  color: #7a807a;
  font-size: 11px;
  line-height: 1.8;
}

.portfolio-home-grid {
  display: grid;
  max-width: 1440px;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin: 0 auto;
}

.portfolio-home-card {
  display: grid;
  min-height: 200px;
  grid-template-columns: 31px minmax(0, 1fr) 20px;
  gap: 17px;
  padding: 24px;
  color: var(--p-ink);
  border: 1px solid var(--p-line);
  border-radius: 4px;
  background: rgb(255 255 255 / 48%);
  text-decoration: none;
  transition: 200ms ease;
  transition-property: transform, background, box-shadow;
}

.portfolio-home-card:hover {
  background: #fff;
  box-shadow: 0 20px 50px rgb(23 34 29 / 8%);
  transform: translateY(-4px);
}

.portfolio-home-card > span {
  color: var(--p-terra);
  font-family: Georgia, 'Songti SC', serif;
  font-size: 11px;
}

.portfolio-home-card > div {
  display: flex;
  flex-direction: column;
}

.portfolio-home-card h3 {
  font-family: Georgia, 'Songti SC', serif;
  font-size: 25px;
  font-weight: 400;
  letter-spacing: -0.03em;
}

.portfolio-home-card p {
  margin-top: 8px;
  color: #848983;
  font-size: 9px;
  line-height: 1.7;
}

.portfolio-home-card svg {
  color: var(--p-terra);
}

.portfolio-page-section {
  display: grid;
  max-width: 1440px;
  grid-template-columns: 150px minmax(0, 1fr);
  gap: clamp(30px, 5vw, 80px);
  margin: 0 auto;
  padding: clamp(85px, 9vw, 130px) clamp(24px, 6vw, 96px);
}

.portfolio-page-section__label {
  display: flex;
  align-items: flex-start;
  gap: 13px;
  padding-top: 10px;
  color: #8b8d85;
  font-size: 8px;
  letter-spacing: 0.16em;
}

.portfolio-page-section__label span {
  color: var(--p-terra);
  font-family: Georgia, 'Songti SC', serif;
  font-size: 13px;
}

.portfolio-page-section__body {
  min-width: 0;
}

.portfolio-page-section__body .portfolio-timeline {
  border-top: 0;
}

.portfolio-page-section__body .portfolio-project-list--tab {
  border-top: 0;
}

.portfolio-page-section__body .portfolio-skills--grid {
  border-top: 0;
}

.portfolio-contact--page {
  min-height: 68vh;
  display: flex;
  align-items: center;
}

.portfolio-site-footer {
  display: flex;
  max-width: 1440px;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  margin: 0 auto;
  padding: 24px clamp(24px, 6vw, 96px);
  color: #9a9c96;
  border-top: 1px solid var(--p-line);
  font-size: 8px;
  letter-spacing: 0.06em;
}

.portfolio-site-footer a {
  color: #777c76;
  text-decoration: none;
}

.portfolio-mobile-menu-enter-active,
.portfolio-mobile-menu-leave-active {
  transition: 180ms ease;
  transition-property: opacity, transform;
}

.portfolio-mobile-menu-enter-from,
.portfolio-mobile-menu-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

@media (max-width: 1100px) {
  .portfolio-nav .portfolio-desktop-nav {
    display: none;
  }

  .portfolio-nav__toggle {
    display: inline-grid;
  }

  .portfolio-nav .portfolio-mobile-menu {
    position: absolute;
    top: 64px;
    right: 16px;
    left: 16px;
    display: flex;
    overflow: hidden;
    flex-direction: column;
    padding: 10px;
    color: #fff;
    border: 1px solid rgb(255 255 255 / 12%);
    border-radius: 8px;
    background: #17221d;
    box-shadow: 0 24px 65px rgb(0 0 0 / 30%);
  }

  .portfolio-nav--scrolled .portfolio-mobile-menu {
    color: var(--p-ink);
    border-color: var(--p-line);
    background: #fbf8f2;
  }

  .portfolio-nav .portfolio-mobile-menu a {
    padding: 13px 14px;
    border-radius: 5px;
    font-size: 11px;
    text-decoration: none;
  }

  .portfolio-nav .portfolio-mobile-menu a.router-link-active,
  .portfolio-nav .portfolio-mobile-menu a:hover {
    background: rgb(197 109 72 / 12%);
  }

  .portfolio-home-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .portfolio-page-section {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .portfolio-page-section__label {
    padding-top: 0;
  }
}

@media (max-width: 720px) {
  .portfolio-nav__actions .portfolio-nav__admin,
  .portfolio-nav__actions .portfolio-nav__contact {
    display: none;
  }

  .portfolio-subhero {
    min-height: 50vh;
    padding: 110px 20px 48px;
  }

  .portfolio-subhero__inner {
    align-items: flex-start;
    flex-direction: column;
    gap: 30px;
  }

  .portfolio-subhero__inner h1 {
    font-size: 54px;
  }

  .portfolio-subhero__number {
    align-self: flex-end;
    font-size: 100px;
  }

  .portfolio-home-about {
    grid-template-columns: 1fr;
    gap: 30px;
    padding: 75px 20px;
  }

  .portfolio-home-about__intro h2 {
    font-size: 43px;
  }

  .portfolio-home-about__facts {
    grid-template-columns: 1fr;
  }

  .portfolio-home-index {
    padding: 75px 20px;
  }

  .portfolio-home-index__head {
    align-items: flex-start;
    flex-direction: column;
    gap: 18px;
  }

  .portfolio-home-grid {
    grid-template-columns: 1fr;
  }

  .portfolio-home-card {
    min-height: 160px;
  }

  .portfolio-page-section {
    padding: 70px 20px;
  }

  .portfolio-site-footer {
    align-items: flex-start;
    flex-direction: column;
    gap: 8px;
    padding: 20px;
  }
}
/* Text overflow safeguards for the public home page */
.portfolio-hero {
  overflow-x: hidden;
  overflow-y: visible;
}

.portfolio-hero__content {
  grid-template-columns: minmax(0, 1fr) minmax(280px, 370px);
  gap: clamp(36px, 6vw, 100px);
}

.portfolio-hero__intro {
  min-width: 0;
}

.portfolio-hero__intro h1 {
  max-width: 100%;
  padding-bottom: 0.08em;
  font-size: clamp(54px, 9.2vw, 145px);
  line-height: 0.94;
  white-space: normal;
  overflow-wrap: anywhere;
  text-wrap: balance;
}

.portfolio-hero__headline,
.portfolio-hero__summary,
.portfolio-home-about__body > p,
.portfolio-profile-card__name strong,
.portfolio-profile-card__name span {
  overflow-wrap: anywhere;
  white-space: normal;
}

.portfolio-hero__summary {
  max-height: none;
  overflow: visible;
}

.portfolio-hero__intro,
.portfolio-hero__actions,
.portfolio-contact-line {
  position: relative;
  z-index: 2;
}

@media (max-width: 1180px) and (min-width: 981px) {
  .portfolio-hero__content {
    grid-template-columns: minmax(0, 1fr) minmax(260px, 320px);
    gap: 42px;
  }

  .portfolio-hero__intro h1 {
    font-size: clamp(54px, 8vw, 102px);
  }
}

@media (max-width: 980px) {
  .portfolio-hero {
    overflow: visible;
  }

  .portfolio-hero__content {
    grid-template-columns: 1fr;
  }

  .portfolio-hero__intro h1 {
    font-size: clamp(56px, 15vw, 112px);
  }
}

@media (max-width: 720px) {
  .portfolio-hero__intro h1 {
    font-size: clamp(52px, 18vw, 86px);
    line-height: 0.98;
  }
}
/* Public skill level display */
.portfolio-skill-group__tags > .portfolio-skill-item {
  display: flex;
  min-width: 138px;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 12px;
  overflow: visible;
}

.portfolio-skill-item b {
  font-size: 10px;
  font-weight: 550;
}

.portfolio-skill-level {
  display: inline-flex;
  flex: 0 0 auto;
  align-items: center;
  gap: 3px;
}

.portfolio-skill-level i {
  position: static;
  display: block;
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: #dedbd4;
}

.portfolio-skill-level i.active {
  background: var(--p-terra);
}
</style>

