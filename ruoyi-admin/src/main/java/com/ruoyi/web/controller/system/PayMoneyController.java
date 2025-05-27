package com.ruoyi.web.controller.system;


import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pay.service.order.PayMoneyService;
import com.ruoyi.pay.service.vo.PayMoneyBaseReqVO;
import com.ruoyi.pay.service.vo.PayMoneyUpdateReqVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "管理后台 - 支付金额配置")
@RestController
@RequestMapping("/system/pay/money")
@Validated
@RequiredArgsConstructor
public class PayMoneyController extends BaseController {

  private final PayMoneyService payMoneyService;

  /**
   * 根据参数编号获取详细信息
   */
  @PreAuthorize("@ss.hasPermi('system:money:query')")
  @GetMapping
  public AjaxResult getInfo() {
    return success(payMoneyService.getMoney());
  }

  /**
   * 根据参数编号获取详细信息
   */
  @PreAuthorize("@ss.hasPermi('system:money:add')")
  @PostMapping
  public AjaxResult insert(@Validated @RequestBody PayMoneyBaseReqVO reqVO) {
    return success(payMoneyService.insertConfig(reqVO));
  }

  /**
   * 根据参数编号获取详细信息
   */
  @PreAuthorize("@ss.hasPermi('system:money:update')")
  @PutMapping
  public AjaxResult update(@Validated @RequestBody PayMoneyUpdateReqVO reqVO) {
    return success(payMoneyService.updateConfig(reqVO));
  }
}
