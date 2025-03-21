package com.bio.drqi.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.BmsBrandTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【bms_brand_tb(品牌信息表)】的数据库操作Mapper
* @createDate 2025-02-27 10:16:09
* @Entity com.bio.drqi.domain.BmsBrandTb
*/
public interface BmsBrandTbMapper extends BaseMapper<BmsBrandTb> {

    BmsBrandTb selectOneByBrandCode(@Param("brandCode") String brandCode);

    List<BmsBrandTb> selectSelective(BmsBrandTb bmsBrandTb);




}




