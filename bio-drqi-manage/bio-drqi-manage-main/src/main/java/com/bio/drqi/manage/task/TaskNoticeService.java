package com.bio.drqi.manage.task;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.cer.domain.BioTaskDtlTb;
import com.bio.cer.enums.TaskCategoryEnum;
import com.bio.cer.listener.CerProjectTaskListener;
import com.bio.cer.listener.CerSeedTaskListener;
import com.bio.cer.listener.EventType;
import com.bio.cer.mapper.BioTaskDtlTbMapper;
import com.easyflow.engine.FlowEngineService;
import com.easyflow.engine.entity.FlowTaskTb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RefreshScope
public class
TaskNoticeService {

    @Resource
    public FlowEngineService flowEngineService;

    @Resource
    private CerSeedTaskListener cerSeedTaskListener;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Resource
    private CerProjectTaskListener cerProjectTaskListener;


    @Scheduled(cron = "${cer.properties.noticeCron: 0 0 10 * * ?}")
    public void notice() {
        if (!isWorkdayUsingCalendar()) {
            log.info("非工作日不执行");
            return;
        }
        List<FlowTaskTb> flowTaskTbList = flowEngineService.getQueryService().findAllActiveTask();
        if (CollectionUtil.isEmpty(flowTaskTbList)) {
            return;
        }
        List<Long> instanceIdList = flowTaskTbList.stream().map(FlowTaskTb::getInstanceId).distinct().collect(Collectors.toList());
        for (Long instanceId : instanceIdList) {
            BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByInstanceId(instanceId);
            if(TaskCategoryEnum.seed.name().equals(bioTaskDtlTb.getTaskCategory())){
                cerSeedTaskListener.notice(EventType.active, () -> bioTaskDtlTb);
            }else {
                cerProjectTaskListener.notice(EventType.active, () -> bioTaskDtlTb);

            }

        }

    }

    private boolean isWorkdayUsingCalendar() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        // 判断是否是周六或者周日
        return (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY);
    }
}
