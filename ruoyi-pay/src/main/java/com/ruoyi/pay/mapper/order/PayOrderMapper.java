package com.ruoyi.pay.mapper.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.page.PageResult;
import com.ruoyi.framework.mybatis.mapper.BaseMapperX;
import com.ruoyi.framework.mybatis.query.LambdaQueryWrapperX;
import com.ruoyi.pay.domain.order.PayOrderDO;
import com.ruoyi.pay.service.vo.order.PayOrderExportReqVO;
import com.ruoyi.pay.service.vo.order.PayOrderPageReqVO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PayOrderMapper extends BaseMapperX<PayOrderDO> {

  default List<PayOrderDO> selectList(PayOrderExportReqVO reqVO) {
    return selectList(new LambdaQueryWrapperX<PayOrderDO>()
        .eqIfPresent(PayOrderDO::getAppId, reqVO.getAppId())
        .eqIfPresent(PayOrderDO::getChannelCode, reqVO.getChannelCode())
        .likeIfPresent(PayOrderDO::getMerchantOrderId, reqVO.getMerchantOrderId())
        .likeIfPresent(PayOrderDO::getChannelOrderNo, reqVO.getChannelOrderNo())
        .likeIfPresent(PayOrderDO::getNo, reqVO.getNo())
        .eqIfPresent(PayOrderDO::getStatus, reqVO.getStatus())
        .betweenIfPresent(PayOrderDO::getCreateTime, reqVO.getCreateTime())
        .orderByDesc(PayOrderDO::getId));
  }

  default Long selectCountByAppId(Long appId) {
    return selectCount(PayOrderDO::getAppId, appId);
  }

  default PayOrderDO selectByAppIdAndMerchantOrderId(Long appId, String merchantOrderId) {
    return selectOne(PayOrderDO::getAppId, appId,
        PayOrderDO::getMerchantOrderId, merchantOrderId);
  }

  default int updateByIdAndStatus(Long id, Integer status, PayOrderDO update) {
    return update(update, new LambdaQueryWrapper<PayOrderDO>()
        .eq(PayOrderDO::getId, id).eq(PayOrderDO::getStatus, status));
  }

  default List<PayOrderDO> selectListByStatusAndExpireTimeLt(Integer status, LocalDateTime expireTime) {
    return selectList(new LambdaQueryWrapper<PayOrderDO>()
        .eq(PayOrderDO::getStatus, status)
        .lt(PayOrderDO::getExpireTime, expireTime));
  }

  default PageResult<PayOrderDO> selectPage(PayOrderPageReqVO reqVO) {
    return selectPage(reqVO, new LambdaQueryWrapperX<PayOrderDO>()
        .eqIfPresent(PayOrderDO::getAppId, reqVO.getAppId())
        .eqIfPresent(PayOrderDO::getChannelCode, reqVO.getChannelCode())
        .likeIfPresent(PayOrderDO::getMerchantOrderId, reqVO.getMerchantOrderId())
        .likeIfPresent(PayOrderDO::getChannelOrderNo, reqVO.getChannelOrderNo())
        .likeIfPresent(PayOrderDO::getNo, reqVO.getNo())
        .eqIfPresent(PayOrderDO::getStatus, reqVO.getStatus())
        .betweenIfPresent(PayOrderDO::getCreateTime, reqVO.getCreateTime())
        .orderByDesc(PayOrderDO::getId));
  }
}
