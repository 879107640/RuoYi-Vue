package com.ruoyi.pay.service.wallet;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.enums.order.PayOrderStatusEnum;
import com.ruoyi.common.enums.refund.PayRefundStatusEnum;
import com.ruoyi.common.enums.wallet.PayWalletBizTypeEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.pay.config.core.enums.refund.PayRefundStatusRespEnum;
import com.ruoyi.pay.domain.order.PayOrderDO;
import com.ruoyi.pay.domain.refund.PayRefundDO;
import com.ruoyi.pay.domain.wallet.PayWalletDO;
import com.ruoyi.pay.domain.wallet.PayWalletRechargeDO;
import com.ruoyi.pay.framework.pay.config.PayProperties;
import com.ruoyi.pay.mapper.wallet.PayWalletRechargeMapper;
import com.ruoyi.pay.service.dto.refund.PayRefundCreateReqDTO;
import com.ruoyi.pay.service.order.PayOrderService;
import com.ruoyi.pay.service.refund.PayRefundService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static cn.hutool.core.util.ObjectUtil.notEqual;
import static com.ruoyi.pay.config.core.enums.order.PayOrderStatusRespEnum.WAITING;
import static com.ruoyi.pay.util.json.JsonUtils.toJsonString;

/**
 * 钱包充值 Service 实现类
 *
 * @author jason
 */
@Service
@Slf4j
public class PayWalletRechargeServiceImpl implements PayWalletRechargeService {

  @Resource
  private PayWalletRechargeMapper walletRechargeMapper;
  @Resource
  private PayWalletService payWalletService;
  @Resource
  private PayOrderService payOrderService;
  @Resource
  private PayRefundService payRefundService;

  @Resource
  private PayProperties payProperties;


  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateWalletRechargerPaid(Long id, Long payOrderId) {
    // 1.1 校验钱包充值是否存在
    PayWalletRechargeDO recharge = walletRechargeMapper.selectById(id);
    if (recharge == null) {
      log.error("[updateWalletRechargerPaid][recharge({}) payOrder({}) 不存在充值订单，请进行处理！]", id, payOrderId);
      throw new ServiceException("钱包充值记录不存在");
    }
    // 1.2 校验钱包充值是否可以支付
    if (recharge.getPayStatus()) {
      // 特殊：如果订单已支付，且支付单号相同，直接返回，说明重复回调
      if (ObjectUtil.equals(recharge.getPayOrderId(), payOrderId)) {
        log.warn("[updateWalletRechargerPaid][recharge({}) 已支付，且支付单号相同({})，直接返回]", recharge, payOrderId);
        return;
      }
      // 异常：支付单号不同，说明支付单号错误
      log.error("[updateWalletRechargerPaid][recharge({}) 已支付，但是支付单号不同({})，请进行处理！]", recharge, payOrderId);
      throw new ServiceException("钱包充值更新支付状态失败，支付单编号不匹配");
    }

    // 2. 校验支付订单的合法性
    PayOrderDO payOrderDO = validatePayOrderPaid(recharge, payOrderId);

    // 3. 更新钱包充值的支付状态
    PayWalletRechargeDO payWalletRechargeDO = new PayWalletRechargeDO();
    payWalletRechargeDO.setId(id);
    payWalletRechargeDO.setPayStatus(true);
    payWalletRechargeDO.setPayTime(LocalDateTime.now());
    payWalletRechargeDO.setPayChannelCode(payOrderDO.getChannelCode());
    int updateCount = walletRechargeMapper.updateByIdAndPaid(id, false, payWalletRechargeDO);
    if (updateCount == 0) {
      throw new ServiceException("钱包充值更新支付状态失败，钱包充值记录不是【未支付】状态");
    }

    // 4. 更新钱包余额
    // TODO @jason：这样的话，未来提现会不会把充值的，也提现走哈。类似先充 100，送 110；然后提现 110；
    // TODO 需要钱包中加个可提现余额
    payWalletService.addWalletBalance(recharge.getWalletId(), String.valueOf(id),
        PayWalletBizTypeEnum.RECHARGE, recharge.getTotalPrice());

    // 5. 发送订阅消息
//    getSelf().sendWalletRechargerPaidMessage(payOrderId, recharge);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void refundWalletRecharge(Long id, String userIp) {
    // 1.1 获取钱包充值记录
    PayWalletRechargeDO walletRecharge = walletRechargeMapper.selectById(id);
    if (walletRecharge == null) {
      log.error("[refundWalletRecharge][钱包充值记录不存在，钱包充值记录 id({})]", id);
      throw new ServiceException("钱包充值记录不存在");
    }
    // 1.2 校验钱包充值是否可以发起退款
    PayWalletDO wallet = validateWalletRechargeCanRefund(walletRecharge);

    // 2. 冻结退款的余额，暂时只处理赠送的余额也全部退回
    payWalletService.freezePrice(wallet.getId(), walletRecharge.getTotalPrice());

    // 3. 创建退款单
    PayRefundCreateReqDTO payRefundCreateReqDTO = getPayRefundCreateReqDTO(id, userIp, walletRecharge);
    Long payRefundId = payRefundService.createPayRefund(payRefundCreateReqDTO);

    // 4. 更新充值记录退款单号
    PayWalletRechargeDO payWalletRechargeDO = new PayWalletRechargeDO();
    payWalletRechargeDO.setPayRefundId(payRefundId);
    payWalletRechargeDO.setRefundStatus(WAITING.getStatus());
    payWalletRechargeDO.setId(walletRecharge.getId());
    walletRechargeMapper.updateById(payWalletRechargeDO);
  }

