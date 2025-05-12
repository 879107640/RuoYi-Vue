package com.ruoyi.pay.mapper.wallet;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.page.PageResult;
import com.ruoyi.framework.mybatis.mapper.BaseMapperX;
import com.ruoyi.framework.mybatis.query.LambdaQueryWrapperX;
import com.ruoyi.pay.domain.wallet.PayWalletRechargePackageDO;
import com.ruoyi.pay.service.vo.WalletRechargePackagePageReqVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PayWalletRechargePackageMapper extends BaseMapperX<PayWalletRechargePackageDO> {

  default PageResult<PayWalletRechargePackageDO> selectPage(WalletRechargePackagePageReqVO reqVO) {
    return selectPage(reqVO, new LambdaQueryWrapperX<PayWalletRechargePackageDO>()
        .likeIfPresent(PayWalletRechargePackageDO::getName, reqVO.getName())
        .eqIfPresent(PayWalletRechargePackageDO::getStatus, reqVO.getStatus())
        .betweenIfPresent(PayWalletRechargePackageDO::getCreateTime, reqVO.getCreateTime())
        .orderByDesc(PayWalletRechargePackageDO::getPayPrice));
  }

  default PayWalletRechargePackageDO selectByName(String name) {
    return selectOne(new LambdaQueryWrapper<PayWalletRechargePackageDO>().eq(PayWalletRechargePackageDO::getName, name));
  }

  default List<PayWalletRechargePackageDO> selectListByStatus(Integer status) {
    return selectList(new LambdaQueryWrapper<PayWalletRechargePackageDO>().eq(PayWalletRechargePackageDO::getStatus, status));
  }

}
