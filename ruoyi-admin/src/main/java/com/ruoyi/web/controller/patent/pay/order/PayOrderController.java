package com.ruoyi.web.controller.patent.pay.order;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Maps;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.PageResult;
import com.ruoyi.common.enums.order.PayOrderStatusEnum;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.common.utils.object.BeanUtils;
import com.ruoyi.pay.config.core.enums.channel.PayChannelEnum;
import com.ruoyi.pay.convert.order.PayOrderConvert;
import com.ruoyi.pay.domain.app.PayAppDO;
import com.ruoyi.pay.domain.order.PayOrderDO;
import com.ruoyi.pay.domain.order.PayOrderExtensionDO;
import com.ruoyi.pay.framework.pay.core.WalletPayClient;
import com.ruoyi.pay.service.app.PayAppService;
import com.ruoyi.pay.service.dto.notify.dto.PayOrderNotifyReqDTO;
import com.ruoyi.pay.service.order.PayOrderService;
import com.ruoyi.pay.service.vo.order.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.util.Map;
import java.util.Objects;

import static com.ruoyi.common.utils.collection.CollectionUtils.convertList;


@Tag(name = "管理后台 - 支付订单")
@RestController
@RequestMapping("/pay/order")
@Validated
public class PayOrderController extends BaseController {

  @Resource
  private PayOrderService orderService;
  @Resource
  private PayAppService appService;

  @GetMapping("/get")
  @Operation(summary = "获得支付订单")
  @Parameters({
      @Parameter(name = "id", description = "编号", required = true, example = "1024"),
      @Parameter(name = "sync", description = "是否同步", example = "true")
  })
  @PreAuthorize("@ss.hasPermi('pay:order:query')")
  public AjaxResult getOrder(@RequestParam("id") Long id,
                             @RequestParam(value = "sync", required = false) Boolean sync) {
    PayOrderDO order = orderService.getOrder(id);
    // sync 仅在等待支付
    if (Boolean.TRUE.equals(sync) && PayOrderStatusEnum.isWaiting(order.getStatus())) {
      orderService.syncOrderQuietly(order.getId());
      // 重新查询，因为同步后，可能会有变化
      order = orderService.getOrder(id);
    }
    return success(BeanUtils.toBean(order, PayOrderRespVO.class));
  }

  @GetMapping("/get-detail")
  @Operation(summary = "获得支付订单详情")
  @Parameter(name = "id", description = "编号", required = true, example = "1024")
  @PreAuthorize("@ss.hasPermi('pay:order:query')")
  public AjaxResult getOrderDetail(@RequestParam("id") Long id) {
    PayOrderDO order = orderService.getOrder(id);
    if (order == null) {
      return success(null);
    }

    // 拼接返回
    PayAppDO app = appService.getApp(order.getAppId());
    PayOrderExtensionDO orderExtension = orderService.getOrderExtension(order.getExtensionId());
    return success(PayOrderConvert.INSTANCE.convert(order, orderExtension, app));
  }

  @PostMapping("/submit")
  @Operation(summary = "提交支付订单")
  public AjaxResult submitPayOrder(@RequestBody PayOrderSubmitReqVO reqVO) {
    // 1. 钱包支付事，需要额外传 user_id 和 user_type
    if (Objects.equals(reqVO.getChannelCode(), PayChannelEnum.WALLET.getCode())) {
      Map<String, String> channelExtras = reqVO.getChannelExtras() == null ?
          Maps.newHashMapWithExpectedSize(2) : reqVO.getChannelExtras();
      channelExtras.put(WalletPayClient.USER_ID_KEY, String.valueOf(getLoginUser().getUserId()));
//            channelExtras.put(WalletPayClient.USER_TYPE_KEY, String.valueOf(getLoginUserType()));
      reqVO.setChannelExtras(channelExtras);
    }

    // 2. 提交支付
    PayOrderSubmitRespVO respVO = orderService.submitOrder(reqVO, IpUtils.getIpAddr());
    return success(respVO);
  }

  @GetMapping("/page")
  @Operation(summary = "获得支付订单分页")
  @PreAuthorize("@ss.hasPermi('pay:order:query')")
  public AjaxResult getOrderPage(@Valid PayOrderPageReqVO pageVO) {
    PageResult<PayOrderDO> pageResult = orderService.getOrderPage(pageVO);
    if (CollectionUtil.isEmpty(pageResult.getList())) {
      return success(new PageResult<>(pageResult.getTotal()));
    }

    // 拼接返回
    Map<Long, PayAppDO> appMap = appService.getAppMap(convertList(pageResult.getList(), PayOrderDO::getAppId));
    return success(PayOrderConvert.INSTANCE.convertPage(pageResult, appMap));
  }

  @PostMapping("/create")
  @Operation(summary = "创建订单")
  public AjaxResult createDemoOrder(@Valid @RequestBody PayOrderCreateReqVO createReqVO) {
    return success(orderService.createOrder(getUserId(), createReqVO));
  }


  @PostMapping("/update-paid")
  @Operation(summary = "更新示例订单为已支付") // 由 pay-module 支付服务，进行回调，可见 PayNotifyJob
  @PermitAll // 无需登录，安全由 PayDemoOrderService 内部校验实现
  public AjaxResult updateOrderPaid(@RequestBody PayOrderNotifyReqDTO notifyReqDTO) {
    orderService.updateOrderPaid(Long.valueOf(notifyReqDTO.getMerchantOrderId()),
        notifyReqDTO.getPayOrderId());
    return success(true);
  }
}
