package com.ruoyi.system.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teautil.models.RuntimeOptions;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtil;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.common.utils.object.BeanUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.framework.sms.dto.SmsSendRespDTO;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.service.vo.SysUserSaveReqVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.validation.Validator;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.hutool.core.util.RandomUtil.randomInt;

/**
 * 用户 业务层处理
 *
 * @author ruoyi
 */
@Service
public class SysUserServiceImpl implements ISysUserService {
  private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);
  private static final String RESPONSE_CODE_SUCCESS = "OK";
  @Autowired
  private SysUserMapper userMapper;

  @Autowired
  private SysRoleMapper roleMapper;

  @Autowired
  private SysPostMapper postMapper;

  @Autowired
  private SysUserRoleMapper userRoleMapper;

  @Autowired
  private SysUserPostMapper userPostMapper;

  @Autowired
  private ISysConfigService configService;

  @Autowired
  private ISysDeptService deptService;

  @Autowired
  protected Validator validator;

  @Resource
  SysConfigMapper configMapper;

  @Resource
  RedisCache redisCache;
  @Resource
  SmsCodeMapper smsCodeMapper;

  /**
   * 根据条件分页查询用户列表
   *
   * @param user 用户信息
   * @return 用户信息集合信息
   */
  @Override
  @DataScope(deptAlias = "d", userAlias = "u")
  public List<SysUser> selectUserList(SysUser user) {
    return userMapper.selectUserList(user);
  }

  /**
   * 根据条件分页查询已分配用户角色列表
   *
   * @param user 用户信息
   * @return 用户信息集合信息
   */
  @Override
  @DataScope(deptAlias = "d", userAlias = "u")
  public List<SysUser> selectAllocatedList(SysUser user) {
    return userMapper.selectAllocatedList(user);
  }

  /**
   * 根据条件分页查询未分配用户角色列表
   *
   * @param user 用户信息
   * @return 用户信息集合信息
   */
  @Override
  @DataScope(deptAlias = "d", userAlias = "u")
  public List<SysUser> selectUnallocatedList(SysUser user) {
    return userMapper.selectUnallocatedList(user);
  }

  /**
   * 通过用户名查询用户
   *
   * @param userName 用户名
   * @return 用户对象信息
   */
  @Override
  public SysUser selectUserByUserName(String userName) {
    return userMapper.selectUserByUserName(userName);
  }

  /**
   * 通过用户ID查询用户
   *
   * @param userId 用户ID
   * @return 用户对象信息
   */
  @Override
  public SysUser selectUserById(Long userId) {
    return userMapper.selectUserById(userId);
  }

  /**
   * 查询用户所属角色组
   *
   * @param userName 用户名
   * @return 结果
   */
  @Override
  public String selectUserRoleGroup(String userName) {
    List<SysRole> list = roleMapper.selectRolesByUserName(userName);
    if (CollectionUtils.isEmpty(list)) {
      return StringUtils.EMPTY;
    }
    return list.stream().map(SysRole::getRoleName).collect(Collectors.joining(","));
  }

  /**
   * 查询用户所属岗位组
   *
   * @param userName 用户名
   * @return 结果
   */
  @Override
  public String selectUserPostGroup(String userName) {
    List<SysPost> list = postMapper.selectPostsByUserName(userName);
    if (CollectionUtils.isEmpty(list)) {
      return StringUtils.EMPTY;
    }
    return list.stream().map(SysPost::getPostName).collect(Collectors.joining(","));
  }

  /**
   * 校验用户名称是否唯一
   *
   * @param user 用户信息
   * @return 结果
   */
  @Override
  public boolean checkUserNameUnique(SysUser user) {
    Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
    SysUser info = userMapper.checkUserNameUnique(user.getUserName());
    if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
      return UserConstants.NOT_UNIQUE;
    }
    return UserConstants.UNIQUE;
  }

  public void checkUserName(SysUser user) {
    SysUser info = userMapper.checkUserNameUnique(user.getUserName());
    if (Objects.nonNull(info)) {
      throw new ServiceException("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
    }
  }

  /**
   * 校验手机号码是否唯一
   *
   * @param user 用户信息
   * @return
   */
  @Override
  public boolean checkPhoneUnique(SysUser user) {
    Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
    SysUser info = userMapper.checkPhoneUnique(user.getPhonenumber());
    if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
      return UserConstants.NOT_UNIQUE;
    }
    return UserConstants.UNIQUE;
  }

  /**
   * 校验email是否唯一
   *
   * @param user 用户信息
   * @return
   */
  @Override
  public boolean checkEmailUnique(SysUser user) {
    Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
    SysUser info = userMapper.checkEmailUnique(user.getEmail());
    if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue()) {
      return UserConstants.NOT_UNIQUE;
    }
    return UserConstants.UNIQUE;
  }

  /**
   * 校验用户是否允许操作
   *
   * @param user 用户信息
   */
  @Override
  public void checkUserAllowed(SysUser user) {
    if (StringUtils.isNotNull(user.getUserId()) && user.isAdmin()) {
      throw new ServiceException("不允许操作超级管理员用户");
    }
  }

  /**
   * 校验用户是否有数据权限
   *
   * @param userId 用户id
   */
  @Override
  public void checkUserDataScope(Long userId) {
    if (!SysUser.isAdmin(SecurityUtils.getUserId())) {
      SysUser user = new SysUser();
      user.setUserId(userId);
      List<SysUser> users = SpringUtils.getAopProxy(this).selectUserList(user);
      if (StringUtils.isEmpty(users)) {
        throw new ServiceException("没有权限访问用户数据！");
      }
    }
  }

  /**
   * 新增保存用户信息
   *
   * @param user 用户信息
   * @return 结果
   */
  @Override
  @Transactional
  public int insertUser(SysUser user) {
    // 新增用户信息
    int rows = userMapper.insertUser(user);
    // 新增用户岗位关联
    insertUserPost(user);
    // 新增用户与角色管理
    insertUserRole(user);
    return rows;
  }

  /**
   * 注册用户信息
   *
   * @param user 用户信息
   * @return 结果
   */
  @Override
  public boolean registerUser(SysUser user) {
    return userMapper.insertUser(user) > 0;
  }

  /**
   * 修改保存用户信息
   *
   * @param user 用户信息
   * @return 结果
   */
  @Override
  @Transactional
  public int updateUser(SysUser user) {
    Long userId = user.getUserId();
    // 删除用户与角色关联
    userRoleMapper.deleteUserRoleByUserId(userId);
    // 新增用户与角色管理
    insertUserRole(user);
    // 删除用户与岗位关联
    userPostMapper.deleteUserPostByUserId(userId);
    // 新增用户与岗位管理
    insertUserPost(user);
    return userMapper.updateUser(user);
  }

  /**
   * 用户授权角色
   *
   * @param userId  用户ID
   * @param roleIds 角色组
   */
  @Override
  @Transactional
  public void insertUserAuth(Long userId, Long[] roleIds) {
    userRoleMapper.deleteUserRoleByUserId(userId);
    insertUserRole(userId, roleIds);
  }

  /**
   * 修改用户状态
   *
   * @param user 用户信息
   * @return 结果
   */
  @Override
  public int updateUserStatus(SysUser user) {
    return userMapper.updateUser(user);
  }

  /**
   * 修改用户基本信息
   *
   * @param user 用户信息
   * @return 结果
   */
  @Override
  public int updateUserProfile(SysUser user) {
    return userMapper.updateUser(user);
  }

  /**
   * 修改用户头像
   *
   * @param userName 用户名
   * @param avatar   头像地址
   * @return 结果
   */
  @Override
  public boolean updateUserAvatar(String userName, String avatar) {
    return userMapper.updateUserAvatar(userName, avatar) > 0;
  }

  /**
   * 重置用户密码
   *
   * @param user 用户信息
   * @return 结果
   */
  @Override
  public int resetPwd(SysUser user) {
    return userMapper.updateUser(user);
  }

  /**
   * 重置用户密码
   *
   * @param userName 用户名
   * @param password 密码
   * @return 结果
   */
  @Override
  public int resetUserPwd(String userName, String password) {
    return userMapper.resetUserPwd(userName, password);
  }

  /**
   * 新增用户角色信息
   *
   * @param user 用户对象
   */
  public void insertUserRole(SysUser user) {
    this.insertUserRole(user.getUserId(), user.getRoleIds());
  }

  /**
   * 新增用户岗位信息
   *
   * @param user 用户对象
   */
  public void insertUserPost(SysUser user) {
    Long[] posts = user.getPostIds();
    if (StringUtils.isNotEmpty(posts)) {
      // 新增用户与岗位管理
      List<SysUserPost> list = new ArrayList<SysUserPost>(posts.length);
      for (Long postId : posts) {
        SysUserPost up = new SysUserPost();
        up.setUserId(user.getUserId());
        up.setPostId(postId);
        list.add(up);
      }
      userPostMapper.batchUserPost(list);
    }
  }

  /**
   * 新增用户角色信息
   *
   * @param userId  用户ID
   * @param roleIds 角色组
   */
  public void insertUserRole(Long userId, Long[] roleIds) {
    if (StringUtils.isNotEmpty(roleIds)) {
      // 新增用户与角色管理
      List<SysUserRole> list = new ArrayList<SysUserRole>(roleIds.length);
      for (Long roleId : roleIds) {
        SysUserRole ur = new SysUserRole();
        ur.setUserId(userId);
        ur.setRoleId(roleId);
        list.add(ur);
      }
      userRoleMapper.batchUserRole(list);
    }
  }

  /**
   * 通过用户ID删除用户
   *
   * @param userId 用户ID
   * @return 结果
   */
  @Override
  @Transactional
  public int deleteUserById(Long userId) {
    // 删除用户与角色关联
    userRoleMapper.deleteUserRoleByUserId(userId);
    // 删除用户与岗位表
    userPostMapper.deleteUserPostByUserId(userId);
    return userMapper.deleteUserById(userId);
  }

  /**
   * 批量删除用户信息
   *
   * @param userIds 需要删除的用户ID
   * @return 结果
   */
  @Override
  @Transactional
  public int deleteUserByIds(Long[] userIds) {
    for (Long userId : userIds) {
      checkUserAllowed(new SysUser(userId));
      checkUserDataScope(userId);
    }
    // 删除用户与角色关联
    userRoleMapper.deleteUserRole(userIds);
    // 删除用户与岗位关联
    userPostMapper.deleteUserPost(userIds);
    return userMapper.deleteUserByIds(userIds);
  }

  /**
   * 导入用户数据
   *
   * @param userList        用户数据列表
   * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
   * @param operName        操作用户
   * @return 结果
   */
  @Override
  public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName) {
    if (StringUtils.isNull(userList) || userList.size() == 0) {
      throw new ServiceException("导入用户数据不能为空！");
    }
    int successNum = 0;
    int failureNum = 0;
    StringBuilder successMsg = new StringBuilder();
    StringBuilder failureMsg = new StringBuilder();
    for (SysUser user : userList) {
      try {
        // 验证是否存在这个用户
        SysUser u = userMapper.selectUserByUserName(user.getUserName());
        if (StringUtils.isNull(u)) {
          BeanValidators.validateWithException(validator, user);
          deptService.checkDeptDataScope(user.getDeptId());
          String password = configService.selectConfigByKey("sys.user.initPassword");
          user.setPassword(SecurityUtils.encryptPassword(password));
          user.setCreateBy(operName);
          userMapper.insertUser(user);
          successNum++;
          successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 导入成功");
        } else if (isUpdateSupport) {
          BeanValidators.validateWithException(validator, user);
          checkUserAllowed(u);
          checkUserDataScope(u.getUserId());
          deptService.checkDeptDataScope(user.getDeptId());
          user.setUserId(u.getUserId());
          user.setUpdateBy(operName);
          userMapper.updateUser(user);
          successNum++;
          successMsg.append("<br/>").append(successNum).append("、账号 ").append(user.getUserName()).append(" 更新成功");
        } else {
          failureNum++;
          failureMsg.append("<br/>").append(failureNum).append("、账号 ").append(user.getUserName()).append(" 已存在");
        }
      } catch (Exception e) {
        failureNum++;
        String msg = "<br/>" + failureNum + "、账号 " + user.getUserName() + " 导入失败：";
        failureMsg.append(msg + e.getMessage());
        log.error(msg, e);
      }
    }
    if (failureNum > 0) {
      failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
      throw new ServiceException(failureMsg.toString());
    } else {
      successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
    }
    return successMsg.toString();
  }

  @Override
  public int register(SysUserSaveReqVO user) {
    SysUser insetUser = BeanUtils.toBean(user, SysUser.class);
    checkPhone(insetUser);
    checkUserName(insetUser);
    String smsCode = redisCache.getCacheObject(user.getPhonenumber());
    if (Objects.isNull(smsCode) || !smsCode.equals(user.getSmsCode())) {
      throw new ServiceException("验证码错误");
    }
    insetUser.setPassword(SecurityUtils.encryptPassword(insetUser.getPassword()));
    return userMapper.insertUser(insetUser);
  }

  @Override
  public Boolean sendCode(String mobile) throws Exception {

    if (mobile == null || mobile.length() != 11) {
      throw new ServiceException("手机号格式错误");
    }

    // 创建验证码
    String code = createSmsCode(mobile, IpUtils.getHostIp());

    // 发送验证码
    SmsSendRespDTO smsSendRespDTO = sendSingleSms(mobile, code);
    redisCache.setCacheObject(mobile, code, 5 * 60, TimeUnit.SECONDS);
    return smsSendRespDTO.getSuccess();
  }

  public SmsSendRespDTO sendSingleSms(String mobile, String code) throws Exception {

    // 1. 执行请求
    Client client = createClient();

    com.aliyun.dysmsapi20170525.models.SendSmsRequest sendSmsRequest = new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
        .setPhoneNumbers(mobile)
        .setSignName("南昌元启知识产权服务有限公司")
        .setTemplateParam("{\"code\":\"" + code + "\"}")
        .setTemplateCode("SMS_320265989");
    SendSmsResponse sendSmsResponse = client.sendSmsWithOptions(sendSmsRequest, new RuntimeOptions());

    JSONObject response = JSONUtil.parseObj(sendSmsResponse);

    // 2. 解析请求
    SmsSendRespDTO smsSendRespDTO = new SmsSendRespDTO();
    smsSendRespDTO.setSuccess(Objects.equals(response.getJSONObject("body").getStr("code"), RESPONSE_CODE_SUCCESS));
    smsSendRespDTO.setSerialNo(response.getJSONObject("body").getStr("bizId"));
    smsSendRespDTO.setApiRequestId(response.getJSONObject("body").getStr("requestId"));
    smsSendRespDTO.setApiCode(response.getJSONObject("body").getStr("code"));
    smsSendRespDTO.setApiMsg(response.getJSONObject("body").getStr("message"));

    return smsSendRespDTO;
  }


  public com.aliyun.dysmsapi20170525.Client createClient() throws Exception {
    SysConfig sysConfig = new SysConfig();
    sysConfig.setConfigKey("accessKeyId");

    SysConfig selectConfig = configMapper.selectConfig(sysConfig);

    com.aliyun.credentials.Client credential = new com.aliyun.credentials.Client();
    com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
        .setCredential(credential);
    // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
    config.endpoint = "dysmsapi.aliyuncs.com";
    String accessKeyId = SecurityUtil.decryptAES(selectConfig.getConfigValue());
    config.setAccessKeyId(accessKeyId);
    sysConfig.setConfigKey("accessKeySecret");
    selectConfig = configMapper.selectConfig(sysConfig);
    String accessKeySecret = SecurityUtil.decryptAES(selectConfig.getConfigValue());
    config.setAccessKeySecret(accessKeySecret);
    return new com.aliyun.dysmsapi20170525.Client(config);
  }

  private String createSmsCode(String mobile, String ip) {

    SmsCodeDO lastSmsCode = smsCodeMapper.selectLastByMobile(mobile);
    if (lastSmsCode != null) {
      if (LocalDateTimeUtil.between(LocalDateTimeUtil.of(lastSmsCode.getCreateTime()), LocalDateTime.now()).toMillis()
          < Duration.ofMillis(10).toMillis()) { // 发送过于频繁
        throw new ServiceException("短信发送过于频繁");
      }
      if (isToday(LocalDateTimeUtil.of(lastSmsCode.getCreateTime())) && // 必须是今天，才能计算超过当天的上限
          lastSmsCode.getTodayIndex() >= 200) { // 超过当天发送的上限。
        throw new ServiceException("超过每日短信发送数量");
      }
    }
    // 创建验证码记录
    String code = String.valueOf(randomInt(0, 9999));
    SmsCodeDO newSmsCode = SmsCodeDO.builder().id(IdUtil.getSnowflakeNextId()).mobile(mobile).code(code)
        .todayIndex(lastSmsCode != null && isToday(LocalDateTimeUtil.of(lastSmsCode.getCreateTime())) ? lastSmsCode.getTodayIndex() + 1 : 1)
        .createIp(ip).used(false).build();
    newSmsCode.setCreateTime(new Date());
    newSmsCode.setUpdateTime(new Date());
    smsCodeMapper.insert(newSmsCode);
    return code;
  }

  public void checkPhone(SysUser user) {
    SysUser info = userMapper.checkPhoneUnique(user.getPhonenumber());
    if (Objects.nonNull(info)) {
      throw new ServiceException("手机号码已存在");
    }
  }

  /**
   * 是否今天
   *
   * @param date 日期
   * @return 是否
   */
  public static boolean isToday(LocalDateTime date) {
    return LocalDateTimeUtil.isSameDay(date, LocalDateTime.now());
  }
}
