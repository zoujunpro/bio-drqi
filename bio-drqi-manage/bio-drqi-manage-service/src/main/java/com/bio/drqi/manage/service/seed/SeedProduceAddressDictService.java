package com.bio.drqi.manage.service.seed;

import com.bio.drqi.common.dto.PageDTO;
import com.bio.drqi.manage.conf.SeedProduceAddressListRsp;
import com.bio.drqi.manage.seed.SeedProduceAddressDictAddDTO;
import com.bio.drqi.manage.seed.SeedProduceAddressDictEditDTO;
import com.bio.drqi.manage.seed.SeedProduceAddressDictListRspDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface SeedProduceAddressDictService {

    PageInfo<SeedProduceAddressDictListRspDTO> listPage(PageDTO pageDTO);

    void edit(SeedProduceAddressDictEditDTO seedProduceAddressDictEditDTO);

    void add(SeedProduceAddressDictAddDTO seedProduceAddressDictAddDTO);

    void delete(Integer id);

    List<SeedProduceAddressListRsp> list();
}
