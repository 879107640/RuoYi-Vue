package com.ruoyi.pay.service.aftersale.vo;

import com.ruoyi.pay.service.aftersale.vo.log.AfterSaleLogRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 售后订单的详情 Response VO")
@Data
public class AfterSaleDetailRespVO extends AfterSaleBaseVO {

    @Schema(description = "售后编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    /**
     * 订单基本信息
     */
    private OrderBaseVO order;

    /**
     * 用户信息
     */
    private MemberUserRespVO user;

    /**
     * 售后日志
     */
    private List<AfterSaleLogRespVO> logs;

}
