package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.entity.BaseEntity;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 手机验证码 DO
 * <p>
 * idx_mobile 索引：基于 {@link #mobile} 字段
 *
 * @author centre
 */
@TableName("sms_code")
@KeySequence("sms_code_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsCodeDO extends BaseEntity {

  /**
   * 编号
   */
  private Long id;
  /**
   * 手机号
   */
  private String mobile;
  /**
   * 验证码
   */
  private String code;
  /**
   * 创建 IP
   */
  private String createIp;
  /**
   * 今日发送的第几条
   */
  private Integer todayIndex;
  /**
   * 是否使用
   */
  private Boolean used;
  /**
   * 使用时间
   */
  private LocalDateTime usedTime;
  /**
   * 使用 IP
   */
  private String usedIp;

}
