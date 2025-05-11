package com.ruoyi.pay.service.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PayChannelUpdateReqVO extends PayChannelBaseVO {

  @NotNull(message = "商户编号不能为空")
  private Long id;

  @NotBlank(message = "渠道配置不能为空")
  private String config;

}
