package com.ruoyi.patent.mapper;

import com.ruoyi.patent.domain.GPatentFavorites;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户收藏专利Mapper接口
 * 
 * @author ruoyi
 * @date 2025-06-23
 */
@Mapper
public interface GPatentFavoritesMapper extends BaseMapper<GPatentFavorites> {

    List<GPatentFavorites> selectByPatentIds(@Param("patentIds") List<String> patentIds, @Param("userId") Long userId);
    
    int deleteByPatentIdAndUserId(@Param("patentId") String patentId, @Param("userId") Long userId);
    
    int insertByPatentIdAndUserId(@Param("patentId") String patentId, @Param("userId") Long userId);

}