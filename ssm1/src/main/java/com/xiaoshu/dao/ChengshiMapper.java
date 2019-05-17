package com.xiaoshu.dao;

import com.xiaoshu.base.dao.BaseMapper;
import com.xiaoshu.entity.Chengshi;
import com.xiaoshu.entity.ChengshiExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ChengshiMapper extends BaseMapper<Chengshi> {
    long countByExample(ChengshiExample example);

    int deleteByExample(ChengshiExample example);

    List<Chengshi> selectByExample(ChengshiExample example);

    int updateByExampleSelective(@Param("record") Chengshi record, @Param("example") ChengshiExample example);

    int updateByExample(@Param("record") Chengshi record, @Param("example") ChengshiExample example);
}