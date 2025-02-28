package com.bio.drqi.manage.service;

import com.bio.drqi.manage.conf.AcceptorMaterialListRspDTO;
import com.bio.drqi.manage.conf.BreedListRspDTO;
import com.bio.drqi.manage.conf.SeedProduceAddressListRsp;
import com.bio.drqi.manage.conf.SpeciesBreedListRspDTO;
import com.bio.drqi.manage.system.rsp.DictInfoRspDTO;

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

