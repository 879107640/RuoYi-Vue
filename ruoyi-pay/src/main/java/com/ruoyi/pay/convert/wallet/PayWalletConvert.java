package com.ruoyi.pay.convert.wallet;

import com.ruoyi.common.core.page.PageResult;
import com.ruoyi.pay.domain.wallet.PayWalletDO;
import com.ruoyi.pay.service.vo.wallet.AppPayWalletRespVO;
import com.ruoyi.pay.service.vo.wallet.PayWalletRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PayWalletConvert {

    PayWalletConvert INSTANCE = Mappers.getMapper(PayWalletConvert.class);

    AppPayWalletRespVO convert(PayWalletDO bean);

    PayWalletRespVO convert02(PayWalletDO bean);

    PageResult<PayWalletRespVO> convertPage(PageResult<PayWalletDO> page);

}
