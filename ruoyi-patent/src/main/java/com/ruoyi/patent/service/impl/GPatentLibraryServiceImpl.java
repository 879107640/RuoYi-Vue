package com.ruoyi.patent.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.config.SnowFlakeIdConfig;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.patent.domain.GPatenLibraryLineUp;
import com.ruoyi.patent.domain.GPatentLibrary;
import com.ruoyi.patent.mapper.GPatenLibraryLineUpMapper;
import com.ruoyi.patent.mapper.GPatentLibraryMapper;
import com.ruoyi.patent.service.IGPatentLibraryService;
import com.ruoyi.patent.service.vo.GPatentLibrarySaveVo;
import com.ruoyi.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 专利库数据Service业务层处理
 *
 * @author hujch
 */
@Service
public class GPatentLibraryServiceImpl implements IGPatentLibraryService {
  @Resource
  private GPatentLibraryMapper gPatentLibraryMapper;

  @Resource
  GPatenLibraryLineUpMapper libraryLineUpMapper;

  @Resource
  SysUserMapper userMapper;
  @Resource
  SnowFlakeIdConfig snowFlakeIdConfig;

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
    if (StrUtil.isNotEmpty(gPatentLibrary.getCreateBy()) && Objects.nonNull(gPatentLibrary.getBookerKey())) {
      gPatentLibrary.setBookerKey(null);
      gPatentLibrary.setForMeBooker(1);
    }
    List<GPatentLibrary> gPatentLibraries = gPatentLibraryMapper.selectGPatentLibraryList(gPatentLibrary);
    for (GPatentLibrary patentLibrary : gPatentLibraries) {
      GPatenLibraryLineUp gPatenLibraryLineUp = new GPatenLibraryLineUp();
      gPatenLibraryLineUp.setgPatentId(patentLibrary.getId());
      List<GPatenLibraryLineUp> gPatenLibraryLineUps = libraryLineUpMapper.selectGPatenLibraryLineUpList(gPatenLibraryLineUp);
      patentLibrary.setLineUpNum(gPatenLibraryLineUps.size());
      for (GPatenLibraryLineUp patenLibraryLineUp : gPatenLibraryLineUps) {
        if (Objects.equals(patenLibraryLineUp.getUserId(), SecurityUtils.getUserId())) {
          patentLibrary.setIsBooker(1);
          break;
        }
      }
    }
    return gPatentLibraries;
  }

  /**
   * 新增专利库数据
   *
   * @param gPatentLibrary 专利库数据
   * @return 结果
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public int insertGPatentLibrary(GPatentLibrary gPatentLibrary) {
    gPatentLibrary.setCreateTime(DateUtils.getNowDate());
    gPatentLibrary.setId(UUID.randomUUID().toString());
    GPatentLibrary isExit = gPatentLibraryMapper.selectGPatentLibraryByNo(gPatentLibrary.getPatentNo());
    if (Objects.nonNull(isExit)) {
      throw new ServiceException("该专利号已存在");
    }
    return gPatentLibraryMapper.insertGPatentLibrary(gPatentLibrary);
  }

  /**
   * 修改专利库数据
   *
   * @param gPatentLibrary 专利库数据
   * @return 结果
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public int updateGPatentLibrary(GPatentLibrary gPatentLibrary) {
    GPatentLibrary old = gPatentLibraryMapper.selectGPatentLibraryById(gPatentLibrary.getId());
    if (Objects.nonNull(old) && !old.getPatentNo().equals(gPatentLibrary.getPatentNo())) {
      throw new ServiceException("专利号不允许修改");
    }
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
  @Transactional(rollbackFor = Exception.class)
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
  @Transactional(rollbackFor = Exception.class)
  public int deleteGPatentLibraryById(String id) {
    return gPatentLibraryMapper.deleteGPatentLibraryById(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
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

    if (Objects.nonNull(gPatentLibrary.getStatusKey()) && gPatentLibrary.getStatusKey().equals("2") &&
        !Objects.equals(gPatentLibrary.getBookerKey(), loginUser.getUserId())) {
      throw new ServiceException("当前专利已被预约，请点击排队预约");
    }

    gPatentLibrary.setStatusKey("2");
    gPatentLibrary.setBookerKey(loginUser.getUserId());
    gPatentLibrary.setBookerValue(loginUser.getUser().getNickName());
    gPatentLibrary.setBookerTime(new Date());
    gPatentLibrary.setDeadline(DateUtils.addDays(gPatentLibrary.getBookerTime(), 2));
    gPatentLibraryMapper.updateGPatentLibrary(gPatentLibrary);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void cancelReserve(String id, LoginUser loginUser) {
    GPatentLibrary gPatentLibrary = this.selectGPatentLibraryById(id);
    if (Objects.isNull(gPatentLibrary)) {
      throw new ServiceException("专利不存在");
    }
    if (Objects.nonNull(gPatentLibrary.getStatusKey()) && !gPatentLibrary.getStatusKey().equals("2")) {
      throw new ServiceException("当前状态不能被取消");
    }

    if (Objects.nonNull(gPatentLibrary.getStatusKey()) && !Objects.equals(gPatentLibrary.getBookerKey(), loginUser.getUserId())) {
      throw new ServiceException("无权取消当前专利预约");
    }


    GPatenLibraryLineUp libraryLineUp = new GPatenLibraryLineUp();
    libraryLineUp.setgPatentId(gPatentLibrary.getId());
    List<GPatenLibraryLineUp> gPatenLibraryLineUps = libraryLineUpMapper.selectGPatenLibraryLineUpList(libraryLineUp);
    if (gPatenLibraryLineUps.isEmpty()) {
      gPatentLibraryMapper.cancelReserve(gPatentLibrary.getId());
      return;
    }

    GPatenLibraryLineUp gPatenLibraryLineUp = gPatenLibraryLineUps.stream().min(Comparator.comparingLong(GPatenLibraryLineUp::getLineUpNum)).orElse(null);
    SysUser sysUser = userMapper.selectUserById(gPatenLibraryLineUp.getUserId());


    gPatentLibrary.setBookerKey(gPatenLibraryLineUp.getUserId());
    if (Objects.nonNull(sysUser) && Objects.nonNull(sysUser.getNickName())) {
      gPatentLibrary.setBookerValue(sysUser.getNickName());
    }

    gPatentLibrary.setBookerTime(new Date());
    int i = gPatentLibraryMapper.updateGPatentLibrary(gPatentLibrary);
    if (i > 0) {
      this.cancelLineUpReserve(gPatenLibraryLineUp.getgPatentId(), gPatenLibraryLineUp.getUserId());
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void lineUpReserve(String id, LoginUser loginUser) {
    GPatentLibrary gPatentLibrary = this.selectGPatentLibraryById(id);
    if (Objects.isNull(gPatentLibrary)) {
      throw new ServiceException("专利不存在");
    }
    if (gPatentLibrary.getStatusKey().equals("1")) {
      throw new ServiceException("当前专利不需要预约排队");
    }

    if (Objects.equals(gPatentLibrary.getBookerKey(), loginUser.getUserId())) {
      throw new ServiceException("该专利已预定，不需要排队预定");
    }

    GPatenLibraryLineUp libraryLineUp = new GPatenLibraryLineUp();
    libraryLineUp.setgPatentId(id);
    List<GPatenLibraryLineUp> gPatenLibraryLineUps = libraryLineUpMapper.selectGPatenLibraryLineUpList(libraryLineUp);
    for (GPatenLibraryLineUp gPatenLibraryLineUp : gPatenLibraryLineUps) {
      if (gPatenLibraryLineUp.getUserId().equals(loginUser.getUserId())) {
        throw new ServiceException("当前专利不允许重复排队");
      }
    }

    Long lineUpNum = gPatenLibraryLineUps.stream().map(GPatenLibraryLineUp::getLineUpNum).max(Long::compare).orElse(0L);
    GPatenLibraryLineUp insert = new GPatenLibraryLineUp();
    insert.setUserId(loginUser.getUserId());
    insert.setCreatedTime(new Date());
    insert.setgPatentId(id);
    insert.setLineUpNum(lineUpNum + 1);
    insert.setId(snowFlakeIdConfig.propertyConfigurer().nextId());
    libraryLineUpMapper.insertGPatenLibraryLineUp(insert);

  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void cancelLineUpReserve(String id, Long userId) {
    GPatenLibraryLineUp libraryLineUp = new GPatenLibraryLineUp();
    libraryLineUp.setgPatentId(id);
    libraryLineUp.setUserId(userId);
    List<GPatenLibraryLineUp> gPatenLibraryLineUps = libraryLineUpMapper.selectGPatenLibraryLineUpList(libraryLineUp);
    if (gPatenLibraryLineUps.isEmpty()) {
      throw new ServiceException("Queue record not found");
    }

    int i = libraryLineUpMapper.deleteByUserAndPatent(id, userId);

    if (i > 0) {
      libraryLineUpMapper.decrementQueueNumber(id, gPatenLibraryLineUps.get(0).getLineUpNum());
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void allDelete(String username) {
    gPatentLibraryMapper.allDelete(username);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int batchAdd(GPatentLibrarySaveVo gPatentLibrary, Long userId) {
    List<GPatentLibrary> gPatentLibraries = gPatentLibrary.getgPatentLibraryList();
    int flag = 0;
    for (GPatentLibrary patentLibrary : gPatentLibraries) {
      int i = this.insertGPatentLibrary(patentLibrary);
      flag += i;
    }
    return flag == gPatentLibrary.getgPatentLibraryList().size() ? 1 : 0;
  }

  @Override
  public void reserveTime(String id, Long userId) {
    GPatentLibrary gPatentLibrary = this.selectGPatentLibraryById(id);
    if (Objects.isNull(gPatentLibrary)) {
      throw new ServiceException("专利不存在");
    }

    if (Objects.nonNull(gPatentLibrary.getStatusKey()) && !Objects.equals(gPatentLibrary.getStatusKey(), "2")) {
      throw new ServiceException("专利暂未预定，不能延长天数");
    }

    if (Objects.nonNull(gPatentLibrary.getBookerKey()) && !Objects.equals(gPatentLibrary.getBookerKey(), userId)) {
      SysUser sysUser = userMapper.selectUserById(userId);
      throw new ServiceException("专利当前预订人为："+ sysUser.getNickName() + "，不能延长天数");
    }

    gPatentLibrary.setDeadline(DateUtils.addDays(gPatentLibrary.getDeadline(), 1));
    gPatentLibraryMapper.updateGPatentLibrary(gPatentLibrary);

  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void soldPat(String id) {
    GPatentLibrary gPatentLibrary = this.selectGPatentLibraryById(id);

    if (Objects.isNull(gPatentLibrary)) {
      throw new ServiceException("专利不存在");
    }

    if (gPatentLibrary.getBookerTime().getTime() < new Date().getTime()) {

      List<GPatenLibraryLineUp> oneByGPatentIdAndUserId = libraryLineUpMapper.getOneByGPatentIdAndUserId(id);
      if (Objects.isNull(oneByGPatentIdAndUserId)) {
        throw new ServiceException("当前预订人预定专利时间已过期，暂未其他预订人。请确认！");
      }

      // 使用 Stream 查找最小值
      GPatenLibraryLineUp minElement = oneByGPatentIdAndUserId.stream()
          .min(Comparator.comparingLong(GPatenLibraryLineUp::getLineUpNum))
          .orElse(null); // 如果列表为空，返回 null；也可以使用 orElseThrow 抛出异常

      if (Objects.isNull(minElement)) {
        throw new ServiceException("获取排队人失败，请联系管理员");
      }

      gPatentLibrary.setSoldUserId(minElement.getUserId());
    } else {
      gPatentLibrary.setSoldUserId(gPatentLibrary.getBookerKey());
    }

    gPatentLibrary.setStatusKey("3");
    gPatentLibrary.setSoldTime(new Date());
    gPatentLibraryMapper.updateGPatentLibrary(gPatentLibrary);
  }


  public static void main(String[] args) {
    System.err.println(new Date().getTime());
  }
}
