package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Collection;

import com.bio.drqi.domain.BioSampleSampleOneResultTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【bio_sample_sample_one_result_tb(一代测序结果)】的数据库操作Mapper
* @createDate 2025-10-28 17:18:21
* @Entity com.bio.drqi.domain.BioSampleSampleOneResultTb
*/
public interface BioSampleSampleOneResultTbMapper extends BaseMapper<BioSampleSampleOneResultTb> {

    int insertBatch(@Param("bioSampleSampleOneResultTbCollection") Collection<BioSampleSampleOneResultTb> bioSampleSampleOneResultTbCollection);

}




