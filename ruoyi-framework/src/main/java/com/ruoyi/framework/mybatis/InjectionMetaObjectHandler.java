package com.ruoyi.framework.mybatis;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.ruoyi.common.core.domain.entity.BaseEntity;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.framework.web.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * MP注入处理器
 *
 * @author Lion Li
 */
@Slf4j
@Component
public class InjectionMetaObjectHandler implements MetaObjectHandler {

  @Resource
  TokenService tokenService;

  /**
   * 插入填充方法，用于在插入数据时自动填充实体对象中的创建时间、更新时间、创建人、更新人等信息
   *
   * @param metaObject 元对象，用于获取原始对象并进行填充
   */
  @Override
  public void insertFill(MetaObject metaObject) {
    try {
      // 分步处理：先检查非空，再检查类型并强制转换
      if (ObjectUtil.isNotNull(metaObject)) {
        Object originalObject = metaObject.getOriginalObject();
        if (originalObject instanceof BaseEntity) {
          BaseEntity baseEntity = (BaseEntity) originalObject; // 显式类型转换
          // 获取当前时间作为创建时间和更新时间，如果创建时间不为空，则使用创建时间，否则使用当前时间
          Date current = baseEntity.getCreateTime() == null ? new Date() : baseEntity.getCreateTime();
          baseEntity.setCreateTime(current);
          baseEntity.setUpdateTime(current);

          // 如果创建人为空，则填充当前登录用户的信息
          if (ObjectUtil.isNull(baseEntity.getCreateBy())) {
            LoginUser loginUser = getLoginUser();
            if (ObjectUtil.isNotNull(loginUser)) {
              String userId = loginUser.getUsername();
              // 填充创建人、更新人和创建部门信息
              baseEntity.setCreateBy(userId);
              baseEntity.setUpdateBy(userId);
            }
          }
        }
      } else {
        Date date = new Date();
        this.strictInsertFill(metaObject, "createTime", Date.class, date);
        this.strictInsertFill(metaObject, "updateTime", Date.class, date);
      }

    } catch (Exception e) {
      throw new ServiceException("自动注入异常 => " + e.getMessage());
    }
  }

  /**
   * 更新填充方法，用于在更新数据时自动填充实体对象中的更新时间和更新人信息
   *
   * @param metaObject 元对象，用于获取原始对象并进行填充
   */
  @Override
  public void updateFill(MetaObject metaObject) {
    try {
      if (ObjectUtil.isNotNull(metaObject)) {
        Object originalObject = metaObject.getOriginalObject();
        if (originalObject instanceof BaseEntity) {
          BaseEntity baseEntity = (BaseEntity) originalObject; // 显式类型转换
          // 获取当前时间作为更新时间，无论原始对象中的更新时间是否为空都填充
          Date current = new Date();
          baseEntity.setUpdateTime(current);

          // 获取当前登录用户的ID，并填充更新人信息
          if (getLoginUser() != null) {
            String userId = getLoginUser().getUsername();
            if (ObjectUtil.isNotNull(userId)) {
              baseEntity.setUpdateBy(userId);
            }
          }
        }
      } else {
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
      }

    } catch (Exception e) {
      throw new ServiceException("自动注入异常 => " + e.getMessage());
    }
  }

  private LoginUser getLoginUser() {
    HttpServletRequest request = getRequest();
    return tokenService.getLoginUser(request);
  }

  public static HttpServletRequest getRequest() {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (!(requestAttributes instanceof ServletRequestAttributes)) {
      return null;
    }
    ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
    return servletRequestAttributes.getRequest();
  }

}
