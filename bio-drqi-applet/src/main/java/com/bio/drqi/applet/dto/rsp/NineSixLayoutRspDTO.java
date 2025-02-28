package com.bio.drqi.applet.dto.rsp;

import com.bio.drqi.manage.base.SampleUnitDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NineSixLayoutRspDTO {
    private List<List<SampleUnitDTO>> layout=new ArrayList<>();
}
