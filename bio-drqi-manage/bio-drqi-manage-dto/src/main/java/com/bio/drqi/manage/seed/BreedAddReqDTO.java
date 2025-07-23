package com.bio.drqi.manage.seed;

import lombok.Data;

@Data
public class BreedAddReqDTO {

    /**
     *
     */
    private String breedName;

    /**
     * 物种
     */
    private Integer speciesId;
}
