package com.ruoyi.pay.convert.wallet;

import com.ruoyi.pay.domain.wallet.PayWalletTransactionDO;
import com.ruoyi.pay.service.wallet.bo.WalletTransactionCreateReqBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PayWalletTransactionConvert {

  PayWalletTransactionConvert INSTANCE = Mappers.getMapper(PayWalletTransactionConvert.class);

  PayWalletTransactionDO convert(WalletTransactionCreateReqBO bean);

}
