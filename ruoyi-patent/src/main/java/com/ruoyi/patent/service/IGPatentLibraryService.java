package com.ruoyi.patent.service;

import java.util.List;

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.patent.domain.GPatentLibrary;

/**
 * 专利库数据Service接口
 *
 * @author hujch
 * @date 2025-04-10
 */
public interface IGPatentLibraryService {
  /**
   * 查询专利库数据
   *
   * @param id 专利库数据主键
   * @return 专利库数据
   */
  public GPatentLibrary selectGPatentLibraryById(Long id);

  /**
   * 查询专利库数据列表
   *
   * @param gPatentLibrary 专利库数据
   * @return 专利库数据集合
   */
  public List<GPatentLibrary> selectGPatentLibraryList(GPatentLibrary gPatentLibrary);

  /**
   * 新增专利库数据
   *
   * @param gPatentLibrary 专利库数据
   * @return 结果
   */
  public int insertGPatentLibrary(GPatentLibrary gPatentLibrary);

  /**
   * 修改专利库数据
   *
   * @param gPatentLibrary 专利库数据
   * @return 结果
   */
  public int updateGPatentLibrary(GPatentLibrary gPatentLibrary);

  /**
   * 批量删除专利库数据
   *
   * @param ids 需要删除的专利库数据主键集合
   * @return 结果
   */
  public int deleteGPatentLibraryByIds(Long[] ids);

  /**
   * 删除专利库数据信息
   *
   * @param id 专利库数据主键
   * @return 结果
   */
  public int deleteGPatentLibraryById(Long id);

  String importGPatentLibrary(List<GPatentLibrary> gPatentLibraries, boolean updateSupport);

  void reserve(String id, LoginUser loginUser);

}
