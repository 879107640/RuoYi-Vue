package com.ruoyi.web.controller.patent;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.patent.domain.GPatentFavorites;
import com.ruoyi.patent.service.IGPatentFavoritesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;

import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户收藏专利Controller
 * 
 * @author ruoyi
 * @date 2025-06-23
 */
@Anonymous
@RestController
@RequestMapping("/patent/favorites")
@Api(value = "用户收藏专利控制器", tags = {"用户收藏专利管理"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GPatentFavoritesController extends BaseController {
    private final IGPatentFavoritesService gPatentFavoritesService;

    /**
     * 新增用户收藏专利
     */
    @ApiOperation("新增用户收藏专利")
    @Log(title = "用户收藏专利", businessType = BusinessType.INSERT)
    @PostMapping("/operate")
    public AjaxResult add(String patentId,Integer type) {
        GPatentFavorites gPatentFavorites = new GPatentFavorites();
        gPatentFavorites.setPatentId(patentId);
        gPatentFavorites.setUserId(SecurityUtils.getUserId());
        if (type == 1) {
            return toAjax(gPatentFavoritesService.insertByPatentIdAndUserId(gPatentFavorites.getPatentId(), gPatentFavorites.getUserId()));
        } else if (type == 0) {
            return toAjax(gPatentFavoritesService.deleteByPatentIdAndUserId(gPatentFavorites.getPatentId(), gPatentFavorites.getUserId()));
        }
        return error();
    }

}
