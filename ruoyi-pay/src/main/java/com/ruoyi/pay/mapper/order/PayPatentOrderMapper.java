package com.ruoyi.pay.mapper.order;

import com.ruoyi.common.core.page.PageResult;
import com.ruoyi.common.utils.PageParam;
import com.ruoyi.framework.mybatis.mapper.BaseMapperX;
import com.ruoyi.framework.mybatis.query.LambdaQueryWrapperX;
import com.ruoyi.pay.domain.order.PayPatentOrderDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 示例订单 Mapper
 *
 * @author centre
 */
@Mapper
public interface PayPatentOrderMapper extends BaseMapperX<PayPatentOrderDO> {

    default PageResult<PayPatentOrderDO> selectPage(PageParam reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PayPatentOrderDO>()
                .orderByDesc(PayPatentOrderDO::getId));
    }

    default int updateByIdAndPayed(Long id, boolean wherePayed, PayPatentOrderDO updateObj) {
        return update(updateObj, new LambdaQueryWrapperX<PayPatentOrderDO>()
                .eq(PayPatentOrderDO::getId, id).eq(PayPatentOrderDO::getPayStatus, wherePayed));
    }

}
