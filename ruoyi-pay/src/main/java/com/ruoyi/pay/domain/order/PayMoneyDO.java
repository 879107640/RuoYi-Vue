package com.ruoyi.pay.domain.order;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;

@TableName("pay_Money")
@KeySequence("pay_order_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayMoneyDO implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 主键id
   */
  private Long id;
  /**
   * 查看金额
   */
  private Long money;
}
