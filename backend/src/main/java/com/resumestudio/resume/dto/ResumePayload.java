package com.resumestudio.resume.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ResumePayload(
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
        @NotNull Boolean published,
        @NotNull Integer version,
        @NotNull List<EducationItem> educations,
        @NotNull List<CampusItem> campusExperiences,
        @NotNull List<CourseItem> courses,
        @NotNull List<SkillItem> skills,
        @NotNull List<ProjectItem> projects
) {

    public record EducationItem(
            String school,
            String major,
            String degree,
            String startDate,
            String endDate,
            String gpa,
            String description
    ) {
    }

    public record CampusItem(
            String organization,
            String role,
            String startDate,
            String endDate,
            String description,
            String achievements
    ) {
    }

    public record CourseItem(
            String name,
            String category,
            String credit,
            String score
    ) {
    }

    public record SkillItem(
            String name,
            String category,
            Integer level
    ) {
    }

    public record ProjectItem(
            String name,
            String role,
            String startDate,
            String endDate,
            String techStack,
            String description,
            String highlights,
            String link
    ) {
    }
}
