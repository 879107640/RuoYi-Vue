package com.ruoyi.pay.mapper.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.pay.domain.order.PayOrderExtensionDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PayOrderExtensionMapper extends BaseMapper<PayOrderExtensionDO> {

  default PayOrderExtensionDO selectByNo(String no) {
    return selectOne(new LambdaQueryWrapper<PayOrderExtensionDO>().eq(PayOrderExtensionDO::getNo, no));
  }

  default int updateByIdAndStatus(Long id, Integer status, PayOrderExtensionDO update) {
    return update(update, new LambdaQueryWrapper<PayOrderExtensionDO>()
        .eq(PayOrderExtensionDO::getId, id).eq(PayOrderExtensionDO::getStatus, status));
  }

  default List<PayOrderExtensionDO> selectListByOrderId(Long orderId) {
    return selectList(new LambdaQueryWrapper<PayOrderExtensionDO>().eq(PayOrderExtensionDO::getOrderId, orderId));
  }

  default List<PayOrderExtensionDO> selectListByOrderIdAndStatus(Long orderId, Integer status) {
    return selectList(new LambdaQueryWrapper<PayOrderExtensionDO>().eq(PayOrderExtensionDO::getOrderId, orderId).eq(
        PayOrderExtensionDO::getStatus, status));
  }

  default List<PayOrderExtensionDO> selectListByStatusAndCreateTimeGe(Integer status, LocalDateTime minCreateTime) {
    return selectList(new LambdaQueryWrapper<PayOrderExtensionDO>()
        .eq(PayOrderExtensionDO::getStatus, status)
        .ge(PayOrderExtensionDO::getCreateTime, minCreateTime));
  }

}
