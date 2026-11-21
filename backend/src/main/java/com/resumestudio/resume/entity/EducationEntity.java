package com.resumestudio.resume.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("educations")
public class EducationEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long resumeId;
    private String school;
    private String major;
    private String degree;
    private String startDate;
    private String endDate;
    private String gpa;
    private String description;
    private Integer sortOrder;
}
