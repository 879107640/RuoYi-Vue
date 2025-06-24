package com.ruoyi.patent.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.entity.BaseEntity;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;
/**
 * 用户收藏专利对象 g_patent_favorites
 *
 * @author ruoyi
 * @date 2025-06-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Accessors(chain = true)
@TableName("g_patent_favorites")
public class GPatentFavorites extends BaseEntity {
    private static final long serialVersionUID=1L;

    /** 主键ID */
    @TableId(value = "id" , type = IdType.ASSIGN_ID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    /** 用户ID */
    @Excel(name = "用户ID")
    @TableField(value = "user_id")
    private Long userId;
    /** 专利ID */
    @Excel(name = "专利ID")
    @TableField(value = "patent_id")
    private String patentId;
}
