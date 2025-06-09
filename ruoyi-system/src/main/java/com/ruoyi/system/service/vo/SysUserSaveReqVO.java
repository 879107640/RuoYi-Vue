package com.ruoyi.system.service.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.*;

import com.ruoyi.common.core.domain.entity.BaseEntity;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysRole;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.annotation.Excel.Type;
import com.ruoyi.common.annotation.Excels;
import com.ruoyi.common.xss.Xss;
import org.springframework.validation.annotation.Validated;

/**
 * 用户对象 sys_user
 *
 * @author ruoyi
 */
@Validated
@Data
public class SysUserSaveReqVO implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * 用户账号
   */
  @NotNull(message = "用户账号不能为空")
  private String userName;

  /**
   * 手机号码
   */
  @NotEmpty(message = "手机号码不能为空")
  private String phonenumber;

  /**
   * 密码
   */
  @NotEmpty(message = "密码不能为空")
  private String password;

  /**
   * 验证码
   */
  @NotEmpty(message = "验证码不能为空")
  private String smsCode;

  @NotNull(message = "用户类型不能为空")
  private Integer userType;
}
