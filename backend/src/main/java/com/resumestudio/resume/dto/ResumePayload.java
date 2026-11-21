package com.resumestudio.resume.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ResumePayload(
        @NotBlank @Size(max = 120) String title,
        @NotBlank @Size(max = 100) String fullName,
        @Size(max = 160) String headline,
        @Pattern(regexp = "^$|^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", message = "邮箱格式不正确") @Size(max = 160) String email,
        @Size(max = 50) String phone,
        @Size(max = 120) String location,
        @Pattern(regexp = "^$|^https?://\\S+$", message = "网址需以 http:// 或 https:// 开头") @Size(max = 300) String website,
        @Pattern(regexp = "^$|^https?://\\S+$", message = "GitHub 地址需以 http:// 或 https:// 开头") @Size(max = 300) String github,
        @Size(max = 4000) String summary,
        @Pattern(regexp = "^$|^https?://\\S+$|^/api/public/uploads/[a-f0-9-]{36}\\.(jpg|png|webp)$", message = "头像地址格式不正确") @Size(max = 500) String avatarUrl,
        @NotNull Boolean published,
        @NotNull @Min(0) Integer version,
        @NotNull @Size(max = 20) List<@Valid EducationItem> educations,
        @NotNull @Size(max = 20) List<@Valid CampusItem> campusExperiences,
        @NotNull @Size(max = 40) List<@Valid CourseItem> courses,
        @NotNull @Size(max = 40) List<@Valid SkillItem> skills,
        @NotNull @Size(max = 30) List<@Valid ProjectItem> projects
) {

    public record EducationItem(
            @Size(max = 160) String school,
            @Size(max = 160) String major,
            @Size(max = 50) String degree,
            @Size(max = 20) String startDate,
            @Size(max = 20) String endDate,
            @Size(max = 40) String gpa,
            @Size(max = 2000) String description
    ) {
    }

    public record CampusItem(
            @Size(max = 160) String organization,
            @Size(max = 120) String role,
            @Size(max = 20) String startDate,
            @Size(max = 20) String endDate,
            @Size(max = 2000) String description,
            @Size(max = 2000) String achievements
    ) {
    }

    public record CourseItem(
            @Size(max = 120) String name,
            @Size(max = 80) String category,
            @Size(max = 20) String credit,
            @Size(max = 20) String score
    ) {
    }

    public record SkillItem(
            @Size(max = 80) String name,
            @Size(max = 80) String category,
            @Min(1) @Max(5) Integer level
    ) {
    }

    public record ProjectItem(
            @Size(max = 160) String name,
            @Size(max = 120) String role,
            @Size(max = 20) String startDate,
            @Size(max = 20) String endDate,
            @Size(max = 300) String techStack,
            @Size(max = 2500) String description,
            @Size(max = 2500) String highlights,
            @Pattern(regexp = "^$|^https?://\\S+$", message = "项目链接需以 http:// 或 https:// 开头") @Size(max = 500) String link
    ) {
    }
}


