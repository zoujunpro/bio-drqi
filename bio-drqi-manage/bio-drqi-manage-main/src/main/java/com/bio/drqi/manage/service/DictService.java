package com.bio.drqi.manage.service;

import com.bio.cer.conf.AcceptorMaterialListRspDTO;
import com.bio.cer.conf.BreedListRspDTO;
import com.bio.cer.conf.SeedProduceAddressListRsp;
import com.bio.cer.conf.SpeciesBreedListRspDTO;
import com.bio.cer.system.rsp.DictInfoRspDTO;

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

