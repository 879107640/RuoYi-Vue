package com.ruoyi.pay.service.wallet;

import cn.hutool.db.PageResult;
import com.ruoyi.common.enums.wallet.PayWalletBizTypeEnum;
import com.ruoyi.pay.domain.wallet.PayWalletTransactionDO;
import com.ruoyi.pay.service.wallet.bo.WalletTransactionCreateReqBO;

import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * 钱包余额流水 Service 接口
 *
 * @author jason
 */
public interface PayWalletTransactionService {

  /**
   * 新增钱包余额流水
   *
   * @param bo 创建钱包流水 bo
   * @return 新建的钱包 do
   */
  PayWalletTransactionDO createWalletTransaction(@Valid WalletTransactionCreateReqBO bo);

  /**
   * 根据 no，获取钱包余流水
   *
   * @param no 流水号
   */
  PayWalletTransactionDO getWalletTransactionByNo(String no);

  /**
   * 获取钱包流水
   *
   * @param bizId 业务编号
   * @param type  业务类型
   * @return 钱包流水
   */
  PayWalletTransactionDO getWalletTransaction(String bizId, PayWalletBizTypeEnum type);

}
