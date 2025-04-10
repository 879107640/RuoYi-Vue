package com.ruoyi.patent.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 专利库数据对象 g_patent_library
 * 
 * @author hujch
 * @date 2025-04-10
 */
public class GPatentLibrary extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private String id;

    /** 专利号 */
    @Excel(name = "专利号")
    private String patentNo;

    /** 专利名称 */
    @Excel(name = "专利名称")
    private String patentName;

    /** 专利类型 */
    @Excel(name = "专利类型")
    private String patentTypeKey;

    /** 专利类型值 */
    private Integer patentTypeValue;

    /** 领域 */
    @Excel(name = "领域")
    private String domain;

    /** 缴费日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "缴费日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date feeDate;

    /** 指导价 */
    @Excel(name = "指导价")
    private BigDecimal guidancePrice;

    /** 是否报过高企 */
    private String highTechReportedKey;

    /** 是否报过高企值 */
    private Integer highTechReportedValue;

    /** 资源方 */
    @Excel(name = "资源方")
    private String resourceProvider;

    /** 状态 */
    @Excel(name = "状态")
    private String statusKey;

    /** 状态 */
    private Integer statusValue;

    /** 预定人 */
    @Excel(name = "预定人")
    private String bookerKey;

    /** 预定人值 */
    private String bookerValue;

    /** 天数 */
    @Excel(name = "天数")
    private Long days;

    /** 截止日期 */
    private Date deadline;

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public void setPatentNo(String patentNo) 
    {
        this.patentNo = patentNo;
    }

    public String getPatentNo() 
    {
        return patentNo;
    }

    public void setPatentName(String patentName) 
    {
        this.patentName = patentName;
    }

    public String getPatentName() 
    {
        return patentName;
    }

    public void setPatentTypeKey(String patentTypeKey) 
    {
        this.patentTypeKey = patentTypeKey;
    }

    public String getPatentTypeKey() 
    {
        return patentTypeKey;
    }

    public void setPatentTypeValue(Integer patentTypeValue) 
    {
        this.patentTypeValue = patentTypeValue;
    }

    public Integer getPatentTypeValue() 
    {
        return patentTypeValue;
    }

    public void setDomain(String domain) 
    {
        this.domain = domain;
    }

    public String getDomain() 
    {
        return domain;
    }

    public void setFeeDate(Date feeDate) 
    {
        this.feeDate = feeDate;
    }

    public Date getFeeDate() 
    {
        return feeDate;
    }

    public void setGuidancePrice(BigDecimal guidancePrice) 
    {
        this.guidancePrice = guidancePrice;
    }

    public BigDecimal getGuidancePrice() 
    {
        return guidancePrice;
    }

    public void setHighTechReportedKey(String highTechReportedKey) 
    {
        this.highTechReportedKey = highTechReportedKey;
    }

    public String getHighTechReportedKey() 
    {
        return highTechReportedKey;
    }

    public void setHighTechReportedValue(Integer highTechReportedValue) 
    {
        this.highTechReportedValue = highTechReportedValue;
    }

    public Integer getHighTechReportedValue() 
    {
        return highTechReportedValue;
    }

    public void setResourceProvider(String resourceProvider) 
    {
        this.resourceProvider = resourceProvider;
    }

    public String getResourceProvider() 
    {
        return resourceProvider;
    }

    public void setStatusKey(String statusKey) 
    {
        this.statusKey = statusKey;
    }

    public String getStatusKey() 
    {
        return statusKey;
    }

    public void setStatusValue(Integer statusValue) 
    {
        this.statusValue = statusValue;
    }

    public Integer getStatusValue() 
    {
        return statusValue;
    }

    public void setBookerKey(String bookerKey) 
    {
        this.bookerKey = bookerKey;
    }

    public String getBookerKey() 
    {
        return bookerKey;
    }

    public void setBookerValue(String bookerValue) 
    {
        this.bookerValue = bookerValue;
    }

    public String getBookerValue() 
    {
        return bookerValue;
    }

    public void setDays(Long days) 
    {
        this.days = days;
    }

    public Long getDays() 
    {
        return days;
    }

    public void setDeadline(Date deadline) 
    {
        this.deadline = deadline;
    }

    public Date getDeadline() 
    {
        return deadline;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("patentNo", getPatentNo())
            .append("patentName", getPatentName())
            .append("patentTypeKey", getPatentTypeKey())
            .append("patentTypeValue", getPatentTypeValue())
            .append("domain", getDomain())
            .append("feeDate", getFeeDate())
            .append("guidancePrice", getGuidancePrice())
            .append("highTechReportedKey", getHighTechReportedKey())
            .append("highTechReportedValue", getHighTechReportedValue())
            .append("resourceProvider", getResourceProvider())
            .append("remark", getRemark())
            .append("statusKey", getStatusKey())
            .append("statusValue", getStatusValue())
            .append("bookerKey", getBookerKey())
            .append("bookerValue", getBookerValue())
            .append("days", getDays())
            .append("deadline", getDeadline())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
