export interface AuthUser {
  id: number
  username: string
  displayName: string
}

export interface Education {
  id?: number
  school: string
  major: string
  degree: string
  startDate: string
  endDate: string
  gpa: string
  description: string
  sortOrder?: number
}

export interface CampusExperience {
  id?: number
  organization: string
  role: string
  startDate: string
  endDate: string
  description: string
  achievements: string
  sortOrder?: number
}

export interface Course {
  id?: number
  name: string
  category: string
  credit: string
  score: string
  sortOrder?: number
}

export interface Skill {
  id?: number
  name: string
  category: string
  level: number
  sortOrder?: number
}

export interface Project {
  id?: number
  name: string
  role: string
  startDate: string
  endDate: string
  techStack: string
  description: string
  highlights: string
  link: string
  sortOrder?: number
}

export interface ResumeData {
  id: number
  published: boolean
  version: number
  title: string
  fullName: string
  headline: string
  email: string
  phone: string
  location: string
  website: string
  github: string
  summary: string
  avatarUrl: string
  updatedAt: string
  educations: Education[]
  campusExperiences: CampusExperience[]
  courses: Course[]
  skills: Skill[]
  projects: Project[]
}

export type SectionKey =
  | 'profile'
  | 'publishing'
  | 'education'
  | 'campus'
  | 'courses'
  | 'skills'
  | 'projects'


