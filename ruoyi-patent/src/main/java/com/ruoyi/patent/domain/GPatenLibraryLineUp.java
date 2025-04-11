package com.ruoyi.patent.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 专利预约排队对象 g_paten_library_line_up
 *
 * @author ruoyi
 * @date 2025-04-10
 */
public class GPatenLibraryLineUp extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    private String id;

    /**
     * 专利主键
     */
    @Excel(name = "专利主键")
    private String gPatentId;

    /**
     * 用户id
     */
    @Excel(name = "用户id")
    private Long userId;

    /**
     * 排队
     */
    @Excel(name = "排队")
    private Long lineUpNum;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdTime;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setgPatentId(String gPatentId) {
        this.gPatentId = gPatentId;
    }

    public String getgPatentId() {
        return gPatentId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setLineUpNum(Long lineUpNum) {
        this.lineUpNum = lineUpNum;
    }

    public Long getLineUpNum() {
        return lineUpNum;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("gPatentId", getgPatentId())
            .append("userId", getUserId())
            .append("lineUpNum", getLineUpNum())
            .append("createdTime", getCreatedTime())
            .toString();
    }
}
