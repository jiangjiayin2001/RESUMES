import { http } from '@/api/http'
import { API_ROUTES } from '@/config/api-routes'
import type { ResumeData } from '@/types/resume'

type ResumePayload = Omit<ResumeData, 'id' | 'updatedAt'>

function stripServerFields(resume: ResumeData): ResumePayload {
  return {
    published: resume.published,
    version: resume.version,
    title: resume.title,
    fullName: resume.fullName,
    headline: resume.headline,
    email: resume.email,
    phone: resume.phone,
    location: resume.location,
    website: resume.website,
    github: resume.github,
    summary: resume.summary,
    avatarUrl: resume.avatarUrl,
    educations: resume.educations.map(({ id: _id, sortOrder: _sortOrder, ...item }) => item),
    campusExperiences: resume.campusExperiences.map(
      ({ id: _id, sortOrder: _sortOrder, ...item }) => item,
    ),
    courses: resume.courses.map(({ id: _id, sortOrder: _sortOrder, ...item }) => item),
    skills: resume.skills.map(({ id: _id, sortOrder: _sortOrder, ...item }) => item),
    projects: resume.projects.map(({ id: _id, sortOrder: _sortOrder, ...item }) => item),
  }
}

export async function fetchCurrentResume(): Promise<ResumeData> {
  const { data } = await http.get<ResumeData>(API_ROUTES.RESUMES.CURRENT)
  return data
}

export async function saveCurrentResume(resume: ResumeData): Promise<ResumeData> {
  const { data } = await http.put<ResumeData>(API_ROUTES.RESUMES.CURRENT, stripServerFields(resume))
  return data
}

export async function fetchPublicResume(): Promise<ResumeData> {
  const { data } = await http.get<ResumeData>(API_ROUTES.RESUMES.PUBLIC)
  return data
}

