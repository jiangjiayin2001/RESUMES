package com.resumestudio.resume.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("skills")
public class SkillEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long resumeId;
    private String name;
    private String category;
    private Integer level;
    private Integer sortOrder;
}
