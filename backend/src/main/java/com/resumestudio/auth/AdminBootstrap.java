package com.resumestudio.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.resumestudio.config.properties.AppProperties;
import com.resumestudio.resume.entity.ResumeEntity;
import com.resumestudio.resume.mapper.ResumeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminBootstrap implements ApplicationRunner {

    private final AppUserMapper appUserMapper;
    private final ResumeMapper resumeMapper;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String adminUsername = appProperties.getBootstrap().getAdminUsername();
        String adminPassword = appProperties.getBootstrap().getAdminPassword();

        if (isBlank(adminUsername) || isBlank(adminPassword)) {
            log.warn("APP_ADMIN_USERNAME / APP_ADMIN_PASSWORD 未配置，跳过初始管理员创建");
            return;
        }

        AppProperties.Security security = appProperties.getSecurity();
        if (adminPassword.length() < security.getNewPasswordMinLength()
                || adminPassword.length() > security.getNewPasswordMaxLength()) {
            throw new IllegalStateException("APP_ADMIN_PASSWORD 长度不符合配置要求");
        }
        if (adminUsername.length() > security.getUsernameMaxLength()) {
            throw new IllegalStateException("APP_ADMIN_USERNAME 长度超过配置上限");
        }

        AppUser existing = appUserMapper.selectOne(new LambdaQueryWrapper<AppUser>()
                .eq(AppUser::getUsername, adminUsername)
                .last("LIMIT 1"));
        if (existing == null) {
            AppUser user = new AppUser();
            user.setUsername(adminUsername.trim());
            user.setPasswordHash(passwordEncoder.encode(adminPassword));
            user.setDisplayName(adminUsername.trim());
            user.setEnabled(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            appUserMapper.insert(user);
            existing = user;
            log.info("Initial administrator created: {}", user.getUsername());
        }

        Long resumeCount = resumeMapper.selectCount(new LambdaQueryWrapper<ResumeEntity>()
                .eq(ResumeEntity::getUserId, existing.getId()));
        if (resumeCount == 0) {
            ResumeEntity resume = new ResumeEntity();
            resume.setUserId(existing.getId());
            resume.setTitle("我的简历");
            resume.setFullName("");
            resume.setPublished(false);
            resume.setVersion(0);
            resume.setCreatedAt(LocalDateTime.now());
            resume.setUpdatedAt(LocalDateTime.now());
            resumeMapper.insert(resume);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
