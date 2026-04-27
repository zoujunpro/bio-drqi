package com.bio.drqi.es.service;

import com.bio.drqi.es.dto.req.TableSyncReqDTO;

public interface EsSyncService {


    void syncTable(TableSyncReqDTO tableSyncReqDTO);
}