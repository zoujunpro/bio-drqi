package com.bio.drqi.manage.dto.plant;

import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.drqi.common.dto.BaseBioTaskDTO;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class PlantDataReportDTO extends BaseBioTaskDTO {

    @NotBlank(message = "参数缺失：excelUrl")
    private String excelUrl;


    @NotEmpty(message = "数据缺失")
    private List<Content> contentList;

    @Data
    @Valid
    public static class Content {

        @ExcelProperty("种植编号")
        private String plantCode;

        @ExcelProperty("播种/移苗日期")
        private String plantDate;

        @ExcelProperty("移栽日期")
        private String transplantDate;

        @ExcelProperty("春化开始日期")
        private String vernalizationBeginDate;

        @ExcelProperty("春化结束日期")
        private String vernalizationEndDate;

        @ExcelProperty("植株状态")
        private String plantStatus;

        @ExcelProperty("授粉方式")
        private String pollinationMethod;


        @ExcelProperty("授粉时间")
        private String pollinationDate;


        @ExcelProperty("收获日期")
        private String harvestDate;

        @ExcelProperty("拔节期")
        private String ba_jie_qi;


        @ExcelProperty("散粉期")
        private String shan_fen_qi;

        @ExcelProperty("吐丝期")
        private String tu_si_qi;

        @ExcelProperty("抽穗期")
        private String chou_hui_qi;

        @ExcelProperty("始花期")
        private String shi_hua_qi;

        @ExcelProperty("盛花期")
        private String sheng_hua_qi;

        @ExcelProperty("扬花期")
        private String yang_hua_qi;

        @ExcelProperty("鼓粒期")
        private String gu_li_qi;

        @ExcelProperty("成熟期")
        private String cheng_shu_qi;
    }


}
