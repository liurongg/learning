package com.xiaoshu.dao;

import com.xiaoshu.base.dao.BaseMapper;
import com.xiaoshu.entity.Fenxiao;
import com.xiaoshu.entity.FenxiaoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface FenxiaoMapper extends BaseMapper<Fenxiao> {
    long countByExample(FenxiaoExample example);

    int deleteByExample(FenxiaoExample example);

    List<Fenxiao> selectByExample(FenxiaoExample example);

    int updateByExampleSelective(@Param("record") Fenxiao record, @Param("example") FenxiaoExample example);

    int updateByExample(@Param("record") Fenxiao record, @Param("example") FenxiaoExample example);
}