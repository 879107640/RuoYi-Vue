package com.ruoyi.pay.mapper.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.common.core.page.PageResult;
import com.ruoyi.framework.mybatis.mapper.BaseMapperX;
import com.ruoyi.framework.mybatis.query.LambdaQueryWrapperX;
import com.ruoyi.pay.domain.app.PayAppDO;
import com.ruoyi.pay.service.vo.PayAppPageReqVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PayAppMapper extends BaseMapperX<PayAppDO> {

  default PayAppDO selectByAppKey(String appKey) {
    return selectOne(new LambdaQueryWrapper<PayAppDO>().eq(PayAppDO::getAppKey, appKey));
  }

  default PageResult<PayAppDO> selectPage(PayAppPageReqVO reqVO) {
    return selectPage(reqVO, new LambdaQueryWrapperX<PayAppDO>()
        .likeIfPresent(PayAppDO::getName, reqVO.getName())
        .likeIfPresent(PayAppDO::getAppKey, reqVO.getAppKey())
        .eqIfPresent(PayAppDO::getStatus, reqVO.getStatus())
        .betweenIfPresent(PayAppDO::getCreateTime, reqVO.getCreateTime())
        .orderByDesc(PayAppDO::getId));
  }

}
