package com.ruoyi.system.domain;

import com.ruoyi.common.utils.StringUtils;
import lombok.Getter;

/**
 * 缓存信息
 *
 * @author ruoyi
 */
@Getter
public class SysCache {
  /**
   * 缓存名称
   */
  private String cacheName = "";

  /**
   * 缓存键名
   */
  private String cacheKey = "";

  /**
   * 缓存内容
   */
  private String cacheValue = "";

  /**
   * 备注
   */
  private String remark = "";

  public SysCache() {

  }

  public SysCache(String cacheName, String remark) {
    this.cacheName = cacheName;
    this.remark = remark;
  }

  public SysCache(String cacheName, String cacheKey, String cacheValue) {
    this.cacheName = StringUtils.replace(cacheName, ":", "");
    this.cacheKey = StringUtils.replace(cacheKey, cacheName, "");
    this.cacheValue = cacheValue;
  }

  public void setCacheName(String cacheName) {
    this.cacheName = cacheName;
  }

  public void setCacheKey(String cacheKey) {
    this.cacheKey = cacheKey;
  }

  public void setCacheValue(String cacheValue) {
    this.cacheValue = cacheValue;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }
}
