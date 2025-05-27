package com.ruoyi.pay.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.pay.domain.order.PayMoneyDO;
import com.ruoyi.pay.mapper.order.PayMoneyMapper;
import com.ruoyi.pay.service.vo.PayMoneyBaseReqVO;
import com.ruoyi.pay.service.vo.PayMoneyUpdateReqVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@RequiredArgsConstructor
public class PayMoneyServiceImpl implements PayMoneyService {

  @Resource
  private PayMoneyMapper payMoneyMapper;

  @Override
  public int insertConfig(PayMoneyBaseReqVO config) {

    Long count = payMoneyMapper.selectCount();
    if (count > 0) {
      throw new ServiceException("当前不允许创建多条配置");
    }

    PayMoneyDO insert = new PayMoneyDO();
    insert.setMoney(config.getMoney());

    return payMoneyMapper.insert(insert);
  }

  @Override
  public int updateConfig(PayMoneyUpdateReqVO config) {
    Long count = payMoneyMapper.selectCount(new LambdaQueryWrapper<PayMoneyDO>().eq(PayMoneyDO::getId, config.getId()));
    if (count ==0 ) {
      throw new ServiceException("配置不存在");
    }

    PayMoneyDO update = new PayMoneyDO();
    update.setId(config.getId());
    update.setMoney(config.getMoney());

    return payMoneyMapper.updateById(update);
  }

  @Override
  public long getMoney() {
    return payMoneyMapper.selectOne(new LambdaQueryWrapper<>()).getMoney();
  }
}
