package com.ruoyi.pay.domain.wallet;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.entity.BaseEntity;
import com.ruoyi.common.enums.UserTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会员钱包 DO
 *
 * @author jason
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "pay_wallet")
@KeySequence("pay_wallet_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
public class PayWalletDO extends BaseEntity {
  private static final long serialVersionUID = 1L;

  /**
   * 编号
   */
  @TableId
  private Long id;

  /**
   * 用户 id
   * <p>
   * 关联 MemberUserDO 的 id 编号
   * 关联 AdminUserDO 的 id 编号
   */
  private Long userId;
  /**
   * 用户类型, 预留 多商户转帐可能需要用到
   * <p>
   * 关联 {@link UserTypeEnum}
   */
  private Integer userType;

  /**
   * 余额，单位分
   */
  private Integer balance;

  /**
   * 冻结金额，单位分
   */
  private Integer freezePrice;

  /**
   * 累计支出，单位分
   */
  private Integer totalExpense;
  /**
   * 累计充值，单位分
   */
  private Integer totalRecharge;

}
