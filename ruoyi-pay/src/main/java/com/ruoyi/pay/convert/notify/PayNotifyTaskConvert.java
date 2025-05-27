package com.ruoyi.pay.convert.notify;

import com.ruoyi.common.core.page.PageResult;
import com.ruoyi.pay.domain.app.PayAppDO;
import com.ruoyi.pay.domain.notify.PayNotifyLogDO;
import com.ruoyi.pay.domain.notify.PayNotifyTaskDO;
import com.ruoyi.pay.service.vo.notify.PayNotifyTaskDetailRespVO;
import com.ruoyi.pay.service.vo.notify.PayNotifyTaskRespVO;
import com.ruoyi.pay.util.MapUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

/**
 * 支付通知 Convert
 *
 * @author centre
 */
@Mapper
public interface PayNotifyTaskConvert {

    PayNotifyTaskConvert INSTANCE = Mappers.getMapper(PayNotifyTaskConvert.class);

    PayNotifyTaskRespVO convert(PayNotifyTaskDO bean);

    default PageResult<PayNotifyTaskRespVO> convertPage(PageResult<PayNotifyTaskDO> page, Map<Long, PayAppDO> appMap){
        PageResult<PayNotifyTaskRespVO> result = convertPage(page);
        result.getList().forEach(order -> MapUtils.findAndThen(appMap, order.getAppId(), app -> order.setAppName(app.getName())));
        return result;
    }
    PageResult<PayNotifyTaskRespVO> convertPage(PageResult<PayNotifyTaskDO> page);

    default PayNotifyTaskDetailRespVO convert(PayNotifyTaskDO task, PayAppDO app, List<PayNotifyLogDO> logs) {
        PayNotifyTaskDetailRespVO respVO = convert(task, logs);
        if (app != null) {
            respVO.setAppName(app.getName());
        }
        return respVO;
    }
    PayNotifyTaskDetailRespVO convert(PayNotifyTaskDO task, List<PayNotifyLogDO> logs);
}
