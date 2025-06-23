package com.bio.drqi.bsm.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class BmsOrderDetailExportExcelReqDTO {

    private List<Integer> idList;
}
