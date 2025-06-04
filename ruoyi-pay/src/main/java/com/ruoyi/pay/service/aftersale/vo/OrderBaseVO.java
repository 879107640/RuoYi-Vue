package com.ruoyi.pay.service.aftersale.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 交易订单 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class OrderBaseVO {

  // ========== 订单基本信息 ==========

  @Schema(description = "订单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
  private Long id;

  @Schema(description = "订单流水号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1146347329394184195")
  private String no;

  @Schema(description = "下单时间", requiredMode = Schema.RequiredMode.REQUIRED)
  private LocalDateTime createTime;

  @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
  private Long userId;

  @Schema(description = "用户 IP", requiredMode = Schema.RequiredMode.REQUIRED, example = "127.0.0.1")
  private String userIp;

  @Schema(description = "用户备注", requiredMode = Schema.RequiredMode.REQUIRED, example = "你猜")
  private String userRemark;

  @Schema(description = "订单状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
  private Integer status;

  @Schema(description = "订单完成时间")
  private LocalDateTime finishTime;

  @Schema(description = "订单取消时间")
  private LocalDateTime cancelTime;

  @Schema(description = "取消类型", example = "10")
  private Integer cancelType;

  @Schema(description = "商家备注", example = "你猜一下")
  private String remark;

  // ========== 价格 + 支付基本信息 ==========

  @Schema(description = "支付订单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
  private Long payOrderId;

  @Schema(description = "是否已支付", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
  private Boolean payStatus;

  @Schema(description = "付款时间")
  private LocalDateTime payTime;

  @Schema(description = "支付渠道", requiredMode = Schema.RequiredMode.REQUIRED, example = "wx_lite")
  private String payChannelCode;

  @Schema(description = "商品原价（总）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1000")
  private Integer totalPrice;

  @Schema(description = "订单优惠（总）", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
  private Integer discountPrice;

  @Schema(description = "应付金额（总）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1000")
  private Integer payPrice;

  // ========== 售后基本信息 ==========

  @Schema(description = "售后状态", example = "1")
  private Integer afterSaleStatus;

  @Schema(description = "退款金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
  private Integer refundPrice;

}
