package com.bio.drqi.manage.service.seed;

import com.bio.cer.seed.*;
import com.bio.cer.seedtask.SeedInDataReqDTO;
import com.bio.drqi.manage.dto.seed.SeedInStoreDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface SeedStockInService {
    PageInfo<SeedStockInRspDTO> listPage(SeedStockInReqDTO seedStockInReqDTO);

    List<ParseSeedInExcelRspDTO> parseSeedInExcel(ParseSeedInExcelReqDTO parseSeedInExcelReqDTO);

   void store( SeedInStoreReqDTO seedInStoreReqDTO);


    PageInfo<SeedInStoreDTO.ExecuteFormContent> seedInData(@RequestParam @Validated SeedInDataReqDTO seedInDataReqDTO);
}
