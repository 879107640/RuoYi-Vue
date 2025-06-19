package com.ruoyi.pay.service.refund;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.ruoyi.common.enums.notify.PayNotifyTypeEnum;
import com.ruoyi.common.enums.order.PayOrderStatusEnum;
import com.ruoyi.common.enums.refund.PayRefundStatusEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.pay.config.core.client.PayClient;
import com.ruoyi.pay.config.core.client.dto.refund.PayRefundRespDTO;
import com.ruoyi.pay.config.core.client.dto.refund.PayRefundUnifiedReqDTO;
import com.ruoyi.pay.config.core.enums.refund.PayRefundStatusRespEnum;
import com.ruoyi.pay.convert.refund.PayRefundConvert;
import com.ruoyi.pay.domain.aftersale.AfterSaleDO;
import com.ruoyi.pay.domain.app.PayAppDO;
import com.ruoyi.pay.domain.channel.PayChannelDO;
import com.ruoyi.pay.domain.order.PayOrderDO;
import com.ruoyi.pay.domain.order.PayPatentOrderDO;
import com.ruoyi.pay.domain.refund.PayRefundDO;
import com.ruoyi.pay.framework.pay.config.PayProperties;
import com.ruoyi.pay.mapper.aftersale.AfterSaleMapper;
import com.ruoyi.pay.mapper.order.PayPatentOrderMapper;
import com.ruoyi.pay.mapper.refund.PayRefundMapper;
import com.ruoyi.pay.redis.no.PayNoRedisDAO;
import com.ruoyi.pay.service.aftersale.enums.AfterSaleStatusEnum;
import com.ruoyi.pay.service.aftersale.enums.AfterSaleWayEnum;
import com.ruoyi.pay.service.app.PayAppService;
import com.ruoyi.pay.service.channel.PayChannelService;
import com.ruoyi.pay.service.dto.refund.PayRefundCreateReqDTO;
import com.ruoyi.pay.service.notify.PayNotifyService;
import com.ruoyi.pay.service.order.PayOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static cn.hutool.core.util.ObjectUtil.notEqual;
import static com.ruoyi.common.utils.SecurityUtils.getUserId;
import static com.ruoyi.pay.util.json.JsonUtils.toJsonString;


/**
 * 退款订单 Service 实现类
 *
 * @author jason
 */
@Service
@Slf4j
@Validated
public class PayRefundServiceImpl implements PayRefundService {


  private static final @NotNull(message = "应用标识不能为空") String PAY_APP_KEY = "patent";

  @Resource
  private PayProperties payProperties;

  @Resource
  private PayRefundMapper refundMapper;
  @Resource
  private PayNoRedisDAO noRedisDAO;
  @Resource
  PayPatentOrderMapper patentOrderMapper;
  @Resource
  private PayOrderService orderService;
  @Resource
  private PayAppService appService;
  @Resource
  private PayChannelService channelService;
  @Resource
  private PayNotifyService notifyService;
  @Resource
  AfterSaleMapper afterSaleMapper;

  @Override
  public PayRefundDO getRefund(Long id) {
    return refundMapper.selectById(id);
  }

  @Override
  public PayRefundDO getRefundByNo(String no) {
    return refundMapper.selectByNo(no);
  }

  @Override
  public Long getRefundCountByAppId(Long appId) {
    return refundMapper.selectCountByAppId(appId);
  }

