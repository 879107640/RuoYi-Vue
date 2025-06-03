package com.ruoyi.pay.domain.aftersale;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.ruoyi.common.core.domain.entity.BaseEntity;
import com.ruoyi.pay.service.aftersale.enums.AfterSaleStatusEnum;
import com.ruoyi.pay.service.aftersale.enums.AfterSaleTypeEnum;
import com.ruoyi.pay.service.aftersale.enums.AfterSaleWayEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author centre
 */
@TableName(value = "patent_after_sale", autoResultMap = true)
@KeySequence("patent_after_sale_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AfterSaleDO extends BaseEntity {
  private static final long serialVersionUID = 1L;

  /**
   * 售后编号，主键自增
   */
  @TableField
  private Long id;

  /**
   * 售后编码
   */
  private String no;
  /**
   * 退款状态
   * <p>
   * 枚举 {@link AfterSaleStatusEnum}
   */
  private Integer status;
  /**
   * 售后方式
   * <p>
   * 枚举 {@link AfterSaleWayEnum}
   */
  private Integer way;
  /**
   * 用户编号
   * <p>
   * 关联 MemberUserDO 的 id 编号
   */
  private Long userId;
  /**
   * 申请原因
   * <p>
   * type = 退款，对应 trade_after_sale_refund_reason 类型
   * type = 退货退款，对应 trade_after_sale_refund_and_return_reason 类型
   */
  private String applyReason;
  /**
   * 补充描述
   */
  private String applyDescription;

  // ========== 交易订单相关 ==========
  /**
   * 交易订单编号
   * <p>
   * 关联 {@link com.ruoyi.pay.domain.order.PayPatentOrderDO#getId()}
   */
  private Long orderId;

  // ========== 退款相关 ==========
  /**
   * 退款金额，单位：分。
   */
  private Integer refundPrice;
  /**
   * 支付退款编号
   * <p>
   * 对接 pay-module-biz 支付服务的退款订单编号，即 PayRefundDO 的 id 编号
   */
  private Long payRefundId;
  /**
   * 退款时间
   */
  private LocalDateTime refundTime;
}
