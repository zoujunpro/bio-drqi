package com.bio.drqi.seed;

import com.bio.drqi.base.PageDTO;
import lombok.Data;

@Data
public class SeedStockOutReqDTO extends PageDTO {
    private String seedNum;

    /**seed_testing_apply 考种  seed_breed_apply繁种  seed_preparation_apply备种  seed_other_out_apply其他 seed_destruction_apply种子销毁  */
    private String useToCode;
    private String taskNum;

}
