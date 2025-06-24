package com.ruoyi.patent.service;

import com.ruoyi.patent.domain.GPatentFavorites;
import java.util.List;

/**
 * 用户收藏专利Service接口
 * 
 * @author ruoyi
 * @date 2025-06-23
 */
public interface IGPatentFavoritesService {
    /**
     * 根据专利ID和用户ID删除收藏记录
     * 
     * @param patentId 专利ID
     * @param userId 用户ID
     * @return 结果
     */
    int deleteByPatentIdAndUserId(String patentId, Long userId);
    
    /**
     * 根据专利ID和用户ID保存收藏记录
     * 
     * @param patentId 专利ID
     * @param userId 用户ID
     * @return 结果
     */
    int insertByPatentIdAndUserId(String patentId, Long userId);
}