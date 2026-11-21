<template>
  <div class="studio-shell">
    <header class="studio-header">
      <div class="studio-header__brand">
        <div class="brand-mark">履</div>
        <div class="studio-brand-copy">
          <span class="brand-word">履迹 · Resume Studio</span>
          <small>个人履历工作台</small>
        </div>
      </div>

      <div class="studio-header__status" aria-live="polite">
        <span v-if="store.saving" class="save-status save-status--saving">
          <LoaderCircle class="spin" :size="15" /> 正在保存
        </span>
        <span v-else-if="store.isDirty" class="save-status save-status--dirty">
          <Circle :size="9" fill="currentColor" /> 有未保存更改
        </span>
        <span v-else class="save-status save-status--saved">
          <Check :size="15" /> {{ savedLabel }}
        </span>
      </div>

      <div class="studio-header__actions">
        <button class="button button--ghost header-preview-button" type="button" @click="mobilePreview = true">
          <Eye :size="17" /> 预览
        </button>
        <a class="button button--ghost" href="/" target="_blank" rel="noopener noreferrer">
          <Globe2 :size="17" /> <span>公开主页</span>
        </a>
        <button class="button button--ghost" type="button" @click="printResume">
          <Printer :size="17" /> <span>导出 PDF</span>
        </button>
        <button class="button button--primary" type="button" :disabled="store.saving || !store.draft" @click="saveNow">
          <LoaderCircle v-if="store.saving" class="spin" :size="17" />
          <Save v-else :size="17" />
          <span>{{ store.saving ? '保存中' : '保存' }}</span>
        </button>
        <div class="account-chip">
          <span class="account-chip__avatar">{{ authInitial }}</span>
          <div class="account-chip__copy">
            <strong>{{ auth.user?.displayName || '管理员' }}</strong>
            <small>{{ auth.user?.username }}</small>
          </div>
          <button class="icon-button icon-button--plain" type="button" aria-label="修改密码" @click="showPasswordModal = true">
            <KeyRound :size="17" />
          </button>
          <button class="icon-button icon-button--plain" type="button" aria-label="退出登录" @click="handleLogout">
            <LogOut :size="17" />
          </button>
        </div>
      </div>
    </header>

    <div v-if="store.loading" class="studio-loading">
      <div class="studio-loading__spinner"><LoaderCircle class="spin" :size="30" /></div>
      <p>正在展开你的履历…</p>
    </div>

    <div v-else-if="store.draft" class="studio-workspace">
      <aside class="studio-sidebar">
        <div class="completion-card">
          <div class="completion-ring" :style="{ '--ring-progress': `${store.completeness * 3.6}deg` }">
            <span>{{ store.completeness }}<small>%</small></span>
          </div>
          <div>
            <p class="eyebrow eyebrow--dark">PROFILE HEALTH</p>
            <strong>{{ completionTitle }}</strong>
            <span>完成度越高，内容越完整</span>
          </div>
        </div>

        <nav class="section-nav" aria-label="简历模块">
          <button
            v-for="section in sections"
            :key="section.key"
            type="button"
            class="section-nav__item"
            :class="{ active: activeSection === section.key }"
            @click="activeSection = section.key"
          >
            <span class="section-nav__icon"><component :is="section.icon" :size="18" /></span>
            <span class="section-nav__copy">
              <strong>{{ section.label }}</strong>
              <small>{{ section.english }}</small>
            </span>
            <span v-if="sectionCount(section.key)" class="section-nav__count">{{ sectionCount(section.key) }}</span>
            <ChevronRight v-else :size="15" class="section-nav__chevron" />
          </button>
        </nav>

        <div class="sidebar-tip">
          <Lightbulb :size="19" />
          <div>
            <strong>写作提示</strong>
            <p>用动词开头，用数字收尾。每段经历至少包含一个可验证结果。</p>
          </div>
        </div>

        <div class="sidebar-version">
          <span>数据版本 v{{ store.draft.version }}</span>
          <span>MySQL · Redis</span>
        </div>
      </aside>

      <main class="studio-editor">
        <div class="workspace-heading">
          <div>
            <p class="eyebrow eyebrow--dark">EDITOR / {{ activeSectionLabel.english }}</p>
            <h1>{{ activeSectionLabel.label }}</h1>
          </div>
          <div class="workspace-heading__actions">
            <span v-if="store.isDirty" class="unsaved-hint">Ctrl / ⌘ + S 快速保存</span>
            <button class="button button--soft button--small" type="button" :disabled="!store.isDirty" @click="store.reset">
              <RotateCcw :size="15" /> 撤销更改
            </button>
          </div>
        </div>

        <Transition name="editor-fade" mode="out-in">
          <component :is="currentEditor" :key="activeSection" :resume="store.draft" />
        </Transition>
      </main>

      <aside class="preview-panel">
        <div class="preview-panel__head">
          <div>
            <p class="eyebrow eyebrow--dark">LIVE PREVIEW</p>
            <strong>A4 简历预览</strong>
          </div>
          <div class="preview-panel__actions">
            <span class="preview-dot"></span>
            <span>实时同步</span>
            <button class="icon-button" type="button" aria-label="打印简历" @click="printResume">
              <Printer :size="16" />
            </button>
          </div>
        </div>
        <div class="preview-scroll">
          <div class="preview-paper-wrap">
            <ResumePreview :resume="store.draft" />
          </div>
        </div>
      </aside>
    </div>

    <div v-else class="studio-error">
      <FileWarning :size="38" />
      <h2>简历暂时无法载入</h2>
      <p>{{ store.error || '请检查网络连接后重试。' }}</p>
      <button class="button button--primary" type="button" @click="loadResume">重新加载</button>
    </div>

    <Transition name="toast">
      <div v-if="toastState" class="toast" :class="`toast--${toastState.type}`" role="status">
        <CheckCircle2 v-if="toastState.type === 'success'" :size="18" />
        <AlertCircle v-else :size="18" />
        {{ toastState.message }}
      </div>
    </Transition>

    <ChangePasswordModal
      v-if="showPasswordModal"
      @close="showPasswordModal = false"
      @success="handlePasswordChanged"
    />

    <Transition name="mobile-preview">
      <div v-if="mobilePreview && store.draft" class="mobile-preview">
        <header>
          <strong>简历预览</strong>
          <div>
            <button class="button button--primary button--small" type="button" @click="printResume">
              <Printer :size="15" /> 导出
            </button>
            <button class="icon-button" type="button" aria-label="关闭预览" @click="mobilePreview = false">
              <X :size="20" />
            </button>
          </div>
        </header>
        <div class="mobile-preview__scroll">
          <ResumePreview :resume="store.draft" />
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { computed, markRaw, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  AlertCircle,
  BookOpen,
  Check,
  CheckCircle2,
  ChevronRight,
  Circle,
  Eye,
  FileWarning,
  FolderKanban,
  Globe2,
  GraduationCap,
  KeyRound,
  Lightbulb,
  LoaderCircle,
  LogOut,
  Printer,
  RotateCcw,
  Save,
  Sparkles,
  UserRound,
  UsersRound,
  X,
} from 'lucide-vue-next'
import ChangePasswordModal from '@/components/ChangePasswordModal.vue'
import ResumePreview from '@/components/ResumePreview.vue'
import ProfileEditor from '@/components/editors/ProfileEditor.vue'
import EducationEditor from '@/components/editors/EducationEditor.vue'
import CampusEditor from '@/components/editors/CampusEditor.vue'
import CoursesEditor from '@/components/editors/CoursesEditor.vue'
import SkillsEditor from '@/components/editors/SkillsEditor.vue'
import ProjectsEditor from '@/components/editors/ProjectsEditor.vue'
import PublishingEditor from '@/components/editors/PublishingEditor.vue'
import { useAuthStore } from '@/stores/auth'
import { useResumeStore } from '@/stores/resume'
import type { SectionKey } from '@/types/resume'

