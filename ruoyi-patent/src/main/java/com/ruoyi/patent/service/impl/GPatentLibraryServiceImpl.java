package com.ruoyi.patent.service.impl;

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.patent.domain.GPatenLibraryLineUp;
import com.ruoyi.patent.domain.GPatentLibrary;
import com.ruoyi.patent.mapper.GPatenLibraryLineUpMapper;
import com.ruoyi.patent.mapper.GPatentLibraryMapper;
import com.ruoyi.patent.service.IGPatentLibraryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 专利库数据Service业务层处理
 *
 * @author hujch
 * @date 2025-04-10
 */
@Service
public class GPatentLibraryServiceImpl implements IGPatentLibraryService {
  @Resource
  private GPatentLibraryMapper gPatentLibraryMapper;

  @Resource
  GPatenLibraryLineUpMapper libraryLineUpMapper;

  /**
   * 查询专利库数据
   *
   * @param id 专利库数据主键
   * @return 专利库数据
   */
  @Override
  public GPatentLibrary selectGPatentLibraryById(String id) {
    return gPatentLibraryMapper.selectGPatentLibraryById(id);
  }

  /**
   * 查询专利库数据列表
   *
   * @param gPatentLibrary 专利库数据
   * @return 专利库数据
   */
  @Override
  public List<GPatentLibrary> selectGPatentLibraryList(GPatentLibrary gPatentLibrary) {
    return gPatentLibraryMapper.selectGPatentLibraryList(gPatentLibrary);
  }

  /**
   * 新增专利库数据
   *
   * @param gPatentLibrary 专利库数据
   * @return 结果
   */
  @Override
  public int insertGPatentLibrary(GPatentLibrary gPatentLibrary) {
    gPatentLibrary.setCreateTime(DateUtils.getNowDate());
    gPatentLibrary.setId(UUID.randomUUID().toString());
    return gPatentLibraryMapper.insertGPatentLibrary(gPatentLibrary);
  }

  /**
   * 修改专利库数据
   *
   * @param gPatentLibrary 专利库数据
   * @return 结果
   */
  @Override
  public int updateGPatentLibrary(GPatentLibrary gPatentLibrary) {
    gPatentLibrary.setUpdateTime(DateUtils.getNowDate());
    return gPatentLibraryMapper.updateGPatentLibrary(gPatentLibrary);
  }

  /**
   * 批量删除专利库数据
   *
   * @param ids 需要删除的专利库数据主键
   * @return 结果
   */
  @Override
  public int deleteGPatentLibraryByIds(String[] ids) {
    return gPatentLibraryMapper.deleteGPatentLibraryByIds(ids);
  }

  /**
   * 删除专利库数据信息
   *
   * @param id 专利库数据主键
   * @return 结果
   */
  @Override
  public int deleteGPatentLibraryById(String id) {
    return gPatentLibraryMapper.deleteGPatentLibraryById(id);
  }

  @Override
  public String importGPatentLibrary(List<GPatentLibrary> gPatentLibraries, boolean updateSupport) {
    for (GPatentLibrary gPatentLibrary : gPatentLibraries) {
      GPatentLibrary oldGPatentLibrary = gPatentLibraryMapper.selectGPatentLibraryByNo(gPatentLibrary.getPatentNo());
      if (Objects.nonNull(oldGPatentLibrary) && updateSupport) {
        gPatentLibrary.setId(oldGPatentLibrary.getId());
        gPatentLibraryMapper.updateGPatentLibrary(gPatentLibrary);
        continue;
      }
      gPatentLibraryMapper.insertGPatentLibrary(gPatentLibrary);
    }

    return "导入成功";
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void reserve(String id, LoginUser loginUser) {
    GPatentLibrary gPatentLibrary = gPatentLibraryMapper.selectGPatentLibraryById(id);

    if (Objects.isNull(gPatentLibrary)) {
      throw new ServiceException("专利不存在");
    }

    if (gPatentLibrary.getStatusKey().equals("2") && !Objects.equals(gPatentLibrary.getReserveUserId(), loginUser.getUserId())) {
      throw new ServiceException("当前专利已被预约，请点击排队预约");
    }

    gPatentLibrary.setStatusKey("2");
    gPatentLibrary.setReserveUserId(loginUser.getUserId());
    gPatentLibraryMapper.updateGPatentLibrary(gPatentLibrary);
  }

  @Override
  public void cancelReserve(String id, LoginUser loginUser) {
    GPatentLibrary gPatentLibrary = this.selectGPatentLibraryById(id);
    if (Objects.isNull(gPatentLibrary)) {
      throw new ServiceException("专利不存在");
    }
    if (!gPatentLibrary.getStatusKey().equals("2")) {
      throw new ServiceException("当前状态不能被取消");
    }

    if (gPatentLibrary.getStatusKey().equals("2") && !Objects.equals(gPatentLibrary.getReserveUserId(), loginUser.getUserId())) {
      throw new ServiceException("无权取消当前专利预约");
    }

    gPatentLibrary.setStatusKey("1");
    gPatentLibrary.setReserveUserId(null);
    gPatentLibraryMapper.updateGPatentLibrary(gPatentLibrary);
  }

  @Override
  public void lineUpReserve(String id, LoginUser loginUser) {
    GPatentLibrary gPatentLibrary = this.selectGPatentLibraryById(id);
    if (Objects.isNull(gPatentLibrary)) {
      throw new ServiceException("专利不存在");
    }
    if (gPatentLibrary.getStatusKey().equals("1")) {
      throw new ServiceException("当前专利不需要预约排队");
    }

    GPatenLibraryLineUp libraryLineUp = new GPatenLibraryLineUp();
    libraryLineUp.setgPatentId(id);
    List<GPatenLibraryLineUp> gPatenLibraryLineUps = libraryLineUpMapper.selectGPatenLibraryLineUpList(libraryLineUp);
    for (GPatenLibraryLineUp gPatenLibraryLineUp : gPatenLibraryLineUps) {
      if (gPatenLibraryLineUp.getUserId().equals(loginUser.getUserId())) {
        throw new ServiceException("当前专利不允许重复排队");
      }
    }
    GPatenLibraryLineUp insert = new GPatenLibraryLineUp();
    insert.setUserId(loginUser.getUserId());
    insert.setCreatedTime(new Date());
    insert.setgPatentId(id);
    insert.setLineUpNum((long) (gPatenLibraryLineUps.size() + 1));
    libraryLineUpMapper.insertGPatenLibraryLineUp(insert);

  }

  @Override
  public void cancelLineUpReserve(String id, LoginUser loginUser) {
    GPatenLibraryLineUp libraryLineUp = new GPatenLibraryLineUp();
    libraryLineUp.setgPatentId(id);
    libraryLineUp.setUserId(loginUser.getUserId());
    List<GPatenLibraryLineUp> gPatenLibraryLineUps = libraryLineUpMapper.selectGPatenLibraryLineUpList(libraryLineUp);
    if (gPatenLibraryLineUps.isEmpty()) {
      throw new ServiceException("Queue record not found");
    }

    int i = libraryLineUpMapper.deleteByUserAndPatent(id, loginUser.getUserId());

    if (i > 0) {
      libraryLineUpMapper.decrementQueueNumber(id, gPatenLibraryLineUps.get(0).getLineUpNum());
    }
  }
}
