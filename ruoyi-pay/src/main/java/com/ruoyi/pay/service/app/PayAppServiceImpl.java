package com.ruoyi.pay.service.app;

import com.ruoyi.common.core.page.PageResult;
import com.ruoyi.common.enums.CommonStatusEnum;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.pay.convert.app.PayAppConvert;
import com.ruoyi.pay.domain.app.PayAppDO;
import com.ruoyi.pay.mapper.app.PayAppMapper;
import com.ruoyi.pay.service.order.PayOrderService;
import com.ruoyi.pay.service.refund.PayRefundService;
import com.ruoyi.pay.service.vo.PayAppCreateReqVO;
import com.ruoyi.pay.service.vo.PayAppPageReqVO;
import com.ruoyi.pay.service.vo.PayAppUpdateReqVO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;


/**
 * 支付应用 Service 实现类
 *
 * @author aquan
 */
@Service
@Validated
public class PayAppServiceImpl implements PayAppService {

  @Resource
  private PayAppMapper appMapper;

  @Resource
  @Lazy // 延迟加载，避免循环依赖报错
  private PayOrderService orderService;
  @Resource
  @Lazy // 延迟加载，避免循环依赖报错
  private PayRefundService refundService;

  @Override
  public int createApp(PayAppCreateReqVO createReqVO) {
    // 验证 appKey 是否重复
    validateAppKeyUnique(null, createReqVO.getAppKey());

    // 插入
    PayAppDO app = PayAppConvert.INSTANCE.convert(createReqVO);
    appMapper.insert(app);
    // 返回
    return app.getId().intValue();
  }

  @Override
  public void updateApp(PayAppUpdateReqVO updateReqVO) {
    // 校验存在
    validateAppExists(updateReqVO.getId());
    // 验证 appKey 是否重复
    validateAppKeyUnique(updateReqVO.getId(), updateReqVO.getAppKey());

    // 更新
    PayAppDO updateObj = PayAppConvert.INSTANCE.convert(updateReqVO);
    appMapper.updateById(updateObj);
  }

  void validateAppKeyUnique(Long id, String appKey) {
    PayAppDO app = appMapper.selectByAppKey(appKey);
    if (app == null) {
      return;
    }
    // 如果 id 为空，说明不用比较是否为相同 appKey 的应用
    if (id == null) {
      throw new ServiceException("支付应用标识已经存在");
    }
    if (!app.getId().equals(id)) {
      throw new ServiceException("支付应用标识已经存在");
    }
  }

  @Override
  public void updateAppStatus(Long id, Integer status) {
    // 校验商户存在
    validateAppExists(id);
    // 更新状态
    PayAppDO payAppDO = new PayAppDO();
    payAppDO.setId(id);
    payAppDO.setStatus(status);
    appMapper.updateById(payAppDO);
  }

  @Override
  public void deleteApp(Long id) {
    // 校验存在
    validateAppExists(id);
    // 校验关联数据是否存在
    if (orderService.getOrderCountByAppId(id) > 0) {
      throw new ServiceException("支付应用存在支付订单，无法删除");
    }
    if (refundService.getRefundCountByAppId(id) > 0) {
      throw new ServiceException("支付应用存在退款订单，无法删除");
    }

    // 删除
    appMapper.deleteById(id);
  }

  private void validateAppExists(Long id) {
    if (appMapper.selectById(id) == null) {
      throw new ServiceException("App 不存在");
    }
  }

  @Override
  public PayAppDO getApp(Long id) {
    return appMapper.selectById(id);
  }

  @Override
  public List<PayAppDO> getAppList(Collection<Long> ids) {
    return appMapper.selectBatchIds(ids);
  }

  @Override
  public List<PayAppDO> getAppList() {
    return appMapper.selectList();
  }

  @Override
  public PageResult<PayAppDO> getAppPage(PayAppPageReqVO pageReqVO) {
    return appMapper.selectPage(pageReqVO);
  }

  @Override
  public PayAppDO validPayApp(Long appId) {
    PayAppDO app = appMapper.selectById(appId);
    return validatePayApp(app);
  }

  @Override
  public PayAppDO validPayApp(String appKey) {
    PayAppDO app = appMapper.selectByAppKey(appKey);
    return validatePayApp(app);
  }

  /**
   * 校验支付应用实体的有效性：存在 + 开启
   *
   * @param app 待校验的支付应用实体
   * @return 校验通过的支付应用实体
   */
  private PayAppDO validatePayApp(PayAppDO app) {
    // 校验是否存在
    if (app == null) {
      throw new ServiceException("App 不存在");
    }
    // 校验是否禁用
    if (CommonStatusEnum.isDisable(app.getStatus())) {
      throw new ServiceException("App 已经被禁用");
    }
    return app;
  }

}
