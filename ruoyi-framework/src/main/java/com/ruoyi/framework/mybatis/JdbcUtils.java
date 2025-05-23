package com.ruoyi.framework.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.ruoyi.common.utils.object.ObjectUtils;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * JDBC 工具类
 *
 * @author centre
 */
public class JdbcUtils {

  /**
   * 判断连接是否正确
   *
   * @param url      数据源连接
   * @param username 账号
   * @param password 密码
   * @return 是否正确
   */
  public static boolean isConnectionOK(String url, String username, String password) {
    try (Connection ignored = DriverManager.getConnection(url, username, password)) {
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  /**
   * 获得 URL 对应的 DB 类型
   *
   * @param url URL
   * @return DB 类型
   */
  public static DbType getDbType(String url) {
    return com.baomidou.mybatisplus.extension.toolkit.JdbcUtils.getDbType(url);
  }

  /**
   * 判断 JDBC 连接是否为 SQLServer 数据库
   *
   * @param url JDBC 连接
   * @return 是否为 SQLServer 数据库
   */
  public static boolean isSQLServer(String url) {
    DbType dbType = getDbType(url);
    return isSQLServer(dbType);
  }

  /**
   * 判断 JDBC 连接是否为 SQLServer 数据库
   *
   * @param dbType DB 类型
   * @return 是否为 SQLServer 数据库
   */
  public static boolean isSQLServer(DbType dbType) {
    return ObjectUtils.equalsAny(dbType, DbType.SQL_SERVER, DbType.SQL_SERVER2005);
  }

}
