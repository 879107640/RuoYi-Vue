package com.ruoyi.pay.mapper.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.pay.domain.app.PayAppDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PayAppMapper extends BaseMapper<PayAppDO> {

  default PayAppDO selectByAppKey(String appKey) {
    return selectOne(new LambdaQueryWrapper<PayAppDO>().eq(PayAppDO::getAppKey, appKey));
  }

}
