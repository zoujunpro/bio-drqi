package com.bio.drqi.manage.seed;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class SeedDictTreeListRspDTO {
    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 字典类型
     */
    private String dictType;

    private List<DictContent> dictContentList=new ArrayList<>();


    @Data
    public static class DictContent {
        private Integer id;
        private String dictValueName;
        private String dictValueCode;

        public DictContent(Integer id, String dictValueName, String dictValueCode) {
            this.id = id;
            this.dictValueName = dictValueName;
            this.dictValueCode = dictValueCode;
        }
    }


}
