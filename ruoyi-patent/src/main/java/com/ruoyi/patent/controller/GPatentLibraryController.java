package com.ruoyi.patent.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.ruoyi.patent.domain.GPatentLibrary;
import com.ruoyi.patent.service.IGPatentLibraryService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 专利库数据Controller
 * 
 * @author hujch
 * @date 2025-04-10
 */
@RestController
@RequestMapping("/patent/library")
public class GPatentLibraryController extends BaseController {

    @Autowired
    private IGPatentLibraryService gPatentLibraryService;

    /**
     * 查询专利库数据列表
     */
    /*@PreAuthorize("@ss.hasPermi('patent:library:list')")*/
    @GetMapping("/list")
    public TableDataInfo list(GPatentLibrary gPatentLibrary,int type) {
        startPage();
        List<GPatentLibrary> list = gPatentLibraryService.selectGPatentLibraryList(gPatentLibrary,type);
        return getDataTable(list);
    }

    /**
     * 导出专利库数据列表
     */
    /*@PreAuthorize("@ss.hasPermi('patent:library:export')")*/
    @Log(title = "专利库数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GPatentLibrary gPatentLibrary,int type) {
        List<GPatentLibrary> list = gPatentLibraryService.selectGPatentLibraryList(gPatentLibrary,type);
        ExcelUtil<GPatentLibrary> util = new ExcelUtil<GPatentLibrary>(GPatentLibrary.class);
        util.exportExcel(response, list, "专利库数据数据");
    }

    /**
     * 获取专利库数据详细信息
     */
    /*@PreAuthorize("@ss.hasPermi('patent:library:query')")*/
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return success(gPatentLibraryService.selectGPatentLibraryById(id));
    }

    /**
     * 新增专利库数据
     */
    /*@PreAuthorize("@ss.hasPermi('patent:library:add')")*/
    @Log(title = "专利库数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GPatentLibrary gPatentLibrary) {
        return toAjax(gPatentLibraryService.insertGPatentLibrary(gPatentLibrary));
    }

    /**
     * 修改专利库数据
     */
    /*@PreAuthorize("@ss.hasPermi('patent:library:edit')")*/
    @Log(title = "专利库数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GPatentLibrary gPatentLibrary) {
        return toAjax(gPatentLibraryService.updateGPatentLibrary(gPatentLibrary));
    }

    /**
     * 删除专利库数据
     */
    /*@PreAuthorize("@ss.hasPermi('patent:library:remove')")*/
    @Log(title = "专利库数据", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        return toAjax(gPatentLibraryService.deleteGPatentLibraryByIds(ids));
    }


}
