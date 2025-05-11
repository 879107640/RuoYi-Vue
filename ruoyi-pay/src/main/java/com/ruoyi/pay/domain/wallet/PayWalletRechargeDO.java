package com.ruoyi.pay.domain.wallet;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.entity.BaseEntity;
import com.ruoyi.common.enums.refund.PayRefundStatusEnum;
import com.ruoyi.pay.domain.order.PayOrderDO;
import com.ruoyi.pay.domain.refund.PayRefundDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 会员钱包充值
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "pay_wallet_recharge")
@KeySequence("pay_wallet_recharge_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
public class PayWalletRechargeDO extends BaseEntity {
  private static final long serialVersionUID = 1L;

  /**
   * 编号
   */
  @TableId
  private Long id;

  /**
   * 钱包编号
   * <p>
   * 关联 {@link PayWalletDO#getId()}
   */
  private Long walletId;

  /**
   * 用户实际到账余额
   * <p>
   * 例如充 100 送 20，则该值是 120
   */
  private Integer totalPrice;
  /**
   * 实际支付金额
   */
  private Integer payPrice;
  /**
   * 钱包赠送金额
   */
  private Integer bonusPrice;

  /**
   * 充值套餐编号
   * <p>
   * 关联 {@link PayWalletRechargeDO#getPackageId()} 字段
   */
  private Long packageId;

  /**
   * 是否已支付
   * <p>
   * true - 已支付
   * false - 未支付
   */
  private Boolean payStatus;

  /**
   * 支付订单编号
   * <p>
   * 关联 {@link PayOrderDO#getId()}
   */
  private Long payOrderId;

  /**
   * 支付成功的支付渠道
   * <p>
   * 冗余 {@link PayOrderDO#getChannelCode()}
   */
  private String payChannelCode;
  /**
   * 订单支付时间
   */
  private LocalDateTime payTime;

  /**
   * 支付退款单编号
   * <p>
   * 关联 {@link PayRefundDO#getId()}
   */
  private Long payRefundId;

  /**
   * 退款金额，包含赠送金额
   */
  private Integer refundTotalPrice;
  /**
   * 退款支付金额
   */
  private Integer refundPayPrice;

  /**
   * 退款钱包赠送金额
   */
  private Integer refundBonusPrice;

  /**
   * 退款时间
   */
  private LocalDateTime refundTime;

  /**
   * 退款状态
   * <p>
   * 枚举 {@link PayRefundStatusEnum}
   */
  private Integer refundStatus;

}
