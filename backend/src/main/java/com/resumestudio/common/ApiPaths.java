package com.resumestudio.common;

public final class ApiPaths {

    public static final String AUTH_BASE = "/api/auth";
    public static final String PUBLIC_BASE = "/api/public";
    public static final String PUBLIC_RESUME = PUBLIC_BASE + "/resume";
    public static final String PUBLIC_UPLOADS = PUBLIC_BASE + "/uploads";
    public static final String UPLOADS_BASE = "/api/uploads";
    public static final String UPLOAD_IMAGES = UPLOADS_BASE + "/images";
    public static final String UPLOAD_CONFIG = UPLOADS_BASE + "/config";
    public static final String CONFIG_BASE = "/api/config";
    public static final String RESUME_FORM_CONFIG = CONFIG_BASE + "/resume-form";
    public static final String RESUMES_BASE = "/api/resumes";
    public static final String CURRENT_RESUME = "/current";

    private ApiPaths() {
    }
}

