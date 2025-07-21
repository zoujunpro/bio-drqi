package com.bio.drqi.manage.seed;

import com.bio.drqi.common.dto.PageDTO;
import lombok.Data;

@Data
public class BreedListReqDTO extends PageDTO {
    private Integer speciesId;
}
