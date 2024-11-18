package com.bio.drqi.manage.service.seed;

import com.bio.cer.seed.SeedDestructionPageReqDTO;
import com.bio.cer.seed.SeedDestructionPageRspDTO;
import com.github.pagehelper.PageInfo;

public interface SeedDestructionService {

    PageInfo<SeedDestructionPageRspDTO> listPage(SeedDestructionPageReqDTO seedDestructionPageReqDTO);
}
