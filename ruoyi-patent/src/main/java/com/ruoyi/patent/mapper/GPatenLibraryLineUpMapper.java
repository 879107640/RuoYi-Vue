package com.ruoyi.patent.mapper;


import com.ruoyi.patent.domain.GPatenLibraryLineUp;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 专利预约排队Mapper接口
 *
 * @author ruoyi
 * @date 2025-04-10
 */
public interface GPatenLibraryLineUpMapper {
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
   * 删除专利预约排队
   *
   * @param id 专利预约排队主键
   * @return 结果
   */
  public int deleteGPatenLibraryLineUpById(Long id);

  /**
   * 批量删除专利预约排队
   *
   * @param ids 需要删除的数据主键集合
   * @return 结果
   */
  public int deleteGPatenLibraryLineUpByIds(Long[] ids);
  /**
   * 更新后续排队号减1
   *
   * @param patentId 专利id
   * @return 结果
   */
  int decrementQueueNumber(@Param("patentId") String patentId, @Param("lineUpNum") Long lineUpNum);

  /**
   * 删除指定用户和专利的排队记录
   * @param patentId 专利id
   */
  int deleteByUserAndPatent(@Param("patentId") String patentId, @Param("userId") Long userId);

  @Select("select * from g_paten_library_line_up where g_paten_library_line_up.g_patent_id = #{patentId}")
  List<GPatenLibraryLineUp> getOneByGPatentIdAndUserId(@Param("patentId") String patentId);
}
