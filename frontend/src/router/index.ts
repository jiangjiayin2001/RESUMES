import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const PublicResumeView = () => import('@/views/PublicResumeView.vue')

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'public-resume',
      component: PublicResumeView,
      meta: { publicPage: 'home' },
    },
    {
      path: '/education',
      name: 'public-education',
      component: PublicResumeView,
      meta: { publicPage: 'education' },
    },
    {
      path: '/campus',
      name: 'public-campus',
      component: PublicResumeView,
      meta: { publicPage: 'campus' },
    },
    {
      path: '/projects',
      name: 'public-projects',
      component: PublicResumeView,
      meta: { publicPage: 'projects' },
    },
    {
      path: '/skills',
      name: 'public-skills',
      component: PublicResumeView,
      meta: { publicPage: 'skills' },
    },
    {
      path: '/courses',
      name: 'public-courses',
      component: PublicResumeView,
      meta: { publicPage: 'courses' },
    },
    {
      path: '/contact',
      name: 'public-contact',
      component: PublicResumeView,
      meta: { publicPage: 'contact' },
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { guestOnly: true },
    },
    {
      path: '/admin',
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
