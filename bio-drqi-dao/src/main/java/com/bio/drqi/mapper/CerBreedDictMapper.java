package com.bio.drqi.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.CerBreedDict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【cer_breed_dict】的数据库操作Mapper
* @createDate 2023-12-28 09:03:07
* @Entity com.bio.cer.domain.CerBreedDict
*/
public interface CerBreedDictMapper extends BaseMapper<CerBreedDict> {

    List<CerBreedDict> selectAllBySpeciesCode(@Param("speciesCode") String speciesCode);

    CerBreedDict selectOneByBreedCodeAndSpeciesCode(@Param("breedCode") String breedCode, @Param("speciesCode") String speciesCode);

    CerBreedDict selectOneByBreedNameAndSpeciesCode(@Param("breedName") String breedName, @Param("speciesCode") String speciesCode);

    List<CerBreedDict> selectAll();

}




