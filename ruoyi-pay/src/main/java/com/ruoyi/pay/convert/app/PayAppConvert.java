package com.ruoyi.pay.convert.app;

import com.ruoyi.common.core.page.PageResult;
import com.ruoyi.common.utils.collection.CollectionUtils;
import com.ruoyi.pay.domain.app.PayAppDO;
import com.ruoyi.pay.domain.channel.PayChannelDO;
import com.ruoyi.pay.service.vo.PayAppCreateReqVO;
import com.ruoyi.pay.service.vo.PayAppPageItemRespVO;
import com.ruoyi.pay.service.vo.PayAppRespVO;
import com.ruoyi.pay.service.vo.PayAppUpdateReqVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 支付应用信息 Convert
 *
 * @author Centre
 */
@Mapper
public interface PayAppConvert {

  PayAppConvert INSTANCE = Mappers.getMapper(PayAppConvert.class);

  PayAppPageItemRespVO pageConvert(PayAppDO bean);

  PayAppDO convert(PayAppCreateReqVO bean);

  PayAppDO convert(PayAppUpdateReqVO bean);

  PayAppRespVO convert(PayAppDO bean);

  List<PayAppRespVO> convertList(List<PayAppDO> list);

  PageResult<PayAppPageItemRespVO> convertPage(PageResult<PayAppDO> page);

  default PageResult<PayAppPageItemRespVO> convertPage(PageResult<PayAppDO> pageResult, List<PayChannelDO> channels) {
    PageResult<PayAppPageItemRespVO> voPageResult = convertPage(pageResult);
    // 处理 channel 关系
    Map<Long, Set<String>> appIdChannelMap = CollectionUtils.convertMultiMap2(channels, PayChannelDO::getAppId, PayChannelDO::getCode);
    voPageResult.getList().forEach(app -> app.setChannelCodes(appIdChannelMap.get(app.getId())));
    return voPageResult;
  }
}
