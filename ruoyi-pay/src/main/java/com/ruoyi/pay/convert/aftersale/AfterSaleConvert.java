package com.ruoyi.pay.convert.aftersale;

import com.ruoyi.pay.domain.aftersale.AfterSaleDO;
import com.ruoyi.pay.service.refund.vo.AfterSaleCreateReqVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * AfterSaleConvert 类用于将 AfterSaleCreateReqVO 对象转换为 AfterSaleDO 对象。
 * 该类使用 MapStruct 进行对象映射，并忽略了一些字段，如 id、createTime、updateTime、createBy 和 updateBy。
 * <p>
 * 核心功能：
 * - 将 AfterSaleCreateReqVO 对象转换为 AfterSaleDO 对象。
 * <p>
 * 使用示例：
 * AfterSaleConvert converter = AfterSaleConvert.INSTANCE;
 * AfterSaleDO afterSaleDO = converter.convert(createReqVO);
 * <p>
 * 构造函数参数：
 * - 无构造函数参数。
 * <p>
 * 使用限制或潜在的副作用：
 * - 忽略了一些字段，因此在转换过程中这些字段不会被映射。
 */
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
