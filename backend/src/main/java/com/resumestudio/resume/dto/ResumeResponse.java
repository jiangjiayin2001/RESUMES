package com.resumestudio.resume.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ResumeResponse(
        Long id,
        Boolean published,
        Integer version,
        String title,
        String fullName,
        String headline,
        String email,
        String phone,
        String location,
        String website,
        String github,
        String summary,
        String avatarUrl,
        LocalDateTime updatedAt,
        List<EducationItem> educations,
        List<CampusItem> campusExperiences,
        List<CourseItem> courses,
        List<SkillItem> skills,
        List<ProjectItem> projects
) {
    public record EducationItem(
            Long id,
            String school,
            String major,
            String degree,
            String startDate,
            String endDate,
            String gpa,
            String description,
            Integer sortOrder
    ) {
    }

    public record CampusItem(
            Long id,
            String organization,
            String role,
            String startDate,
            String endDate,
            String description,
            String achievements,
            Integer sortOrder
    ) {
    }

    public record CourseItem(
            Long id,
            String name,
            String category,
            String credit,
            String score,
            Integer sortOrder
    ) {
    }

    public record SkillItem(
            Long id,
            String name,
            String category,
            Integer level,
            Integer sortOrder
    ) {
    }

    public record ProjectItem(
            Long id,
            String name,
            String role,
            String startDate,
            String endDate,
            String techStack,
            String description,
            String highlights,
            String link,
            Integer sortOrder
    ) {
    }
}


