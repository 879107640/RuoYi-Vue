package com.ruoyi.common.utils.servlet;


import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 客户端工具类
 *
 * @author centre
 */
public class ServletUtils {


  /**
   * @param request 请求
   * @return ua
   */
  public static String getUserAgent(HttpServletRequest request) {
    String ua = request.getHeader("User-Agent");
    return ua != null ? ua : "";
  }
  /**
   * 获得请求
   *
   * @return HttpServletRequest
   */
  public static HttpServletRequest getRequest() {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (!(requestAttributes instanceof ServletRequestAttributes)) {
      return null;
    }
    return (HttpServletRequest) ((ServletRequestAttributes) requestAttributes).getRequest();
  }

  public static String getUserAgent() {
    HttpServletRequest request = getRequest();
    if (request == null) {
      return null;
    }
    return getUserAgent(request);
  }

  public static String getClientIP() {
    HttpServletRequest request = getRequest();
    if (request == null) {
      return null;
    }
    return JakartaServletUtil.getClientIP(request);
  }

  public static boolean isJsonRequest(ServletRequest request) {
    return StrUtil.startWithIgnoreCase(request.getContentType(), MediaType.APPLICATION_JSON_VALUE);
  }

  public static String getClientIP(HttpServletRequest request) {
    return JakartaServletUtil.getClientIP(request);
  }

}
