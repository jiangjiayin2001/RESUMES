package com.resumestudio.resume.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("resumes")
public class ResumeEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String fullName;
    private String headline;
    private String email;
    private String phone;
    private String location;
    private String website;
    private String github;
    private String summary;
    private String avatarUrl;
    private Boolean published;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

