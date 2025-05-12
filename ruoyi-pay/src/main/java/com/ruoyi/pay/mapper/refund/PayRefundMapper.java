package com.ruoyi.pay.mapper.refund;

import cn.hutool.db.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.framework.mybatis.mapper.BaseMapperX;
import com.ruoyi.framework.mybatis.query.LambdaQueryWrapperX;
import com.ruoyi.pay.domain.refund.PayRefundDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PayRefundMapper extends BaseMapperX<PayRefundDO> {

  default Long selectCountByAppId(Long appId) {
    return selectCount(PayRefundDO::getAppId, appId);
  }

  default PayRefundDO selectByAppIdAndMerchantRefundId(Long appId, String merchantRefundId) {
    return selectOne(new LambdaQueryWrapperX<PayRefundDO>()
        .eq(PayRefundDO::getAppId, appId)
        .eq(PayRefundDO::getMerchantRefundId, merchantRefundId));
  }

  default Long selectCountByAppIdAndOrderId(Long appId, Long orderId, Integer status) {
    return selectCount(new LambdaQueryWrapperX<PayRefundDO>()
        .eq(PayRefundDO::getAppId, appId)
        .eq(PayRefundDO::getOrderId, orderId)
        .eq(PayRefundDO::getStatus, status));
  }

  default PayRefundDO selectByAppIdAndNo(Long appId, String no) {
    return selectOne(new LambdaQueryWrapperX<PayRefundDO>()
        .eq(PayRefundDO::getAppId, appId)
        .eq(PayRefundDO::getNo, no));
  }

  default PayRefundDO selectByNo(String no) {
    return selectOne(PayRefundDO::getNo, no);
  }

  default int updateByIdAndStatus(Long id, Integer status, PayRefundDO update) {
    return update(update, new LambdaQueryWrapper<PayRefundDO>()
        .eq(PayRefundDO::getId, id).eq(PayRefundDO::getStatus, status));
  }

  default List<PayRefundDO> selectListByStatus(Integer status) {
    return selectList(PayRefundDO::getStatus, status);
  }
}
