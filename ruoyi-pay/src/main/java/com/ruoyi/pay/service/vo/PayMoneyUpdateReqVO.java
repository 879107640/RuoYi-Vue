package com.ruoyi.pay.service.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class PayMoneyUpdateReqVO extends PayMoneyBaseReqVO {

  @NotNull(message = "查看金额不能为空")
  private Long id;
}