  @Override
  public Long createPayRefund(PayRefundCreateReqDTO reqDTO) {
    // 1.1 校验 App
    PayAppDO app = appService.validPayApp(reqDTO.getAppKey());
    // 1.2 校验支付订单
    PayOrderDO order = validatePayOrderCanRefund(reqDTO, app.getId());
    // 1.3 校验支付渠道是否有效
    PayChannelDO channel = channelService.validPayChannel(order.getChannelId());
    PayClient client = channelService.getPayClient(channel.getId());
    if (client == null) {
      log.error("[refund][渠道编号({}) 找不到对应的支付客户端]", channel.getId());
      throw new ServiceException("支付渠道的配置不存在");
    }
    // 1.4 校验退款订单是否已经存在
    PayRefundDO refund = refundMapper.selectByAppIdAndMerchantRefundId(
        app.getId(), reqDTO.getMerchantRefundId());
    if (refund != null) {
      throw new ServiceException("已经存在退款单");
    }

    // 2.1 插入退款单
    String no = noRedisDAO.generate(payProperties.getRefundNoPrefix());
    refund = PayRefundConvert.INSTANCE.convert(reqDTO);
    refund.setNo(no);
    refund.setAppId(app.getId());
    refund.setOrderId(order.getId());
    refund.setOrderNo(order.getNo());
    refund.setChannelId(order.getChannelId());
    refund.setChannelCode(order.getChannelCode());
    // 商户相关的字段
    refund.setNotifyUrl(app.getRefundNotifyUrl());
    // 渠道相关字段
    refund.setChannelOrderNo(order.getChannelOrderNo());
    // 退款相关字段
    refund.setStatus(PayRefundStatusEnum.WAITING.getStatus());
    refund.setPayPrice(order.getPrice());
    refund.setRefundPrice(reqDTO.getPrice());
    refundMapper.insert(refund);
    try {
      // 2.2 向渠道发起退款申请
      PayRefundUnifiedReqDTO unifiedReqDTO = new PayRefundUnifiedReqDTO();
      unifiedReqDTO.setPayPrice(order.getPrice());
      unifiedReqDTO.setRefundPrice(reqDTO.getPrice());
      unifiedReqDTO.setOutTradeNo(order.getNo());
      unifiedReqDTO.setOutRefundNo(refund.getNo());
      unifiedReqDTO.setNotifyUrl(genChannelRefundNotifyUrl(channel));
      unifiedReqDTO.setReason(reqDTO.getReason());
      PayRefundRespDTO refundRespDTO = client.unifiedRefund(unifiedReqDTO);
      // 2.3 处理退款返回
      getSelf().notifyRefund(channel, refundRespDTO);
    } catch (Throwable e) {
      // 注意：这里仅打印异常，不进行抛出。
      // 原因是：虽然调用支付渠道进行退款发生异常（网络请求超时），实际退款成功。这个结果，后续通过退款回调、或者退款轮询补偿可以拿到。
      // 最终，在异常的情况下，支付中心会异步回调业务的退款回调接口，提供退款结果
      log.error("[createPayRefund][退款 id({}) requestDTO({}) 发生异常]",
          refund.getId(), reqDTO, e);
    }

    // 返回退款编号
    return refund.getId();
  }

  /**
   * 校验支付订单是否可以退款
   *
   * @param reqDTO 退款申请信息
   * @return 支付订单
   */
  private PayOrderDO validatePayOrderCanRefund(PayRefundCreateReqDTO reqDTO, Long appId) {
    PayOrderDO order = orderService.getOrder(appId, reqDTO.getMerchantOrderId());
    if (order == null) {
      throw new ServiceException("支付订单不存在");
    }
    // 校验状态，必须是已支付、或者已退款
    if (!PayOrderStatusEnum.isSuccessOrRefund(order.getStatus())) {
      throw new ServiceException("支付订单退款失败，原因：状态不是已支付或已退款");
    }

    // 校验金额，退款金额不能大于原定的金额
    if (reqDTO.getPrice() + order.getRefundPrice() > order.getPrice()) {
      throw new ServiceException("退款金额超过订单可退款金额");
    }
    // 是否有退款中的订单
    if (refundMapper.selectCountByAppIdAndOrderId(appId, order.getId(),
        PayRefundStatusEnum.WAITING.getStatus()) > 0) {
      throw new ServiceException("已经有退款在处理中");
    }
    return order;
  }

  /**
   * 根据支付渠道的编码，生成支付渠道的回调地址
   *
   * @param channel 支付渠道
   * @return 支付渠道的回调地址  配置地址 + "/" + channel id
   */
  private String genChannelRefundNotifyUrl(PayChannelDO channel) {
    return payProperties.getRefundNotifyUrl() + "/" + channel.getId();
  }

