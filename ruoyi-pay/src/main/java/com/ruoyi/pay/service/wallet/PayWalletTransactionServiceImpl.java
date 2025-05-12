package com.ruoyi.pay.service.wallet;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.db.PageResult;
import com.ruoyi.common.enums.wallet.PayWalletBizTypeEnum;
import com.ruoyi.pay.convert.wallet.PayWalletTransactionConvert;
import com.ruoyi.pay.domain.wallet.PayWalletDO;
import com.ruoyi.pay.domain.wallet.PayWalletTransactionDO;
import com.ruoyi.pay.mapper.wallet.PayWalletTransactionMapper;
import com.ruoyi.pay.redis.no.PayNoRedisDAO;
import com.ruoyi.pay.service.wallet.bo.WalletTransactionCreateReqBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;


/**
 * 钱包流水 Service 实现类
 *
 * @author jason
 */
@Service
@Slf4j
@Validated
public class PayWalletTransactionServiceImpl implements PayWalletTransactionService {

  /**
   * 钱包流水的 no 前缀
   */
  private static final String WALLET_NO_PREFIX = "W";

  @Resource
  private PayWalletService payWalletService;
  @Resource
  private PayWalletTransactionMapper payWalletTransactionMapper;
  @Resource
  private PayNoRedisDAO noRedisDAO;

  @Override
  public PayWalletTransactionDO createWalletTransaction(WalletTransactionCreateReqBO bo) {
    PayWalletTransactionDO transaction = PayWalletTransactionConvert.INSTANCE.convert(bo);
    transaction.setNo(noRedisDAO.generate(WALLET_NO_PREFIX));
    payWalletTransactionMapper.insert(transaction);
    return transaction;
  }

  @Override
  public PayWalletTransactionDO getWalletTransactionByNo(String no) {
    return payWalletTransactionMapper.selectByNo(no);
  }

  @Override
  public PayWalletTransactionDO getWalletTransaction(String bizId, PayWalletBizTypeEnum type) {
    return payWalletTransactionMapper.selectByBiz(bizId, type.getType());
  }
}
