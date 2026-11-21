package com.resumestudio.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.List;

@Data
@Validated
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Cors cors = new Cors();
    private Bootstrap bootstrap = new Bootstrap();
    private Security security = new Security();
    private Storage storage = new Storage();
    private Cache cache = new Cache();
    private Validation validation = new Validation();

    @Data
    public static class Cors {
        private List<String> allowedOrigins = List.of("http://localhost:5173");
    }

    @Data
    public static class Bootstrap {
        private String adminUsername = "";
        private String adminPassword = "";
    }

    @Data
    public static class Security {
        private int loginMaxAttempts = 5;
        private int loginLockMinutes = 15;
        private String loginAttemptStore = "redis";
        private String loginAttemptKeyPrefix = "resume-studio:login-attempt:";
        private int usernameMaxLength = 80;
        private int passwordMaxLength = 200;
        private int newPasswordMinLength = 12;
        private int newPasswordMaxLength = 72;
    }

    @Data
    public static class Storage {
        private String type = "local";
        private long maxImageBytes = 8_388_608L;
        private int maxImageDimension = 8000;
        private int imageCacheMaxAgeDays = 365;
        private List<String> allowedFormats = List.of("jpg", "png", "webp");
        private List<String> allowedContentTypes = List.of("image/jpeg", "image/png", "image/webp");
        private Local local = new Local();
        private Oss oss = new Oss();
    }

    @Data
    public static class Local {
        private String root = "./data/uploads";
    }

    @Data
    public static class Oss {
        private String endpoint = "";
        private String bucket = "";
        private String accessKeyId = "";
        private String accessKeySecret = "";
        private String publicBaseUrl = "";
        private String keyPrefix = "resume/avatar/";
    }

    @Data
    public static class Cache {
        private Duration resumeTtl = Duration.ofMinutes(10);
    }

    @Data
    public static class Validation {
        private int resumeTitleMaxLength = 120;
        private int fullNameMaxLength = 100;
        private int headlineMaxLength = 160;
        private int emailMaxLength = 160;
        private int phoneMaxLength = 50;
        private int degreeMaxLength = 50;
        private int gpaMaxLength = 40;
        private int creditMaxLength = 20;
        private int scoreMaxLength = 20;
        private int locationMaxLength = 120;
        private int urlMaxLength = 300;
        private int summaryMaxLength = 4000;
        private int avatarUrlMaxLength = 500;
        private int itemNameMaxLength = 160;
        private int itemRoleMaxLength = 120;
        private int skillNameMaxLength = 80;
        private int courseNameMaxLength = 120;
        private int categoryMaxLength = 80;
        private int dateMaxLength = 20;
        private int descriptionMaxLength = 2000;
        private int projectDescriptionMaxLength = 2500;
        private int techStackMaxLength = 300;
        private int linkMaxLength = 500;
        private int maxEducationEntries = 20;
        private int maxCampusEntries = 20;
        private int maxCourseEntries = 40;
        private int maxSkillEntries = 40;
        private int maxProjectEntries = 30;
        private int minSkillLevel = 1;
        private int maxSkillLevel = 5;
    }
}




