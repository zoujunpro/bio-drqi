package com.bio.drqi.manage.service.seed;

import com.bio.base.base.PageDTO;
import com.bio.base.bio.req.SpeciesAddReqDTO;
import com.bio.base.bio.req.SpeciesEditDTO;
import com.bio.base.bio.rsp.SpeciesListRspDTO;
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
}
