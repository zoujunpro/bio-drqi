package com.bio.drqi.manage.service.seed;


import com.bio.drqi.manage.seed.BreedAddReqDTO;
import com.bio.drqi.manage.seed.BreedEditReqDTO;
import com.bio.drqi.manage.seed.BreedListReqDTO;
import com.bio.drqi.manage.seed.BreedListRspDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface SeedBreedDictService {

    PageInfo<BreedListRspDTO> listPage(BreedListReqDTO breedListReqDTO);

    /**
     * 品种列表
     *
     * @param speciesId
     * @return
     */
    List<BreedListRspDTO> list(String speciesCode);

    /**
     * 新增品种
     *
     * @param breedAddReqDTO
     * @return
     */
    void add(BreedAddReqDTO breedAddReqDTO);

    /**
     * 删除品种
     *
     * @param id
     * @return
     */
    void delete(Integer id);

    /**
     * 更新品种
     *
     * @param breedEditReqDTO
     * @return
     */
    void edit(BreedEditReqDTO breedEditReqDTO);
}