const auth = useAuthStore()
const store = useResumeStore()
const router = useRouter()

const activeSection = ref<SectionKey>('profile')
const mobilePreview = ref(false)
const showPasswordModal = ref(false)
const toastState = ref<{ message: string; type: 'success' | 'error' } | null>(null)
let toastTimer: ReturnType<typeof setTimeout> | undefined

const sections = [
  { key: 'profile' as const, label: '基本信息', english: 'Profile', icon: markRaw(UserRound) },
  { key: 'education' as const, label: '教育经历', english: 'Education', icon: markRaw(GraduationCap) },
  { key: 'campus' as const, label: '校园经历', english: 'Campus', icon: markRaw(UsersRound) },
  { key: 'courses' as const, label: '主修课程', english: 'Courses', icon: markRaw(BookOpen) },
  { key: 'skills' as const, label: '专业技能', english: 'Skills', icon: markRaw(Sparkles) },
  { key: 'projects' as const, label: '项目经历', english: 'Projects', icon: markRaw(FolderKanban) },
  { key: 'publishing' as const, label: '公开主页', english: 'Publish', icon: markRaw(Globe2) },
]

const editorMap = {
  profile: markRaw(ProfileEditor),
  education: markRaw(EducationEditor),
  campus: markRaw(CampusEditor),
  courses: markRaw(CoursesEditor),
  skills: markRaw(SkillsEditor),
  projects: markRaw(ProjectsEditor),
  publishing: markRaw(PublishingEditor),
}

