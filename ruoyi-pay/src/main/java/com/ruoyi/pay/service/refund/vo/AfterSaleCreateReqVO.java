package com.ruoyi.pay.service.refund.vo;

import com.ruoyi.pay.service.aftersale.enums.AfterSaleWayEnum;
import com.ruoyi.pay.util.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "交易售后创建 Request VO")
@Data
public class AfterSaleCreateReqVO {

  @Schema(description = "订单项编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
  @NotNull(message = "订单项编号不能为空")
  private Long orderId;

  @Schema(description = "申请原因", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
  @NotNull(message = "申请原因不能为空")
  private String applyReason;

  @Schema(description = "补充描述", example = "商品质量不好")
  private String applyDescription;


}
