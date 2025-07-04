package com.bio.drqi.bsm.kd;
import com.bio.drqi.mapper.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class KdTaskServiceImpl implements KdTaskService {

    @Resource
    private BmsProductTbMapper bmsProductTbMapper;

    @Resource
    private BmsProjectDictMapper bmsProjectDictMapper;

    @Resource
    private BmsBrandTbMapper bmsBrandTbMapper;

    @Resource
    private BmsSupplierTbMapper bmsSupplierTbMapper;

    @Resource
    private BmsStockLocationDictMapper bmsStockLocationDictMapper;

    @Resource
    private BmsProductCategoryTbMapper bmsProductCategoryTbMapper;

    @Resource
    private BmsProductStockOutLogMapper bmsProductStockOutLogMapper;

    @Resource
    private BmsProductStockInLogMapper bmsProductStockInLogMapper;


    @Override
    public void synProjectTask() {

    }

    @Override
    public void synBrandTask() {

    }

    @Override
    public void synStockTask() {

    }

    @Override
    public void synMaterialGroupTask() {

    }

    @Override
    public void synSupplierTask() {

    }

    @Override
    public void synMaterialTask() {

    }

    @Override
    public void synInStockTask() {

    }

    @Override
    public void synOutStockTask() {

    }
}
