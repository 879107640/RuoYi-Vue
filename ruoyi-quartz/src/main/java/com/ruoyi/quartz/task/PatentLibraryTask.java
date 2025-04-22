package com.ruoyi.quartz.task;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.patent.domain.GPatentLibrary;
import com.ruoyi.patent.mapper.GPatentLibraryMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

//@Component
public class PatentLibraryTask {

  @Resource
  private GPatentLibraryMapper gPatentLibraryMapper;
}
