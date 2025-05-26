package com.ruoyi.pay.mapper.channel;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.pay.domain.channel.PayChannelDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface PayChannelMapper extends BaseMapper<PayChannelDO> {

  default PayChannelDO selectByAppIdAndCode(Long appId, String code) {
    return selectOne(new LambdaQueryWrapper<PayChannelDO>().eq(PayChannelDO::getAppId, appId).eq(PayChannelDO::getCode, code));
  }

  default List<PayChannelDO> selectListByAppIds(Collection<Long> appIds) {
    return selectList(
            new LambdaQueryWrapper<PayChannelDO>()
                    .in(CollectionUtils.isNotEmpty(appIds), PayChannelDO::getAppId, appIds)
    );
  }

  default List<PayChannelDO> selectListByAppId(Long appId, Integer status) {
    return selectList(new LambdaQueryWrapper<PayChannelDO>()
        .eq(PayChannelDO::getAppId, appId)
        .eq(PayChannelDO::getStatus, status));
  }

}
