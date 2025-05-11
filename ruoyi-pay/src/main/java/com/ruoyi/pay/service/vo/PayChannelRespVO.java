package com.ruoyi.pay.service.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PayChannelRespVO extends PayChannelBaseVO {

  private Long id;

  private LocalDateTime createTime;

  private String code;

  private String config;

}
