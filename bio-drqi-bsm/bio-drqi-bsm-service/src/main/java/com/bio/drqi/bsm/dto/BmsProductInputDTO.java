package com.bio.drqi.bsm.dto;

import cn.hutool.json.JSONUtil;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class BmsProductInputDTO {

    @NotBlank(message = "入库参数缺少：订单明细")
    private String orderDetailNum;

    /**
     * 批次号
     */

    private String batchNo;

    /**
     * 入库数量
     */
    @NotNull(message = "入库参数缺少：入库数量")
    private Integer number;

    /**
     * 库存位置号
     */
    private List<String> stockLocationNumberList;


    public static void main(String[] args) {
        List<BmsProductInputDTO> list=new ArrayList<>();
        BmsProductInputDTO bmsProductInputDTO1=new BmsProductInputDTO();
        bmsProductInputDTO1.setOrderDetailNum("1");
        bmsProductInputDTO1.setBatchNo("2");
        bmsProductInputDTO1.setNumber(1);
        bmsProductInputDTO1.setStockLocationNumberList(Arrays.asList("1","2"));
        list.add(bmsProductInputDTO1);
        BmsProductInputDTO bmsProductInputDTO2=new BmsProductInputDTO();
        bmsProductInputDTO2.setOrderDetailNum("2");
        bmsProductInputDTO2.setBatchNo("2");
        bmsProductInputDTO2.setNumber(1);
        bmsProductInputDTO2.setStockLocationNumberList(Arrays.asList("1","2"));
        list.add(bmsProductInputDTO2);
        System.out.println(JSONUtil.toJsonStr(list));


    }
}
