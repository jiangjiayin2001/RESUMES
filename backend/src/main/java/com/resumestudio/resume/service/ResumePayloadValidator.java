package com.resumestudio.resume.service;

import com.resumestudio.common.ApiPaths;
import com.resumestudio.common.BadRequestException;
import com.resumestudio.config.properties.AppProperties;
import com.resumestudio.resume.dto.ResumePayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class ResumePayloadValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern LOCAL_IMAGE_KEY_PATTERN = Pattern.compile("^[a-f0-9-]{36}\\.[a-z0-9]+$");

    private final AppProperties appProperties;

    public void validate(ResumePayload payload) {
        AppProperties.Validation limits = appProperties.getValidation();
        ConfigValidator validator = new ConfigValidator(limits);

        validator.required("简历名称", payload.title());
        validator.required("姓名", payload.fullName());
        validator.maxLength("简历名称", payload.title(), limits.getResumeTitleMaxLength());
        validator.maxLength("姓名", payload.fullName(), limits.getFullNameMaxLength());
        validator.maxLength("职业定位", payload.headline(), limits.getHeadlineMaxLength());
        validator.maxLength("邮箱", payload.email(), limits.getEmailMaxLength());
        validator.maxLength("手机", payload.phone(), limits.getPhoneMaxLength());
        validator.maxLength("所在城市", payload.location(), limits.getLocationMaxLength());
        validator.maxLength("个人网站", payload.website(), limits.getUrlMaxLength());
        validator.maxLength("GitHub", payload.github(), limits.getUrlMaxLength());
        validator.maxLength("个人简介", payload.summary(), limits.getSummaryMaxLength());
        validator.maxLength("头像地址", payload.avatarUrl(), limits.getAvatarUrlMaxLength());

        if (payload.version() != null && payload.version() < 0) {
            throw new BadRequestException("简历版本号不能为负数");
        }
        if (validator.hasText(payload.email()) && !EMAIL_PATTERN.matcher(payload.email().trim()).matches()) {
            throw new BadRequestException("邮箱格式不正确");
        }
        validator.httpUrl("个人网站", payload.website());
        validator.httpUrl("GitHub", payload.github());
        validateAvatarUrl(payload.avatarUrl());

        validator.maxItems("教育经历", payload.educations(), limits.getMaxEducationEntries());
        validator.maxItems("校园经历", payload.campusExperiences(), limits.getMaxCampusEntries());
        validator.maxItems("主修课程", payload.courses(), limits.getMaxCourseEntries());
        validator.maxItems("专业技能", payload.skills(), limits.getMaxSkillEntries());
        validator.maxItems("项目经历", payload.projects(), limits.getMaxProjectEntries());

        validateEducations(payload.educations(), validator, limits);
        validateCampus(payload.campusExperiences(), validator, limits);
        validateCourses(payload.courses(), validator, limits);
        validateSkills(payload.skills(), validator, limits);
        validateProjects(payload.projects(), validator, limits);
    }

    private void validateEducations(
            List<ResumePayload.EducationItem> items,
            ConfigValidator validator,
            AppProperties.Validation limits
    ) {
        for (ResumePayload.EducationItem item : items) {
            validator.maxLength("学校", item.school(), limits.getItemNameMaxLength());
            validator.maxLength("专业", item.major(), limits.getItemNameMaxLength());
            validator.maxLength("学历", item.degree(), limits.getDegreeMaxLength());
            validator.maxLength("开始时间", item.startDate(), limits.getDateMaxLength());
            validator.maxLength("结束时间", item.endDate(), limits.getDateMaxLength());
            validator.maxLength("GPA", item.gpa(), limits.getGpaMaxLength());
            validator.maxLength("教育描述", item.description(), limits.getDescriptionMaxLength());
        }
    }

    private void validateCampus(
            List<ResumePayload.CampusItem> items,
            ConfigValidator validator,
            AppProperties.Validation limits
    ) {
        for (ResumePayload.CampusItem item : items) {
            validator.maxLength("组织名称", item.organization(), limits.getItemNameMaxLength());
            validator.maxLength("校园角色", item.role(), limits.getItemRoleMaxLength());
            validator.maxLength("开始时间", item.startDate(), limits.getDateMaxLength());
            validator.maxLength("结束时间", item.endDate(), limits.getDateMaxLength());
            validator.maxLength("职责描述", item.description(), limits.getDescriptionMaxLength());
            validator.maxLength("代表成果", item.achievements(), limits.getDescriptionMaxLength());
        }
    }

    private void validateCourses(
            List<ResumePayload.CourseItem> items,
            ConfigValidator validator,
            AppProperties.Validation limits
    ) {
        for (ResumePayload.CourseItem item : items) {
            validator.maxLength("课程名称", item.name(), limits.getCourseNameMaxLength());
            validator.maxLength("课程类别", item.category(), limits.getCategoryMaxLength());
            validator.maxLength("学分", item.credit(), limits.getCreditMaxLength());
            validator.maxLength("成绩", item.score(), limits.getScoreMaxLength());
        }
    }

    private void validateSkills(
            List<ResumePayload.SkillItem> items,
            ConfigValidator validator,
            AppProperties.Validation limits
    ) {
        for (ResumePayload.SkillItem item : items) {
            validator.maxLength("技能名称", item.name(), limits.getSkillNameMaxLength());
            validator.maxLength("技能分类", item.category(), limits.getCategoryMaxLength());
            if (item.level() != null
                    && (item.level() < limits.getMinSkillLevel() || item.level() > limits.getMaxSkillLevel())) {
                throw new BadRequestException("技能熟练度超出允许范围");
            }
        }
    }

    private void validateProjects(
            List<ResumePayload.ProjectItem> items,
            ConfigValidator validator,
            AppProperties.Validation limits
    ) {
        for (ResumePayload.ProjectItem item : items) {
            validator.maxLength("项目名称", item.name(), limits.getItemNameMaxLength());
            validator.maxLength("项目角色", item.role(), limits.getItemRoleMaxLength());
            validator.maxLength("开始时间", item.startDate(), limits.getDateMaxLength());
            validator.maxLength("结束时间", item.endDate(), limits.getDateMaxLength());
            validator.maxLength("技术栈", item.techStack(), limits.getTechStackMaxLength());
            validator.maxLength("项目描述", item.description(), limits.getProjectDescriptionMaxLength());
            validator.maxLength("项目亮点", item.highlights(), limits.getProjectDescriptionMaxLength());
            validator.maxLength("项目链接", item.link(), limits.getLinkMaxLength());
            validator.httpUrl("项目链接", item.link());
        }
    }

    private void validateAvatarUrl(String avatarUrl) {
        if (!hasText(avatarUrl)) {
            return;
        }
        String value = avatarUrl.trim();
        if (isHttpUrl(value)) {
            return;
        }
        String prefix = ApiPaths.PUBLIC_UPLOADS + "/";
        if (value.startsWith(prefix)) {
            String key = value.substring(prefix.length());
            if (LOCAL_IMAGE_KEY_PATTERN.matcher(key).matches()) {
                return;
            }
        }
        throw new BadRequestException("头像地址格式不正确");
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private boolean isHttpUrl(String value) {
        if (!hasText(value)) {
            return true;
        }
        try {
            URI uri = URI.create(value.trim());
            return "http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme());
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private static final class ConfigValidator {
        private final AppProperties.Validation limits;

        private ConfigValidator(AppProperties.Validation limits) {
            this.limits = limits;
        }

        private void required(String field, String value) {
            if (value == null || value.isBlank()) {
                throw new BadRequestException(field + "不能为空");
            }
        }

        private void maxLength(String field, String value, int maxLength) {
            if (value != null && value.length() > maxLength) {
                throw new BadRequestException(field + "不能超过 " + maxLength + " 个字符");
            }
        }

        private void maxItems(String field, List<?> items, int maxItems) {
            if (items != null && items.size() > maxItems) {
                throw new BadRequestException(field + "最多允许 " + maxItems + " 条");
            }
        }

        private void httpUrl(String field, String value) {
            if (!hasText(value)) {
                return;
            }
            try {
                URI uri = URI.create(value.trim());
                if (!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme())) {
                    throw new BadRequestException(field + "必须以 http:// 或 https:// 开头");
                }
            } catch (IllegalArgumentException exception) {
                throw new BadRequestException(field + "格式不正确");
            }
        }

        private boolean hasText(String value) {
            return value != null && !value.isBlank();
        }
    }
}

