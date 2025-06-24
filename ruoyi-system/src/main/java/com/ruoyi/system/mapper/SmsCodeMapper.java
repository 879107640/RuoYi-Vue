package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SmsCodeDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SmsCodeMapper  {

    /**
     * 获得手机号的最后一个手机验证码
     *
     * @param mobile 手机号
     * @return 手机验证码
     */
    SmsCodeDO selectLastByMobile(String mobile);

    void insert(SmsCodeDO newSmsCode);
}
