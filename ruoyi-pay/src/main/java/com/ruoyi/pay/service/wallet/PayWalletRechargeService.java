package com.ruoyi.pay.service.wallet;


/**
 * 钱包充值 Service 接口
 *
 * @author jason
 */
public interface PayWalletRechargeService {

  /**
   * 更新钱包充值成功
   *
   * @param id         钱包充值记录 id
   * @param payOrderId 支付订单 id
   */
  void updateWalletRechargerPaid(Long id, Long payOrderId);

  /**
   * 发起钱包充值退款
   *
   * @param id     钱包充值编号
   * @param userIp 用户 ip 地址
   */
  void refundWalletRecharge(Long id, String userIp);

  /**
   * 更新钱包充值记录为已退款
   *
   * @param id          钱包充值 id
   * @param payRefundId 退款单id
   */
  void updateWalletRechargeRefunded(Long id, Long payRefundId);

}
