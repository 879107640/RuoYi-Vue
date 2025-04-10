package com.ruoyi.patent.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 求购信息对象 g_purchase_request
 *
 * @author ruoyi
 * @date 2025-04-11
 */
public class GPurchaseRequest extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，UUID
     */
    private String id;

    /**
     * 求购信息
     */
    @Excel(name = "求购信息")
    private String info;

    /**
     * 推荐数量
     */
    @Excel(name = "推荐数量")
    private Long recommendCount;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发布时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date publishTime;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public void setRecommendCount(Long recommendCount) {
        this.recommendCount = recommendCount;
    }

    public Long getRecommendCount() {
        return recommendCount;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("info", getInfo())
                .append("recommendCount", getRecommendCount())
                .append("publishTime", getPublishTime())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("createBy", getCreateBy())
                .append("updateBy", getUpdateBy())
                .toString();
    }
}
