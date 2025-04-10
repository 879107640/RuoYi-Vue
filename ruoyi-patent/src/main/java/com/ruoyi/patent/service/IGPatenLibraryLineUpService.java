package com.ruoyi.patent.service;

import com.ruoyi.patent.domain.GPatenLibraryLineUp;

import java.util.List;


/**
 * 专利预约排队Service接口
 *
 * @author ruoyi
 * @date 2025-04-10
 */
public interface IGPatenLibraryLineUpService {
  /**
   * 查询专利预约排队
   *
   * @param id 专利预约排队主键
   * @return 专利预约排队
   */
  public GPatenLibraryLineUp selectGPatenLibraryLineUpById(Long id);

  /**
   * 查询专利预约排队列表
   *
   * @param gPatenLibraryLineUp 专利预约排队
   * @return 专利预约排队集合
   */
  public List<GPatenLibraryLineUp> selectGPatenLibraryLineUpList(GPatenLibraryLineUp gPatenLibraryLineUp);

  /**
   * 新增专利预约排队
   *
   * @param gPatenLibraryLineUp 专利预约排队
   * @return 结果
   */
  public int insertGPatenLibraryLineUp(GPatenLibraryLineUp gPatenLibraryLineUp);

  /**
   * 修改专利预约排队
   *
   * @param gPatenLibraryLineUp 专利预约排队
   * @return 结果
   */
  public int updateGPatenLibraryLineUp(GPatenLibraryLineUp gPatenLibraryLineUp);

  /**
   * 批量删除专利预约排队
   *
   * @param ids 需要删除的专利预约排队主键集合
   * @return 结果
   */
  public int deleteGPatenLibraryLineUpByIds(Long[] ids);

  /**
   * 删除专利预约排队信息
   *
   * @param id 专利预约排队主键
   * @return 结果
   */
  public int deleteGPatenLibraryLineUpById(Long id);
}
