package com.ruoyi.pay.service.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class WalletRechargePackageRespVO extends WalletRechargePackageBaseVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private LocalDateTime createTime;

}
