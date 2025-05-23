package com.ruoyi.pay.service.channel;

import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.ruoyi.common.enums.CommonStatusEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.json.JsonUtils;
import com.ruoyi.pay.config.core.client.PayClient;
import com.ruoyi.pay.config.core.client.PayClientConfig;
import com.ruoyi.pay.config.core.client.PayClientFactory;
import com.ruoyi.pay.config.core.enums.channel.PayChannelEnum;
import com.ruoyi.pay.convert.channel.PayChannelConvert;
import com.ruoyi.pay.domain.channel.PayChannelDO;
import com.ruoyi.pay.framework.pay.core.WalletPayClient;
import com.ruoyi.pay.mapper.channel.PayChannelMapper;
import com.ruoyi.pay.service.vo.PayChannelCreateReqVO;
import com.ruoyi.pay.service.vo.PayChannelUpdateReqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.Validator;
import java.util.Collection;
import java.util.List;


/**
 * 支付渠道 Service 实现类
 *
 * @author aquan
 */
@Service
@Slf4j
@Validated
public class PayChannelServiceImpl implements PayChannelService {

  @Resource
  private PayClientFactory payClientFactory;

  @Resource
  private PayChannelMapper payChannelMapper;

  @Resource
  private Validator validator;

  /**
   * 初始化，为了注册钱包
   */
  @PostConstruct
  public void init() {
    payClientFactory.registerPayClientClass(PayChannelEnum.WALLET, WalletPayClient.class);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Long createChannel(PayChannelCreateReqVO reqVO) {
    // 断言是否有重复的
    PayChannelDO dbChannel = getChannelByAppIdAndCode(reqVO.getAppId(), reqVO.getCode());
    if (dbChannel != null) {
      throw new ServiceException("已存在相同的渠道");
    }

    // 新增渠道
    PayChannelDO channel = PayChannelConvert.INSTANCE.convert(reqVO);
    channel.setConfig(parseConfig(reqVO.getCode(), reqVO.getConfig()));
    payChannelMapper.insert(channel);
    return channel.getId();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateChannel(PayChannelUpdateReqVO updateReqVO) {
    // 校验存在
    PayChannelDO dbChannel = validateChannelExists(updateReqVO.getId());

    // 更新
    PayChannelDO channel = PayChannelConvert.INSTANCE.convert(updateReqVO);
    channel.setConfig(parseConfig(dbChannel.getCode(), updateReqVO.getConfig()));
    payChannelMapper.updateById(channel);
  }

  /**
   * 解析并校验配置
   *
   * @param code      渠道编码
   * @param configStr 配置
   * @return 支付配置
   */
  private PayClientConfig parseConfig(String code, String configStr) {
    // 解析配置
    Class<? extends PayClientConfig> payClass = PayChannelEnum.getByCode(code).getConfigClass();
    if (ObjectUtil.isNull(payClass)) {
      throw new ServiceException("支付渠道的配置不存在");
    }
    PayClientConfig config = JSONUtil.toBean(configStr, payClass);
    Assert.notNull(config);

    // 验证参数
    if (config != null) {
      config.validate(validator);
    }
    return config;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteChannel(Long id) {
    // 校验存在
    validateChannelExists(id);

    // 删除
    payChannelMapper.deleteById(id);
  }

  private PayChannelDO validateChannelExists(Long id) {
    PayChannelDO channel = payChannelMapper.selectById(id);
    if (channel == null) {
      throw new ServiceException("支付渠道的配置不存在");
    }
    return channel;
  }

  @Override
  public PayChannelDO getChannel(Long id) {
    return payChannelMapper.selectById(id);
  }

  @Override
  public List<PayChannelDO> getChannelListByAppIds(Collection<Long> appIds) {
    return payChannelMapper.selectListByAppIds(appIds);
  }

  @Override
  public PayChannelDO getChannelByAppIdAndCode(Long appId, String code) {
    return payChannelMapper.selectByAppIdAndCode(appId, code);
  }

  @Override
  public PayChannelDO validPayChannel(Long id) {
    PayChannelDO channel = payChannelMapper.selectById(id);
    validPayChannel(channel);
    return channel;
  }

  @Override
  public PayChannelDO validPayChannel(Long appId, String code) {
    PayChannelDO channel = payChannelMapper.selectByAppIdAndCode(appId, code);
    validPayChannel(channel);
    return channel;
  }

  private void validPayChannel(PayChannelDO channel) {
    if (channel == null) {
      throw new ServiceException("支付渠道的配置不存在");
    }
    if (CommonStatusEnum.DISABLE.getStatus().equals(channel.getStatus())) {
      throw new ServiceException("支付渠道已经禁用");
    }
  }

  @Override
  public List<PayChannelDO> getEnableChannelList(Long appId) {
    return payChannelMapper.selectListByAppId(appId, CommonStatusEnum.ENABLE.getStatus());
  }

  @Override
  public PayClient getPayClient(Long id) {
    PayChannelDO channel = validPayChannel(id);
    return payClientFactory.createOrUpdatePayClient(id, channel.getCode(), channel.getConfig());
  }

}
