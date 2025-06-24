package com.ruoyi.patent.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.patent.domain.GPatentFavorites;
import com.ruoyi.patent.mapper.GPatentFavoritesMapper;
import com.ruoyi.patent.service.IGPatentFavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户收藏专利Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-23
 */
@Service
public class GPatentFavoritesServiceImpl implements IGPatentFavoritesService {

    @Autowired
    private GPatentFavoritesMapper gPatentFavoritesMapper;
    
    @Override
    public int deleteByPatentIdAndUserId(String patentId, Long userId) {
        return gPatentFavoritesMapper.deleteByPatentIdAndUserId(patentId, userId);
    }

    @Override
    public int insertByPatentIdAndUserId(String patentId, Long userId) {
        return gPatentFavoritesMapper.insertByPatentIdAndUserId(patentId, userId);
    }
}