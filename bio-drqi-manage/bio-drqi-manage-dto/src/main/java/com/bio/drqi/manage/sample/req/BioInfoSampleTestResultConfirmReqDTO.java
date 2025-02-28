package com.bio.drqi.manage.sample.req;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BioInfoSampleTestResultConfirmReqDTO {

    private Integer id;

    private List<Integer> bioInfoIdList=new ArrayList<>();





}
