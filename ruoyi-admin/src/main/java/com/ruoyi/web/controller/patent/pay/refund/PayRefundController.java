package com.ruoyi.web.controller.patent.pay.refund;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pay.convert.refund.PayRefundConvert;
import com.ruoyi.pay.domain.app.PayAppDO;
import com.ruoyi.pay.domain.refund.PayRefundDO;
import com.ruoyi.pay.service.app.PayAppService;
import com.ruoyi.pay.service.refund.PayRefundService;
import com.ruoyi.pay.service.vo.refund.PayRefundDetailsRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@Tag(name = "管理后台 - 退款订单")
@RestController
@RequestMapping("/pay/refund")
@Validated
public class PayRefundController extends BaseController {

  @Resource
  private PayRefundService refundService;
  @Resource
  private PayAppService appService;

  @GetMapping("/get")
  @Operation(summary = "获得退款订单")
  @Parameter(name = "id", description = "编号", required = true, example = "1024")
  @PreAuthorize("@ss.hasPermi('pay:refund:query')")
  public AjaxResult getRefund(@RequestParam("id") Long id) {
    PayRefundDO refund = refundService.getRefund(id);
    if (refund == null) {
      return success(new PayRefundDetailsRespVO());
    }

    // 拼接数据
    PayAppDO app = appService.getApp(refund.getAppId());
    return success(PayRefundConvert.INSTANCE.convert(refund, app));
  }


}
