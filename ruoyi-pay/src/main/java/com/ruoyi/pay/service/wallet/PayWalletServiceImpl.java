package com.ruoyi.pay.service.wallet;

import cn.hutool.core.lang.Assert;
import com.ruoyi.common.core.page.PageResult;
import com.ruoyi.common.enums.wallet.PayWalletBizTypeEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.date.DateUtils;
import com.ruoyi.pay.convert.wallet.PayWalletRechargeConvert;
import com.ruoyi.pay.domain.order.PayOrderExtensionDO;
import com.ruoyi.pay.domain.refund.PayRefundDO;
import com.ruoyi.pay.domain.wallet.PayWalletDO;
import com.ruoyi.pay.domain.wallet.PayWalletRechargeDO;
import com.ruoyi.pay.domain.wallet.PayWalletRechargePackageDO;
import com.ruoyi.pay.domain.wallet.PayWalletTransactionDO;
import com.ruoyi.pay.framework.pay.config.PayProperties;
import com.ruoyi.pay.mapper.wallet.PayWalletMapper;
import com.ruoyi.pay.mapper.wallet.PayWalletRechargeMapper;
import com.ruoyi.pay.redis.wallet.PayWalletLockRedisDAO;
import com.ruoyi.pay.service.dto.PayOrderCreateReqDTO;
import com.ruoyi.pay.service.order.PayOrderService;
import com.ruoyi.pay.service.refund.PayRefundService;
import com.ruoyi.pay.service.vo.wallet.PayWalletPageReqVO;
import com.ruoyi.pay.service.vo.wallet.PayWalletRechargeCreateReqVO;
import com.ruoyi.pay.service.wallet.bo.WalletTransactionCreateReqBO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Date;
import java.util.Objects;

import static com.ruoyi.common.enums.wallet.PayWalletBizTypeEnum.PAYMENT;
import static com.ruoyi.common.enums.wallet.PayWalletBizTypeEnum.PAYMENT_REFUND;
import static com.ruoyi.common.utils.date.LocalDateTimeUtils.addTime;


/**
 * 钱包 Service 实现类
 *
 * @author jason
 */
@Service
@Slf4j
public class PayWalletServiceImpl implements PayWalletService {

  /**
   * 通知超时时间，单位：毫秒
   */
  public static final long UPDATE_TIMEOUT_MILLIS = 120 * DateUtils.SECOND_MILLIS;

  @Resource
  private PayWalletMapper walletMapper;
  @Resource
  private PayWalletLockRedisDAO lockRedisDAO;
  @Resource
  private PayWalletRechargeMapper walletRechargeMapper;
  @Resource
  @Lazy // 延迟加载，避免循环依赖
  private PayWalletTransactionService walletTransactionService;
  @Resource
  @Lazy // 延迟加载，避免循环依赖
  private PayOrderService orderService;
  @Resource
  @Lazy // 延迟加载，避免循环依赖
  private PayRefundService refundService;
  @Resource
  private PayWalletService payWalletService;
  @Resource
  private PayWalletRechargePackageService payWalletRechargePackageService;
  @Resource
  private PayOrderService payOrderService;
  @Resource
  private PayProperties payProperties;

  @Override
  public PayWalletDO getOrCreateWallet(Long userId, Integer userType) {
    PayWalletDO wallet = walletMapper.selectByUserIdAndType(userId, userType);
    if (wallet == null) {
      wallet = new PayWalletDO();
      wallet.setUserId(userId);
      wallet.setUserType(userType);
      wallet.setBalance(0);
      wallet.setTotalExpense(0);
      wallet.setTotalRecharge(0);
      wallet.setCreateTime(new Date());
      walletMapper.insert(wallet);
    }
    return wallet;
  }

  @Override
  public PayWalletDO getWallet(Long walletId) {
    return walletMapper.selectById(walletId);
  }

