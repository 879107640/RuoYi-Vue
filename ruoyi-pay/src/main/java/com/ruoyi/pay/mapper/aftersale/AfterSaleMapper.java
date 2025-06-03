package com.ruoyi.pay.mapper.aftersale;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ruoyi.common.core.page.PageResult;
import com.ruoyi.common.utils.PageParam;
import com.ruoyi.framework.mybatis.mapper.BaseMapperX;
import com.ruoyi.framework.mybatis.query.LambdaQueryWrapperX;
import com.ruoyi.pay.domain.aftersale.AfterSaleDO;
import com.ruoyi.pay.service.aftersale.vo.AfterSalePageReqVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;

@Mapper
public interface AfterSaleMapper extends BaseMapperX<AfterSaleDO> {

    default PageResult<AfterSaleDO> selectPage(AfterSalePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AfterSaleDO>()
                .eqIfPresent(AfterSaleDO::getUserId, reqVO.getUserId())
                .likeIfPresent(AfterSaleDO::getNo, reqVO.getNo())
                .eqIfPresent(AfterSaleDO::getStatus, reqVO.getStatus())
                .eqIfPresent(AfterSaleDO::getWay, reqVO.getWay())
                .betweenIfPresent(AfterSaleDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AfterSaleDO::getId));
    }

    default PageResult<AfterSaleDO> selectPage(Long userId, PageParam pageParam, String orgCode) {
        return selectPage(pageParam, new LambdaQueryWrapperX<AfterSaleDO>()
                .eqIfPresent(AfterSaleDO::getUserId, userId)
                .orderByDesc(AfterSaleDO::getId));
    }

    default int updateByIdAndStatus(Long id, Integer status, AfterSaleDO update) {
        return update(update, new LambdaUpdateWrapper<AfterSaleDO>()
                .eq(AfterSaleDO::getId, id).eq(AfterSaleDO::getStatus, status));
    }

    default int updateByOrderIdAndStatus(Long id, AfterSaleDO update) {
        return update(update, new LambdaUpdateWrapper<AfterSaleDO>()
            .eq(AfterSaleDO::getOrderId, id));
    }

    default AfterSaleDO selectByIdAndUserId(Long id, Long userId,String orgCode) {
        return selectOne(new LambdaQueryWrapperX<AfterSaleDO>()
                .eqIfPresent(AfterSaleDO::getId, id)
                .eqIfPresent(AfterSaleDO::getUserId, userId));
    }

    default Long selectCountByUserIdAndStatus(Long userId, Collection<Integer> statuses,String orgCode) {
        return selectCount(new LambdaQueryWrapperX<AfterSaleDO>()
                .eq(AfterSaleDO::getUserId, userId)
                .in(AfterSaleDO::getStatus, statuses));
    }

}