  private @NotNull PayRefundCreateReqDTO getPayRefundCreateReqDTO(Long id, String userIp, PayWalletRechargeDO walletRecharge) {
    String walletRechargeId = String.valueOf(id);
    String refundId = walletRechargeId + "-refund";
    PayRefundCreateReqDTO payRefundCreateReqDTO = new PayRefundCreateReqDTO();
    payRefundCreateReqDTO.setAppKey(payProperties.getWalletPayAppKey());
    payRefundCreateReqDTO.setUserIp(userIp);
    payRefundCreateReqDTO.setMerchantOrderId(walletRechargeId);
    payRefundCreateReqDTO.setMerchantRefundId(refundId);
    payRefundCreateReqDTO.setReason("想退钱");
    payRefundCreateReqDTO.setPrice(walletRecharge.getPayPrice());
    return payRefundCreateReqDTO;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateWalletRechargeRefunded(Long id, Long payRefundId) {
    // 1.1 获取钱包充值记录
    PayWalletRechargeDO walletRecharge = walletRechargeMapper.selectById(id);
    if (walletRecharge == null) {
      log.error("[updateWalletRechargerPaid][钱包充值记录不存在，钱包充值记录 id({})]", id);
      throw new ServiceException("钱包充值记录不存在");
    }
    // 1.2 校验钱包充值是否可以更新已退款
    PayRefundDO payRefund = validateWalletRechargeCanRefunded(walletRecharge, payRefundId);

    PayWalletRechargeDO updateObj = new PayWalletRechargeDO();
    updateObj.setId(id);
    // 退款成功
    if (PayRefundStatusEnum.isSuccess(payRefund.getStatus())) {
      // 2.1 更新钱包余额
      payWalletService.reduceWalletBalance(walletRecharge.getWalletId(), id,
          PayWalletBizTypeEnum.RECHARGE_REFUND, walletRecharge.getTotalPrice());

      updateObj.setRefundStatus(PayRefundStatusEnum.SUCCESS.getStatus());
      updateObj.setRefundTime(payRefund.getSuccessTime());
      updateObj.setRefundTotalPrice(walletRecharge.getTotalPrice());
      updateObj.setRefundPayPrice(walletRecharge.getPayPrice());
      updateObj.setRefundBonusPrice(walletRecharge.getBonusPrice());
    }
    // 退款失败
    if (PayRefundStatusRespEnum.isFailure(payRefund.getStatus())) {
      // 2.2 解冻余额
      payWalletService.unfreezePrice(walletRecharge.getWalletId(), walletRecharge.getTotalPrice());

      updateObj.setRefundStatus(PayRefundStatusEnum.FAILURE.getStatus());
    }
    // 3. 更新钱包充值的退款字段
    walletRechargeMapper.updateByIdAndRefunded(id, WAITING.getStatus(), updateObj);
  }

  private PayRefundDO validateWalletRechargeCanRefunded(PayWalletRechargeDO walletRecharge, Long payRefundId) {
    // 1. 校验退款订单匹配
    if (notEqual(walletRecharge.getPayRefundId(), payRefundId)) {
      log.error("[validateWalletRechargeCanRefunded][钱包充值({}) 退款单不匹配({})，请进行处理！钱包充值的数据是：{}]",
          walletRecharge.getId(), payRefundId, toJsonString(walletRecharge));
      throw new ServiceException("钱包退款更新失败，钱包退款单编号不匹配");
    }

    // 2.1 校验退款订单
    PayRefundDO payRefund = payRefundService.getRefund(payRefundId);
    if (payRefund == null) {
      log.error("[validateWalletRechargeCanRefunded][payRefund({})不存在]", payRefundId);
      throw new ServiceException("钱包退款更新失败，退款订单不存在");
    }
    // 2.2 校验退款金额一致
    if (notEqual(payRefund.getRefundPrice(), walletRecharge.getPayPrice())) {
      log.error("[validateWalletRechargeCanRefunded][钱包({}) payRefund({}) 退款金额不匹配，请进行处理！钱包数据是：{}，payRefund 数据是：{}]",
          walletRecharge.getId(), payRefundId, toJsonString(walletRecharge), toJsonString(payRefund));
      throw new ServiceException("钱包退款更新失败，退款单金额不匹配");
    }
    // 2.3 校验退款订单商户订单是否匹配
    if (notEqual(payRefund.getMerchantOrderId(), walletRecharge.getId().toString())) {
      log.error("[validateWalletRechargeCanRefunded][钱包({}) 退款单不匹配({})，请进行处理！payRefund 数据是：{}]",
          walletRecharge.getId(), payRefundId, toJsonString(payRefund));
      throw new ServiceException("钱包退款更新失败，钱包退款单编号不匹配");
    }
    return payRefund;
  }

  private PayWalletDO validateWalletRechargeCanRefund(PayWalletRechargeDO walletRecharge) {
    // 校验充值订单是否支付
    if (!walletRecharge.getPayStatus()) {
      throw new ServiceException("钱包发起退款失败，钱包充值订单未支付");
    }
    // 校验充值订单是否已退款
    if (walletRecharge.getPayRefundId() != null) {
      throw new ServiceException("钱包发起退款失败，钱包充值订单已退款");
    }
    // 校验钱包余额是否足够
    PayWalletDO wallet = payWalletService.getWallet(walletRecharge.getWalletId());
    Assert.notNull(wallet, "用户钱包({}) 不存在", wallet.getId());
    if (wallet.getBalance() < walletRecharge.getTotalPrice()) {
      throw new ServiceException("钱包发起退款失败，钱包余额不足");
    }
    // TODO @芋艿：需要考虑下，赠送的金额，会不会导致提现超过；
    return wallet;
  }

  /**
   * 校验支付订单的合法性
   *
   * @param recharge   充值订单
   * @param payOrderId 支付订单编号
   * @return 支付订单
   */
  private PayOrderDO validatePayOrderPaid(PayWalletRechargeDO recharge, Long payOrderId) {
    // 1. 校验支付单是否存在
    PayOrderDO payOrder = payOrderService.getOrder(payOrderId);
    if (payOrder == null) {
      log.error("[validatePayOrderPaid][充值订单({}) payOrder({}) 不存在，请进行处理！]",
          recharge.getId(), payOrderId);
      throw new ServiceException("支付订单不存在");
    }

    // 2.1 校验支付单已支付
    if (!PayOrderStatusEnum.isSuccess(payOrder.getStatus())) {
      log.error("[validatePayOrderPaid][充值订单({}) payOrder({}) 未支付，请进行处理！payOrder 数据是：{}]",
          recharge.getId(), payOrderId, toJsonString(payOrder));
      throw new ServiceException("钱包充值更新支付状态失败，支付单状态不是【支付成功】状态");
    }
    // 2.2 校验支付金额一致
    if (notEqual(payOrder.getPrice(), recharge.getPayPrice())) {
      log.error("[validatePayOrderPaid][充值订单({}) payOrder({}) 支付金额不匹配，请进行处理！钱包 数据是：{}，payOrder 数据是：{}]",
          recharge.getId(), payOrderId, toJsonString(recharge), toJsonString(payOrder));
      throw new ServiceException("钱包充值更新支付状态失败，支付单金额不匹配");
    }
    // 2.3 校验支付订单的商户订单匹配
    if (notEqual(payOrder.getMerchantOrderId(), recharge.getId().toString())) {
      log.error("[validatePayOrderPaid][充值订单({}) 支付单不匹配({})，请进行处理！payOrder 数据是：{}]",
          recharge.getId(), payOrderId, toJsonString(payOrder));
      throw new ServiceException("钱包充值更新支付状态失败，支付单编号不匹配");
    }
    return payOrder;
  }
}
