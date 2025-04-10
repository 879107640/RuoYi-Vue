package com.ruoyi.patent.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.patent.mapper.GPatentLibraryMapper;
import com.ruoyi.patent.domain.GPatentLibrary;
import com.ruoyi.patent.service.IGPatentLibraryService;

/**
 * 专利库数据Service业务层处理
 * 
 * @author hujch
 * @date 2025-04-10
 */
@Service
public class GPatentLibraryServiceImpl implements IGPatentLibraryService 
{
    @Autowired
    private GPatentLibraryMapper gPatentLibraryMapper;

    /**
     * 查询专利库数据
     * 
     * @param id 专利库数据主键
     * @return 专利库数据
     */
    @Override
    public GPatentLibrary selectGPatentLibraryById(String id)
    {
        return gPatentLibraryMapper.selectGPatentLibraryById(id);
    }

    /**
     * 查询专利库数据列表
     * 
     * @param gPatentLibrary 专利库数据
     * @return 专利库数据
     */
    @Override
    public List<GPatentLibrary> selectGPatentLibraryList(GPatentLibrary gPatentLibrary,int type)
    {
        return gPatentLibraryMapper.selectGPatentLibraryList(gPatentLibrary);
    }

    /**
     * 新增专利库数据
     * 
     * @param gPatentLibrary 专利库数据
     * @return 结果
     */
    @Override
    public int insertGPatentLibrary(GPatentLibrary gPatentLibrary)
    {
        gPatentLibrary.setCreateTime(DateUtils.getNowDate());
        return gPatentLibraryMapper.insertGPatentLibrary(gPatentLibrary);
    }

    /**
     * 修改专利库数据
     * 
     * @param gPatentLibrary 专利库数据
     * @return 结果
     */
    @Override
    public int updateGPatentLibrary(GPatentLibrary gPatentLibrary)
    {
        gPatentLibrary.setUpdateTime(DateUtils.getNowDate());
        return gPatentLibraryMapper.updateGPatentLibrary(gPatentLibrary);
    }

    /**
     * 批量删除专利库数据
     * 
     * @param ids 需要删除的专利库数据主键
     * @return 结果
     */
    @Override
    public int deleteGPatentLibraryByIds(String[] ids)
    {
        return gPatentLibraryMapper.deleteGPatentLibraryByIds(ids);
    }

    /**
     * 删除专利库数据信息
     * 
     * @param id 专利库数据主键
     * @return 结果
     */
    @Override
    public int deleteGPatentLibraryById(String id)
    {
        return gPatentLibraryMapper.deleteGPatentLibraryById(id);
    }
}
