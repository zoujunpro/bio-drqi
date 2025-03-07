package com.bio.drqi.bsm.rsp;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class BmsProductTyListPageRspDTO {
    /**
     * 主键ID
     */
    @TableId
    private Integer id;

    /**
     * 商品类型编号
     */
    private String productTypeCode;

    /**
     * 商品类型名称
     */
    private String productTypeName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人名称
     */
    private String createUserName;
}
