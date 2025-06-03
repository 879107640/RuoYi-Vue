package com.ruoyi.pay.convert.aftersale;

import com.ruoyi.pay.domain.aftersale.AfterSaleDO;
import com.ruoyi.pay.service.refund.vo.AfterSaleCreateReqVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AfterSaleConvert {

    AfterSaleConvert INSTANCE = Mappers.getMapper(AfterSaleConvert.class);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "createTime", ignore = true),
        @Mapping(target = "updateTime", ignore = true),
        @Mapping(target = "createBy", ignore = true),
        @Mapping(target = "updateBy", ignore = true),
    })
    AfterSaleDO convert(AfterSaleCreateReqVO createReqVO);
}
