package com.bio.drqi.es.service;

import com.bio.drqi.es.dto.req.TableSyncReqDTO;
import com.bio.drqi.es.dto.req.TablesSyncReqDTO;

public interface EsSyncService {


    void syncTable(TableSyncReqDTO tableSyncReqDTO);

    void syncTables(TablesSyncReqDTO tablesSyncReqDTO);

    void deleteTable(TableSyncReqDTO tableSyncReqDTO);
}
