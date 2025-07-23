package com.bio.drqi.manage.service.seed;

import com.bio.drqi.common.dto.PageDTO;
import com.bio.drqi.manage.conf.SpeciesBreedListRspDTO;
import com.bio.drqi.manage.seed.SpeciesAddReqDTO;
import com.bio.drqi.manage.seed.SpeciesEditDTO;
import com.bio.drqi.manage.seed.SpeciesListRspDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface SeedSpeciesDictService {

    PageInfo<SpeciesListRspDTO> listPage(PageDTO pageDTO);


    /**
     * 品种列表
     *
     * @return
     */
    List<SpeciesListRspDTO> list();

    /**
     * 新增品种
     *
     * @return
     */
    void add(SpeciesAddReqDTO speciesAddReqDTO);

    /**
     * 删除品种
     *
     * @return
     */
    void delete(Integer id);

    /**
     * 更新品种
     *
     * @return
     */
    void edit(SpeciesEditDTO speciesEditDTO);

    List<SpeciesBreedListRspDTO> speciesBreedList();
}
