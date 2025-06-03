package com.ruoyi.pay.service.aftersale.enums;

import com.ruoyi.common.core.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;

import static cn.hutool.core.util.ArrayUtil.firstMatch;

/**
 * 售后状态的枚举
 *
 * <a href="https://www.processon.com/view/link/63731a270e3e742ce7b7c194">状态流转</a>
 *
 * @author centre
 */
@AllArgsConstructor
@Getter
public enum AfterSaleStatusEnum implements ArrayValuable<Integer> {

    /**
     * 【申请售后】
     */
    APPLY(10,"申请中", "会员申请退款"), // 有赞的状态提示：退款申请待商家处理

    /**
     * 卖家已收货，等待平台退款；等待退款【等待退款】
     */
    WAIT_REFUND(40, "等待平台退款", "商家收货"), // 有赞的状态提示：无（有赞无该状态）
    /**
     * 完成退款【退款成功】
     */
    COMPLETE(50, "完成", "商家确认退款"), // 有赞的状态提示：退款成功
    ;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(AfterSaleStatusEnum::getStatus).toArray(Integer[]::new);

    /**
     * 进行中的售后状态
     *
     * 不包括已经结束的状态
     */
    public static final Collection<Integer> APPLYING_STATUSES = Arrays.asList(
            APPLY.getStatus(),
            WAIT_REFUND.getStatus()
    );

    /**
     * 状态
     */
    private final Integer status;
    /**
     * 状态名
     */
    private final String name;
    /**
     * 操作内容
     *
     * 目的：记录售后日志的内容
     */
    private final String content;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static AfterSaleStatusEnum valueOf(Integer status) {
        return firstMatch(value -> value.getStatus().equals(status), values());
    }

}
