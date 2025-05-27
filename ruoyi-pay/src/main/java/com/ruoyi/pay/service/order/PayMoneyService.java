package com.ruoyi.pay.service.order;

import com.ruoyi.pay.service.vo.PayMoneyBaseReqVO;
import com.ruoyi.pay.service.vo.PayMoneyUpdateReqVO;

public interface PayMoneyService {
  /**
   * 新增参数配置
   *
   * @param config 参数配置信息
   * @return 结果
   */
  int insertConfig(PayMoneyBaseReqVO config);

  /**
   * 修改参数配置
   *
   * @param config 参数配置信息
   * @return 结果
   */
  int updateConfig(PayMoneyUpdateReqVO config);

  long getMoney();
}
