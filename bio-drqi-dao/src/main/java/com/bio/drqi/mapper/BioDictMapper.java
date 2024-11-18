package com.bio.drqi.mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

import com.bio.drqi.domain.BioDict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【bio_dict(字典信息表)】的数据库操作Mapper
* @createDate 2024-06-18 15:33:44
* @Entity com.bio.cer.domain.BioDict
*/
public interface BioDictMapper extends BaseMapper<BioDict> {
    List<BioDict> selectAll();

    BioDict selectOneByDictTypeAndDictValueCode(@Param("dictType") String dictType, @Param("dictValueCode") String dictValueCode);


    BioDict selectOneByDictTypeAndDictValueName(@Param("dictType") String dictType, @Param("dictValueName") String dictValueName);
}




