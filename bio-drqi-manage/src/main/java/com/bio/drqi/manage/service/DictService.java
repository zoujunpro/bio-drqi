package com.bio.drqi.manage.service;

import com.bio.drqi.conf.AcceptorMaterialListRspDTO;
import com.bio.drqi.conf.BreedListRspDTO;
import com.bio.drqi.conf.SeedProduceAddressListRsp;
import com.bio.drqi.conf.SpeciesBreedListRspDTO;
import com.bio.drqi.system.rsp.DictInfoRspDTO;

import java.util.List;

public interface DictService {
    List<DictInfoRspDTO> list();

    List<SeedProduceAddressListRsp> seedProduceAddressList();

    /**
     * 获取物种下材料
     *
     * @return
     */
    List<AcceptorMaterialListRspDTO> acceptorMaterialList(String speciesCode);

    List<BreedListRspDTO> breedList(String speciesCode);

    List<SpeciesBreedListRspDTO> speciesBreedList();


}

