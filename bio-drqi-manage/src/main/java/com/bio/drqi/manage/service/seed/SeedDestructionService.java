package com.bio.drqi.manage.service.seed;

import com.bio.drqi.seed.SeedDestructionPageReqDTO;
import com.bio.drqi.seed.SeedDestructionPageRspDTO;
import com.github.pagehelper.PageInfo;

public interface SeedDestructionService {

    PageInfo<SeedDestructionPageRspDTO> listPage(SeedDestructionPageReqDTO seedDestructionPageReqDTO);
}
