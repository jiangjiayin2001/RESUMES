package com.resumestudio.resume.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("projects")
public class ProjectEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long resumeId;
    private String name;
    private String role;
    private String startDate;
    private String endDate;
    private String techStack;
    private String description;
    private String highlights;
    private String link;
    private Integer sortOrder;
}
