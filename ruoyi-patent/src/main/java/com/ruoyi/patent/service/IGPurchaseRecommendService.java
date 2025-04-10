package com.ruoyi.patent.service;

import java.util.List;

import com.ruoyi.patent.domain.GPurchaseRecommend;

/**
 * 推荐信息Service接口
 *
 * @author ruoyi
 * @date 2025-04-11
 */
public interface IGPurchaseRecommendService {
    /**
     * 查询推荐信息
     *
     * @param id 推荐信息主键
     * @return 推荐信息
     */
    public GPurchaseRecommend selectGPurchaseRecommendById(String id);

    /**
     * 查询推荐信息列表
     *
     * @param gPurchaseRecommend 推荐信息
     * @return 推荐信息集合
     */
    public List<GPurchaseRecommend> selectGPurchaseRecommendList(GPurchaseRecommend gPurchaseRecommend);

    /**
     * 新增推荐信息
     *
     * @param gPurchaseRecommend 推荐信息
     * @return 结果
     */
    public int insertGPurchaseRecommend(GPurchaseRecommend gPurchaseRecommend);

    /**
     * 修改推荐信息
     *
     * @param gPurchaseRecommend 推荐信息
     * @return 结果
     */
    public int updateGPurchaseRecommend(GPurchaseRecommend gPurchaseRecommend);

    /**
     * 批量删除推荐信息
     *
     * @param ids 需要删除的推荐信息主键集合
     * @return 结果
     */
    public int deleteGPurchaseRecommendByIds(String[] ids);

    /**
     * 删除推荐信息信息
     *
     * @param id 推荐信息主键
     * @return 结果
     */
    public int deleteGPurchaseRecommendById(String id);
}
