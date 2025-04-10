package com.ruoyi.patent.service.impl;

import java.util.List;

import com.ruoyi.patent.domain.GPatenLibraryLineUp;
import com.ruoyi.patent.mapper.GPatenLibraryLineUpMapper;
import com.ruoyi.patent.service.IGPatenLibraryLineUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 专利预约排队Service业务层处理
 *
 * @author ruoyi
 * @date 2025-04-10
 */
@Service
public class GPatenLibraryLineUpServiceImpl implements IGPatenLibraryLineUpService {
  @Autowired
  private GPatenLibraryLineUpMapper gPatenLibraryLineUpMapper;

  /**
   * 查询专利预约排队
   *
   * @param id 专利预约排队主键
   * @return 专利预约排队
   */
  @Override
  public GPatenLibraryLineUp selectGPatenLibraryLineUpById(Long id) {
    return gPatenLibraryLineUpMapper.selectGPatenLibraryLineUpById(id);
  }

  /**
   * 查询专利预约排队列表
   *
   * @param gPatenLibraryLineUp 专利预约排队
   * @return 专利预约排队
   */
  @Override
  public List<GPatenLibraryLineUp> selectGPatenLibraryLineUpList(GPatenLibraryLineUp gPatenLibraryLineUp) {
    return gPatenLibraryLineUpMapper.selectGPatenLibraryLineUpList(gPatenLibraryLineUp);
  }

  /**
   * 新增专利预约排队
   *
   * @param gPatenLibraryLineUp 专利预约排队
   * @return 结果
   */
  @Override
  public int insertGPatenLibraryLineUp(GPatenLibraryLineUp gPatenLibraryLineUp) {
        return gPatenLibraryLineUpMapper.insertGPatenLibraryLineUp(gPatenLibraryLineUp);
  }

  /**
   * 修改专利预约排队
   *
   * @param gPatenLibraryLineUp 专利预约排队
   * @return 结果
   */
  @Override
  public int updateGPatenLibraryLineUp(GPatenLibraryLineUp gPatenLibraryLineUp) {
    return gPatenLibraryLineUpMapper.updateGPatenLibraryLineUp(gPatenLibraryLineUp);
  }

  /**
   * 批量删除专利预约排队
   *
   * @param ids 需要删除的专利预约排队主键
   * @return 结果
   */
  @Override
  public int deleteGPatenLibraryLineUpByIds(Long[] ids) {
    return gPatenLibraryLineUpMapper.deleteGPatenLibraryLineUpByIds(ids);
  }

  /**
   * 删除专利预约排队信息
   *
   * @param id 专利预约排队主键
   * @return 结果
   */
  @Override
  public int deleteGPatenLibraryLineUpById(Long id) {
    return gPatenLibraryLineUpMapper.deleteGPatenLibraryLineUpById(id);
  }
}
