import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { PUBLIC_PAGE_ROUTES } from '@/config/api-routes'

const PublicResumeView = () => import('@/views/PublicResumeView.vue')

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: PUBLIC_PAGE_ROUTES.HOME,
      name: 'public-resume',
      component: PublicResumeView,
      meta: { publicPage: 'home' },
    },
    {
      path: PUBLIC_PAGE_ROUTES.EDUCATION,
      name: 'public-education',
      component: PublicResumeView,
      meta: { publicPage: 'education' },
    },
    {
      path: PUBLIC_PAGE_ROUTES.CAMPUS,
      name: 'public-campus',
      component: PublicResumeView,
      meta: { publicPage: 'campus' },
    },
    {
      path: PUBLIC_PAGE_ROUTES.PROJECTS,
      name: 'public-projects',
      component: PublicResumeView,
      meta: { publicPage: 'projects' },
    },
    {
      path: PUBLIC_PAGE_ROUTES.SKILLS,
      name: 'public-skills',
      component: PublicResumeView,
      meta: { publicPage: 'skills' },
    },
    {
      path: PUBLIC_PAGE_ROUTES.COURSES,
      name: 'public-courses',
      component: PublicResumeView,
      meta: { publicPage: 'courses' },
    },
    {
      path: PUBLIC_PAGE_ROUTES.CONTACT,
      name: 'public-contact',
      component: PublicResumeView,
      meta: { publicPage: 'contact' },
    },
    {
      path: PUBLIC_PAGE_ROUTES.LOGIN,
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { guestOnly: true },
    },
    {
      path: PUBLIC_PAGE_ROUTES.ADMIN,
      name: 'studio',
      component: () => import('@/views/ResumeStudioView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/',
    },
  ],
  scrollBehavior: () => ({ top: 0 }),
})

router.beforeEach(async (to) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth || to.meta.guestOnly) {
    await auth.initialize()
  }
  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (to.meta.guestOnly && auth.isAuthenticated) {
    return { name: 'studio' }
  }
  return true
})

export default router

