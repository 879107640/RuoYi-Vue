package com.ruoyi.pay.service.vo.wallet;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "创建钱包充值 Resp VO")
@Data
public class PayWalletRechargeCreateRespVO {

  @Schema(description = "钱包充值编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
  @JsonSerialize(using = ToStringSerializer.class)
  private Long id;

  @Schema(description = "支付订单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
  @JsonSerialize(using = ToStringSerializer.class)
  private Long payOrderId;

}
