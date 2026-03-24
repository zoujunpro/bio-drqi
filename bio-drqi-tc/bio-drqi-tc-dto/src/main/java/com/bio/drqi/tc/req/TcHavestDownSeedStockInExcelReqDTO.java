package com.bio.drqi.tc.req;

import cn.hutool.json.JSONUtil;
import com.bio.drqi.common.validator.EnumValue;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Data
public class TcHavestDownSeedStockInExcelReqDTO {

    @NotEmpty(message = "入参缺失")
    private List<Integer> idList;

    public static void main(String[] args) {
        TcHavestDownSeedStockInExcelReqDTO tcHavestDownSeedStockInExcelReqDTO=new TcHavestDownSeedStockInExcelReqDTO();
        List<Integer> integers=new ArrayList<>();
        integers.add(1);
        tcHavestDownSeedStockInExcelReqDTO.setIdList(integers);
        System.out.println(JSONUtil.toJsonStr(tcHavestDownSeedStockInExcelReqDTO));
    }
}
