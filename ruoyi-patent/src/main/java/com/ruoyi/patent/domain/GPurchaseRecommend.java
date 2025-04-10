package com.ruoyi.patent.domain;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 推荐信息对象 g_purchase_recommend
 *
 * @author ruoyi
 * @date 2025-04-11
 */
public class GPurchaseRecommend extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，UUID
     */
    private String id;

    /**
     * 对应求购ID
     */
    @Excel(name = "对应求购ID")
    private String purchaseId;

    /**
     * 对应专利ID
     */
    @Excel(name = "对应专利ID")
    private String patentId;

    /**
     * 专利号
     */
    @Excel(name = "专利号")
    private String patentNo;

    /**
     * 专利名称
     */
    @Excel(name = "专利名称")
    private String patentName;

    /**
     * 指导价
     */
    @Excel(name = "指导价")
    private BigDecimal guidancePrice;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setPurchaseId(String purchaseId) {
        this.purchaseId = purchaseId;
    }

    public String getPurchaseId() {
        return purchaseId;
    }

    public void setPatentId(String patentId) {
        this.patentId = patentId;
    }

    public String getPatentId() {
        return patentId;
    }

    public void setPatentNo(String patentNo) {
        this.patentNo = patentNo;
    }

    public String getPatentNo() {
        return patentNo;
    }

    public void setPatentName(String patentName) {
        this.patentName = patentName;
    }

    public String getPatentName() {
        return patentName;
    }

    public void setGuidancePrice(BigDecimal guidancePrice) {
        this.guidancePrice = guidancePrice;
    }

    public BigDecimal getGuidancePrice() {
        return guidancePrice;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("purchaseId", getPurchaseId())
                .append("patentId", getPatentId())
                .append("patentNo", getPatentNo())
                .append("patentName", getPatentName())
                .append("guidancePrice", getGuidancePrice())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("createBy", getCreateBy())
                .append("updateBy", getUpdateBy())
                .toString();
    }
}
