package com.bio.drqi.mapper;
import java.util.List;

import com.bio.drqi.domain.CerAcceptorMaterialDict;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【cer_acceptor_material_dict】的数据库操作Mapper
* @createDate 2023-12-22 14:33:02
* @Entity com.bio.cer.domain.CerAcceptorMaterialDict
*/
public interface CerAcceptorMaterialDictMapper extends BaseMapper<CerAcceptorMaterialDict> {
    CerAcceptorMaterialDict selectOneByAcceptorMaterialCode(@Param("acceptorMaterialCode") String acceptorMaterialCode);

    List<CerAcceptorMaterialDict> selectAllBySpeciesCode(@Param("speciesCode") String speciesCode);
}




