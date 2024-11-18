package com.bio.drqi.project.rsp;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ConversionAndTransRspDTO {

    private Integer id;


    /**
     * 交接日期
     */
    private String handoverDate;

    /**
     * 提交日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;

    /**
     * 提交人
     */
    private Integer createUserId;

    /**
     * 提交人名称
     */
    private String createUserName;

    /**
     * 任务编号
     */
    private String taskNum;

    private String imageUrl;

    private String remark;
    /**
     * 图片地址
     */
    private List<String> imageUrlList;

    private Integer transNumber;


    public List<String> getImageUrlList() {
        return JSONUtil.toList(this.getImageUrl(),String.class);
    }

    public void setImageUrlList(List<String> imageUrlList) {
        this.imageUrlList = imageUrlList;
    }
}
