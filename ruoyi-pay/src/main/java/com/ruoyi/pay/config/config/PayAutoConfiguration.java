package com.ruoyi.pay.config.config;

import com.ruoyi.pay.config.core.client.PayClientFactory;
import com.ruoyi.pay.config.core.client.impl.PayClientFactoryImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 支付配置类
 *
 * @author 芋道源码
 */
@AutoConfiguration
public class PayAutoConfiguration {

  @Bean
  public PayClientFactory payClientFactory() {
    return new PayClientFactoryImpl();
  }

}
