package com.ruoyi.pay.convert.refund;

import com.ruoyi.common.core.page.PageResult;
import com.ruoyi.pay.domain.app.PayAppDO;
import com.ruoyi.pay.domain.order.PayOrderDO;
import com.ruoyi.pay.domain.refund.PayRefundDO;
import com.ruoyi.pay.service.dto.refund.PayRefundCreateReqDTO;
import com.ruoyi.pay.service.vo.refund.PayRefundDetailsRespVO;
import com.ruoyi.pay.service.vo.refund.PayRefundPageItemRespVO;
import com.ruoyi.pay.util.MapUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper
public interface PayRefundConvert {

  PayRefundConvert INSTANCE = Mappers.getMapper(PayRefundConvert.class);


  default PayRefundDetailsRespVO convert(PayRefundDO refund, PayAppDO app) {
    PayRefundDetailsRespVO respVO = convert(refund);
    if (app != null) {
      respVO.setAppName(app.getName());
    }
    return respVO;
  }

  PayRefundDetailsRespVO convert(PayRefundDO bean);

  PayRefundDetailsRespVO.Order convert(PayOrderDO bean);

  default PageResult<PayRefundPageItemRespVO> convertPage(PageResult<PayRefundDO> page, Map<Long, PayAppDO> appMap) {
    PageResult<PayRefundPageItemRespVO> result = convertPage(page);
    result.getList().forEach(order -> MapUtils.findAndThen(appMap, order.getAppId(), app -> order.setAppName(app.getName())));
    return result;
  }

  PageResult<PayRefundPageItemRespVO> convertPage(PageResult<PayRefundDO> page);

  PayRefundDO convert(PayRefundCreateReqDTO bean);

}
