package com.ruoyi.pay.service.refund;


import cn.hutool.db.PageResult;
import com.ruoyi.pay.config.core.client.dto.refund.PayRefundRespDTO;
import com.ruoyi.pay.domain.refund.PayRefundDO;
import com.ruoyi.pay.service.dto.refund.PayRefundCreateReqDTO;

import java.util.List;

/**
 * 退款订单 Service 接口
 *
 * @author aquan
 */
public interface PayRefundService {

    /**
     * 获得退款订单
     *
     * @param id 编号
     * @return 退款订单
     */
    PayRefundDO getRefund(Long id);

    /**
     * 获得退款订单
     *
     * @param no 外部退款单号
     * @return 退款订单
     */
    PayRefundDO getRefundByNo(String no);

    /**
     * 获得指定应用的退款数量
     *
     * @param appId 应用编号
     * @return 退款数量
     */
    Long getRefundCountByAppId(Long appId);

    /**
     * 渠道的退款通知
     *
     * @param channelId  渠道编号
     * @param notify     通知
     */
    void notifyRefund(Long channelId, PayRefundRespDTO notify);

    /**
     * 同步渠道退款的退款状态
     *
     * @return 同步到状态的退款数量，包括退款成功、退款失败
     */
    int syncRefund();

    /**
     * 创建退款申请
     *
     * @param reqDTO 退款申请信息
     * @return 退款单号
     */
    Long createPayRefund(PayRefundCreateReqDTO reqDTO);

}
