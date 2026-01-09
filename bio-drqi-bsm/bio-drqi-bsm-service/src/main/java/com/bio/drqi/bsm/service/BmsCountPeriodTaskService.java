package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.dto.BmsCountPeriodTaskDTO;

import java.util.List;

public interface BmsCountPeriodTaskService {
    public void createPeriodData(String dateTime, List<BmsCountPeriodTaskDTO> list);
}
