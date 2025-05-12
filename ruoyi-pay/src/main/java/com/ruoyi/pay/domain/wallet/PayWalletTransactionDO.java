package com.ruoyi.pay.domain.wallet;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.entity.BaseEntity;
import com.ruoyi.common.enums.wallet.PayWalletBizTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会员钱包流水 DO
 *
 * @author jason
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "pay_wallet_transaction")
@KeySequence("pay_wallet_transaction_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
public class PayWalletTransactionDO extends BaseEntity {
  private static final long serialVersionUID = 1L;

  /**
   * 编号
   */
  @TableId
  private Long id;

  /**
   * 流水号
   */
  private String no;

  /**
   * 钱包编号
   * <p>
   * 关联 {@link PayWalletDO#getId()}
   */
  private Long walletId;

  /**
   * 关联业务分类
   * <p>
   * 枚举 {@link PayWalletBizTypeEnum#getType()}
   */
  private Integer bizType;

  /**
   * 关联业务编号
   */
  private String bizId;

  /**
   * 流水说明
   */
  private String title;

  /**
   * 交易金额，单位分
   * <p>
   * 正值表示余额增加，负值表示余额减少
   */
  private Integer price;

  /**
   * 交易后余额，单位分
   */
  private Integer balance;
}
