package com.ruoyi.patent.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.patent.domain.GPurchaseRequest;
import com.ruoyi.patent.service.IGPurchaseRequestService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 求购信息Controller
 *
 * @author ruoyi
 * @date 2025-04-11
 */
@Anonymous
@RestController
@RequestMapping("/patent/purchase")
public class GPurchaseRequestController extends BaseController {
    @Autowired
    private IGPurchaseRequestService gPurchaseRequestService;

    /**
     * 查询求购信息列表
     */
    @GetMapping("/list")
    public TableDataInfo list(GPurchaseRequest gPurchaseRequest) {
        startPage();
        List<GPurchaseRequest> list = gPurchaseRequestService.selectGPurchaseRequestList(gPurchaseRequest);
        return getDataTable(list);
    }

    /**
     * 导出求购信息列表
     */
    @Log(title = "求购信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GPurchaseRequest gPurchaseRequest) {
        List<GPurchaseRequest> list = gPurchaseRequestService.selectGPurchaseRequestList(gPurchaseRequest);
        ExcelUtil<GPurchaseRequest> util = new ExcelUtil<GPurchaseRequest>(GPurchaseRequest.class);
        util.exportExcel(response, list, "求购信息数据");
    }

    /**
     * 获取求购信息详细信息
     */
    @GetMapping(value = "/info")
    public AjaxResult getInfo(String id) {
        return success(gPurchaseRequestService.selectGPurchaseRequestById(id));
    }

    /**
     * 新增求购信息
     */
    @Log(title = "求购信息", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody GPurchaseRequest gPurchaseRequest) {
        LoginUser user = this.getLoginUser();
        if (StringUtils.isNotEmpty(gPurchaseRequest.getCreateBy()) && user != null) {
            gPurchaseRequest.setCreateBy(String.valueOf(user.getUserId()));
        }
        return toAjax(gPurchaseRequestService.insertGPurchaseRequest(gPurchaseRequest));
    }

    /**
     * 修改求购信息
     */
    @Log(title = "求购信息", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    public AjaxResult edit(@RequestBody GPurchaseRequest gPurchaseRequest) {
        return toAjax(gPurchaseRequestService.updateGPurchaseRequest(gPurchaseRequest));
    }

    /**
     * 删除求购信息
     */
    @Log(title = "求购信息", businessType = BusinessType.DELETE)
    @GetMapping("/delete")
    public AjaxResult remove(String[] ids) {
        return toAjax(gPurchaseRequestService.deleteGPurchaseRequestByIds(ids));
    }
}
