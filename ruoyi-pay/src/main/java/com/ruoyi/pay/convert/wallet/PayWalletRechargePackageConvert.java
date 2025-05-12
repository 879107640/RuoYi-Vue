package com.ruoyi.pay.convert.wallet;

import com.ruoyi.common.core.page.PageResult;
import com.ruoyi.pay.domain.wallet.PayWalletRechargePackageDO;
import com.ruoyi.pay.service.vo.WalletRechargePackageCreateReqVO;
import com.ruoyi.pay.service.vo.WalletRechargePackageRespVO;
import com.ruoyi.pay.service.vo.WalletRechargePackageUpdateReqVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PayWalletRechargePackageConvert {

  PayWalletRechargePackageConvert INSTANCE = Mappers.getMapper(PayWalletRechargePackageConvert.class);

  PayWalletRechargePackageDO convert(WalletRechargePackageCreateReqVO bean);

  PayWalletRechargePackageDO convert(WalletRechargePackageUpdateReqVO bean);

  WalletRechargePackageRespVO convert(PayWalletRechargePackageDO bean);

  List<WalletRechargePackageRespVO> convertList(List<PayWalletRechargePackageDO> list);

  PageResult<WalletRechargePackageRespVO> convertPage(PageResult<PayWalletRechargePackageDO> page);

}
