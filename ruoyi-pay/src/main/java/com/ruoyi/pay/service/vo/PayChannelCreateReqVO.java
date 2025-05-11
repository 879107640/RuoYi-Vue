package com.ruoyi.pay.service.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PayChannelCreateReqVO extends PayChannelBaseVO {

  @NotNull(message = "渠道编码不能为空")
  private String code;

  @NotBlank(message = "渠道配置不能为空")
  private String config;

}
