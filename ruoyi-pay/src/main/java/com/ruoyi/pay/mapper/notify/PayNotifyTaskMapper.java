package com.ruoyi.pay.mapper.notify;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.enums.notify.PayNotifyStatusEnum;
import com.ruoyi.framework.mybatis.mapper.BaseMapperX;
import com.ruoyi.pay.domain.notify.PayNotifyTaskDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PayNotifyTaskMapper extends BaseMapperX<PayNotifyTaskDO> {

  /**
   * 获得需要通知的 PayNotifyTaskDO 记录。需要满足如下条件：
   * <p>
   * 1. status 非成功
   * 2. nextNotifyTime 小于当前时间
   *
   * @return PayTransactionNotifyTaskDO 数组
   */
  default List<PayNotifyTaskDO> selectListByNotify() {
    return selectList(new LambdaQueryWrapper<PayNotifyTaskDO>()
        .in(PayNotifyTaskDO::getStatus, PayNotifyStatusEnum.WAITING.getStatus(),
            PayNotifyStatusEnum.REQUEST_SUCCESS.getStatus(), PayNotifyStatusEnum.REQUEST_FAILURE.getStatus())
        .le(PayNotifyTaskDO::getNextNotifyTime, LocalDateTime.now()));
  }


}
