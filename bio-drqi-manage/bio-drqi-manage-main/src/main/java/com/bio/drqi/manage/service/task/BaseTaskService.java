package com.bio.drqi.manage.service.task;


import com.bio.drqi.domain.BioTaskDtlTb;

public interface BaseTaskService {

    void taskCheck(BioTaskDtlTb bioTaskDtlTb);

    void executeTask(BioTaskDtlTb bioTaskDtlTb);

    void cancelTask(BioTaskDtlTb bioTaskDtlTb);
}
