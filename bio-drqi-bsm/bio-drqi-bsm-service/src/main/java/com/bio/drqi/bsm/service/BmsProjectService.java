package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.req.BmsProjectQueryAllReqDTO;

import java.util.List;

public interface BmsProjectService {
    List<BmsProjectQueryAllReqDTO> queryAll();
}
