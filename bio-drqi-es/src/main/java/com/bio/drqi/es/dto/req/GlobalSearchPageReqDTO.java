package com.bio.drqi.es.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public  class GlobalSearchPageReqDTO {

    @NotBlank(message = "systemCode 不能为空")
    private String systemCode;

    @NotBlank(message = "keyword 不能为空")
    private String keyword;
    private List<String> bizTypes;
    private Integer pageSize;
    private Object[] searchAfter;

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<String> getBizTypes() {
        return bizTypes;
    }

    public void setBizTypes(List<String> bizTypes) {
        this.bizTypes = bizTypes;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Object[] getSearchAfter() {
        return searchAfter;
    }

    public void setSearchAfter(Object[] searchAfter) {
        this.searchAfter = searchAfter;
    }
}
