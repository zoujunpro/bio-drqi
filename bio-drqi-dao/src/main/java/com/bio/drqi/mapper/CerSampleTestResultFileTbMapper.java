package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

import com.bio.drqi.domain.CerSampleTestResultFileTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【cer_sample_test_result_file_tb(取样检测结果文件)】的数据库操作Mapper
* @createDate 2025-10-24 17:58:43
* @Entity com.bio.drqi.domain.CerSampleTestResultFileTb
*/
public interface CerSampleTestResultFileTbMapper extends BaseMapper<CerSampleTestResultFileTb> {

    List<CerSampleTestResultFileTb> selectSelective(CerSampleTestResultFileTb cerSampleTestResultFileTb);

}