const currentEditor = computed(() => editorMap[activeSection.value])
const activeSectionLabel = computed(
  () => sections.find((section) => section.key === activeSection.value) ?? sections[0],
)

const authInitial = computed(() => (auth.user?.displayName || auth.user?.username || 'A').slice(0, 1))

const completionTitle = computed(() => {
  if (store.completeness >= 90) return '内容已经非常完整'
  if (store.completeness >= 60) return '基础信息已就绪'
  return '继续补充你的故事'
})

const savedLabel = computed(() => {
  if (!store.lastSavedAt) return '尚未保存'
  const time = new Intl.DateTimeFormat('zh-CN', {
    hour: '2-digit',
    minute: '2-digit',
  }).format(store.lastSavedAt)
  return `已保存于 ${time}`
})

function sectionCount(key: SectionKey): number {
  if (!store.draft) return 0
  if (key === 'education') return store.draft.educations.filter((item) => item.school || item.major).length
  if (key === 'campus') return store.draft.campusExperiences.filter((item) => item.organization).length
  if (key === 'courses') return store.draft.courses.filter((item) => item.name).length
  if (key === 'skills') return store.draft.skills.filter((item) => item.name).length
  if (key === 'projects') return store.draft.projects.filter((item) => item.name).length
  if (key === 'publishing') return store.draft.published ? 1 : 0
  return 0
}

async function loadResume() {
  try {
    await store.load()
  } catch {
    showToast(store.error || '简历加载失败', 'error')
  }
}

async function saveNow() {
  if (!store.isDirty) {
    showToast('当前已是最新内容', 'success')
    return
  }
  try {
    await store.save()
    showToast('简历已安全保存', 'success')
  } catch (error) {
    showToast(error instanceof Error ? error.message : '保存失败', 'error')
  }
}

function printResume() {
  mobilePreview.value = false
  window.setTimeout(() => window.print(), 80)
}

function handlePasswordChanged() {
  showPasswordModal.value = false
  showToast('登录密码已更新', 'success')
}

async function handleLogout() {
  if (store.isDirty && !window.confirm('当前有未保存的更改，确定退出吗？')) return
  await auth.logout()
  await router.replace({ name: 'login' })
}

function showToast(message: string, type: 'success' | 'error') {
  toastState.value = { message, type }
  if (toastTimer) clearTimeout(toastTimer)
  toastTimer = setTimeout(() => {
    toastState.value = null
  }, 3200)
}

function beforeUnload(event: BeforeUnloadEvent) {
  if (!store.isDirty) return
  event.preventDefault()
  event.returnValue = ''
}

function handleShortcut(event: KeyboardEvent) {
  if ((event.ctrlKey || event.metaKey) && event.key.toLowerCase() === 's') {
    event.preventDefault()
    void saveNow()
  }
}

onMounted(() => {
  void loadResume()
  window.addEventListener('beforeunload', beforeUnload)
  window.addEventListener('keydown', handleShortcut)
})

onBeforeUnmount(() => {
  window.removeEventListener('beforeunload', beforeUnload)
  window.removeEventListener('keydown', handleShortcut)
  if (toastTimer) clearTimeout(toastTimer)
})
</script>




