package com.ruoyi.pay.service.order;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.google.common.annotations.VisibleForTesting;
import com.ruoyi.common.core.page.PageResult;
import com.ruoyi.common.enums.notify.PayNotifyTypeEnum;
import com.ruoyi.common.enums.order.PayOrderStatusEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.date.LocalDateTimeUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.framework.mybatis.query.LambdaQueryWrapperX;
import com.ruoyi.patent.domain.GPatentLibrary;
import com.ruoyi.patent.mapper.GPatentLibraryMapper;
import com.ruoyi.pay.config.core.client.PayClient;
import com.ruoyi.pay.config.core.client.dto.order.PayOrderRespDTO;
import com.ruoyi.pay.config.core.client.dto.order.PayOrderUnifiedReqDTO;
import com.ruoyi.pay.config.core.enums.order.PayOrderStatusRespEnum;
import com.ruoyi.pay.convert.order.PayOrderConvert;
import com.ruoyi.pay.domain.app.PayAppDO;
import com.ruoyi.pay.domain.channel.PayChannelDO;
import com.ruoyi.pay.domain.order.PayOrderDO;
import com.ruoyi.pay.domain.order.PayOrderExtensionDO;
import com.ruoyi.pay.domain.order.PayPatentOrderDO;
import com.ruoyi.pay.framework.pay.config.PayProperties;
import com.ruoyi.pay.mapper.order.PayOrderExtensionMapper;
import com.ruoyi.pay.mapper.order.PayOrderMapper;
import com.ruoyi.pay.mapper.order.PayPatentOrderMapper;
import com.ruoyi.pay.redis.no.PayNoRedisDAO;
import com.ruoyi.pay.service.app.PayAppService;
import com.ruoyi.pay.service.channel.PayChannelService;
import com.ruoyi.pay.service.dto.PayOrderCreateReqDTO;
import com.ruoyi.pay.service.notify.PayNotifyService;
import com.ruoyi.pay.service.vo.order.*;
import com.ruoyi.pay.util.number.MoneyUtils;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.mapper.SysConfigMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.vo.SysUserRespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static cn.hutool.core.util.ObjectUtil.notEqual;
import static com.ruoyi.common.utils.date.LocalDateTimeUtils.addTime;
import static com.ruoyi.pay.util.json.JsonUtils.toJsonString;

/**
 * 支付订单 Service 实现类
 *
 * @author aquan
 */
@Service
@Validated
@Slf4j
public class PayOrderServiceImpl implements PayOrderService {

  @Resource
  private PayProperties payProperties;

  @Resource
  private PayOrderMapper orderMapper;
  @Resource
  private PayOrderExtensionMapper orderExtensionMapper;
  @Resource
  private PayNoRedisDAO noRedisDAO;

  @Resource
  private PayAppService appService;
  @Resource
  private PayChannelService channelService;
  @Resource
  private PayNotifyService notifyService;
  @Resource
  PayPatentOrderMapper patentOrderMapper;
  @Resource
  GPatentLibraryMapper patentLibraryMapper;
  @Resource
  SysUserMapper userMapper;
  @Resource
  SysConfigMapper configMapper;


  @Override
  public PayOrderDO getOrder(Long id) {
    return orderMapper.selectById(id);
  }

  @Override
  public PayOrderDO getOrder(Long appId, String merchantOrderId) {
    return orderMapper.selectByAppIdAndMerchantOrderId(appId, merchantOrderId);
  }

  @Override
  public List<PayOrderDO> getOrderList(Collection<Long> ids) {
    if (CollUtil.isEmpty(ids)) {
      return Collections.emptyList();
    }
    return orderMapper.selectBatchIds(ids);
  }

  @Override
  public Long getOrderCountByAppId(Long appId) {
    return orderMapper.selectCountByAppId(appId);
  }

  @Override
  public PageResult<PayOrderDO> getOrderPage(PayOrderPageReqVO pageReqVO) {
    return orderMapper.selectPage(pageReqVO);
  }


