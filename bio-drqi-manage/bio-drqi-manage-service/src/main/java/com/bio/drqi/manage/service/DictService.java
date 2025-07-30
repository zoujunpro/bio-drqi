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




}