  @Override
  public void notifyRefund(Long channelId, PayRefundRespDTO notify) {
    // 校验支付渠道是否有效
    PayChannelDO channel = channelService.validPayChannel(channelId);
    // 更新退款订单
    getSelf().notifyRefund(channel, notify);
  }

  /**
   * 通知并更新订单的退款结果
   *
   * @param channel 支付渠道
   * @param notify  通知
   */
  // 注意，如果是方法内调用该方法，需要通过 getSelf().notifyRefund(channel, notify) 调用，否则事务不生效
  @Transactional(rollbackFor = Exception.class)
  public void notifyRefund(PayChannelDO channel, PayRefundRespDTO notify) {
    // 情况一：退款成功
    if (PayRefundStatusRespEnum.isSuccess(notify.getStatus())) {
      notifyRefundSuccess(channel, notify);
      return;
    }
    // 情况二：退款失败
    if (PayRefundStatusRespEnum.isFailure(notify.getStatus())) {
      notifyRefundFailure(channel, notify);
    }
  }

  private void notifyRefundSuccess(PayChannelDO channel, PayRefundRespDTO notify) {
    // 1.1 查询 PayRefundDO
    PayRefundDO refund = refundMapper.selectByAppIdAndNo(
        channel.getAppId(), notify.getOutRefundNo());
    if (refund == null) {
      throw new ServiceException("支付退款单不存在");
    }
    if (PayRefundStatusEnum.isSuccess(refund.getStatus())) { // 如果已经是成功，直接返回，不用重复更新
      log.info("[notifyRefundSuccess][退款订单({}) 已经是退款成功，无需更新]", refund.getId());
      return;
    }
    if (!PayRefundStatusEnum.WAITING.getStatus().equals(refund.getStatus())) {
      throw new ServiceException("支付退款单不处于待退款");
    }
    // 1.2 更新 PayRefundDO
    PayRefundDO updateRefundObj = new PayRefundDO();
    updateRefundObj.setSuccessTime(notify.getSuccessTime());
    updateRefundObj.setChannelRefundNo(notify.getChannelRefundNo());
    updateRefundObj.setStatus(PayRefundStatusEnum.SUCCESS.getStatus());
    updateRefundObj.setChannelNotifyData(toJsonString(notify));
    int updateCounts = refundMapper.updateByIdAndStatus(refund.getId(), refund.getStatus(), updateRefundObj);
    if (updateCounts == 0) { // 校验状态，必须是等待状态
      throw new ServiceException("支付退款单不处于待退款");
    }
    log.info("[notifyRefundSuccess][退款订单({}) 更新为退款成功]", refund.getId());

    // 2. 更新订单
    orderService.updateOrderRefundPrice(refund.getOrderId(), refund.getRefundPrice());

    // 3. 插入退款通知记录
    notifyService.createPayNotifyTask(PayNotifyTypeEnum.REFUND.getType(),
        refund.getId());
  }

  private void notifyRefundFailure(PayChannelDO channel, PayRefundRespDTO notify) {
    // 1.1 查询 PayRefundDO
    PayRefundDO refund = refundMapper.selectByAppIdAndNo(
        channel.getAppId(), notify.getOutRefundNo());
    if (refund == null) {
      throw new ServiceException("支付退款单不存在");
    }
    if (PayRefundStatusEnum.isFailure(refund.getStatus())) { // 如果已经是成功，直接返回，不用重复更新
      log.info("[notifyRefundSuccess][退款订单({}) 已经是退款关闭，无需更新]", refund.getId());
      return;
    }
    if (!PayRefundStatusEnum.WAITING.getStatus().equals(refund.getStatus())) {
      throw new ServiceException("支付退款单不处于待退款");
    }
    // 1.2 更新 PayRefundDO
    PayRefundDO updateRefundObj = new PayRefundDO();
    updateRefundObj.setChannelRefundNo(notify.getChannelRefundNo());
    updateRefundObj.setStatus(PayRefundStatusEnum.FAILURE.getStatus());
    updateRefundObj.setChannelNotifyData(toJsonString(notify));
    updateRefundObj.setChannelErrorCode(notify.getChannelErrorCode());
    updateRefundObj.setChannelErrorMsg(notify.getChannelErrorMsg());
    int updateCounts = refundMapper.updateByIdAndStatus(refund.getId(), refund.getStatus(), updateRefundObj);
    if (updateCounts == 0) { // 校验状态，必须是等待状态
      throw new ServiceException("支付退款单不处于待退款");
    }
    log.info("[notifyRefundFailure][退款订单({}) 更新为退款失败]", refund.getId());

    // 2. 插入退款通知记录
    notifyService.createPayNotifyTask(PayNotifyTypeEnum.REFUND.getType(),
        refund.getId());
  }

