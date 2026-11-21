package com.resumestudio.resume.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("campus_experiences")
public class CampusExperienceEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long resumeId;
    private String organization;
    private String role;
    private String startDate;
    private String endDate;
    private String description;
    private String achievements;
    private Integer sortOrder;
}
