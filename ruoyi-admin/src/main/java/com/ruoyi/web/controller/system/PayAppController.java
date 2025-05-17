package com.ruoyi.web.controller.system;

import cn.hutool.core.collection.CollUtil;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.PageResult;
import com.ruoyi.common.enums.CommonStatusEnum;
import com.ruoyi.pay.convert.app.PayAppConvert;
import com.ruoyi.pay.domain.app.PayAppDO;
import com.ruoyi.pay.domain.channel.PayChannelDO;
import com.ruoyi.pay.service.app.PayAppService;
import com.ruoyi.pay.service.channel.PayChannelService;
import com.ruoyi.pay.service.vo.PayAppCreateReqVO;
import com.ruoyi.pay.service.vo.PayAppPageReqVO;
import com.ruoyi.pay.service.vo.PayAppUpdateReqVO;
import com.ruoyi.pay.service.vo.PayAppUpdateStatusReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static com.ruoyi.common.utils.collection.CollectionUtils.convertList;

@Slf4j
@Tag(name = "管理后台 - 支付应用信息")
@RestController
@RequestMapping("/system/pay/app")
@Validated
public class PayAppController extends BaseController {

  @Resource
  private PayAppService appService;
  @Resource
  private PayChannelService channelService;

  @PostMapping("/create")
  @Operation(summary = "创建支付应用信息")
  @PreAuthorize("@ss.hasPermi('pay:app:create')")
  public AjaxResult createApp(@Valid @RequestBody PayAppCreateReqVO createReqVO) {
    return toAjax(appService.createApp(createReqVO));
  }

  @PutMapping("/update")
  @Operation(summary = "更新支付应用信息")
  @PreAuthorize("@ss.hasPermi('pay:app:update')")
  public AjaxResult updateApp(@Valid @RequestBody PayAppUpdateReqVO updateReqVO) {
    appService.updateApp(updateReqVO);
    return success(true);
  }

  @PutMapping("/update-status")
  @Operation(summary = "更新支付应用状态")
  @PreAuthorize("@ss.hasPermi('pay:app:update')")
  public AjaxResult updateAppStatus(@Valid @RequestBody PayAppUpdateStatusReqVO updateReqVO) {
    appService.updateAppStatus(updateReqVO.getId(), updateReqVO.getStatus());
    return success(true);
  }

  @DeleteMapping("/delete")
  @Operation(summary = "删除支付应用信息")
  @Parameter(name = "id", description = "编号", required = true)
  @PreAuthorize("@ss.hasPermi('pay:app:delete')")
  public AjaxResult deleteApp(@RequestParam("id") Long id) {
    appService.deleteApp(id);
    return success(true);
  }

  @GetMapping("/get")
  @Operation(summary = "获得支付应用信息")
  @Parameter(name = "id", description = "编号", required = true, example = "1024")
  @PreAuthorize("@ss.hasPermi('pay:app:query')")
  public AjaxResult getApp(@RequestParam("id") Long id) {
    PayAppDO app = appService.getApp(id);
    return success(PayAppConvert.INSTANCE.convert(app));
  }

  @GetMapping("/page")
  @Operation(summary = "获得支付应用信息分页")
  @PreAuthorize("@ss.hasPermi('pay:app:query')")
  public AjaxResult getAppPage(@Valid PayAppPageReqVO pageVO) {
    // 得到应用分页列表
    PageResult<PayAppDO> pageResult = appService.getAppPage(pageVO);
    if (CollUtil.isEmpty(pageResult.getList())) {
      return success(PageResult.empty());
    }

    // 得到所有的应用编号，查出所有的渠道，并移除未启用的渠道
    List<PayChannelDO> channels = channelService.getChannelListByAppIds(
        convertList(pageResult.getList(), PayAppDO::getId));
    channels.removeIf(channel -> !CommonStatusEnum.ENABLE.getStatus().equals(channel.getStatus()));

    // 拼接后返回
    return success(PayAppConvert.INSTANCE.convertPage(pageResult, channels));
  }

  @GetMapping("/list")
  @Operation(summary = "获得应用列表")
  @PreAuthorize("@ss.hasPermi('pay:merchant:query')")
  public AjaxResult getAppList() {
    List<PayAppDO> appListDO = appService.getAppList();
    return success(PayAppConvert.INSTANCE.convertList(appListDO));
  }

}
