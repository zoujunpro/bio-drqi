package com.bio.drqi.bsm.kd.service;


import com.bio.drqi.bsm.kd.dto.base.KdModel;
import com.bio.drqi.bsm.kd.enums.FormIdEnum;

public interface KdApiService {

   String  executeSave(FormIdEnum formIdEnum, KdModel kdModel,String FNumber);




}
