package com.bio.drqi.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.bio.drqi.domain.BioPrintLabelInfoTb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author zou'jun
* @description 针对表【bio_print_label_info_tb(打印信息表)】的数据库操作Mapper
* @createDate 2024-12-16 16:12:33
* @Entity com.bio.drqi.domain.BioPrintLabelInfoTb
*/
public interface BioPrintLabelInfoTbMapper extends BaseMapper<BioPrintLabelInfoTb> {

    BioPrintLabelInfoTb selectOneByUniqueCode(@Param("uniqueCode") String uniqueCode);

    List<BioPrintLabelInfoTb> searchAllByLabelType(@Param("labelType") String labelType);

}




