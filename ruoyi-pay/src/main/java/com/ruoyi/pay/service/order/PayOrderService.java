package com.ruoyi.pay.service.order;


import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.PageResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.patent.domain.GPatentLibrary;
import com.ruoyi.pay.config.core.client.dto.order.PayOrderRespDTO;
import com.ruoyi.pay.domain.order.PayOrderDO;
import com.ruoyi.pay.domain.order.PayOrderExtensionDO;
import com.ruoyi.pay.service.dto.PayOrderCreateReqDTO;
import com.ruoyi.pay.service.vo.order.*;
import com.ruoyi.system.service.vo.SysUserRespVo;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 支付订单 Service 接口
 *
 * @author aquan
 */
public interface PayOrderService {

  /**
   * 获得支付订单
   *
   * @param id 编号
   * @return 支付订单
   */
  PayOrderDO getOrder(Long id);

  /**
   * 获得支付订单
   *
   * @param appId           应用编号
   * @param merchantOrderId 商户订单编号
   * @return 支付订单
   */
  PayOrderDO getOrder(Long appId, String merchantOrderId);

  /**
   * 获得支付订单列表
   *
   * @param ids 编号数组
   * @return 支付订单列表
   */
  List<PayOrderDO> getOrderList(Collection<Long> ids);

  /**
   * 获得指定应用的订单数量
   *
   * @param appId 应用编号
   * @return 订单数量
   */
  Long getOrderCountByAppId(Long appId);

  /**
   * 获得支付订单分页
   *
   * @param pageReqVO 分页查询
   * @return 支付订单分页
   */
  PageResult<PayOrderDO> getOrderPage(PayOrderPageReqVO pageReqVO);

  /**
   * 获得支付订单列表, 用于 Excel 导出
   *
   * @param exportReqVO 查询条件
   * @return 支付订单列表
   */
  List<PayOrderDO> getOrderList(PayOrderExportReqVO exportReqVO);

  /**
   * 创建支付单
   *
   * @param reqDTO 创建请求
   * @return 支付单编号
   */
  Long createPayOrder(@Valid PayOrderCreateReqDTO reqDTO);

  /**
   * 创建示例订单
   *
   * @param userId      用户编号
   * @param createReqVO 创建信息
   * @return 编号
   */
  String createOrder(Long userId, @Valid PayOrderCreateReqVO createReqVO);

  /**
   * 提交支付
   * 此时，会发起支付渠道的调用
   *
   * @param reqVO  提交请求
   * @param userIp 提交 IP
   * @return 提交结果
   */
  PayOrderSubmitRespVO submitOrder(@Valid PayOrderSubmitReqVO reqVO,
                                   @NotEmpty(message = "提交 IP 不能为空") String userIp);

  /**
   * 通知支付单成功
   *
   * @param channelId 渠道编号
   * @param notify    通知
   */
  void notifyOrder(Long channelId, PayOrderRespDTO notify);

  /**
   * 更新支付订单的退款金额
   *
   * @param id              编号
   * @param incrRefundPrice 增加的退款金额
   */
  void updateOrderRefundPrice(Long id, Integer incrRefundPrice);

  /**
   * 更新支付订单价格
   *
   * @param id       支付单编号
   * @param payPrice 支付单价格
   */
  void updatePayOrderPrice(Long id, Integer payPrice);

  /**
   * 获得支付订单
   *
   * @param id 编号
   * @return 支付订单
   */
  PayOrderExtensionDO getOrderExtension(Long id);

  /**
   * 获得支付订单
   *
   * @param no 支付订单 no
   * @return 支付订单
   */
  PayOrderExtensionDO getOrderExtensionByNo(String no);

  /**
   * 同步订单的支付状态
   *
   * @param minCreateTime 最小创建时间
   * @return 同步到已支付的订单数量
   */
  int syncOrder(LocalDateTime minCreateTime);

  /**
   * 同步订单的支付状态
   * <p>
   * 1. Quietly 表示，即使同步失败，也不会抛出异常
   * 2. 什么时候回出现异常？因为是主动同步，可能和支付渠道的异步回调存在并发冲突，导致抛出异常
   *
   * @param id 订单编号
   */
  void syncOrderQuietly(Long id);

  /**
   * 将已过期的订单，状态修改为已关闭
   *
   * @return 过期的订单数量
   */
  int expireOrder();

  /**
   * 更新订单为已支付
   *
   * @param id 编号
   * @param payOrderId 支付订单号
   */
  void updateOrderPaid(Long id, Long payOrderId);

  SysUserRespVo getPatentInfo(String id) ;

}
