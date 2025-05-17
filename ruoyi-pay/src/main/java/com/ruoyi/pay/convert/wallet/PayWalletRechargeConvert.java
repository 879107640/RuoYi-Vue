package com.ruoyi.pay.convert.wallet;

import cn.hutool.db.PageResult;
import com.ruoyi.common.utils.collection.CollectionUtils;
import com.ruoyi.common.utils.object.BeanUtils;
import com.ruoyi.pay.domain.order.PayOrderDO;
import com.ruoyi.pay.domain.wallet.PayWalletRechargeDO;
import com.ruoyi.pay.service.vo.wallet.PayWalletRechargeCreateRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

@Mapper
public interface PayWalletRechargeConvert {

  PayWalletRechargeConvert INSTANCE = Mappers.getMapper(PayWalletRechargeConvert.class);

  @Mapping(target = "totalPrice", expression = "java( payPrice + bonusPrice)")
  PayWalletRechargeDO convert(Long walletId, Integer payPrice, Integer bonusPrice, Long packageId);

  PayWalletRechargeCreateRespVO convert(PayWalletRechargeDO bean);

}
