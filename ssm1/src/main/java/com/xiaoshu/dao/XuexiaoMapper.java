package com.xiaoshu.dao;

import com.xiaoshu.base.dao.BaseMapper;
import com.xiaoshu.entity.Xuexiao;
import com.xiaoshu.entity.XuexiaoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface XuexiaoMapper extends BaseMapper<Xuexiao> {
    long countByExample(XuexiaoExample example);

    int deleteByExample(XuexiaoExample example);

    List<Xuexiao> selectByExample(XuexiaoExample example);

    int updateByExampleSelective(@Param("record") Xuexiao record, @Param("example") XuexiaoExample example);

    int updateByExample(@Param("record") Xuexiao record, @Param("example") XuexiaoExample example);

	List<Xuexiao> selectByXuexiao(XuexiaoExample example);

	void getMQ(Xuexiao t);
}