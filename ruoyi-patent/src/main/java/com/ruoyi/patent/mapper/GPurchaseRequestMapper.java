package com.ruoyi.patent.mapper;

import java.util.List;

import com.ruoyi.patent.domain.GPurchaseRequest;

/**
 * 求购信息Mapper接口
 *
 * @author ruoyi
 * @date 2025-04-11
 */
public interface GPurchaseRequestMapper {
    /**
     * 查询求购信息
     *
     * @param id 求购信息主键
     * @return 求购信息
     */
    public GPurchaseRequest selectGPurchaseRequestById(String id);

    /**
     * 查询求购信息列表
     *
     * @param gPurchaseRequest 求购信息
     * @return 求购信息集合
     */
    public List<GPurchaseRequest> selectGPurchaseRequestList(GPurchaseRequest gPurchaseRequest);

    /**
     * 新增求购信息
     *
     * @param gPurchaseRequest 求购信息
     * @return 结果
     */
    public int insertGPurchaseRequest(GPurchaseRequest gPurchaseRequest);

    /**
     * 修改求购信息
     *
     * @param gPurchaseRequest 求购信息
     * @return 结果
     */
    public int updateGPurchaseRequest(GPurchaseRequest gPurchaseRequest);

    /**
     * 删除求购信息
     *
     * @param id 求购信息主键
     * @return 结果
     */
    public int deleteGPurchaseRequestById(String id);

    /**
     * 批量删除求购信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGPurchaseRequestByIds(String[] ids);
}
