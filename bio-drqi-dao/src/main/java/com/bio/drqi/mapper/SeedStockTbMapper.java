package com.bio.drqi.mapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.SeedStockTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author zou'jun
 * @description 针对表【seed_stock_tb】的数据库操作Mapper
 * @createDate 2023-12-28 09:12:24
 * @Entity com.bio.cer.domain.SeedStockTb
 */
public interface SeedStockTbMapper extends BaseMapper<SeedStockTb> {
    SeedStockTb selectOneBySeedNum(@Param("seedNum") String seedNum);

    int updateRemarksById(@Param("remarks") String remarks, @Param("id") Integer id);

    List<SeedStockTb> selectAllByBreedCodeAndGeneration(@Param("breedCode") String breedCode, @Param("generation") String generation);

    List<SeedStockTb> selectSelective(SeedStockTb seedStockTb);

    SeedStockTb selectOneByPlantCode(@Param("plantNum") String plantNum);


    int updateParentNumById(@Param("parentNum") String parentNum, @Param("id") Integer id);

    List<SeedStockTb> selectAllBySeedNumIn(@Param("seedNumList") Collection<String> seedNumList);

    List<SeedStockTb> selectAllByVectorTaskCode(@Param("vectorTaskCode") String vectorTaskCode);

    int deleteBySeedNumIn(@Param("seedNumList") Collection<String> seedNumList);

    List<SeedStockTb> selectAllByBreedCode(@Param("breedCode") String breedCode);

    List<SeedStockTb> selectAllByMatherSeedNum(@Param("matherSeedNum") String matherSeedNum);

    List<SeedStockTb> selectAllByFatherSeedNum(@Param("fatherSeedNum") String fatherSeedNum);

    int updatePlantCodeAndRemarksById(@Param("plantCode") String plantCode, @Param("remarks") String remarks, @Param("id") Integer id);

    int updatePdNumAndVectorTaskCodeAndProjectCodeById(@Param("pdNum") String pdNum, @Param("vectorTaskCode") String vectorTaskCode, @Param("projectCode") String projectCode, @Param("id") Integer id);
}




