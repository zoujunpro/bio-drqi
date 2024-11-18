package com.bio.drqi.timePlan;

import cn.hutool.core.date.DateUtil;
import com.bio.common.core.util.StringUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class VectorTaskTimePlanListRspDTO {

    private Long estimatedTotalDay=0L;

    private Long actualTotalDay=0L;

    private List<Content> contentList = new ArrayList<>();

    @Data
    public static class Content {

        /**
         * 主键ID
         */
        private Integer id;

        /**
         * 实施方案ID
         */
        private Integer vectorTaskId;

        /**
         * 事件类型
         */
        private String eventType;

        /**
         * 预估开始时间
         */
        private String estimatedStartTime;

        /**
         * 预估结束时间
         */
        private String estimatedEndTime;

        private Long estimatedDate;

        /**
         * 实际开始时间
         */
        private String actualStartTime;

        /**
         * 实际结束时间
         */
        private String actualEndTime;

        private Long actualDate;

        /**
         * 用户ID
         */
        private Integer userId;

        /**
         * 用户名
         */
        private String userName;

        /**
         * 是否超时 ，true超时 false没有超时
         */
        private boolean overTimeFlag;
    }

    public VectorTaskTimePlanListRspDTO countEstimatedTotalDay() {
        for (Content content : contentList) {
            if(StringUtils.isNotEmpty(content.estimatedStartTime)&&StringUtils.isNotEmpty(content.estimatedEndTime)){
                content.setEstimatedDate(DateUtil.betweenDay(DateUtil.parse(content.getEstimatedStartTime(), "yyyy-MM-dd"), DateUtil.parse(content.getEstimatedEndTime(), "yyyy-MM-dd"), true)+1L);
                this.estimatedTotalDay=this.estimatedTotalDay+content.getEstimatedDate();
            }
            if(StringUtils.isNotEmpty(content.actualStartTime)&&StringUtils.isNotEmpty(content.actualEndTime)){
                content.setActualDate(DateUtil.betweenDay(DateUtil.parse(content.getActualStartTime(), "yyyy-MM-dd"), DateUtil.parse(content.getActualEndTime(), "yyyy-MM-dd"), true)+1L);
                this.actualTotalDay=this.actualTotalDay+content.getActualDate();
            }
        }

        return this;
    }
    public VectorTaskTimePlanListRspDTO buildOverTimeFlag() {
        for (Content content : contentList) {
            if (StringUtils.isNotEmpty(content.actualEndTime)) {
                if (content.estimatedEndTime.compareTo(content.actualEndTime) >= 0) {
                    content.overTimeFlag = false;
                } else {
                    content.overTimeFlag = true;
                }
            } else {
                String date = DateUtil.format(new Date(), "yyyy-MM-dd");
                if (content.estimatedEndTime.compareTo(date) >= 0) {
                    content.overTimeFlag = false;
                } else {
                    content.overTimeFlag = true;
                }
            }
        }
        return this;
    }
}
