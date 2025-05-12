package com.ruoyi.pay.mapper.wallet;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.pay.domain.wallet.PayWalletTransactionDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mapper
public interface PayWalletTransactionMapper extends BaseMapper<PayWalletTransactionDO> {

    Integer TYPE_INCOME = 1;
    Integer TYPE_EXPENSE = 2;

    default Integer selectPriceSum(Long walletId, Integer type, LocalDateTime[] createTime) {
        // SQL sum 查询
        List<Map<String, Object>> result = selectMaps(new QueryWrapper<PayWalletTransactionDO>()
                .select("SUM(price) AS priceSum")
                .gt(Objects.equals(type, TYPE_INCOME), "price", 0) // 收入
                .lt(Objects.equals(type, TYPE_EXPENSE), "price", 0) // 支出
                .eq("wallet_id", walletId)
                .between("create_time", createTime[0], createTime[1]));
        // 获得 sum 结果
        Map<String, Object> first = CollUtil.getFirst(result);
        return MapUtil.getInt(first, "priceSum", 0);
    }

    default PayWalletTransactionDO selectByNo(String no) {
        return selectOne(new LambdaQueryWrapper<PayWalletTransactionDO>().eq(PayWalletTransactionDO::getNo, no));
    }

    default PayWalletTransactionDO selectByBiz(String bizId, Integer bizType) {
        return selectOne(new LambdaQueryWrapper<PayWalletTransactionDO>().eq(PayWalletTransactionDO::getBizId, bizId).eq(PayWalletTransactionDO::getBizType, bizType));
    }

}




