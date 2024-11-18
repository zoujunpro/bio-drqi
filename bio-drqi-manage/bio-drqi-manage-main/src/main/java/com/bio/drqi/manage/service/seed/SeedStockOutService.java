package com.bio.drqi.manage.service.seed;

import com.bio.cer.seed.SeedStockOutReqDTO;
import com.bio.cer.seed.SeedStockOutRspDTO;
import com.github.pagehelper.PageInfo;

public interface SeedStockOutService {

    PageInfo<SeedStockOutRspDTO> listPage(SeedStockOutReqDTO seedStockOutReqDTO);
}
