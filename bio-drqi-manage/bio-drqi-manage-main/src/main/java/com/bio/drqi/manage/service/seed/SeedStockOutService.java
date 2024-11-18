package com.bio.drqi.manage.service.seed;

import com.bio.drqi.seed.SeedStockOutReqDTO;
import com.bio.drqi.seed.SeedStockOutRspDTO;
import com.github.pagehelper.PageInfo;

public interface SeedStockOutService {

    PageInfo<SeedStockOutRspDTO> listPage(SeedStockOutReqDTO seedStockOutReqDTO);
}
