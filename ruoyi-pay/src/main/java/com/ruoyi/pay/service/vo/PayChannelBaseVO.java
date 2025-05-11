package com.ruoyi.pay.service.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 支付渠道 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class PayChannelBaseVO {

  @NotNull(message = "开启状态不能为空")
  private Integer status;

  private String remark;

  @NotNull(message = "渠道费率，单位：百分比不能为空")
  private Double feeRate;

  @NotNull(message = "应用编号不能为空")
  private Long appId;

}
