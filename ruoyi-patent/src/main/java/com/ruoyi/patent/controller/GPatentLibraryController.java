package com.ruoyi.patent.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.entity.SysUser;
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
import com.ruoyi.patent.domain.GPatentLibrary;
import com.ruoyi.patent.service.IGPatentLibraryService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 专利库数据Controller
 *
 * @author hujch
 * @date 2025-04-10
 */
@Anonymous
@RestController
@RequestMapping("/patent/library")
public class GPatentLibraryController extends BaseController {

  @Autowired
  private IGPatentLibraryService gPatentLibraryService;

  /**
   * 查询专利库数据列表
   */
  @GetMapping("/list")
  public TableDataInfo list(GPatentLibrary gPatentLibrary) {
    startPage();
    List<GPatentLibrary> list = gPatentLibraryService.selectGPatentLibraryList(gPatentLibrary);
    return getDataTable(list);
  }

  /**
   * 导出专利库数据列表
   */
  @Log(title = "专利库数据", businessType = BusinessType.EXPORT)
  @PostMapping("/export")
  public void export(HttpServletResponse response, GPatentLibrary gPatentLibrary) {
    List<GPatentLibrary> list = gPatentLibraryService.selectGPatentLibraryList(gPatentLibrary);
    ExcelUtil<GPatentLibrary> util = new ExcelUtil<GPatentLibrary>(GPatentLibrary.class);
    util.exportExcel(response, list, "专利库数据数据");
  }

  /**
   * 获取专利库数据详细信息
   */
  @GetMapping(value = "/info")
  public AjaxResult getInfo(String id) {
    return success(gPatentLibraryService.selectGPatentLibraryById(id));
  }

  /**
   * 新增专利库数据
   */
  @Log(title = "专利库数据", businessType = BusinessType.INSERT)
  @PostMapping("/add")
  public AjaxResult add(@RequestBody GPatentLibrary gPatentLibrary) {
    LoginUser user = this.getLoginUser();
    if (StringUtils.isNotEmpty(gPatentLibrary.getCreateBy()) && user != null) {
      gPatentLibrary.setCreateBy(String.valueOf(user.getUserId()));
    }
    return toAjax(gPatentLibraryService.insertGPatentLibrary(gPatentLibrary));
  }

  /**
   * 修改专利库数据
   */
  @Log(title = "专利库数据", businessType = BusinessType.UPDATE)
  @PostMapping("/update")
  public AjaxResult edit(@RequestBody GPatentLibrary gPatentLibrary) {
    return toAjax(gPatentLibraryService.updateGPatentLibrary(gPatentLibrary));
  }

  /**
   * 删除专利库数据
   */
  @Log(title = "专利库数据", businessType = BusinessType.DELETE)
  @GetMapping("/delete")
  public AjaxResult remove(String[] ids) {
    return toAjax(gPatentLibraryService.deleteGPatentLibraryByIds(ids));
  }

  @Log(title = "专利导入", businessType = BusinessType.IMPORT)
  @PostMapping("/importData")
  public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
    ExcelUtil<GPatentLibrary> util = new ExcelUtil<>(GPatentLibrary.class);
    List<GPatentLibrary> gPatentLibraries = util.importExcel(file.getInputStream());
    String message = gPatentLibraryService.importGPatentLibrary(gPatentLibraries, updateSupport);
    return success(message);
  }

  @PostMapping("/importTemplate")
  public void importTemplate(HttpServletResponse response) {
    ExcelUtil<GPatentLibrary> util = new ExcelUtil<>(GPatentLibrary.class);
    util.importTemplateExcel(response, "专利模版");
  }

  /**
   * 预定
   * @param id
   */
  @PutMapping("/reserve/{id}")
  public AjaxResult reserve(@PathVariable("id") String id) {
    LoginUser loginUser = getLoginUser();
    gPatentLibraryService.reserve(id, loginUser);
    return success();
  }

  /**
   * 取消预定
   * @param id
   */
  @PutMapping("/cancel-reserve/{id}")
  public AjaxResult cancelReserve(@PathVariable("id") String id) {
    LoginUser loginUser = getLoginUser();
    gPatentLibraryService.cancelReserve(id, loginUser);
    return success();
  }

  /**
   * 预定排队
   * @param id
   */
  @PutMapping("/line-up-reserve/{id}")
  public AjaxResult lineUpReserve(@PathVariable("id") String id) {
    LoginUser loginUser = getLoginUser();
    gPatentLibraryService.lineUpReserve(id, loginUser);
    return success();
  }

  /**
   * 取消排队
   * @param id
   */
  @PutMapping("/cancel-line-up-reserve/{id}")
  public AjaxResult cancelLineUpReserve(@PathVariable("id") String id) {
    LoginUser loginUser = getLoginUser();
    gPatentLibraryService.cancelLineUpReserve(id, loginUser);
    return success();
  }
}
