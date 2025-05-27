package com.ruoyi.pay.service.vo;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@ToString(callSuper = true)
public class PayMoneyBaseReqVO {

  @NotNull(message = "查看金额不能为空")
  private Long money;
}
