package com.ruoyi.pay.service.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 订单创建 Request VO")
@Data
public class PayOrderCreateReqVO {

    @Schema(description = "专利号", requiredMode = Schema.RequiredMode.REQUIRED, example = "17682")
    @NotNull(message = "专利号不能为空")
    private String patentNo;

}
