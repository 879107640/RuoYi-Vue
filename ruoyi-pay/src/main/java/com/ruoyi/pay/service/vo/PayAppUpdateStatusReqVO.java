package com.ruoyi.pay.service.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PayAppUpdateStatusReqVO {

  @NotNull(message = "应用编号不能为空")
  private Long id;

  @NotNull(message = "状态不能为空")
  private Integer status;

}
