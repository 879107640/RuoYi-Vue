package com.ruoyi.pay.mapper.wallet;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.pay.domain.wallet.PayWalletRechargePackageDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PayWalletRechargePackageMapper extends BaseMapper<PayWalletRechargePackageDO> {


    default PayWalletRechargePackageDO selectByName(String name) {
        return selectOne(new LambdaQueryWrapper<PayWalletRechargePackageDO>().eq(PayWalletRechargePackageDO::getName, name));
    }

    default List<PayWalletRechargePackageDO> selectListByStatus(Integer status) {
        return selectList(new LambdaQueryWrapper<PayWalletRechargePackageDO>().eq(PayWalletRechargePackageDO::getStatus, status));
    }

}
