package com.bio.drqi.manage.base;

import lombok.Data;

import java.util.List;

@Data
public class PrintRspDTO {

    private String printName;

    private List<String> printDataList;

    public PrintRspDTO(String printName, List<String> printDataList) {
        this.printName = printName;
        this.printDataList = printDataList;
    }

    public PrintRspDTO() {
    }
}

