package com.bio.drqi.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import java.util.Collection;

import com.bio.drqi.domain.BioSampleTestOneResultTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【bio_sample_test_one_result_tb(一代测序结果)】的数据库操作Mapper
* @createDate 2025-10-28 17:18:21
* @Entity com.bio.drqi.domain.BioSampleSampleOneResultTb
*/
public interface BioSampleTestOneResultTbMapper extends BaseMapper<BioSampleTestOneResultTb> {

    int insertBatch(@Param("bioSampleSampleOneResultTbCollection") Collection<BioSampleTestOneResultTb> bioSampleSampleOneResultTbCollection);

    int deleteByTaskNum(@Param("taskNum") String taskNum);

    List<BioSampleTestOneResultTb> selectSelective(BioSampleTestOneResultTb bioSampleSampleOneResultTb);

}




