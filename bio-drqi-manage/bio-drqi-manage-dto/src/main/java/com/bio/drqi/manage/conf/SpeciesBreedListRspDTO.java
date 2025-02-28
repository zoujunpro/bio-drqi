package com.bio.drqi.manage.conf;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SpeciesBreedListRspDTO {

    /**
     * 物种名称
     */
    private String speciesName;

    /**
     * 物种编码
     */
    private String speciesCode;

    private List<Breed> breedList =new ArrayList<>();

    @Data
    public static class Breed{
        private String breedCode;
        private String breedName;
    }


}