  @Override
  public PageResult<PayWalletDO> getWalletPage(PayWalletPageReqVO pageReqVO) {
    return walletMapper.selectPage(pageReqVO);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public PayWalletTransactionDO orderPay(Long userId, Integer userType, String outTradeNo, Integer price) {
    // 1. 判断支付交易拓展单是否存
    PayOrderExtensionDO orderExtension = orderService.getOrderExtensionByNo(outTradeNo);
    if (orderExtension == null) {
      throw new ServiceException("支付交易拓展单不存在");
    }
    PayWalletDO wallet = getOrCreateWallet(userId, userType);
    // 2. 扣减余额
    return reduceWalletBalance(wallet.getId(), orderExtension.getOrderId(), PAYMENT, price);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public PayWalletTransactionDO orderRefund(String outRefundNo, Integer refundPrice, String reason) {
    // 1.1 判断退款单是否存在
    PayRefundDO payRefund = refundService.getRefundByNo(outRefundNo);
    if (payRefund == null) {
      throw new ServiceException("支付退款单不存在");
    }
    // 1.2 校验是否可以退款
    Long walletId = validateWalletCanRefund(payRefund.getId(), payRefund.getChannelOrderNo());
    PayWalletDO wallet = walletMapper.selectById(walletId);
    Assert.notNull(wallet, "钱包 {} 不存在", walletId);

    // 2. 增加余额
    return addWalletBalance(walletId, String.valueOf(payRefund.getId()), PAYMENT_REFUND, refundPrice);
  }

  /**
   * 校验是否能退款
   *
   * @param refundId    支付退款单 id
   * @param walletPayNo 钱包支付 no
   */
  private Long validateWalletCanRefund(Long refundId, String walletPayNo) {
    // 1. 校验钱包支付交易存在
    PayWalletTransactionDO walletTransaction = walletTransactionService.getWalletTransactionByNo(walletPayNo);
    if (walletTransaction == null) {
      throw new ServiceException("未找到对应的钱包交易");
    }
    // 2. 校验退款是否存在
    PayWalletTransactionDO refundTransaction = walletTransactionService.getWalletTransaction(
        String.valueOf(refundId), PAYMENT_REFUND);
    if (refundTransaction != null) {
      throw new ServiceException("已经存在钱包退款");
    }
    return walletTransaction.getWalletId();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @SneakyThrows
  public PayWalletTransactionDO reduceWalletBalance(Long walletId, Long bizId,
                                                    PayWalletBizTypeEnum bizType, Integer price) {
    // 1. 获取钱包
    PayWalletDO payWallet = getWallet(walletId);
    if (payWallet == null) {
      log.error("[reduceWalletBalance][用户钱包({})不存在]", walletId);
      throw new ServiceException("用户钱包不存在");
    }

    // 2. 加锁，更新钱包余额（目的：避免钱包流水的并发更新时，余额变化不连贯）
    return lockRedisDAO.lock(walletId, UPDATE_TIMEOUT_MILLIS, () -> {
      // 2. 扣除余额
      int updateCounts;
      switch (bizType) {
        case PAYMENT: {
          updateCounts = walletMapper.updateWhenConsumption(payWallet.getId(), price);
          break;
        }
        case RECHARGE_REFUND: {
          updateCounts = walletMapper.updateWhenRechargeRefund(payWallet.getId(), price);
          break;
        }
        default: {
          // TODO 其它类型待实现
          throw new ServiceException("待实现");
        }
      }
      if (updateCounts == 0) {
        throw new ServiceException("钱包余额不足");
      }

      // 3. 生成钱包流水
      Integer afterBalance = payWallet.getBalance() - price;
      WalletTransactionCreateReqBO bo = new WalletTransactionCreateReqBO();
      bo.setWalletId(payWallet.getId());
      bo.setPrice(-price);
      bo.setBalance(afterBalance);
      bo.setBizId(String.valueOf(bizId));
      bo.setBizType(bizType.getType());
      bo.setTitle(bizType.getDescription());
      return walletTransactionService.createWalletTransaction(bo);
    });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @SneakyThrows
  public PayWalletTransactionDO addWalletBalance(Long walletId, String bizId,
                                                 PayWalletBizTypeEnum bizType, Integer price) {
    // 1. 获取钱包
    PayWalletDO payWallet = getWallet(walletId);
    if (payWallet == null) {
      log.error("[addWalletBalance][用户钱包({})不存在]", walletId);
      throw new ServiceException("用户钱包不存在");
    }

    // 2. 加锁，更新钱包余额（目的：避免钱包流水的并发更新时，余额变化不连贯）
    return lockRedisDAO.lock(walletId, UPDATE_TIMEOUT_MILLIS, () -> {
      // 3. 更新钱包金额
      switch (bizType) {
        case PAYMENT_REFUND: { // 退款更新
          walletMapper.updateWhenConsumptionRefund(payWallet.getId(), price);
          break;
        }
        case RECHARGE: { // 充值更新
          walletMapper.updateWhenRecharge(payWallet.getId(), price);
          break;
        }
        case UPDATE_BALANCE: // 更新余额
        case BROKERAGE_WITHDRAW: // 分佣提现
          walletMapper.updateWhenAdd(payWallet.getId(), price);
          break;
        default: {
          throw new ServiceException("待实现：" + bizType);
        }
      }

      // 4. 生成钱包流水
      WalletTransactionCreateReqBO transactionCreateReqBO = new WalletTransactionCreateReqBO();
      transactionCreateReqBO.setWalletId(payWallet.getId());
      transactionCreateReqBO.setPrice(price);
      transactionCreateReqBO.setBalance(payWallet.getBalance() + price);
      transactionCreateReqBO  .setBizId(bizId);
      transactionCreateReqBO.setBizType(bizType.getType());
      transactionCreateReqBO.setTitle(bizType.getDescription());
      return walletTransactionService.createWalletTransaction(transactionCreateReqBO);
    });
  }

  @Override
  public void freezePrice(Long id, Integer price) {
    int updateCounts = walletMapper.freezePrice(id, price);
    if (updateCounts == 0) {
      throw new ServiceException("钱包余额不足");
    }
  }

  @Override
  public void unfreezePrice(Long id, Integer price) {
    int updateCounts = walletMapper.unFreezePrice(id, price);
    if (updateCounts == 0) {
      throw new ServiceException("钱包冻结余额不足");
    }
  }

  @Override
  public PayWalletRechargeDO createWalletRecharge(Long userId, Integer memberType, String ipAddr, PayWalletRechargeCreateReqVO reqVO) {
    // 1.1 计算充值金额
    int payPrice;
    int bonusPrice = 0;
    if (Objects.nonNull(reqVO.getPackageId())) {
      PayWalletRechargePackageDO rechargePackage = payWalletRechargePackageService.validWalletRechargePackage(reqVO.getPackageId());
      payPrice = rechargePackage.getPayPrice();
      bonusPrice = rechargePackage.getBonusPrice();
    } else {
      payPrice = reqVO.getPayPrice();
    }
    // 1.2 插入充值记录
    PayWalletDO wallet = payWalletService.getOrCreateWallet(userId, memberType);
    PayWalletRechargeDO recharge = PayWalletRechargeConvert.INSTANCE.convert(wallet.getId(), payPrice, bonusPrice, reqVO.getPackageId());
    walletRechargeMapper.insert(recharge);

    // 2.1 创建支付单
    PayOrderCreateReqDTO payOrderCreateReqDTO = new PayOrderCreateReqDTO();
    payOrderCreateReqDTO.setAppKey("wallet");
    payOrderCreateReqDTO.setUserIp(ipAddr);
    payOrderCreateReqDTO.setMerchantOrderId(recharge.getId().toString()); // 业务的订单编号
    payOrderCreateReqDTO.setSubject("钱包余额充值");
    payOrderCreateReqDTO.setBody("");
    payOrderCreateReqDTO.setPrice(recharge.getPayPrice());
    payOrderCreateReqDTO.setExpireTime(addTime(Duration.ofHours(2L)));
    Long payOrderId = payOrderService.createPayOrder(payOrderCreateReqDTO); // TODO @Centre：支付超时时间
    // 2.2 更新钱包充值记录中支付订单
    PayWalletRechargeDO payWalletRechargeDO = new PayWalletRechargeDO();
    payWalletRechargeDO.setId(recharge.getId());
    payWalletRechargeDO.setPayOrderId(payOrderId);
    walletRechargeMapper.updateById(payWalletRechargeDO);
    recharge.setPayOrderId(payOrderId);
    return recharge;
  }

}
