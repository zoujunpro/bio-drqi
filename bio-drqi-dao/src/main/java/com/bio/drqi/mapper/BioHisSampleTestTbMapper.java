package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Collection;

import com.bio.drqi.domain.BioHisSampleTestTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【bio_his_sample_test_tb(取样检测信息表)】的数据库操作Mapper
* @createDate 2025-11-21 14:13:42
* @Entity com.bio.drqi.domain.BioHisSampleTestTb
*/
public interface BioHisSampleTestTbMapper extends BaseMapper<BioHisSampleTestTb> {

    int deleteByApplyNo(@Param("applyNo") String applyNo);

    int insertBatch(@Param("bioHisSampleTestTbCollection") Collection<BioHisSampleTestTb> bioHisSampleTestTbCollection);

}




