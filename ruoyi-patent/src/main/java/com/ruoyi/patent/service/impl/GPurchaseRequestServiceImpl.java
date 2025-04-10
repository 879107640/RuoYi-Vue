package com.ruoyi.patent.service.impl;

import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.uuid.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.patent.mapper.GPurchaseRequestMapper;
import com.ruoyi.patent.domain.GPurchaseRequest;
import com.ruoyi.patent.service.IGPurchaseRequestService;

/**
 * 求购信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-04-11
 */
@Service
public class GPurchaseRequestServiceImpl implements IGPurchaseRequestService {
    @Autowired
    private GPurchaseRequestMapper gPurchaseRequestMapper;

    /**
     * 查询求购信息
     *
     * @param id 求购信息主键
     * @return 求购信息
     */
    @Override
    public GPurchaseRequest selectGPurchaseRequestById(String id) {
        return gPurchaseRequestMapper.selectGPurchaseRequestById(id);
    }

    /**
     * 查询求购信息列表
     *
     * @param gPurchaseRequest 求购信息
     * @return 求购信息
     */
    @Override
    public List<GPurchaseRequest> selectGPurchaseRequestList(GPurchaseRequest gPurchaseRequest) {
        return gPurchaseRequestMapper.selectGPurchaseRequestList(gPurchaseRequest);
    }

    /**
     * 新增求购信息
     *
     * @param gPurchaseRequest 求购信息
     * @return 结果
     */
    @Override
    public int insertGPurchaseRequest(GPurchaseRequest gPurchaseRequest) {
        gPurchaseRequest.setCreateTime(DateUtils.getNowDate());
        gPurchaseRequest.setId(UUID.randomUUID().toString());
        return gPurchaseRequestMapper.insertGPurchaseRequest(gPurchaseRequest);
    }

    /**
     * 修改求购信息
     *
     * @param gPurchaseRequest 求购信息
     * @return 结果
     */
    @Override
    public int updateGPurchaseRequest(GPurchaseRequest gPurchaseRequest) {
        gPurchaseRequest.setUpdateTime(DateUtils.getNowDate());
        return gPurchaseRequestMapper.updateGPurchaseRequest(gPurchaseRequest);
    }

    /**
     * 批量删除求购信息
     *
     * @param ids 需要删除的求购信息主键
     * @return 结果
     */
    @Override
    public int deleteGPurchaseRequestByIds(String[] ids) {
        return gPurchaseRequestMapper.deleteGPurchaseRequestByIds(ids);
    }

    /**
     * 删除求购信息信息
     *
     * @param id 求购信息主键
     * @return 结果
     */
    @Override
    public int deleteGPurchaseRequestById(String id) {
        return gPurchaseRequestMapper.deleteGPurchaseRequestById(id);
    }
}
