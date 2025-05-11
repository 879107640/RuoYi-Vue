package com.ruoyi.pay.domain.app;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.entity.BaseEntity;
import com.ruoyi.common.enums.CommonStatusEnum;
import lombok.*;

/**
 * 支付应用 DO
 * 一个商户下，可能会有多个支付应用。例如说，京东有京东商城、京东到家等等
 * 不过一般来说，一个商户，只有一个应用哈~
 *
 * 即 PayMerchantDO : PayAppDO = 1 : n
 *
 * @author 芋道源码
 */
@TableName("pay_app")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayAppDO extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 应用编号，数据库自增
     */
    @TableId
    private Long id;
    /**
     * 应用标识
     */
    private String appKey;
    /**
     * 应用名
     */
    private String name;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 支付结果的回调地址
     */
    private String orderNotifyUrl;
    /**
     * 退款结果的回调地址
     */
    private String refundNotifyUrl;

    /**
     * 转账结果的回调地址
     */
    private String transferNotifyUrl;

}
