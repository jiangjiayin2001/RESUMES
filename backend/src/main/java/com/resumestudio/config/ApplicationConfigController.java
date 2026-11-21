package com.resumestudio.config;

import com.resumestudio.common.ApiPaths;
import com.resumestudio.config.properties.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApplicationConfigController {

    private final AppProperties appProperties;

    @GetMapping(ApiPaths.RESUME_FORM_CONFIG)
    public ResumeFormConfigResponse resumeFormConfig() {
        AppProperties.Validation limits = appProperties.getValidation();
        return new ResumeFormConfigResponse(
                limits.getResumeTitleMaxLength(),
                limits.getFullNameMaxLength(),
                limits.getHeadlineMaxLength(),
                limits.getEmailMaxLength(),
                limits.getPhoneMaxLength(),
                limits.getLocationMaxLength(),
                limits.getUrlMaxLength(),
                limits.getSummaryMaxLength(),
                limits.getAvatarUrlMaxLength(),
                limits.getItemNameMaxLength(),
                limits.getItemRoleMaxLength(),
                limits.getCategoryMaxLength(),
                limits.getDateMaxLength(),
                limits.getDescriptionMaxLength(),
                limits.getProjectDescriptionMaxLength(),
                limits.getTechStackMaxLength(),
                limits.getLinkMaxLength(),
                limits.getDegreeMaxLength(),
                limits.getGpaMaxLength(),
                limits.getCreditMaxLength(),
                limits.getScoreMaxLength(),
                limits.getSkillNameMaxLength(),
                limits.getCourseNameMaxLength(),
                limits.getMaxEducationEntries(),
                limits.getMaxCampusEntries(),
                limits.getMaxCourseEntries(),
                limits.getMaxSkillEntries(),
                limits.getMaxProjectEntries(),
                limits.getMinSkillLevel(),
                limits.getMaxSkillLevel()
        );
    }

    public record ResumeFormConfigResponse(
            int titleMaxLength,
            int fullNameMaxLength,
            int headlineMaxLength,
            int emailMaxLength,
            int phoneMaxLength,
            int locationMaxLength,
            int urlMaxLength,
            int summaryMaxLength,
            int avatarUrlMaxLength,
            int itemNameMaxLength,
            int itemRoleMaxLength,
            int categoryMaxLength,
            int dateMaxLength,
            int descriptionMaxLength,
            int projectDescriptionMaxLength,
            int techStackMaxLength,
            int linkMaxLength,
            int degreeMaxLength,
            int gpaMaxLength,
            int creditMaxLength,
            int scoreMaxLength,
            int skillNameMaxLength,
            int courseNameMaxLength,
            int maxEducationEntries,
            int maxCampusEntries,
            int maxCourseEntries,
            int maxSkillEntries,
            int maxProjectEntries,
            int minSkillLevel,
            int maxSkillLevel
    ) {
    }
}
