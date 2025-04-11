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
import com.ruoyi.patent.domain.GPurchaseRecommend;
import com.ruoyi.patent.service.IGPurchaseRecommendService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 推荐信息Controller
 *
 * @author ruoyi
 * @date 2025-04-11
 */
@Anonymous
@RestController
@RequestMapping("/patent/recommend")
public class GPurchaseRecommendController extends BaseController {
    @Autowired
    private IGPurchaseRecommendService gPurchaseRecommendService;

    /**
     * 查询推荐信息列表
     */
    @GetMapping("/list")
    public TableDataInfo list(GPurchaseRecommend gPurchaseRecommend) {
        startPage();
        List<GPurchaseRecommend> list = gPurchaseRecommendService.selectGPurchaseRecommendList(gPurchaseRecommend);
        return getDataTable(list);
    }

    /**
     * 导出推荐信息列表
     */
    @Log(title = "推荐信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GPurchaseRecommend gPurchaseRecommend) {
        List<GPurchaseRecommend> list = gPurchaseRecommendService.selectGPurchaseRecommendList(gPurchaseRecommend);
        ExcelUtil<GPurchaseRecommend> util = new ExcelUtil<GPurchaseRecommend>(GPurchaseRecommend.class);
        util.exportExcel(response, list, "推荐信息数据");
    }

    /**
     * 获取推荐信息详细信息
     */
    @GetMapping(value = "/info")
    public AjaxResult getInfo(String id) {
        return success(gPurchaseRecommendService.selectGPurchaseRecommendById(id));
    }

    /**
     * 新增推荐信息
     */
    @Log(title = "推荐信息", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody GPurchaseRecommend gPurchaseRecommend) {
        LoginUser user = this.getLoginUser();
        if (StringUtils.isNotEmpty(gPurchaseRecommend.getCreateBy()) && user != null) {
            gPurchaseRecommend.setCreateBy(String.valueOf(user.getUserId()));
        }
        return toAjax(gPurchaseRecommendService.insertGPurchaseRecommend(gPurchaseRecommend));
    }

    /**
     * 修改推荐信息
     */
    @Log(title = "推荐信息", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    public AjaxResult edit(@RequestBody GPurchaseRecommend gPurchaseRecommend) {
        return toAjax(gPurchaseRecommendService.updateGPurchaseRecommend(gPurchaseRecommend));
    }

    /**
     * 删除推荐信息
     */
    @Log(title = "推荐信息", businessType = BusinessType.DELETE)
    @GetMapping("/delete")
    public AjaxResult remove(@PathVariable String[] ids) {
        return toAjax(gPurchaseRecommendService.deleteGPurchaseRecommendByIds(ids));
    }
}