  @Override
  public List<PayOrderDO> getOrderList(PayOrderExportReqVO exportReqVO) {
    return orderMapper.selectList(exportReqVO);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Long createPayOrder(PayOrderCreateReqDTO reqDTO) {
    // 校验 App
    PayAppDO app = appService.validPayApp(reqDTO.getAppKey());

    // 查询对应的支付交易单是否已经存在。如果是，则直接返回
    PayOrderDO order = orderMapper.selectByAppIdAndMerchantOrderId(
        app.getId(), reqDTO.getMerchantOrderId());
    if (order != null) {
      log.warn("[createOrder][appId({}) merchantOrderId({}) 已经存在对应的支付单({})]", order.getAppId(),
          order.getMerchantOrderId(), toJsonString(order)); // 理论来说，不会出现这个情况
      return order.getId();
    }

    // 创建支付交易单
    order = PayOrderConvert.INSTANCE.convert(reqDTO);
    order.setAppId(app.getId());
    // 商户相关字段
    order.setNotifyUrl(app.getOrderNotifyUrl());
    // 订单相关字段
    order.setStatus(PayOrderStatusEnum.WAITING.getStatus());
    // 退款相关字段
    order.setRefundPrice(0);
    orderMapper.insert(order);
    return order.getId();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public String createOrder(Long userId, PayOrderCreateReqVO createReqVO) {

    GPatentLibrary gPatentLibrary = patentLibraryMapper.selectGPatentLibraryByNo(createReqVO.getPatentNo());

    if (Objects.isNull(gPatentLibrary)) {
      throw new ServiceException("专利信息不存在");
    }

    if (!gPatentLibrary.getBookerKey().equals(userId)) {
      throw new ServiceException("该专利预定人为他人，暂不可查看信息");
    }

    // 1.2 插入 demo 订单
    PayPatentOrderDO patentOrderDO = new PayPatentOrderDO();
    patentOrderDO.setUserId(userId);
    patentOrderDO.setPatentNo(createReqVO.getPatentNo());
    SysConfig sysConfig = new SysConfig();
    sysConfig.setConfigKey("getPatenInfoPrice");
    SysConfig config = configMapper.selectConfig(sysConfig);
    patentOrderDO.setPrice(Integer.valueOf(config.getConfigValue()));
    patentOrderDO.setPayStatus(false);
    patentOrderDO.setRefundPrice(0);
    patentOrderDO.setCreateBy(SecurityUtils.getUserId().toString());
    patentOrderMapper.insert(patentOrderDO);

    // 2.1 创建支付单
    PayOrderCreateReqDTO payOrderCreateReqDTO = new PayOrderCreateReqDTO();
    payOrderCreateReqDTO.setAppKey("patent");
    payOrderCreateReqDTO.setUserIp(IpUtils.getHostIp());// 支付应用
    payOrderCreateReqDTO.setMerchantOrderId(patentOrderDO.getId().toString());// 业务的订单编号
    payOrderCreateReqDTO.setSubject("");
    payOrderCreateReqDTO.setBody(createReqVO.getPatentNo());
    payOrderCreateReqDTO.setPrice(patentOrderDO.getPrice()); // 价格信息
    payOrderCreateReqDTO.setExpireTime(addTime(Duration.ofHours(2L)));
    Long payOrderId = createPayOrder(payOrderCreateReqDTO); // 支付的过期时间
    // 2.2 更新支付单到 demo 订单
    patentOrderDO.setPayOrderId(payOrderId);
    patentOrderMapper.updateById(patentOrderDO);
    // 返回
    return payOrderId.toString();
  }

  @Override // 注意，这里不能添加事务注解，避免调用支付渠道失败时，将 PayOrderExtensionDO 回滚了
  public PayOrderSubmitRespVO submitOrder(PayOrderSubmitReqVO reqVO, String userIp) {
    // 1.1 获得 PayOrderDO ，并校验其是否存在
    PayOrderDO order = validateOrderCanSubmit(reqVO.getId());
    // 1.32 校验支付渠道是否有效
    PayChannelDO channel = validateChannelCanSubmit(order.getAppId(), reqVO.getChannelCode());
    PayClient client = channelService.getPayClient(channel.getId());

    // 2. 插入 PayOrderExtensionDO
    String no = noRedisDAO.generate(payProperties.getOrderNoPrefix());
    PayOrderExtensionDO orderExtension = PayOrderConvert.INSTANCE.convert(reqVO, userIp);
    orderExtension.setOrderId(order.getId());
    orderExtension.setNo(no);
    orderExtension.setChannelId(channel.getId());
    orderExtension.setChannelCode(channel.getCode());
    orderExtension.setStatus(PayOrderStatusEnum.WAITING.getStatus());
    orderExtensionMapper.insert(orderExtension);

    // 3. 调用三方接口
    PayOrderUnifiedReqDTO unifiedOrderReqDTO = PayOrderConvert.INSTANCE.convert2(reqVO, userIp);
    // 商户相关的字段
    unifiedOrderReqDTO.setOutTradeNo(orderExtension.getNo()); // 注意，此处使用的是 PayOrderExtensionDO.no 属性！
    unifiedOrderReqDTO.setSubject("查看专利信息：" + SecurityUtils.getUsername());
    unifiedOrderReqDTO.setBody(order.getBody());
    unifiedOrderReqDTO.setNotifyUrl(genChannelOrderNotifyUrl(channel));
    unifiedOrderReqDTO.setReturnUrl(reqVO.getReturnUrl());
    // 订单相关字段
    unifiedOrderReqDTO.setPrice(order.getPrice());
    unifiedOrderReqDTO.setExpireTime(order.getExpireTime());
    PayOrderRespDTO unifiedOrderResp = client.unifiedOrder(unifiedOrderReqDTO);

    // 4. 如果调用直接支付成功，则直接更新支付单状态为成功。例如说：付款码支付，免密支付时，就直接验证支付成功
    if (unifiedOrderResp != null) {
      try {
        getSelf().notifyOrder(channel, unifiedOrderResp);
      } catch (Exception e) {
        // 兼容 https://gitee.com/zhijiantianya/george-cloud/issues/I8SM9H 场景
        // 支付宝或微信扫码之后时，由于 PayClient 是直接返回支付成功，而支付也会有回调，导致存在并发更新问题，此时一般是可以 try catch 直接忽略
        log.warn("[submitOrder][order({}) channel({}) 支付结果({}) 通知时发生异常，可能是并发问题]",
            order, channel, unifiedOrderResp, e);
      }
      // 如有渠道错误码，则抛出业务异常，提示用户
      if (StrUtil.isNotEmpty(unifiedOrderResp.getChannelErrorCode())) {
        throw new ServiceException(StringUtils.format("发起支付报错，错误码：{}，错误提示：{}", unifiedOrderResp.getChannelErrorCode(),
            unifiedOrderResp.getChannelErrorMsg()));
      }
      // 此处需要读取最新的状态
      order = orderMapper.selectById(order.getId());
    }
    return PayOrderConvert.INSTANCE.convert(order, unifiedOrderResp);
  }

  private PayOrderDO validateOrderCanSubmit(Long id) {
    PayOrderDO order = orderMapper.selectById(id);
    if (order == null) { // 是否存在
      throw new ServiceException("支付订单不存在");
    }
    if (PayOrderStatusEnum.isSuccess(order.getStatus())) { // 校验状态，发现已支付
      throw new ServiceException("订单已支付，请刷新页面");
    }
    if (!PayOrderStatusEnum.WAITING.getStatus().equals(order.getStatus())) { // 校验状态，必须是待支付
      throw new ServiceException("支付订单不处于待支付");
    }
    if (LocalDateTimeUtils.beforeNow(order.getExpireTime())) { // 校验是否过期
      throw new ServiceException("支付订单已经过期");
    }

    // 【重要】校验是否支付拓展单已支付，只是没有回调、或者数据不正常
    validateOrderActuallyPaid(id);
    return order;
  }

  /**
   * 校验支付订单实际已支付
   *
   * @param id 支付编号
   */
  @VisibleForTesting
  void validateOrderActuallyPaid(Long id) {
    List<PayOrderExtensionDO> orderExtensions = orderExtensionMapper.selectListByOrderId(id);
    orderExtensions.forEach(orderExtension -> {
      // 情况一：校验数据库中的 orderExtension 是不是已支付
      if (PayOrderStatusEnum.isSuccess(orderExtension.getStatus())) {
        log.warn("[validateOrderCanSubmit][order({}) 的 extension({}) 已支付，可能是数据不一致]",
            id, orderExtension.getId());
        throw new ServiceException("订单已支付，请等待支付结果");
      }
      // 情况二：调用三方接口，查询支付单状态，是不是已支付
      PayClient payClient = channelService.getPayClient(orderExtension.getChannelId());
      if (payClient == null) {
        log.error("[validateOrderCanSubmit][渠道编号({}) 找不到对应的支付客户端]", orderExtension.getChannelId());
        return;
      }
      PayOrderRespDTO respDTO = payClient.getOrder(orderExtension.getNo());
      if (respDTO != null && PayOrderStatusRespEnum.isSuccess(respDTO.getStatus())) {
        log.warn("[validateOrderCanSubmit][order({}) 的 PayOrderRespDTO({}) 已支付，可能是回调延迟]",
            id, toJsonString(respDTO));
        throw new ServiceException("订单已支付，请等待支付结果");
      }
    });
  }

  private PayChannelDO validateChannelCanSubmit(Long appId, String channelCode) {
    // 校验 App
    appService.validPayApp(appId);
    // 校验支付渠道是否有效
    PayChannelDO channel = channelService.validPayChannel(appId, channelCode);
    PayClient client = channelService.getPayClient(channel.getId());
    if (client == null) {
      log.error("[validatePayChannelCanSubmit][渠道编号({}) 找不到对应的支付客户端]", channel.getId());
      throw new ServiceException("支付渠道的配置不存在");
    }
    return channel;
  }

  /**
   * 根据支付渠道的编码，生成支付渠道的回调地址
   *
   * @param channel 支付渠道
   * @return 支付渠道的回调地址  配置地址 + "/" + channel id
   */
  private String genChannelOrderNotifyUrl(PayChannelDO channel) {
    return payProperties.getOrderNotifyUrl() + "/" + channel.getId();
  }

  @Override
  public void notifyOrder(Long channelId, PayOrderRespDTO notify) {
    // 校验支付渠道是否有效
    PayChannelDO channel = channelService.validPayChannel(channelId);
    // 更新支付订单为已支付
    getSelf().notifyOrder(channel, notify);
  }

  /**
   * 通知并更新订单的支付结果
   *
   * @param channel 支付渠道
   * @param notify  通知
   */
  @Transactional(rollbackFor = Exception.class)
  // 注意，如果是方法内调用该方法，需要通过 getSelf().notifyPayOrder(channel, notify) 调用，否则事务不生效
  public void notifyOrder(PayChannelDO channel, PayOrderRespDTO notify) {
    // 情况一：支付成功的回调
    if (PayOrderStatusRespEnum.isSuccess(notify.getStatus())) {
      notifyOrderSuccess(channel, notify);
      return;
    }
    // 情况二：支付失败的回调
    if (PayOrderStatusRespEnum.isClosed(notify.getStatus())) {
      notifyOrderClosed(notify);
    }
    // 情况三：WAITING：无需处理
    // 情况四：REFUND：通过退款回调处理
  }

  private void notifyOrderSuccess(PayChannelDO channel, PayOrderRespDTO notify) {
    // 1. 更新 PayOrderExtensionDO 支付成功
    PayOrderExtensionDO orderExtension = updateOrderSuccess(notify);
    // 2. 更新 PayOrderDO 支付成功
    Boolean paid = updateOrderSuccess(channel, orderExtension, notify);
    if (paid) { // 如果之前已经成功回调，则直接返回，不用重复记录支付通知记录；例如说：支付平台重复回调
      return;
    }

    // 3. 插入支付通知记录
    notifyService.createPayNotifyTask(PayNotifyTypeEnum.ORDER.getType(),
        orderExtension.getOrderId());
  }

  /**
   * 更新 PayOrderExtensionDO 支付成功
   *
   * @param notify 通知
   * @return PayOrderExtensionDO 对象
   */
  private PayOrderExtensionDO updateOrderSuccess(PayOrderRespDTO notify) {
    // 1. 查询 PayOrderExtensionDO
    PayOrderExtensionDO orderExtension = orderExtensionMapper.selectByNo(notify.getOutTradeNo());
    if (orderExtension == null) {
      throw new ServiceException("支付交易拓展单不存在");
    }
    if (PayOrderStatusEnum.isSuccess(orderExtension.getStatus())) { // 如果已经是成功，直接返回，不用重复更新
      log.info("[updateOrderExtensionSuccess][orderExtension({}) 已经是已支付，无需更新]", orderExtension.getId());
      return orderExtension;
    }
    if (ObjectUtil.notEqual(orderExtension.getStatus(), PayOrderStatusEnum.WAITING.getStatus())) { // 校验状态，必须是待支付
      throw new ServiceException("支付交易拓展单不处于待支付");
    }

    // 2. 更新 PayOrderExtensionDO
    int updateCounts = orderExtensionMapper.updateByIdAndStatus(orderExtension.getId(), orderExtension.getStatus(),
        PayOrderExtensionDO.builder().status(PayOrderStatusEnum.SUCCESS.getStatus()).channelNotifyData(toJsonString(notify)).build());
    if (updateCounts == 0) { // 校验状态，必须是待支付
      throw new ServiceException("支付交易拓展单不处于待支付");
    }
    log.info("[updateOrderExtensionSuccess][orderExtension({}) 更新为已支付]", orderExtension.getId());
    return orderExtension;
  }

  /**
   * 更新 PayOrderDO 支付成功
   *
   * @param channel        支付渠道
   * @param orderExtension 支付拓展单
   * @param notify         通知回调
   * @return 是否之前已经成功回调
   */
  private Boolean updateOrderSuccess(PayChannelDO channel, PayOrderExtensionDO orderExtension,
                                     PayOrderRespDTO notify) {
    // 1. 判断 PayOrderDO 是否处于待支付
    PayOrderDO order = orderMapper.selectById(orderExtension.getOrderId());
    if (order == null) {
      throw new ServiceException("支付订单不存在");
    }
    if (PayOrderStatusEnum.isSuccess(order.getStatus()) // 如果已经是成功，直接返回，不用重复更新
        && Objects.equals(order.getExtensionId(), orderExtension.getId())) {
      log.info("[updateOrderExtensionSuccess][order({}) 已经是已支付，无需更新]", order.getId());
      return true;
    }
    if (!PayOrderStatusEnum.WAITING.getStatus().equals(order.getStatus())) { // 校验状态，必须是待支付
      throw new ServiceException("支付订单不处于待支付");
    }

    // 2. 更新 PayOrderDO
    int updateCounts = orderMapper.updateByIdAndStatus(order.getId(), PayOrderStatusEnum.WAITING.getStatus(),
        PayOrderDO.builder().status(PayOrderStatusEnum.SUCCESS.getStatus())
            .channelId(channel.getId()).channelCode(channel.getCode())
            .successTime(notify.getSuccessTime()).extensionId(orderExtension.getId()).no(orderExtension.getNo())
            .channelOrderNo(notify.getChannelOrderNo()).channelUserId(notify.getChannelUserId())
            .channelFeeRate(channel.getFeeRate())
            .channelFeePrice(MoneyUtils.calculateRatePrice(order.getPrice(), channel.getFeeRate()))
            .build());
    if (updateCounts == 0) { // 校验状态，必须是待支付
      throw new ServiceException("支付订单不处于待支付");
    }
    log.info("[updateOrderExtensionSuccess][order({}) 更新为已支付]", order.getId());
    return false;
  }

  private void notifyOrderClosed(PayOrderRespDTO notify) {
    updateOrderExtensionClosed(notify);
  }

  private void updateOrderExtensionClosed(PayOrderRespDTO notify) {
    // 1. 查询 PayOrderExtensionDO
    PayOrderExtensionDO orderExtension = orderExtensionMapper.selectByNo(notify.getOutTradeNo());
    if (orderExtension == null) {
      throw new ServiceException("支付交易拓展单不存在");
    }
    if (PayOrderStatusEnum.isClosed(orderExtension.getStatus())) { // 如果已经是关闭，直接返回，不用重复更新
      log.info("[updateOrderExtensionClosed][orderExtension({}) 已经是支付关闭，无需更新]", orderExtension.getId());
      return;
    }
    // 一般出现先是支付成功，然后支付关闭，都是全部退款导致关闭的场景。这个情况，我们不更新支付拓展单，只通过退款流程，更新支付单
    if (PayOrderStatusEnum.isSuccess(orderExtension.getStatus())) {
      log.info("[updateOrderExtensionClosed][orderExtension({}) 是已支付，无需更新为支付关闭]", orderExtension.getId());
      return;
    }
    if (ObjectUtil.notEqual(orderExtension.getStatus(), PayOrderStatusEnum.WAITING.getStatus())) { // 校验状态，必须是待支付
      throw new ServiceException("支付交易拓展单不处于待支付");
    }

    // 2. 更新 PayOrderExtensionDO
    int updateCounts = orderExtensionMapper.updateByIdAndStatus(orderExtension.getId(), orderExtension.getStatus(),
        PayOrderExtensionDO.builder().status(PayOrderStatusEnum.CLOSED.getStatus()).channelNotifyData(toJsonString(notify))
            .channelErrorCode(notify.getChannelErrorCode()).channelErrorMsg(notify.getChannelErrorMsg()).build());
    if (updateCounts == 0) { // 校验状态，必须是待支付
      throw new ServiceException("支付交易拓展单不处于待支付");
    }
    log.info("[updateOrderExtensionClosed][orderExtension({}) 更新为支付关闭]", orderExtension.getId());
  }

  @Override
  public void updateOrderRefundPrice(Long id, Integer incrRefundPrice) {
    PayOrderDO order = orderMapper.selectById(id);
    if (order == null) {
      throw new ServiceException("支付订单不存在");
    }
    if (!PayOrderStatusEnum.isSuccessOrRefund(order.getStatus())) {
      throw new ServiceException("支付订单退款失败，原因：状态不是已支付或已退款");
    }
    if (order.getRefundPrice() + incrRefundPrice > order.getPrice()) {
      throw new ServiceException("退款金额超过订单可退款金额");
    }

    // 更新订单
    PayOrderDO updateObj = new PayOrderDO();
    updateObj.setRefundPrice(order.getRefundPrice() + incrRefundPrice);
    updateObj.setStatus(PayOrderStatusEnum.REFUND.getStatus());
    int updateCount = orderMapper.updateByIdAndStatus(id, order.getStatus(), updateObj);
    if (updateCount == 0) {
      throw new ServiceException("支付订单退款失败，原因：状态不是已支付或已退款");
    }
  }

  @Override
  public void updatePayOrderPrice(Long id, Integer payPrice) {
    PayOrderDO order = orderMapper.selectById(id);
    if (order == null) {
      throw new ServiceException("支付订单不存在");
    }
    if (ObjectUtil.notEqual(PayOrderStatusEnum.WAITING.getStatus(), order.getStatus())) {
      throw new ServiceException("支付订单不处于待支付");
    }
    if (ObjectUtil.equal(order.getPrice(), payPrice)) {
      return;
    }

    PayOrderDO payOrderDO = new PayOrderDO();
    payOrderDO.setId(order.getId());
    payOrderDO.setPrice(payPrice);
    orderMapper.updateById(payOrderDO);
  }

  @Override
  public PayOrderExtensionDO getOrderExtension(Long id) {
    return orderExtensionMapper.selectById(id);
  }

  @Override
  public PayOrderExtensionDO getOrderExtensionByNo(String no) {
    return orderExtensionMapper.selectByNo(no);
  }

  @Override
  public int syncOrder(LocalDateTime minCreateTime) {
    // 1. 查询指定创建时间前的待支付订单
    List<PayOrderExtensionDO> orderExtensions = orderExtensionMapper.selectListByStatusAndCreateTimeGe(
        PayOrderStatusEnum.WAITING.getStatus(), minCreateTime);
    if (CollUtil.isEmpty(orderExtensions)) {
      return 0;
    }
    // 2. 遍历执行
    int count = 0;
    for (PayOrderExtensionDO orderExtension : orderExtensions) {
      count += syncOrder(orderExtension) ? 1 : 0;
    }
    return count;
  }

  @Override
  public void syncOrderQuietly(Long id) {
    // 1. 查询待支付订单
    List<PayOrderExtensionDO> orderExtensions = orderExtensionMapper.selectListByOrderIdAndStatus(id,
        PayOrderStatusEnum.WAITING.getStatus());

    // 2. 遍历执行
    for (PayOrderExtensionDO orderExtension : orderExtensions) {
      syncOrder(orderExtension);
    }
  }

  /**
   * 同步单个支付拓展单
   *
   * @param orderExtension 支付拓展单
   * @return 是否已支付
   */
  private boolean syncOrder(PayOrderExtensionDO orderExtension) {
    try {
      // 1.1 查询支付订单信息
      PayClient payClient = channelService.getPayClient(orderExtension.getChannelId());
      if (payClient == null) {
        log.error("[syncOrder][渠道编号({}) 找不到对应的支付客户端]", orderExtension.getChannelId());
        return false;
      }
      PayOrderRespDTO respDTO = payClient.getOrder(orderExtension.getNo());
      // 如果查询到订单不存在，PayClient 返回的状态为关闭。但此时不能关闭订单。存在以下一种场景：
      //  拉起渠道支付后，短时间内用户未及时完成支付，但是该订单同步定时任务恰巧自动触发了，主动查询结果为订单不存在。
      //  当用户支付成功之后，该订单状态在渠道的回调中无法从已关闭改为已支付，造成重大影响。
      // 考虑此定时任务是异常场景的兜底操作，因此这里不做变更，优先以回调为准。
      // 让订单自动随着支付渠道那边一起等到过期，确保渠道先过期关闭支付入口，而后通过订单过期定时任务关闭自己的订单。
      if (PayOrderStatusRespEnum.isClosed(respDTO.getStatus())) {
        return false;
      }
      // 1.2 回调支付结果
      notifyOrder(orderExtension.getChannelId(), respDTO);

      // 2. 如果是已支付，则返回 true
      return PayOrderStatusRespEnum.isSuccess(respDTO.getStatus());
    } catch (Throwable e) {
      log.error("[syncOrder][orderExtension({}) 同步支付状态异常]", orderExtension.getId(), e);
      return false;
    }
  }

  @Override
  public int expireOrder() {
    // 1. 查询过期的待支付订单
    List<PayOrderDO> orders = orderMapper.selectListByStatusAndExpireTimeLt(
        PayOrderStatusEnum.WAITING.getStatus(), LocalDateTime.now());
    if (CollUtil.isEmpty(orders)) {
      return 0;
    }

    // 2. 遍历执行
    int count = 0;
    for (PayOrderDO order : orders) {
      count += expireOrder(order) ? 1 : 0;
    }
    return count;
  }

  @Override
  public void updateOrderPaid(Long id, Long payOrderId) {
    // 1.1 校验订单是否存在
    PayPatentOrderDO order = patentOrderMapper.selectById(id);
    if (order == null) {
      log.error("[updateDemoOrderPaid][order({}) payOrder({}) 不存在订单，请进行处理！]", id, payOrderId);
      throw new ServiceException("订单不存在");
    }
    // 1.2 校验订单已支付
    if (order.getPayStatus()) {
      // 特殊：如果订单已支付，且支付单号相同，直接返回，说明重复回调
      if (ObjectUtil.equals(order.getPayOrderId(), payOrderId)) {
        log.warn("[updateDemoOrderPaid][order({}) 已支付，且支付单号相同({})，直接返回]", order, payOrderId);
        return;
      }
      // 异常：支付单号不同，说明支付单号错误
      log.error("[updateDemoOrderPaid][order({}) 支付单不匹配({})，请进行处理！order 数据是：{}]",
          order, payOrderId, toJsonString(order));
      throw new ServiceException("订单更新支付状态失败，支付单编号不匹配");
    }

    // 2. 校验支付订单的合法性
    com.ruoyi.pay.service.dto.PayOrderRespDTO payOrder = validatePayOrderPaid(order, payOrderId);

    // 3. 更新 PayPatentOrderDO 状态为已支付
    PayPatentOrderDO payPatentOrderDO = new PayPatentOrderDO();
    payPatentOrderDO.setPayStatus(true);
    payPatentOrderDO.setPayTime(LocalDateTime.now());
    payPatentOrderDO.setPayChannelCode(payOrder.getChannelCode());
    int updateCount = patentOrderMapper.updateByIdAndPayed(id, false, payPatentOrderDO);
    if (updateCount == 0) {
      throw new ServiceException("订单更新支付状态失败，订单不是【未支付】状态");
    }
  }

  @Override
  public SysUserRespVo getPatentInfo(String id) {
    GPatentLibrary gPatentLibrary = patentLibraryMapper.selectGPatentLibraryById(id);
    if (Objects.isNull(gPatentLibrary)) {
      throw new ServiceException("该专利号不存在");
    }

    if (gPatentLibrary.getBookerKey() == null) {
      throw new ServiceException("当前专利暂无预定，无法查看联系人信息");
    }

    if (!Objects.equals(gPatentLibrary.getBookerKey(), SecurityUtils.getUserId())) {
      throw new ServiceException("当前专利号已被他人预定，您无权查看专利信息");
    }

    PayPatentOrderDO patentOrderDO = patentOrderMapper.selectOne(new LambdaQueryWrapperX<PayPatentOrderDO>()
        .eq(PayPatentOrderDO::getPatentNo, gPatentLibrary.getPatentNo())
        .eq(PayPatentOrderDO::getUserId, SecurityUtils.getUserId())
        .eq(PayPatentOrderDO::getPayStatus, true));

    if (Objects.isNull(patentOrderDO)) {
      return null;
    }

    return userMapper.getUserById(Long.valueOf(gPatentLibrary.getCreateBy()));
  }

  /**
   * 校验支付订单的合法性
   *
   * @param order      交易订单
   * @param payOrderId 支付订单编号
   * @return 支付订单
   */
  private com.ruoyi.pay.service.dto.PayOrderRespDTO validatePayOrderPaid(PayPatentOrderDO order, Long payOrderId) {
    // 1. 校验支付单是否存在
    PayOrderDO payOrder = getOrder(payOrderId);
    if (payOrder == null) {
      log.error("[validatePayOrderPaid][order({}) payOrder({}) 不存在，请进行处理！]", order.getId(), payOrderId);
      throw new ServiceException("支付订单不存在");
    }
    // 2.1 校验支付单已支付
    if (!PayOrderStatusEnum.isSuccess(payOrder.getStatus())) {
      log.error("[validatePayOrderPaid][order({}) payOrder({}) 未支付，请进行处理！payOrder 数据是：{}]",
          order.getId(), payOrderId, toJsonString(payOrder));
      throw new ServiceException("订单更新支付状态失败，支付单状态不是【支付成功】状态");
    }
    // 2.1 校验支付金额一致
    if (notEqual(payOrder.getPrice(), order.getPrice())) {
      log.error("[validatePayOrderPaid][order({}) payOrder({}) 支付金额不匹配，请进行处理！order 数据是：{}，payOrder 数据是：{}]",
          order.getId(), payOrderId, toJsonString(order), toJsonString(payOrder));
      throw new ServiceException("订单更新支付状态失败，支付单金额不匹配");
    }
    // 2.2 校验支付订单匹配（二次）
    if (notEqual(payOrder.getMerchantOrderId(), order.getId().toString())) {
      log.error("[validatePayOrderPaid][order({}) 支付单不匹配({})，请进行处理！payOrder 数据是：{}]",
          order.getId(), payOrderId, toJsonString(payOrder));
      throw new ServiceException("订单更新支付状态失败，支付单编号不匹配");
    }
    return PayOrderConvert.INSTANCE.convert3(payOrder);
  }

  /**
   * 同步单个支付单
   *
   * @param order 支付单
   * @return 是否已过期
   */
  private boolean expireOrder(PayOrderDO order) {
    try {
      // 1. 需要先处理关联的支付拓展单，避免错误的过期已支付 or 已退款的订单
      List<PayOrderExtensionDO> orderExtensions = orderExtensionMapper.selectListByOrderId(order.getId());
      for (PayOrderExtensionDO orderExtension : orderExtensions) {
        if (PayOrderStatusEnum.isClosed(orderExtension.getStatus())) {
          continue;
        }
        // 情况一：校验数据库中的 orderExtension 是不是已支付
        if (PayOrderStatusEnum.isSuccess(orderExtension.getStatus())) {
          log.error("[expireOrder][order({}) 的 extension({}) 已支付，可能是数据不一致]",
              order.getId(), orderExtension.getId());
          return false;
        }
        // 情况二：调用三方接口，查询支付单状态，是不是已支付/已退款
        PayClient payClient = channelService.getPayClient(orderExtension.getChannelId());
        if (payClient == null) {
          log.error("[expireOrder][渠道编号({}) 找不到对应的支付客户端]", orderExtension.getChannelId());
          return false;
        }
        PayOrderRespDTO respDTO = payClient.getOrder(orderExtension.getNo());
        if (PayOrderStatusRespEnum.isRefund(respDTO.getStatus())) {
          // 补充说明：按道理，应该是 WAITING => SUCCESS => REFUND 状态，如果直接 WAITING => REFUND 状态，说明中间丢了过程
          // 此时，需要人工介入，手工补齐数据，保持 WAITING => SUCCESS => REFUND 的过程
          log.error("[expireOrder][extension({}) 的 PayOrderRespDTO({}) 已退款，可能是回调延迟]",
              orderExtension.getId(), toJsonString(respDTO));
          return false;
        }
        if (PayOrderStatusRespEnum.isSuccess(respDTO.getStatus())) {
          notifyOrder(orderExtension.getChannelId(), respDTO);
          return false;
        }
        // 兜底逻辑：将支付拓展单更新为已关闭
        PayOrderExtensionDO updateObj = new PayOrderExtensionDO();
        updateObj.setStatus(PayOrderStatusEnum.CLOSED.getStatus());
        updateObj.setChannelNotifyData(toJsonString(respDTO));
        if (orderExtensionMapper.updateByIdAndStatus(orderExtension.getId(), PayOrderStatusEnum.WAITING.getStatus(),
            updateObj) == 0) {
          log.error("[expireOrder][extension({}) 更新为支付关闭失败]", orderExtension.getId());
          return false;
        }
        log.info("[expireOrder][extension({}) 更新为支付关闭成功]", orderExtension.getId());
      }

      // 2. 都没有上述情况，可以安心更新为已关闭
      PayOrderDO updateObj = new PayOrderDO();
      updateObj.setStatus(PayOrderStatusEnum.CLOSED.getStatus());
      if (orderMapper.updateByIdAndStatus(order.getId(), order.getStatus(), updateObj) == 0) {
        log.error("[expireOrder][order({}) 更新为支付关闭失败]", order.getId());
        return false;
      }
      log.info("[expireOrder][order({}) 更新为支付关闭失败]", order.getId());
      return true;
    } catch (Throwable e) {
      log.error("[expireOrder][order({}) 过期订单异常]", order.getId(), e);
      return false;
    }
  }

  /**
   * 获得自身的代理对象，解决 AOP 生效问题
   *
   * @return 自己
   */
  private PayOrderServiceImpl getSelf() {
    return SpringUtil.getBean(getClass());
  }

}
