package com.bio.drqi.bsm.kd;

import java.util.Date;

public interface KdTaskService {

     /**
      * 同步项目
      */
    void synProjectTask();

     /**
      * 同步库房
      */
    void synStockTask();
     /**
      * 同步材料类别
      */
    void synMaterialGroupTask();

     /**
      * 同步供应商
      */
    void synSupplierTask();

     /**
      * 同步材料
      */
    void synMaterialTask();


     /**
      * 同步入库
      */
    void synInStockTask(String startDate, String endDate);


     /**
      * 同步出库
      */
     void synOutStockTask(String startDate,String endDate);


    /**
     * 同步退货
     */
    void synReturnStockTask(String startDate,String endDate);

    void  synMoveStockTask(String startDate, String endDate);



}
