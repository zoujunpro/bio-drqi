package com.bio.drqi.mapper;
import java.util.List;
import com.bio.drqi.domain.SeedProduceAddressDict;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【seed_produce_address_dict(种子生产地点)】的数据库操作Mapper
* @createDate 2023-12-25 10:42:41
* @Entity com.bio.cer.domain.SeedProduceAddressDict
*/
public interface SeedProduceAddressDictMapper extends BaseMapper<SeedProduceAddressDict> {

    SeedProduceAddressDict selectOneByAddressName(@Param("addressName") String addressName);

    List<SeedProduceAddressDict> selectAll();

    SeedProduceAddressDict selectOneByAddressCode(@Param("addressCode") String addressCode);
}




