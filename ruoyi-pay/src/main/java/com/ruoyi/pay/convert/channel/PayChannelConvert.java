package com.ruoyi.pay.convert.channel;

import cn.hutool.db.PageResult;
import com.ruoyi.pay.domain.channel.PayChannelDO;
import com.ruoyi.pay.service.vo.PayChannelCreateReqVO;
import com.ruoyi.pay.service.vo.PayChannelRespVO;
import com.ruoyi.pay.service.vo.PayChannelUpdateReqVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PayChannelConvert {

    PayChannelConvert INSTANCE = Mappers.getMapper(PayChannelConvert.class);

    @Mapping(target = "config",ignore = true)
    PayChannelDO convert(PayChannelCreateReqVO bean);

    @Mapping(target = "config",ignore = true)
    PayChannelDO convert(PayChannelUpdateReqVO bean);

    @Mapping(target = "config",expression = "java(com.ruoyi.common.utils.json.JsonUtils.toJsonString(bean.getConfig()))")
    PayChannelRespVO convert(PayChannelDO bean);


}
