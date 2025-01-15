package com.bio.drqi.manage.service.seed;

import com.bio.drqi.seed.*;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface SeedStoreService {
    SeedDetailRspDTO querySeedByNum(String seedNum);

    PageInfo<SeedStockPageRspDTO> listPage(SeedStockPageReqDTO seedStockPageReqDTO);

    PageInfo<SeedStockPageRspDTO> queryList(SeedStockPageReqDTO seedStockPageReqDTO);

    void moveStockLocationNum(List<MoveStockLocationNumReqDTO> moveStockLocationNumReqDTOList);

    void aliasName(AliasNameSeedReqDTO aliasNameSeedReqDTO);

    List<SeedOperateDetailRspDTO> seedOperateDetail(String seedNum);
}
