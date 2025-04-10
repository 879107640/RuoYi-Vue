package com.ruoyi.patent.service.impl;

import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.uuid.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.patent.mapper.GPurchaseRecommendMapper;
import com.ruoyi.patent.domain.GPurchaseRecommend;
import com.ruoyi.patent.service.IGPurchaseRecommendService;

/**
 * 推荐信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-04-11
 */
@Service
public class GPurchaseRecommendServiceImpl implements IGPurchaseRecommendService {
    @Autowired
    private GPurchaseRecommendMapper gPurchaseRecommendMapper;

    /**
     * 查询推荐信息
     *
     * @param id 推荐信息主键
     * @return 推荐信息
     */
    @Override
    public GPurchaseRecommend selectGPurchaseRecommendById(String id) {
        return gPurchaseRecommendMapper.selectGPurchaseRecommendById(id);
    }

    /**
     * 查询推荐信息列表
     *
     * @param gPurchaseRecommend 推荐信息
     * @return 推荐信息
     */
    @Override
    public List<GPurchaseRecommend> selectGPurchaseRecommendList(GPurchaseRecommend gPurchaseRecommend) {
        return gPurchaseRecommendMapper.selectGPurchaseRecommendList(gPurchaseRecommend);
    }

    /**
     * 新增推荐信息
     *
     * @param gPurchaseRecommend 推荐信息
     * @return 结果
     */
    @Override
    public int insertGPurchaseRecommend(GPurchaseRecommend gPurchaseRecommend) {
        gPurchaseRecommend.setCreateTime(DateUtils.getNowDate());
        gPurchaseRecommend.setId(UUID.randomUUID().toString());
        return gPurchaseRecommendMapper.insertGPurchaseRecommend(gPurchaseRecommend);
    }

    /**
     * 修改推荐信息
     *
     * @param gPurchaseRecommend 推荐信息
     * @return 结果
     */
    @Override
    public int updateGPurchaseRecommend(GPurchaseRecommend gPurchaseRecommend) {
        gPurchaseRecommend.setUpdateTime(DateUtils.getNowDate());
        return gPurchaseRecommendMapper.updateGPurchaseRecommend(gPurchaseRecommend);
    }

    /**
     * 批量删除推荐信息
     *
     * @param ids 需要删除的推荐信息主键
     * @return 结果
     */
    @Override
    public int deleteGPurchaseRecommendByIds(String[] ids) {
        return gPurchaseRecommendMapper.deleteGPurchaseRecommendByIds(ids);
    }

    /**
     * 删除推荐信息信息
     *
     * @param id 推荐信息主键
     * @return 结果
     */
    @Override
    public int deleteGPurchaseRecommendById(String id) {
        return gPurchaseRecommendMapper.deleteGPurchaseRecommendById(id);
    }
}
