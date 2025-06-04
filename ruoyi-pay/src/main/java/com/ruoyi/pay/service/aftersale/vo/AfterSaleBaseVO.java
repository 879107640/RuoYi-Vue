package com.ruoyi.pay.service.aftersale.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

import static com.ruoyi.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;


/**
 * 交易售后 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class AfterSaleBaseVO {

  @Schema(description = "售后流水号", requiredMode = Schema.RequiredMode.REQUIRED, example = "202211190847450020500077")
  @NotNull(message = "售后流水号不能为空")
  private String no;

  @Schema(description = "售后状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
  @NotNull(message = "售后状态不能为空")
  private Integer status;

  @Schema(description = "售后类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
  @NotNull(message = "售后类型不能为空")
  private Integer type;

  @Schema(description = "售后方式", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
  @NotNull(message = "售后方式不能为空")
  private Integer way;

  @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "30337")
  @NotNull(message = "用户编号不能为空")
  private Long userId;

  @Schema(description = "申请原因", requiredMode = Schema.RequiredMode.REQUIRED, example = "不喜欢")
  @NotNull(message = "申请原因不能为空")
  private String applyReason;

  @Schema(description = "补充描述", example = "你说的对")
  private String applyDescription;

  @Schema(description = "订单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "18078")
  @NotNull(message = "订单编号不能为空")
  private Long orderId;

  @Schema(description = "订单流水号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2022111917190001")
  @NotNull(message = "订单流水号不能为空")
  private String orderNo;

  @Schema(description = "订单项编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "572")
  @NotNull(message = "订单项编号不能为空")
  private Long orderItemId;


  @Schema(description = "退款金额，单位：分", requiredMode = Schema.RequiredMode.REQUIRED, example = "18077")
  @NotNull(message = "退款金额，单位：分不能为空")
  private Integer refundPrice;

  @Schema(description = "支付退款编号", example = "10271")
  private Long payRefundId;

  @Schema(description = "退款时间")
  @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
  private LocalDateTime refundTime;


}
