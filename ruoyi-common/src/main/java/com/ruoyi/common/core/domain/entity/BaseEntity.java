package com.ruoyi.common.core.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Entity基类
 *
 * @author Lion Li
 */
@Data
public class BaseEntity implements Serializable {
  private static final long serialVersionUID = 1L;


  /**
   * 创建者
   */
  @TableField(fill = FieldFill.INSERT)
  private String createBy;

  /**
   * 创建时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @TableField(fill = FieldFill.INSERT)
  private Date createTime;

  /**
   * 更新者
   */
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private String updateBy;

  /**
   * 更新时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private Date updateTime;

  /**
   * 备注
   */
  private String remark;

  /**
   * 请求参数
   */
  @Setter
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  @TableField(exist = false)
  private Map<String, Object> search;


  public Map<String, Object> getParams() {
    if (search == null) {
      search = new HashMap<>();
    }
    return search;
  }

}
