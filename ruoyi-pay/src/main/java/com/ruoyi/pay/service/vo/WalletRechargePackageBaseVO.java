package com.ruoyi.pay.service.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 充值套餐 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class WalletRechargePackageBaseVO {

  @NotNull(message = "套餐名不能为空")
  private String name;

  @NotNull(message = "支付金额不能为空")
  private Integer payPrice;

  @NotNull(message = "赠送金额不能为空")
  private Integer bonusPrice;

  @NotNull(message = "状态不能为空")
  private Byte status;

}
