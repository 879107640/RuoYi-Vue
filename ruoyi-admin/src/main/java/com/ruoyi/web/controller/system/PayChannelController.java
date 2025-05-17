package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pay.convert.channel.PayChannelConvert;
import com.ruoyi.pay.domain.channel.PayChannelDO;
import com.ruoyi.pay.service.channel.PayChannelService;
import com.ruoyi.pay.service.vo.PayChannelCreateReqVO;
import com.ruoyi.pay.service.vo.PayChannelUpdateReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static com.ruoyi.common.utils.collection.CollectionUtils.convertSet;


@Tag(name = "管理后台 - 支付渠道")
@RestController
@RequestMapping("/system/pay/channel")
@Validated
public class PayChannelController extends BaseController {

    @Resource
    private PayChannelService channelService;

    @PostMapping("/create")
    @Operation(summary = "创建支付渠道 ")
    @PreAuthorize("@ss.hasPermi('pay:channel:create')")
    public AjaxResult createChannel(@Valid @RequestBody PayChannelCreateReqVO createReqVO) {
        return toAjax(channelService.createChannel(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新支付渠道 ")
    @PreAuthorize("@ss.hasPermi('pay:channel:update')")
    public AjaxResult updateChannel(@Valid @RequestBody PayChannelUpdateReqVO updateReqVO) {
        channelService.updateChannel(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除支付渠道 ")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermi('pay:channel:delete')")
    public AjaxResult deleteChannel(@RequestParam("id") Long id) {
        channelService.deleteChannel(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得支付渠道")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermi('pay:channel:query')")
    public AjaxResult getChannel(@RequestParam(value = "id", required = false) Long id,
                                                     @RequestParam(value = "appId", required = false) Long appId,
                                                     @RequestParam(value = "code", required = false) String code) {
        PayChannelDO channel = null;
        if (id != null) {
            channel = channelService.getChannel(id);
        } else if (appId != null && code != null) {
            channel = channelService.getChannelByAppIdAndCode(appId, code);
        }
        return success(PayChannelConvert.INSTANCE.convert(channel));
    }

    @GetMapping("/get-enable-code-list")
    @Operation(summary = "获得指定应用的开启的支付渠道编码列表")
    @Parameter(name = "appId", description = "应用编号", required = true, example = "1")
    public AjaxResult getEnableChannelCodeList(@RequestParam("appId") Long appId) {
        List<PayChannelDO> channels = channelService.getEnableChannelList(appId);
        return success(convertSet(channels, PayChannelDO::getCode));
    }

}
