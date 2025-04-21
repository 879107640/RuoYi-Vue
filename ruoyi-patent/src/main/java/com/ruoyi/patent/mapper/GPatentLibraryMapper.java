package com.ruoyi.patent.mapper;

import java.util.List;

import com.ruoyi.patent.domain.GPatentLibrary;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 专利库数据Mapper接口
 *
 * @author hujch
 * @date 2025-04-10
 */
public interface GPatentLibraryMapper {
  /**
   * 查询专利库数据
   *
   * @param id 专利库数据主键
   * @return 专利库数据
   */
  public GPatentLibrary selectGPatentLibraryById(String id);

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
   * 删除专利库数据
   *
   * @param id 专利库数据主键
   * @return 结果
   */
  public int deleteGPatentLibraryById(String id);

  /**
   * 批量删除专利库数据
   *
   * @param ids 需要删除的数据主键集合
   * @return 结果
   */
  public int deleteGPatentLibraryByIds(String[] ids);

  GPatentLibrary selectGPatentLibraryByNo(@Param("patentNo") String patentNo);

  @Delete("delete from g_patent_library where create_by = #{username} and status_key != 2")
  void allDelete(String username);

  @Update("update g_patent_library set status_key = 1, booker_key = null, booker_value = null, booker_time = null, deadline = null where id = #{id}")
  void cancelReserve(String id);

}
