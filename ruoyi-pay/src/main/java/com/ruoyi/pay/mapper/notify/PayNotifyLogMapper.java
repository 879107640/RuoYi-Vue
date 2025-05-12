package com.ruoyi.pay.mapper.notify;

import com.ruoyi.framework.mybatis.mapper.BaseMapperX;
import com.ruoyi.pay.domain.notify.PayNotifyLogDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PayNotifyLogMapper extends BaseMapperX<PayNotifyLogDO> {

  default List<PayNotifyLogDO> selectListByTaskId(Long taskId) {
    return selectList(PayNotifyLogDO::getTaskId, taskId);
  }

}
