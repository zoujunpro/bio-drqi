package com.bio.drqi.manage.service.seed;

import com.bio.drqi.manage.seed.SeedDestructionPageReqDTO;
import com.bio.drqi.manage.seed.SeedDestructionPageRspDTO;
import com.github.pagehelper.PageInfo;

public interface SeedDestructionService {

    PageInfo<SeedDestructionPageRspDTO> listPage(SeedDestructionPageReqDTO seedDestructionPageReqDTO);
}
