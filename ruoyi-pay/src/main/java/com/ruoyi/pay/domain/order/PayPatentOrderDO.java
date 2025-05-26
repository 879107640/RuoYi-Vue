package com.ruoyi.pay.domain.order;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.entity.BaseEntity;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 示例订单
 * <p>
 * 演示业务系统的订单，如何接入 pay 系统的支付与退款
 *
 * @author centre
 */
@TableName("pay_patent_order")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayPatentOrderDO extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 订单编号，自增
     */
    @TableId
    private Long id;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 专利号
     */
    private String patentNo;
    /**
     * 价格，单位：分
     */
    private Integer price;

    // ========== 支付相关字段 ==========

    /**
     * 是否支付
     */
    private Boolean payStatus;
    /**
     * 支付订单编号
     * <p>
     * 对接 pay-module-biz 支付服务的支付订单编号，即 PayOrderDO 的 id 编号
     */
    private Long payOrderId;
    /**
     * 付款时间
     */
    private LocalDateTime payTime;
    /**
     * 支付渠道
     * <p>
     * 对应 PayChannelEnum 枚举
     */
    private String payChannelCode;

    // ========== 退款相关字段 ==========
    /**
     * 支付退款单号
     */
    private Long payRefundId;
    /**
     * 退款金额，单位：分
     */
    private Integer refundPrice;
    /**
     * 退款完成时间
     */
    private LocalDateTime refundTime;

}
