package com.resumestudio.resume.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.resumestudio.resume.dto.ResumePayload;
import com.resumestudio.resume.dto.ResumeResponse;
import com.resumestudio.resume.entity.CampusExperienceEntity;
import com.resumestudio.resume.entity.CourseEntity;
import com.resumestudio.resume.entity.EducationEntity;
import com.resumestudio.resume.entity.ProjectEntity;
import com.resumestudio.resume.entity.ResumeEntity;
import com.resumestudio.resume.entity.SkillEntity;
import com.resumestudio.resume.mapper.CampusExperienceMapper;
import com.resumestudio.resume.mapper.CourseMapper;
import com.resumestudio.resume.mapper.EducationMapper;
import com.resumestudio.resume.mapper.ProjectMapper;
import com.resumestudio.resume.mapper.ResumeMapper;
import com.resumestudio.resume.mapper.SkillMapper;
import com.resumestudio.common.CacheNames;
import com.resumestudio.common.ConflictException;
import com.resumestudio.common.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeMapper resumeMapper;
    private final EducationMapper educationMapper;
    private final CampusExperienceMapper campusExperienceMapper;
    private final CourseMapper courseMapper;
    private final SkillMapper skillMapper;
    private final ProjectMapper projectMapper;

    @Cacheable(cacheNames = CacheNames.RESUME_CURRENT, key = "#userId")
    @Transactional(readOnly = true)
    public ResumeResponse getCurrent(Long userId) {
        return readResume(userId);
    }

    @Cacheable(cacheNames = CacheNames.RESUME_PUBLIC, key = "'current'")
    @Transactional(readOnly = true)
    public ResumeResponse getPublished() {
        ResumeEntity resume = resumeMapper.selectOne(new LambdaQueryWrapper<ResumeEntity>()
                .eq(ResumeEntity::getPublished, true)
                .orderByDesc(ResumeEntity::getUpdatedAt)
                .last("LIMIT 1"));
        if (resume == null) {
            throw new ResourceNotFoundException("公开简历尚未发布");
        }
        return mapResume(resume);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.RESUME_CURRENT, key = "#userId"),
            @CacheEvict(cacheNames = CacheNames.RESUME_PUBLIC, allEntries = true)
    })
    @Transactional
    public ResumeResponse saveCurrent(Long userId, ResumePayload payload) {
        ResumeEntity resume = findResume(userId);
        LocalDateTime now = LocalDateTime.now();

        LambdaUpdateWrapper<ResumeEntity> update = Wrappers.lambdaUpdate();
        update.eq(ResumeEntity::getId, resume.getId())
                .eq(ResumeEntity::getVersion, payload.version())
                .set(ResumeEntity::getPublished, Boolean.TRUE.equals(payload.published()))
                .set(ResumeEntity::getTitle, payload.title().trim())
                .set(ResumeEntity::getFullName, payload.fullName().trim())
                .set(ResumeEntity::getHeadline, normalize(payload.headline()))
                .set(ResumeEntity::getEmail, normalize(payload.email()))
                .set(ResumeEntity::getPhone, normalize(payload.phone()))
                .set(ResumeEntity::getLocation, normalize(payload.location()))
                .set(ResumeEntity::getWebsite, normalize(payload.website()))
                .set(ResumeEntity::getGithub, normalize(payload.github()))
                .set(ResumeEntity::getSummary, normalize(payload.summary()))
                .set(ResumeEntity::getAvatarUrl, normalize(payload.avatarUrl()))
                .set(ResumeEntity::getUpdatedAt, now)
                .setSql("version = version + 1");

        int updated = resumeMapper.update(null, update);
        if (updated == 0) {
            throw new ConflictException("简历已在其他窗口更新，请刷新后重试");
        }

        replaceChildren(resume.getId(), payload);
        return readResume(userId);
    }

    private ResumeResponse readResume(Long userId) {
        return mapResume(findResume(userId));
    }

    private ResumeResponse mapResume(ResumeEntity resume) {
        List<ResumeResponse.EducationItem> educations = educationMapper.selectList(
                        Wrappers.<EducationEntity>lambdaQuery()
                                .eq(EducationEntity::getResumeId, resume.getId())
                                .orderByAsc(EducationEntity::getSortOrder))
                .stream()
                .map(item -> new ResumeResponse.EducationItem(
                        item.getId(), item.getSchool(), item.getMajor(), item.getDegree(),
                        item.getStartDate(), item.getEndDate(), item.getGpa(), item.getDescription(),
                        item.getSortOrder()))
                .toList();

        List<ResumeResponse.CampusItem> campusExperiences = campusExperienceMapper.selectList(
                        Wrappers.<CampusExperienceEntity>lambdaQuery()
                                .eq(CampusExperienceEntity::getResumeId, resume.getId())
                                .orderByAsc(CampusExperienceEntity::getSortOrder))
                .stream()
                .map(item -> new ResumeResponse.CampusItem(
                        item.getId(), item.getOrganization(), item.getRole(), item.getStartDate(),
                        item.getEndDate(), item.getDescription(), item.getAchievements(), item.getSortOrder()))
                .toList();

        List<ResumeResponse.CourseItem> courses = courseMapper.selectList(
                        Wrappers.<CourseEntity>lambdaQuery()
                                .eq(CourseEntity::getResumeId, resume.getId())
                                .orderByAsc(CourseEntity::getSortOrder))
                .stream()
                .map(item -> new ResumeResponse.CourseItem(
                        item.getId(), item.getName(), item.getCategory(), item.getCredit(),
                        item.getScore(), item.getSortOrder()))
                .toList();

        List<ResumeResponse.SkillItem> skills = skillMapper.selectList(
                        Wrappers.<SkillEntity>lambdaQuery()
                                .eq(SkillEntity::getResumeId, resume.getId())
                                .orderByAsc(SkillEntity::getSortOrder))
                .stream()
                .map(item -> new ResumeResponse.SkillItem(
                        item.getId(), item.getName(), item.getCategory(), item.getLevel(), item.getSortOrder()))
                .toList();

        List<ResumeResponse.ProjectItem> projects = projectMapper.selectList(
                        Wrappers.<ProjectEntity>lambdaQuery()
                                .eq(ProjectEntity::getResumeId, resume.getId())
                                .orderByAsc(ProjectEntity::getSortOrder))
                .stream()
                .map(item -> new ResumeResponse.ProjectItem(
                        item.getId(), item.getName(), item.getRole(), item.getStartDate(), item.getEndDate(),
                        item.getTechStack(), item.getDescription(), item.getHighlights(), item.getLink(),
                        item.getSortOrder()))
                .toList();

        return new ResumeResponse(
                resume.getId(),
                Boolean.TRUE.equals(resume.getPublished()),
                resume.getVersion(),
                resume.getTitle(),
                resume.getFullName(),
                resume.getHeadline(),
                resume.getEmail(),
                resume.getPhone(),
                resume.getLocation(),
                resume.getWebsite(),
                resume.getGithub(),
                resume.getSummary(),
                resume.getAvatarUrl(),
                resume.getUpdatedAt(),
                educations,
                campusExperiences,
                courses,
                skills,
                projects
        );
    }

    private ResumeEntity findResume(Long userId) {
        ResumeEntity resume = resumeMapper.selectOne(new LambdaQueryWrapper<ResumeEntity>()
                .eq(ResumeEntity::getUserId, userId)
                .last("LIMIT 1"));
        if (resume == null) {
            throw new ResourceNotFoundException("未找到当前用户的简历");
        }
        return resume;
    }

    private void replaceChildren(Long resumeId, ResumePayload payload) {
        educationMapper.delete(Wrappers.<EducationEntity>lambdaQuery()
                .eq(EducationEntity::getResumeId, resumeId));
        campusExperienceMapper.delete(Wrappers.<CampusExperienceEntity>lambdaQuery()
                .eq(CampusExperienceEntity::getResumeId, resumeId));
        courseMapper.delete(Wrappers.<CourseEntity>lambdaQuery()
                .eq(CourseEntity::getResumeId, resumeId));
        skillMapper.delete(Wrappers.<SkillEntity>lambdaQuery()
                .eq(SkillEntity::getResumeId, resumeId));
        projectMapper.delete(Wrappers.<ProjectEntity>lambdaQuery()
                .eq(ProjectEntity::getResumeId, resumeId));

        for (int index = 0; index < payload.educations().size(); index++) {
            ResumePayload.EducationItem item = payload.educations().get(index);
            EducationEntity entity = new EducationEntity();
            entity.setResumeId(resumeId);
            entity.setSchool(normalize(item.school()));
            entity.setMajor(normalize(item.major()));
            entity.setDegree(normalize(item.degree()));
            entity.setStartDate(normalize(item.startDate()));
            entity.setEndDate(normalize(item.endDate()));
            entity.setGpa(normalize(item.gpa()));
            entity.setDescription(normalize(item.description()));
            entity.setSortOrder(index);
            educationMapper.insert(entity);
        }

        for (int index = 0; index < payload.campusExperiences().size(); index++) {
            ResumePayload.CampusItem item = payload.campusExperiences().get(index);
            CampusExperienceEntity entity = new CampusExperienceEntity();
            entity.setResumeId(resumeId);
            entity.setOrganization(normalize(item.organization()));
            entity.setRole(normalize(item.role()));
            entity.setStartDate(normalize(item.startDate()));
            entity.setEndDate(normalize(item.endDate()));
            entity.setDescription(normalize(item.description()));
            entity.setAchievements(normalize(item.achievements()));
            entity.setSortOrder(index);
            campusExperienceMapper.insert(entity);
        }

        for (int index = 0; index < payload.courses().size(); index++) {
            ResumePayload.CourseItem item = payload.courses().get(index);
            CourseEntity entity = new CourseEntity();
            entity.setResumeId(resumeId);
            entity.setName(normalize(item.name()));
            entity.setCategory(normalize(item.category()));
            entity.setCredit(normalize(item.credit()));
            entity.setScore(normalize(item.score()));
            entity.setSortOrder(index);
            courseMapper.insert(entity);
        }

        for (int index = 0; index < payload.skills().size(); index++) {
            ResumePayload.SkillItem item = payload.skills().get(index);
            SkillEntity entity = new SkillEntity();
            entity.setResumeId(resumeId);
            entity.setName(normalize(item.name()));
            entity.setCategory(normalize(item.category()));
            entity.setLevel(item.level() == null ? 3 : item.level());
            entity.setSortOrder(index);
            skillMapper.insert(entity);
        }

        for (int index = 0; index < payload.projects().size(); index++) {
            ResumePayload.ProjectItem item = payload.projects().get(index);
            ProjectEntity entity = new ProjectEntity();
            entity.setResumeId(resumeId);
            entity.setName(normalize(item.name()));
            entity.setRole(normalize(item.role()));
            entity.setStartDate(normalize(item.startDate()));
            entity.setEndDate(normalize(item.endDate()));
            entity.setTechStack(normalize(item.techStack()));
            entity.setDescription(normalize(item.description()));
            entity.setHighlights(normalize(item.highlights()));
            entity.setLink(normalize(item.link()));
            entity.setSortOrder(index);
            projectMapper.insert(entity);
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}