  @Override
  public int syncRefund() {
    // 1. 查询指定创建时间内的待退款订单
    List<PayRefundDO> refunds = refundMapper.selectListByStatus(PayRefundStatusEnum.WAITING.getStatus());
    if (CollUtil.isEmpty(refunds)) {
      return 0;
    }
    // 2. 遍历执行
    int count = 0;
    for (PayRefundDO refund : refunds) {
      count += syncRefund(refund) ? 1 : 0;
    }
    return count;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void refundOrder(Long orderId, String userIp) {
    // 1. 校验订单是否可以退款
    PayPatentOrderDO order = validateDemoOrderCanRefund(orderId);

    PayRefundCreateReqDTO payRefundCreateReqDTO = getPayRefundCreateReqDTO(order);

    createAfterSale(orderId, order.getPrice());

    Long payRefundId = createPayRefund(payRefundCreateReqDTO);// 价格信息
    // 2 更新退款单到 订单
    PayPatentOrderDO payPatentOrderDO = new PayPatentOrderDO();
    payPatentOrderDO.setId(orderId);
    payPatentOrderDO.setPayRefundId(payRefundId);
    payPatentOrderDO.setRefundPrice(order.getPrice());
    payPatentOrderDO.setRefundTime(LocalDateTime.now());
    patentOrderMapper.updateById(payPatentOrderDO);
  }

  private void createAfterSale(Long orderId, Integer refundPrice) {

    // 创建售后单
    AfterSaleDO afterSale = new AfterSaleDO();
    afterSale.setNo(noRedisDAO.refundGenerate(PayNoRedisDAO.AFTER_SALE_NO_PREFIX));
    afterSale.setStatus(AfterSaleStatusEnum.APPLY.getStatus());
    afterSale.setWay(AfterSaleWayEnum.REFUND.getWay());
    afterSale.setRefundPrice(refundPrice);
    afterSale.setUserId(getUserId());
    afterSale.setOrderId(orderId);
    afterSaleMapper.insert(afterSale);
  }

  private static PayRefundCreateReqDTO getPayRefundCreateReqDTO(PayPatentOrderDO order) {
    String refundId = order.getId() + "-refund";
    // 2.2 创建退款单
    PayRefundCreateReqDTO payRefundCreateReqDTO = new PayRefundCreateReqDTO();
    payRefundCreateReqDTO.setAppKey(PAY_APP_KEY);
    payRefundCreateReqDTO.setUserIp(IpUtils.getIpAddr()); // 支付应用
    payRefundCreateReqDTO.setMerchantOrderId(String.valueOf(order.getId()));// 支付单号
    payRefundCreateReqDTO.setMerchantRefundId(refundId);
    payRefundCreateReqDTO.setReason("想退钱");
    payRefundCreateReqDTO.setPrice(order.getPrice());
    return payRefundCreateReqDTO;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateOrderRefunded(Long id, Long payRefundId) {
    // 1. 校验并获得退款订单（可退款）
    com.ruoyi.pay.service.dto.refund.PayRefundRespDTO payRefund = validateOrderCanRefunded(id, payRefundId);
    // 2 更新退款单到 订单
    PayPatentOrderDO payPatentOrderDO = patentOrderMapper.selectById(id);


    payPatentOrderDO.setRefundTime(payRefund != null && payRefund.getSuccessTime() != null ? payRefund.getSuccessTime() : LocalDateTime.now());
    patentOrderMapper.updateById(payPatentOrderDO);
    // 3. 更新售后服务单
    AfterSaleDO afterSaleDO = new AfterSaleDO();
    afterSaleDO.setOrderId(id);
    afterSaleDO.setStatus(AfterSaleStatusEnum.COMPLETE.getStatus());
    afterSaleMapper.updateByOrderIdAndStatus(id, afterSaleDO);
  }

  private com.ruoyi.pay.service.dto.refund.PayRefundRespDTO validateOrderCanRefunded(Long id, Long payRefundId) {
    // 1.1 校验示例订单
    PayPatentOrderDO order = patentOrderMapper.selectById(id);
    if (order == null) {
      throw new ServiceException("订单不存在");
    }
    // 1.2 校验退款订单匹配
    if (!Objects.equals(order.getPayRefundId(), payRefundId)) {
      log.error("[validateDemoOrderCanRefunded][order({}) 退款单不匹配({})，请进行处理！order 数据是：{}]",
          id, payRefundId, toJsonString(order));
      throw new ServiceException("发起退款失败，退款单编号不匹配");
    }

    // 2.1 校验退款订单
    PayRefundDO payRefund = getRefund(payRefundId);
    if (payRefund == null) {
      throw new ServiceException("发起退款失败，退款订单不存在");
    }
    // 2.2
    if (!PayRefundStatusEnum.isSuccess(payRefund.getStatus())) {
      throw new ServiceException("发起退款失败，退款订单未退款成功");
    }
    // 2.3 校验退款金额一致
    if (notEqual(payRefund.getRefundPrice(), order.getPrice())) {
      log.error("[validateDemoOrderCanRefunded][order({}) payRefund({}) 退款金额不匹配，请进行处理！order 数据是：{}，payRefund 数据是：{}]",
          id, payRefundId, toJsonString(order), toJsonString(payRefund));
      throw new ServiceException("发起退款失败，退款单金额不匹配");
    }
    // 2.4 校验退款订单匹配（二次）
    if (notEqual(payRefund.getMerchantOrderId(), id.toString())) {
      log.error("[validateDemoOrderCanRefunded][order({}) 退款单不匹配({})，请进行处理！payRefund 数据是：{}]",
          id, payRefundId, toJsonString(payRefund));
      throw new ServiceException("发起退款失败，退款单编号不匹配");
    }
    return PayRefundConvert.INSTANCE.convert02(getRefund(id));
  }

  private PayPatentOrderDO validateDemoOrderCanRefund(Long id) {
    // 校验订单是否存在
    PayPatentOrderDO order = patentOrderMapper.selectById(id);
    if (order == null) {
      throw new ServiceException("订单不存在");
    }
    // 校验订单是否支付
    if (!order.getPayStatus()) {
      throw new ServiceException("发起退款失败，订单未支付");
    }
    // 校验订单是否已退款
    if (order.getPayRefundId() != null) {
      throw new ServiceException("发起退款失败，订单已退款");
    }
    return order;
  }

  /**
   * 同步单个退款订单
   *
   * @param refund 退款订单
   * @return 是否同步到
   */
  private boolean syncRefund(PayRefundDO refund) {
    try {
      // 1.1 查询退款订单信息
      PayClient payClient = channelService.getPayClient(refund.getChannelId());
      if (payClient == null) {
        log.error("[syncRefund][渠道编号({}) 找不到对应的支付客户端]", refund.getChannelId());
        return false;
      }
      PayRefundRespDTO respDTO = payClient.getRefund(refund.getOrderNo(), refund.getNo());
      // 1.2 回调退款结果
      notifyRefund(refund.getChannelId(), respDTO);

      // 2. 如果同步到，则返回 true
      return PayRefundStatusEnum.isSuccess(respDTO.getStatus())
          || PayRefundStatusEnum.isFailure(respDTO.getStatus());
    } catch (Throwable e) {
      log.error("[syncRefund][refund({}) 同步退款状态异常]", refund.getId(), e);
      return false;
    }
  }

  /**
   * 获得自身的代理对象，解决 AOP 生效问题
   *
   * @return 自己
   */
  private PayRefundServiceImpl getSelf() {
    return SpringUtil.getBean(getClass());
  }

}
