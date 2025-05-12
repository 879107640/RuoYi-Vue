package com.ruoyi.web.controller.patent.wallet;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pay.service.dto.notify.dto.PayOrderNotifyReqDTO;
import com.ruoyi.pay.service.dto.notify.dto.PayRefundNotifyReqDTO;
import com.ruoyi.pay.service.wallet.PayWalletRechargeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;

import static com.ruoyi.common.utils.servlet.ServletUtils.getClientIP;

@Tag(name = "管理后台 - 钱包充值")
@RestController
@RequestMapping("/pay/wallet-recharge")
@Validated
@Slf4j
public class PayWalletRechargeController extends BaseController {

  @Resource
  private PayWalletRechargeService walletRechargeService;

  @PostMapping("/update-paid")
  @Operation(summary = "更新钱包充值为已充值") // 由 pay-module 支付服务，进行回调，可见 PayNotifyJob
  @PermitAll // 无需登录， 内部校验实现
  public AjaxResult updateWalletRechargerPaid(@Valid @RequestBody PayOrderNotifyReqDTO notifyReqDTO) {
    walletRechargeService.updateWalletRechargerPaid(Long.valueOf(notifyReqDTO.getMerchantOrderId()),
        notifyReqDTO.getPayOrderId());
    return success(true);
  }

  // TODO @jason：发起退款，要 post 操作哈；
  @GetMapping("/refund")
  @Operation(summary = "发起钱包充值退款")
  @Parameter(name = "id", description = "编号", required = true, example = "1024")
  public AjaxResult refundWalletRecharge(@RequestParam("id") Long id) {
    walletRechargeService.refundWalletRecharge(id, getClientIP());
    return success(true);
  }

  @PostMapping("/update-refunded")
  @Operation(summary = "更新钱包充值为已退款") // 由 pay-module 支付服务，进行回调，可见 PayNotifyJob
  @PermitAll // 无需登录， 内部校验实现
  public AjaxResult updateWalletRechargeRefunded(@RequestBody PayRefundNotifyReqDTO notifyReqDTO) {
    walletRechargeService.updateWalletRechargeRefunded(
        Long.valueOf(notifyReqDTO.getMerchantOrderId()), notifyReqDTO.getPayRefundId());
    return success(true);
  }

}
