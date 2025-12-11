package com.bio.drqi.manage.service.seed;

import com.bio.drqi.manage.seed.SeedDictAddReqDTO;
import com.bio.drqi.manage.seed.SeedDictEditReqDTO;
import com.bio.drqi.manage.seed.SeedDictTreeListRspDTO;

import java.util.List;
public interface SeedBioDictService {
    List<SeedDictTreeListRspDTO> list();

    void add(SeedDictAddReqDTO seedDictAddReqDTO);

    void delete(Integer id);

    void edit(SeedDictEditReqDTO seedDictEditReqDTO);
}
