package com.ruoyi.web.controller.patent.pay.refund;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.pay.convert.refund.PayRefundConvert;
import com.ruoyi.pay.domain.app.PayAppDO;
import com.ruoyi.pay.domain.refund.PayRefundDO;
import com.ruoyi.pay.service.app.PayAppService;
import com.ruoyi.pay.service.dto.notify.dto.PayRefundNotifyReqDTO;
import com.ruoyi.pay.service.refund.PayRefundService;
import com.ruoyi.pay.service.refund.vo.AfterSaleCreateReqVO;
import com.ruoyi.pay.service.vo.refund.PayRefundDetailsRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;


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

  @PutMapping("/refund")
  @Operation(summary = "发起订单的退款")
  public AjaxResult refundOrder(@RequestBody AfterSaleCreateReqVO createReqVO) {
    refundService.refundOrder(createReqVO, IpUtils.getIpAddr());
    return success(true);
  }

  @PostMapping("/update-refunded")
  @Operation(summary = "更新订单为已退款") // 由 pay-module 支付服务，进行回调，可见 PayNotifyJob
  @Anonymous // 无需登录，安全由 PayDemoOrderService 内部校验实现
  public AjaxResult updateOrderRefunded(@RequestBody PayRefundNotifyReqDTO notifyReqDTO) {
    refundService.updateOrderRefunded(Long.valueOf(notifyReqDTO.getMerchantOrderId()),
        notifyReqDTO.getPayRefundId());
    return success(true);
  }

}
