export const API_ROUTES = Object.freeze({
  AUTH: Object.freeze({
    CSRF: '/auth/csrf',
    LOGIN: '/auth/login',
    ME: '/auth/me',
    LOGOUT: '/auth/logout',
    PASSWORD: '/auth/password',
  }),
  RESUMES: Object.freeze({
    CURRENT: '/resumes/current',
    PUBLIC: '/public/resume',
  }),
  CONFIG: Object.freeze({
    RESUME_FORM: '/config/resume-form',
  }),
  UPLOADS: Object.freeze({
    IMAGES: '/uploads/images',
    CONFIG: '/uploads/config',
  }),
})

export const PUBLIC_PAGE_ROUTES = Object.freeze({
  HOME: '/',
  EDUCATION: '/education',
  CAMPUS: '/campus',
  PROJECTS: '/projects',
  SKILLS: '/skills',
  COURSES: '/courses',
  CONTACT: '/contact',
  LOGIN: '/login',
  ADMIN: '/admin',
})

