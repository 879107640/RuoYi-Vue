package com.ruoyi.pay.service.vo;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 支付应用信息 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class PayAppBaseVO {

  @NotEmpty(message = "应用标识不能为空")
  private String appKey;

  @NotNull(message = "应用名不能为空")
  private String name;

  @NotNull(message = "开启状态不能为空")
  private Integer status;

  private String remark;

  @NotNull(message = "支付结果的回调地址不能为空")
  @URL(message = "支付结果的回调地址必须为 URL 格式")
  private String orderNotifyUrl;

  @NotNull(message = "退款结果的回调地址不能为空")
  @URL(message = "退款结果的回调地址必须为 URL 格式")
  private String refundNotifyUrl;

  @URL(message = "转账结果的回调地址必须为 URL 格式")
  private String transferNotifyUrl;

}
