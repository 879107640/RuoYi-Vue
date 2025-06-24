package com.ruoyi.web.controller.patent.pay.wallet;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.PageResult;
import com.ruoyi.common.enums.wallet.PayWalletBizTypeEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.pay.convert.wallet.PayWalletConvert;
import com.ruoyi.pay.convert.wallet.PayWalletRechargeConvert;
import com.ruoyi.pay.domain.wallet.PayWalletDO;
import com.ruoyi.pay.domain.wallet.PayWalletRechargeDO;
import com.ruoyi.pay.service.vo.wallet.*;
import com.ruoyi.pay.service.wallet.PayWalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static com.ruoyi.common.enums.UserTypeEnum.MEMBER;


@Tag(name = "管理后台 - 用户钱包")
@RestController
@RequestMapping("/pay/wallet")
@Validated
@Slf4j
public class PayWalletController extends BaseController {

  @Resource
  private PayWalletService payWalletService;

  @GetMapping("/get")
  @Operation(summary = "获得用户钱包明细")
  public AjaxResult getWallet(PayWalletUserReqVO reqVO) {
    PayWalletDO wallet = payWalletService.getOrCreateWallet(reqVO.getUserId(), MEMBER.getValue());
    return success(PayWalletConvert.INSTANCE.convert02(wallet));
  }

  @GetMapping("/page")
  @Operation(summary = "获得会员钱包分页")
  public AjaxResult getWalletPage(@Valid PayWalletPageReqVO pageVO) {
    PageResult<PayWalletDO> pageResult = payWalletService.getWalletPage(pageVO);
    return success(PayWalletConvert.INSTANCE.convertPage(pageResult));
  }

  @PutMapping("/update-balance")
  @Operation(summary = "更新会员用户余额")
  public AjaxResult updateWalletBalance(@Valid @RequestBody PayWalletUpdateBalanceReqVO updateReqVO) {
    // 获得用户钱包
    PayWalletDO wallet = payWalletService.getOrCreateWallet(updateReqVO.getUserId(), MEMBER.getValue());
    if (wallet == null) {
      log.error("[updateWalletBalance]，updateReqVO({}) 用户钱包不存在.", updateReqVO);
      throw new ServiceException("用户钱包不存在");
    }

    // 更新钱包余额
    payWalletService.addWalletBalance(wallet.getId(), String.valueOf(updateReqVO.getUserId()), PayWalletBizTypeEnum.UPDATE_BALANCE, updateReqVO.getBalance());
    return success(true);
  }

  @PostMapping("/create")
  @Operation(summary = "创建钱包充值记录（发起充值）")
  public AjaxResult createWalletRecharge(@Valid @RequestBody PayWalletRechargeCreateReqVO reqVO) {
    PayWalletRechargeDO walletRecharge = payWalletService.createWalletRecharge(SecurityUtils.getLoginUser().getUserId(), 1,
        IpUtils.getIpAddr(), reqVO);
    return success(PayWalletRechargeConvert.INSTANCE.convert(walletRecharge));
  }

}
