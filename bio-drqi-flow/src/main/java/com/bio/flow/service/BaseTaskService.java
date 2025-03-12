package com.bio.flow.service;


import com.bio.drqi.domain.BioTaskDtlTb;

public interface BaseTaskService {

    void taskApply(BioTaskDtlTb bioTaskDtlTb);

    void executeTask(BioTaskDtlTb bioTaskDtlTb);

    void cancelTask(BioTaskDtlTb bioTaskDtlTb);
}
