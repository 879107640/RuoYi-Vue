package com.ruoyi.system.framework.sms.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.Duration;

@Validated
@Data
@Configuration
public class SmsCodeProperties {

    /**
     * 过期时间
     */
    @Value("${yuanqi.smsCode.expireTimes}")
    private Duration expireTimes;
    /**
     * 短信发送频率
     */
    @NotNull(message = "短信发送频率不能为空")
    @Value("${yuanqi.smsCode.sendFrequency}")
    private Duration sendFrequency;
    /**
     * 每日发送最大数量
     */
    @NotNull(message = "每日发送最大数量不能为空")
    @Value("${yuanqi.smsCode.sendMaximumQuantityPerDay}")
    private Integer sendMaximumQuantityPerDay;
    /**
     * 验证码最小值
     */
    @NotNull(message = "验证码最小值不能为空")
    @Value("${yuanqi.smsCode.beginCode}")
    private Integer beginCode;
    /**
     * 验证码最大值
     */
    @NotNull(message = "验证码最大值不能为空")
    @Value("${yuanqi.smsCode.endCode}")
    private Integer endCode;

}
