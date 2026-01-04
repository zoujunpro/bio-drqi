package com.bio.drqi.bsm.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.uuid.IdUtils;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.contents.BioBsmContents;
import com.bio.drqi.bsm.dto.BmsProductInputDTO;
import com.bio.drqi.bsm.dto.BmsProductOutDTO;
import com.bio.drqi.bsm.enums.CooperateFormEnum;
import com.bio.drqi.bsm.enums.PurchaseTypeEnum;
import com.bio.drqi.bsm.kd.KdTaskService;
import com.bio.drqi.bsm.kd.enums.FormIdEnum;
import com.bio.drqi.bsm.kd.properties.KdProperties;
import com.bio.drqi.bsm.req.BmsProductAddReqDTO;
import com.bio.drqi.bsm.service.BmsProductService;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import com.bio.print.rsp.BmsLabelPrintDTO;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据初始化清洗
 */
@Slf4j
@RestController
@RequestMapping("/bmsTest")
public class BmsTestController {

    @Resource
    private BmsSupplierTbMapper bmsSupplierTbMapper;

    @Resource
    private BmsStockLocationDictMapper bmsStockLocationDictMapper;


    @Resource
    private SystemUserTbMapper systemUserTbMapper;

    @Resource
    private BmsProductStockTbMapper bmsProductStockTbMapper;

    @Resource
    private BmsBrandTbMapper bmsBrandTbMapper;

    @Resource
    private BmsProductService bmsProductService;

    @Resource
    private BmsProductCategoryTbMapper bmsProductCategoryTbMapper;

    @Resource
    private BmsProjectDictMapper bmsProjectDictMapper;


    @Resource
    private BmsProductTbMapper bmsProductTbMapper;

    @Resource
    private KdTaskService kdTaskService;

    @Resource
    private BmsStockDictMapper bmsStockDictMapper;

    @Resource
    private KdProperties kdProperties;


    @Resource
    private BmsProductStockInLogMapper bmsProductStockInLogMapper;


    @Resource
    private BmsProductStockOutLogMapper bmsProductStockOutLogMapper;


    @Resource
    private BmsOrderDetailTbMapper bmsOrderDetailTbMapper;


    @Resource
    private BioPrintLabelInfoTbMapper bioPrintLabelInfoTbMapper;


    @Resource
    private BmsReturnOrderDetailTbMapper bmsReturnOrderDetailTbMapper;


    @Resource
    private BmsMoveOrderDetailTbMapper bmsMoveOrderDetailTbMapper;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Resource
    private BmsOrderTbMapper bmsOrderTbMapper;


    @GetMapping("/addData")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> addData() {
        List<BmsStock> list = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\5月1号之后数据-LR.xlsx", BmsStock.class);
        for (BmsStock bmsStock : list) {
            log.info("bmsStock="+JSONUtil.toJsonStr(bmsStock));
            BmsProductTb bmsProductTb = bmsProductTbMapper.selectOneByProductInnerCode(bmsStock.productInnerCode);
            //补充入库记录
            BmsProductStockInLog bmsProductStockInLog = new BmsProductStockInLog();
            bmsProductStockInLog.setOrderDetailNum(null);
            bmsProductStockInLog.setProductName(bmsProductTb.getProductName());
            bmsProductStockInLog.setProductOutCode(bmsProductTb.getProductOutCode());
            bmsProductStockInLog.setProductCategoryCode(bmsProductTb.getProductCategoryCode());
            bmsProductStockInLog.setBrandCode(bmsProductTb.getBrandCode());
            bmsProductStockInLog.setProductSpecs(bmsProductTb.getProductSpecs());
            bmsProductStockInLog.setBatchNo(bmsStock.getBatchNo());
            bmsProductStockInLog.setProjectCode(bmsStock.getProjectCode());
            bmsProductStockInLog.setProductPrice(new BigDecimal(bmsStock.getProductPrice()));
            bmsProductStockInLog.setStoreNumber(bmsStock.getCurrentStockNumber());
            bmsProductStockInLog.setStoreAmount(bmsProductStockInLog.getProductPrice().multiply(new BigDecimal(bmsProductStockInLog.getStoreNumber())));
            bmsProductStockInLog.setApplyUserId(86);
            bmsProductStockInLog.setApplyUserName("邹军");
            bmsProductStockInLog.setCreateTime(new Date());
            bmsProductStockInLog.setTaskNum(null);
            bmsProductStockInLog.setOrderNum(null);
            bmsProductStockInLog.setStockLocationNumber(null);
            bmsProductStockInLog.setUnitCode(bmsStock.getUnitCode());
            bmsProductStockInLog.setUniqueCode(bmsStock.getUniqueCode());
            bmsProductStockInLog.setSupplierCode(bmsStock.getSupplierCode());
            bmsProductStockInLog.setProductInnerCode(bmsStock.getProductInnerCode());
            bmsProductStockInLog.setProduceDate("2025-05-08");
            bmsProductStockInLog.setExpirationDate("2028-05-08");
            bmsProductStockInLog.setTaxRate("0");
            bmsProductStockInLog.setStockCode(bmsStock.getStockCode());
            bmsProductStockInLog.setReturnNumber(0);
            bmsProductStockInLogMapper.insert(bmsProductStockInLog);
        }
        return ResponseResult.getSuccess("ok");

    }


    @GetMapping("/cleanAmount")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanAmount() {
        List<BmsProductStockTb> bmsProductStockTbList = bmsProductStockTbMapper.selectSelective(null);
        for (BmsProductStockTb bmsProductStockTb : bmsProductStockTbList) {
            log.info("bmsProductStockTb=" + JSONUtil.toJsonStr(bmsProductStockTb));
            List<BmsProductStockInLog> bmsProductStockInLogList = bmsProductStockInLogMapper.selectSelective(BmsProductStockInLog.builder().productInnerCode(bmsProductStockTb.getProductInnerCode()).build());
            bmsProductStockTb.setProductPrice(bmsProductStockInLogList.get(0).getProductPrice());
            bmsProductStockTbMapper.updateById(bmsProductStockTb);
        }
        bmsProductStockOutLogMapper.selectSelective(null).forEach(bmsProductStockOutLog -> {
            log.info("bmsProductStockOutLog=" + JSONUtil.toJsonStr(bmsProductStockOutLog));
            BmsProductStockTb bmsProductStockTb = bmsProductStockTbMapper.selectOneByUniqueCode(bmsProductStockOutLog.getUniqueCode());
            bmsProductStockOutLog.setProductPrice(bmsProductStockTb.getProductPrice());
            bmsProductStockOutLog.setOutAmount(bmsProductStockTb.getProductPrice().multiply(new BigDecimal(bmsProductStockOutLog.getOutNumber())));
            bmsProductStockOutLogMapper.updateById(bmsProductStockOutLog);
        });

        bmsMoveOrderDetailTbMapper.selectSelective(null).forEach(bmsMoveOrderDetailTb -> {
            log.info("bmsMoveOrderDetailTb=" + JSONUtil.toJsonStr(bmsMoveOrderDetailTb));
            BmsProductStockTb bmsProductStockTb = bmsProductStockTbMapper.selectOneByUniqueCode(bmsMoveOrderDetailTb.getUniqueCode());
            bmsMoveOrderDetailTb.setProductPrice(bmsProductStockTb.getProductPrice());
            bmsMoveOrderDetailTb.setMoveAmount(bmsProductStockTb.getProductPrice().multiply(new BigDecimal(bmsMoveOrderDetailTb.getMoveNumber())));
            bmsMoveOrderDetailTbMapper.updateById(bmsMoveOrderDetailTb);
        });
        return ResponseResult.getSuccess("ok");

    }

    @GetMapping("cleanStockNew")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanStockNew() {
        List<BmsStockDict> bmsStockDictList = bmsStockDictMapper.selectList(null);
        for (BmsStockDict bmsStockDict : bmsStockDictList) {
            if (bmsStockDict.getStockCode().length() > 30) {
                bmsStockDict.setStockCode(bmsStockDict.getStockCode().substring(0, 30));
                bmsStockDictMapper.updateById(bmsStockDict);
            }
        }
        List<BmsMoveOrderDetailTb> bmsMoveOrderDetailTbList = bmsMoveOrderDetailTbMapper.selectList(null);
        for (BmsMoveOrderDetailTb bmsMoveOrderDetailTb : bmsMoveOrderDetailTbList) {
            if (bmsMoveOrderDetailTb.getToStockCode().length() > 30) {
                bmsMoveOrderDetailTb.setFromStockCode(bmsMoveOrderDetailTb.getFromStockCode().substring(0, 30));
                bmsMoveOrderDetailTb.setToStockCode(bmsMoveOrderDetailTb.getToStockCode().substring(0, 30));
                bmsMoveOrderDetailTbMapper.updateById(bmsMoveOrderDetailTb);
            }

        }

        bmsReturnOrderDetailTbMapper.selectSelective(null).forEach(bmsReturnOrderDetailTb -> {
            if (bmsReturnOrderDetailTb.getStockCode().length() > 30) {
                bmsReturnOrderDetailTb.setStockCode(bmsReturnOrderDetailTb.getStockCode().substring(0, 30));
                bmsReturnOrderDetailTbMapper.updateById(bmsReturnOrderDetailTb);
            }

        });
        bmsProductStockTbMapper.selectList(null).forEach(bmsProductStockTb -> {
            if (bmsProductStockTb.getStockCode().length() > 30) {
                bmsProductStockTb.setStockCode(bmsProductStockTb.getStockCode().substring(0, 30));
                bmsProductStockTbMapper.updateById(bmsProductStockTb);
            }

        });

        bmsProductStockOutLogMapper.selectSelective(null).forEach(bmsProductStockOutLog -> {
            if (bmsProductStockOutLog.getStockCode().length() > 30) {
                bmsProductStockOutLog.setStockCode(bmsProductStockOutLog.getStockCode().substring(0, 30));
                bmsProductStockOutLogMapper.updateById(bmsProductStockOutLog);
            }

        });

        bmsProductStockInLogMapper.selectSelective(null).forEach(bmsProductStockInLog -> {
            if (bmsProductStockInLog.getStockCode().length() > 30) {
                bmsProductStockInLog.setStockCode(bmsProductStockInLog.getStockCode().substring(0, 30));
                bmsProductStockInLogMapper.updateById(bmsProductStockInLog);
            }

        });

        List<BioTaskDtlTb> inBioTaskDtlTbList = bioTaskDtlTbMapper.selectAllByTaskTypeCode("bms_product_input");
        if (CollectionUtil.isNotEmpty(inBioTaskDtlTbList)) {
            inBioTaskDtlTbList.forEach(bioTaskDtlTb -> {
                BmsProductInputDTO bmsProductInputDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), BmsProductInputDTO.class);
                if (CollectionUtil.isNotEmpty(bmsProductInputDTO.getOrderDetailList())) {
                    bmsProductInputDTO.getOrderDetailList().forEach(orderDetail -> {
                        if (StringUtils.isNotEmpty(orderDetail.getStockCode()) && orderDetail.getStockCode().length() > 30) {
                            orderDetail.setStockCode(orderDetail.getStockCode().substring(0, 30));
                        }

                    });
                }
                bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(bmsProductInputDTO));
                bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
            });
        }


        List<BioTaskDtlTb> outBioTaskDtlTbList = bioTaskDtlTbMapper.selectAllByTaskTypeCode("bms_product_out");
        if (CollectionUtil.isNotEmpty(outBioTaskDtlTbList)) {
            outBioTaskDtlTbList.forEach(bioTaskDtlTb -> {
                List<BmsProductOutDTO> bmsProductOutDTOList = JSONUtil.toList(bioTaskDtlTb.getTaskForm(), BmsProductOutDTO.class);
                for (BmsProductOutDTO bmsProductOutDTO : bmsProductOutDTOList) {
                    if (StringUtils.isNotEmpty(bmsProductOutDTO.getStockCode()) && bmsProductOutDTO.getStockCode().length() > 30) {
                        bmsProductOutDTO.setStockCode(bmsProductOutDTO.getStockCode().substring(0, 30));
                    }
                }
                bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(bmsProductOutDTOList));
                bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
            });
        }

        List<BioPrintLabelInfoTb> bioPrintLabelInfoTbList = bioPrintLabelInfoTbMapper.searchAllByLabelType("bms_label_print");
        for (BioPrintLabelInfoTb bioPrintLabelInfoTb : bioPrintLabelInfoTbList) {
            String uniqueCode = bioPrintLabelInfoTb.getUniqueCode();
            String[] uniqueCodeArr = uniqueCode.split("\\|");
            if (uniqueCodeArr.length != 4) {
                throw new BusinessException("旧二维码已经废弃，请重新打印");
            }
            if (uniqueCodeArr[3].length() > 30) {
                bioPrintLabelInfoTb.setUniqueCode(uniqueCodeArr[0] + "|" + uniqueCodeArr[1] + "|" + uniqueCodeArr[2] + "|" + uniqueCodeArr[3].substring(0, 30));
                BmsLabelPrintDTO bmsLabelPrintDTO = JSONUtil.toBean(bioPrintLabelInfoTb.getLabelText(), BmsLabelPrintDTO.class);
                bmsLabelPrintDTO.setStockCode(bmsLabelPrintDTO.getStockCode().substring(0, 30));
                bioPrintLabelInfoTb.setLabelText(JSONUtil.toJsonStr(bmsLabelPrintDTO));
                bioPrintLabelInfoTbMapper.updateById(bioPrintLabelInfoTb);
            }


        }
        return ResponseResult.getSuccess("ok");

    }


    @GetMapping("cleanBrand20251224")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanBrand20251224() {
        List<BmsBrand> bmsBrandList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\品牌(1).xlsx", BmsBrand.class);
        bmsBrandList = bmsBrandList.stream().filter(bmsBrand -> StringUtils.isNotEmpty(bmsBrand.repeatBrandName)).collect(Collectors.toList());
        for (BmsBrand bmsBrand : bmsBrandList) {
            log.info("bmsBrand=" + JSONUtil.toJsonStr(bmsBrand));
            BmsBrandTb repeatBmsBrandTb = bmsBrandTbMapper.selectOneByBrandName(bmsBrand.repeatBrandName);
            BmsBrandTb sourceBmsBrandTb = bmsBrandTbMapper.selectOneByBrandCode(bmsBrand.brandCode);
            if (sourceBmsBrandTb == null) {
                continue;
            }
            if (repeatBmsBrandTb == null) {
                sourceBmsBrandTb.setBrandName(bmsBrand.repeatBrandName);
                bmsBrandTbMapper.updateById(sourceBmsBrandTb);
            } else {
                List<BmsProductTb> bmsProductTbList = bmsProductTbMapper.selectSelective(BmsProductTb.builder().brandCode(sourceBmsBrandTb.getBrandCode()).build());
                if (CollectionUtil.isNotEmpty(bmsProductTbList)) {
                    bmsProductTbList.forEach(bmsProductTb -> {
                        bmsProductTb.setBrandCode(repeatBmsBrandTb.getBrandCode());
                        bmsProductTbMapper.updateById(bmsProductTb);
                    });
                }

                List<BmsOrderDetailTb> bmsOrderDetailTbList = bmsOrderDetailTbMapper.selectSelective(BmsOrderDetailTb.builder().brandCode(sourceBmsBrandTb.getBrandCode()).build());
                if (CollectionUtil.isNotEmpty(bmsOrderDetailTbList)) {
                    bmsOrderDetailTbList.forEach(bmsOrderDetailTb -> {
                        bmsOrderDetailTb.setBrandCode(repeatBmsBrandTb.getBrandCode());
                        bmsOrderDetailTbMapper.updateById(bmsOrderDetailTb);
                    });
                }

                List<BmsProductStockTb> bmsProductStockTbList = bmsProductStockTbMapper.selectSelective(BmsProductStockTb.builder().brandCode(sourceBmsBrandTb.getBrandCode()).build());
                if (CollectionUtil.isNotEmpty(bmsProductStockTbList)) {
                    bmsProductStockTbList.forEach(bmsProductStockTb -> {
                        bmsProductStockTb.setBrandCode(repeatBmsBrandTb.getBrandCode());
                        bmsProductStockTbMapper.updateById(bmsProductStockTb);
                    });
                }

                List<BmsProductStockInLog> bmsProductStockInLogList = bmsProductStockInLogMapper.selectSelective(BmsProductStockInLog.builder().brandCode(sourceBmsBrandTb.getBrandCode()).build());
                if (CollectionUtil.isNotEmpty(bmsProductStockInLogList)) {
                    bmsProductStockInLogList.forEach(bmsProductStockInLog -> {
                        bmsProductStockInLog.setBrandCode(repeatBmsBrandTb.getBrandCode());
                        bmsProductStockInLogMapper.updateById(bmsProductStockInLog);
                    });
                }

                List<BmsProductStockOutLog> bmsProductStockOutLogList = bmsProductStockOutLogMapper.selectSelective(BmsProductStockOutLog.builder().brandCode(sourceBmsBrandTb.getBrandCode()).build());
                if (CollectionUtil.isNotEmpty(bmsProductStockOutLogList)) {
                    bmsProductStockOutLogList.forEach(bmsProductStockOutLog -> {
                        bmsProductStockOutLog.setBrandCode(repeatBmsBrandTb.getBrandCode());
                        bmsProductStockOutLogMapper.updateById(bmsProductStockOutLog);
                    });
                }

                List<BmsReturnOrderDetailTb> bmsReturnOrderDetailTbList = bmsReturnOrderDetailTbMapper.selectSelective(BmsReturnOrderDetailTb.builder().brandCode(sourceBmsBrandTb.getBrandCode()).build());
                if (CollectionUtil.isNotEmpty(bmsReturnOrderDetailTbList)) {
                    bmsReturnOrderDetailTbList.forEach(bmsReturnOrderDetailTb -> {
                        bmsReturnOrderDetailTb.setBrandCode(repeatBmsBrandTb.getBrandCode());
                        bmsReturnOrderDetailTbMapper.updateById(bmsReturnOrderDetailTb);
                    });
                }

                List<BmsMoveOrderDetailTb> bmsMoveOrderDetailTbList = bmsMoveOrderDetailTbMapper.selectSelective(BmsMoveOrderDetailTb.builder().brandCode(sourceBmsBrandTb.getBrandCode()).build());
                if (CollectionUtil.isNotEmpty(bmsMoveOrderDetailTbList)) {
                    bmsMoveOrderDetailTbList.forEach(bmsMoveOrderDetailTb -> {
                        bmsMoveOrderDetailTb.setBrandCode(repeatBmsBrandTb.getBrandCode());
                        bmsMoveOrderDetailTbMapper.updateById(bmsMoveOrderDetailTb);
                    });
                }
            }

        }
        return ResponseResult.getSuccess("ok");
    }


    @Data
    public static class BmsBrand {

        @ExcelProperty("品牌")
        private String brandName;

        @ExcelProperty("品牌编码")
        private String brandCode;

        @ExcelProperty("重复品牌名字")
        private String repeatBrandName;
    }


    @GetMapping("/cleanTianJinStock")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanTianJinStock() {
        List<BmsProductStockTb> bmsProductStockTbList = bmsProductStockTbMapper.selectSelective(BmsProductStockTb.builder().unitCode("tianjin").build());
        for (BmsProductStockTb bmsProductStockTb : bmsProductStockTbList) {
            log.info("修正数据：" + JSONUtil.toJsonStr(bmsProductStockTb));
            List<BmsProductStockOutLog> bmsProductStockOutLogList = bmsProductStockOutLogMapper.selectAllByUniqueCode(bmsProductStockTb.getUniqueCode());
            Integer outNum = 0;
            int intNum = 0;
            if (CollectionUtil.isNotEmpty(bmsProductStockOutLogList)) {
                for (BmsProductStockOutLog bmsProductStockOutLog : bmsProductStockOutLogList) {
                    outNum = outNum + bmsProductStockOutLog.getOutNumber();
                }
            }

            List<BmsProductStockInLog> bmsProductStockInLogList = bmsProductStockInLogMapper.selectAllByUniqueCode(bmsProductStockTb.getUniqueCode());
            if (CollectionUtil.isNotEmpty(bmsProductStockInLogList)) {
                for (BmsProductStockInLog bmsProductStockInLog : bmsProductStockInLogList) {
                    intNum = intNum + bmsProductStockInLog.getStoreNumber();
                }
            }
            bmsProductStockTb.setTotalStoreNumber(intNum);
            bmsProductStockTb.setTotalOutNumber(outNum);
            bmsProductStockTbMapper.updateById(bmsProductStockTb);
        }
        return ResponseResult.getSuccess("ok");

    }


    @GetMapping("/synStockLocationSave")
    public ResponseResult<String> synKdStockLocation() {
        kdTaskService.synStockTask();
        return ResponseResult.getSuccess("OK");
    }


    @GetMapping("/synProjectSave")
    public ResponseResult<String> synProjectSave() {
        kdTaskService.synProjectTask();

        return ResponseResult.getSuccess("OK");
    }

    @GetMapping("/materialGroup")
    public ResponseResult<String> synGroup() {
        kdTaskService.synMaterialGroupTask();
        return ResponseResult.getSuccess("OK");
    }

    @GetMapping("/synMaterial")
    public ResponseResult<String> synMaterial() {
        kdTaskService.synMaterialTask();
        return ResponseResult.getSuccess("OK");
    }


    @GetMapping("/synSupplier")
    public ResponseResult<String> synSupplier() {
        kdTaskService.synSupplierTask();
        return ResponseResult.getSuccess("OK");
    }

    @GetMapping("/synInStock")
    public ResponseResult<String> synInStock() {
        kdTaskService.synInStockTask("2025-07-01", "2025-09-16");
        return ResponseResult.getSuccess("OK");
    }

    @GetMapping("/synOutStock")
    public ResponseResult<String> synOutStock() {
        kdTaskService.synOutStockTask("2025-07-01", "2025-09-16");
        return ResponseResult.getSuccess("OK");
    }


    @GetMapping("/synReturnStock")
    public ResponseResult<String> synReturnStock() {
        kdTaskService.synReturnStockTask("2025-07-01", "2025-09-16");
        return ResponseResult.getSuccess("OK");
    }

    @GetMapping("/synMoveStockTask")
    public ResponseResult<String> synMoveStockTask() {
        kdTaskService.synMoveStockTask("2025-07-01", "2025-09-16");
        return ResponseResult.getSuccess("OK");
    }


    @GetMapping("/testKd")
    public ResponseResult testKd() {
        String json = "{\"NeedReturnFields\":[],\"IsDeleteEntry\":\"true\",\"IsVerifyBaseDataFiel\":\"false\",\"IsEntryBatchFil\":\"true\",\"ValidateFlag\":\"true\",\"NumberSearch\":\"true\",\"IsAutoAdjustField\":\"false\",\"IsAutoSubmitAndAudit\":\"true\",\"Model\":{\"FEntryID\":\"0\",\"Fnumber\":\"ET\",\"FDataValue\":\"番茄基因编辑\",\"FId\":{\"FNumber\":\"XM\"},\"FCreateOrgId\":{\"FNumber\":\"1001\"},\"fUseOrgId\":{\"FNumber\":\"1001\"}}}";
        K3CloudApi k3CloudApi = new K3CloudApi(kdProperties.getIdentifyInfo(), false);
        try {
            String s = k3CloudApi.save(FormIdEnum.BOS_ASSISTANTDATA_DETAIL.name(), json);
            System.out.println(s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseResult.getSuccess("ok");
    }


    @GetMapping("/cleanLabel")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanLabel() {
        List<BioPrintLabelInfoTb> bioPrintLabelInfoTbList = bioPrintLabelInfoTbMapper.searchAllByLabelType("bms_label_print");
        for (BioPrintLabelInfoTb bioPrintLabelInfoTb : bioPrintLabelInfoTbList) {
            log.info("bioPrintLabelInfoTb={}", JSONUtil.toJsonStr(bioPrintLabelInfoTb));
            String[] uniqueCodeArr = bioPrintLabelInfoTb.getUniqueCode().split("\\|");
            if (uniqueCodeArr.length != 4) {

                throw new BusinessException("标签异常");
            }
            String stockCode = uniqueCodeArr[3];
            BmsLabelPrintDTO bmsLabelPrintDTO = JSONUtil.toBean(bioPrintLabelInfoTb.getLabelText(), BmsLabelPrintDTO.class);
            bmsLabelPrintDTO.setStockCode(stockCode);
            bmsLabelPrintDTO.setUniqueCode(bioPrintLabelInfoTb.getUniqueCode());
            bmsLabelPrintDTO.setUnitCode(uniqueCodeArr[2]);
            bioPrintLabelInfoTb.setLabelText(JSONUtil.toJsonStr(bmsLabelPrintDTO));
            bioPrintLabelInfoTbMapper.updateById(bioPrintLabelInfoTb);

        }
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 清洗库位
     *
     * @return
     */
    @GetMapping("/cleanStock")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanStock() {
        log.info("数据清洗开始");
        /**
         *清洗库存
         */
        List<BmsStockLocationDict> bmsStockLocationDictList = bmsStockLocationDictMapper.selectList(null);
        Map<String, List<BmsStockLocationDict>> bmsStockLocationDictListMap = bmsStockLocationDictList.stream().collect(Collectors.groupingBy(BmsStockLocationDict::getStockCode));
        bmsStockLocationDictListMap.forEach((stockCode, list) -> {
            BmsStockDict bmsStockDict = bmsStockDictMapper.selectOneByStockCode(stockCode);
            if (bmsStockDict == null) {
                bmsStockDict = new BmsStockDict();
                bmsStockDict.setStockName(list.get(0).getStockName());
                bmsStockDict.setStockCode(stockCode);
                bmsStockDict.setUnitCode(list.get(0).getUnitCode());
                bmsStockDict.setKdNumber(null);
                bmsStockDict.setCreateTime(new Date());
                bmsStockDict.setCreateUserId(list.get(0).getCreateUserId());
                bmsStockDict.setCreateUserName(list.get(0).getCreateUserName());
                bmsStockDictMapper.insert(bmsStockDict);
            }
        });
        log.info("库存清洗结束");
        /**
         * 退货数量清洗
         */
        List<BmsProductStockInLog> bmsProductStockInLogList = bmsProductStockInLogMapper.selectList(null);
        bmsProductStockInLogList.forEach(bmsProductStockInLog -> {
            bmsProductStockInLog.setReturnNumber(0);
            bmsProductStockInLogMapper.updateById(bmsProductStockInLog);
        });
        bmsProductStockTbMapper.selectList(null).forEach(bmsProductStockTb -> {
            bmsProductStockTb.setReturnNumber(0);
            bmsProductStockTbMapper.updateById(bmsProductStockTb);
        });
        log.info("库存数量returnNumber赋值结束");

        /**
         * 库存位置赋值
         */
        Map<String, BmsStockLocationDict> bmsStockLocationDictMap = bmsStockLocationDictList.stream().collect(Collectors.toMap(BmsStockLocationDict::getLocationNumber, bmsStockLocationDict -> bmsStockLocationDict));
        bmsProductStockTbMapper.selectList(null).forEach(bmsProductStockTb -> {
            if (StringUtils.isEmpty(bmsProductStockTb.getStockCode())) {
                String location = null;
                String b = bmsProductStockTb.getStockLocationNumber();
                if (b.contains("[")) {
                    List<String> locationList = JSONUtil.toList(b, String.class);
                    if (CollectionUtil.isNotEmpty(locationList)) {
                        location = locationList.get(0);
                    }
                } else {
                    location = b;
                }

                if (StringUtils.isNotEmpty(location)) {
                    BmsStockLocationDict bmsStockLocationDict = bmsStockLocationDictMap.get(location);
                    if (bmsStockLocationDict != null) {
                        bmsProductStockTb.setStockCode(bmsStockLocationDict.getStockCode());
                        bmsProductStockTbMapper.updateById(bmsProductStockTb);
                    }
                }

                if (StringUtils.isEmpty(bmsProductStockTb.getStockCode())) {
                    String stockCode = null;
                    if ("beijing".equals(bmsProductStockTb.getUnitCode())) {
                        BmsStockDict bmsStockDict = bmsStockDictMapper.selectOneByStockName("一号库");
                        stockCode = bmsStockDict.getStockCode();
                    } else {
                        BmsStockDict bmsStockDict = bmsStockDictMapper.selectOneByStockName("16楼库");
                        stockCode = bmsStockDict.getStockCode();
                    }
                    bmsProductStockTb.setStockCode(stockCode);
                    bmsProductStockTbMapper.updateById(bmsProductStockTb);
                }

            }
        });
        log.info("库存明细清洗结束");
        List<BmsProductStockOutLog> bmsProductStockOutLogList = bmsProductStockOutLogMapper.selectList(null);
        bmsProductStockOutLogList.forEach(bmsProductStockOutLog -> {
            BmsProductStockTb bmsProductStockTb = bmsProductStockTbMapper.selectOneByUniqueCode(bmsProductStockOutLog.getUniqueCode());
            if (bmsProductStockTb != null && StringUtils.isNotEmpty(bmsProductStockTb.getStockCode())) {
                bmsProductStockOutLog.setStockCode(bmsProductStockTb.getStockCode());
                bmsProductStockOutLogMapper.updateById(bmsProductStockOutLog);
            }

            if (StringUtils.isEmpty(bmsProductStockOutLog.getStockCode())) {
                String stockCode = null;
                if ("beijing".equals(bmsProductStockOutLog.getUnitCode())) {
                    BmsStockDict bmsStockDict = bmsStockDictMapper.selectOneByStockName("一号库");
                    stockCode = bmsStockDict.getStockCode();
                } else {
                    BmsStockDict bmsStockDict = bmsStockDictMapper.selectOneByStockName("16楼库");
                    stockCode = bmsStockDict.getStockCode();
                }
                bmsProductStockOutLog.setStockCode(stockCode);
                bmsProductStockOutLogMapper.updateById(bmsProductStockOutLog);
            }

        });
        log.info("出库记录清洗库房编码结束");
        bmsProductStockInLogList.forEach(bmsProductStockInLog -> {
            List<BmsProductStockTb> bmsProductStockTbList = bmsProductStockTbMapper.selectAllByProductInnerCodeAndUnitCodeAndBatchNo(bmsProductStockInLog.getProductInnerCode(), bmsProductStockInLog.getUnitCode(), bmsProductStockInLog.getBatchNo());
            if (CollectionUtil.isNotEmpty(bmsProductStockTbList)) {
                bmsProductStockInLog.setStockCode(bmsProductStockTbList.get(0).getStockCode());
                bmsProductStockInLogMapper.updateById(bmsProductStockInLog);
            }
            if (StringUtils.isEmpty(bmsProductStockInLog.getStockCode())) {
                String stockCode = null;
                if ("beijing".equals(bmsProductStockInLog.getUnitCode())) {
                    BmsStockDict bmsStockDict = bmsStockDictMapper.selectOneByStockName("一号库");
                    stockCode = bmsStockDict.getStockCode();
                } else {
                    BmsStockDict bmsStockDict = bmsStockDictMapper.selectOneByStockName("16楼库");
                    stockCode = bmsStockDict.getStockCode();
                }
                bmsProductStockInLog.setStockCode(stockCode);
                bmsProductStockInLogMapper.updateById(bmsProductStockInLog);
            }
        });
        log.info("入库明细清洗库房结束");

        log.info("数据清洗结束");
        return ResponseResult.getSuccess("ok");
    }


    @GetMapping("/cleanStockDate")
    public ResponseResult<String> cleanStockDate() {
        List<BmsProductStockTb> bmsProductStockTbList = bmsProductStockTbMapper.selectSelective(null);
        for (BmsProductStockTb bmsProductStockTb : bmsProductStockTbList) {
            boolean editFlag = false;
            String expirationDate = bmsProductStockTb.getExpirationDate();
            String produceDate = bmsProductStockTb.getProduceDate();
            if (StringUtils.isNotEmpty(expirationDate)) {
                if (expirationDate.contains("/")) {
                    String[] s = expirationDate.split("/");
                    bmsProductStockTb.setExpirationDate(s[0] + "-" + StringUtils.padl(s[1], 2, '0') + "-" + StringUtils.padl(s[2], 2, '0'));
                    System.out.println("更改后日期expirationDate=" + bmsProductStockTb.getExpirationDate());
                    editFlag = true;
                }
            }
            if (StringUtils.isNotEmpty(produceDate)) {
                if (produceDate.contains("/")) {
                    String[] s = produceDate.split("/");
                    bmsProductStockTb.setProduceDate(s[0] + "-" + StringUtils.padl(s[1], 2, '0') + "-" + StringUtils.padl(s[2], 2, '0'));
                    System.out.println("更改后日期produceDate=" + bmsProductStockTb.getProduceDate());
                    editFlag = true;
                }
            }
            if (editFlag) {
                bmsProductStockTbMapper.updateById(bmsProductStockTb);
            }

        }


        return ResponseResult.getSuccess("ok");
    }


    /**
     * 供应商数据清洗
     *
     * @return
     */
    @GetMapping("/supplierDataClean")
    @WebLog(desc = "数据清洗")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> supplierDataClean() {

        List<SystemUserTb> systemUserTbList = systemUserTbMapper.selectList(null);
        Map<String, Integer> userMap = systemUserTbList.stream().collect(Collectors.toMap(SystemUserTb::getNickname, SystemUserTb::getId));
        List<SupplierCleanDataExcel> supplierCleanDataExcelList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\供应商数据清洗excel.xlsx", SupplierCleanDataExcel.class);
        for (SupplierCleanDataExcel supplierCleanDataExcel : supplierCleanDataExcelList) {
            log.info("清洗" + supplierCleanDataExcel.getSupplierCode());
            BmsSupplierTb bmsSupplierTb = bmsSupplierTbMapper.selectOneBySupplierName(supplierCleanDataExcel.supplierName);
            if (bmsSupplierTb != null) {
                log.info("供应商重复*****" + bmsSupplierTb.getSupplierName());
                continue;
            }
            bmsSupplierTb = new BmsSupplierTb();
            bmsSupplierTb.setSupplierCode(supplierCleanDataExcel.supplierCode);
            bmsSupplierTb.setSupplierName(supplierCleanDataExcel.supplierName);
            bmsSupplierTb.setOpeningBank(supplierCleanDataExcel.openingBank);
            bmsSupplierTb.setBankAccount(supplierCleanDataExcel.bankAccount);
            bmsSupplierTb.setTaxId(supplierCleanDataExcel.taxId);
            bmsSupplierTb.setQualificationLocation(null);
            bmsSupplierTb.setBusinessScope(supplierCleanDataExcel.business_scope);
            if ("合同".equals(supplierCleanDataExcel.cooperate_form)) {
                bmsSupplierTb.setCooperateForm(CooperateFormEnum.contract.name());
            } else if ("订单".equals(supplierCleanDataExcel.cooperate_form)) {
                bmsSupplierTb.setCooperateForm(CooperateFormEnum.order.name());
            } else if ("框架协议".equals(supplierCleanDataExcel.cooperate_form)) {
                bmsSupplierTb.setCooperateForm(CooperateFormEnum.protocol.name());
            } else {
                bmsSupplierTb.setCooperateForm(CooperateFormEnum.other.name());
            }

            bmsSupplierTb.setFrameworkAgreementNumber(supplierCleanDataExcel.framework_agreement_number);
            bmsSupplierTb.setFrameworkAgreementAnnex(null);
            bmsSupplierTb.setExpirationDate(supplierCleanDataExcel.expiration_date);
            bmsSupplierTb.setContactUserName(supplierCleanDataExcel.contact_user_name);
            bmsSupplierTb.setContactUserTelephone(supplierCleanDataExcel.contact_user_telephone);
            bmsSupplierTb.setLeaderUserName(supplierCleanDataExcel.leaderUserName);
            bmsSupplierTb.setLeaderUserId(userMap.get(supplierCleanDataExcel.leaderUserName));
            bmsSupplierTb.setRemark(supplierCleanDataExcel.remark);
            bmsSupplierTb.setCreateTime(new Date());
            bmsSupplierTb.setCreateUserName(null);
            bmsSupplierTb.setCreateUserId(null);
            bmsSupplierTb.setSupplierStatus(BioBsmContents.Y);
            bmsSupplierTbMapper.insert(bmsSupplierTb);


        }

        return ResponseResult.getSuccess("OK");
    }

    @Transactional(rollbackFor = Exception.class)
    @GetMapping("/cleanProductStockExcel")
    public ResponseResult<String> cleanProductStockExcel() {
        List<ProductStockCleanDataExcel> productStockCleanDataExcelList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\耗材当前库存盘点-天津.xlsx", ProductStockCleanDataExcel.class);
        for (ProductStockCleanDataExcel productStockCleanDataExcel : productStockCleanDataExcelList) {
            log.info("当前处理数据" + JSONUtil.toJsonStr(productStockCleanDataExcel));
            productStockCleanDataExcel.setProductName(productStockCleanDataExcel.getProductName().trim());
            BmsProductTb bmsProductTb = null;
            bmsProductTb = bmsProductTbMapper.selectOneByProductName(productStockCleanDataExcel.productName);
            if (bmsProductTb == null) {
                //如果是新品牌就插入
                BmsBrandTb bmsBrandTb = null;
                if (StringUtils.isNotEmpty(productStockCleanDataExcel.brandName)) {
                    bmsBrandTb = bmsBrandTbMapper.selectOneByBrandName(productStockCleanDataExcel.brandName);
                    if (bmsBrandTb == null) {
                        bmsBrandTb = new BmsBrandTb();
                        bmsBrandTb.setBrandCode(IdUtils.simpleUUID());
                        bmsBrandTb.setBrandName(productStockCleanDataExcel.brandName);
                        bmsBrandTb.setCreateTime(new Date());
                        bmsBrandTb.setBrandStatus("Y");
                        bmsBrandTbMapper.insert(bmsBrandTb);
                    }
                }

                //如果是新类别就插入
                BmsProductCategoryTb bmsProductCategoryTb = bmsProductCategoryTbMapper.selectOneByProductCategoryName(productStockCleanDataExcel.productCategory);
                if (bmsProductCategoryTb == null) {
                    bmsProductCategoryTb = new BmsProductCategoryTb();
                    bmsProductCategoryTb.setProductCategoryName(productStockCleanDataExcel.productCategory);
                    bmsProductCategoryTb.setProductCategoryCode(IdUtils.simpleUUID());
                    bmsProductCategoryTb.setCreateTime(new Date());
                    bmsProductCategoryTbMapper.insert(bmsProductCategoryTb);
                }

                BmsProductAddReqDTO bmsProductAddReqDTO = new BmsProductAddReqDTO();
                bmsProductAddReqDTO.setProductName(productStockCleanDataExcel.productName);
                bmsProductAddReqDTO.setProductOutCode(productStockCleanDataExcel.productOutCode);
                bmsProductAddReqDTO.setProductCategoryCode(bmsProductCategoryTb.getProductCategoryCode());
                bmsProductAddReqDTO.setBrandCode(bmsBrandTb.getBrandCode());
                bmsProductAddReqDTO.setProductSpecs(productStockCleanDataExcel.product_specs);
                bmsProductTb = bmsProductService.add(bmsProductAddReqDTO);
            }
            //插入数据
            BmsProductStockTb bmsProductStockTb = new BmsProductStockTb();
            bmsProductStockTb.setProductName(bmsProductTb.getProductName());
            bmsProductStockTb.setProductOutCode(bmsProductTb.getProductOutCode());
            bmsProductStockTb.setProductCategoryCode(bmsProductTb.getProductCategoryCode());
            if (bmsProductTb.getBrandCode() != null) {
                BmsBrandTb bmsBrandTb = bmsBrandTbMapper.selectOneByBrandCode(bmsProductTb.getBrandCode());
                bmsProductStockTb.setBrandCode(bmsBrandTb.getBrandCode());
            }

            if (StringUtils.isNotEmpty(productStockCleanDataExcel.getSupplierCode())) {
                BmsSupplierTb bmsSupplierTb = bmsSupplierTbMapper.selectOneBySupplierCode(productStockCleanDataExcel.getSupplierCode());
                bmsProductStockTb.setSupplierCode(bmsSupplierTb.getSupplierCode());
            }

            bmsProductStockTb.setProductSpecs(bmsProductTb.getProductSpecs());
            bmsProductStockTb.setBatchNo(productStockCleanDataExcel.batchNo);
            bmsProductStockTb.setTotalStoreNumber(Integer.valueOf(productStockCleanDataExcel.current_stock_number));
            bmsProductStockTb.setCurrentStockNumber(Integer.valueOf(productStockCleanDataExcel.current_stock_number));
            bmsProductStockTb.setTotalOutNumber(Integer.valueOf(productStockCleanDataExcel.current_stock_number));
            bmsProductStockTb.setUnitCode("tianjin");
            bmsProductStockTb.setStockLocationNumber(productStockCleanDataExcel.stock_location_number);
            bmsProductStockTb.setProductInnerCode(bmsProductTb.getProductInnerCode());
            bmsProductStockTb.setUniqueCode(IdUtils.simpleUUID());
            bmsProductStockTb.setProduceDate(productStockCleanDataExcel.produce_date);
            bmsProductStockTb.setExpirationDate(productStockCleanDataExcel.expiration_date);
            bmsProductStockTbMapper.insert(bmsProductStockTb);
        }
        return ResponseResult.getSuccess("OK");
    }

    @Transactional(rollbackFor = Exception.class)
    @GetMapping("/cleanProductDataExcel")
    public ResponseResult<String> cleanProductDataExcel() {
        List<ProductCleanDataExcel> productCleanDataExcelList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\商品.xlsx", ProductCleanDataExcel.class);
        for (ProductCleanDataExcel productCleanDataExcel : productCleanDataExcelList) {
            log.info("清洗数据={}", productCleanDataExcel);
            String brandCode = null;
            BmsBrandTb bmsBrandTb = null;
            if (StringUtils.isNotEmpty(productCleanDataExcel.brandName)) {
                bmsBrandTb = bmsBrandTbMapper.selectOneByBrandName(productCleanDataExcel.brandName);
                if (bmsBrandTb == null) {
                    bmsBrandTb = new BmsBrandTb();
                    bmsBrandTb.setBrandName(productCleanDataExcel.brandName);
                    bmsBrandTb.setBrandCode(IdUtils.simpleUUID());
                    bmsBrandTb.setBrandStatus(BioBsmContents.Y);
                    bmsBrandTbMapper.insert(bmsBrandTb);
                }
                brandCode = bmsBrandTb.getBrandCode();
            }
            BmsSupplierTb bmsSupplierTb = bmsSupplierTbMapper.selectOneBySupplierCode(productCleanDataExcel.supplierCode);
            if (bmsSupplierTb == null) {
                throw new BusinessException("供应商不存在" + productCleanDataExcel.supplierCode);
            }

            BmsProductCategoryTb bmsProductCategoryTb = bmsProductCategoryTbMapper.selectOneByProductCategoryName(productCleanDataExcel.productCategory);
            if (bmsProductCategoryTb == null) {
                throw new BusinessException("类别找不到" + productCleanDataExcel.productCategory);
            }

            BmsProductTb bmsProductTb = bmsProductTbMapper.selectOneByProductNameAndBrandCodeAndProductSpecs(productCleanDataExcel.productName, bmsBrandTb != null ? bmsBrandTb.getBrandCode() : null, productCleanDataExcel.product_specs);
            if (bmsProductTb == null) {
                BmsProductAddReqDTO bmsProductAddReqDTO = new BmsProductAddReqDTO();
                bmsProductAddReqDTO.setProductName(productCleanDataExcel.productName);
                bmsProductAddReqDTO.setProductOutCode(productCleanDataExcel.productCode);
                bmsProductAddReqDTO.setProductCategoryCode(bmsProductCategoryTb.getProductCategoryCode());
                bmsProductAddReqDTO.setBrandCode(brandCode);
                bmsProductAddReqDTO.setProductSpecs(productCleanDataExcel.product_specs);
                bmsProductService.add(bmsProductAddReqDTO);
            } else {
                log.info("商品重复添加：" + JSONUtil.toJsonStr(productCleanDataExcel));
            }

        }


        return ResponseResult.getSuccess("OK");
    }

    @GetMapping("/cleanProject")
    public ResponseResult<String> cleanProject() {
        List<CmsProjectDataExcel> cmsProjectDataExcelList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\cms项目21.xlsx", CmsProjectDataExcel.class);
        for (CmsProjectDataExcel cmsProjectDataExcel : cmsProjectDataExcelList) {
            if ("其他".equals(cmsProjectDataExcel.getProjectCode())) {
                cmsProjectDataExcel.setProjectName("其他");
            }
            BmsProjectDict bmsProjectDict = bmsProjectDictMapper.selectOneByProjectCode(cmsProjectDataExcel.projectCode);

            if (bmsProjectDict == null) {
                bmsProjectDict = new BmsProjectDict();
                bmsProjectDict.setProjectCode(cmsProjectDataExcel.projectCode);
                bmsProjectDict.setProjectName(cmsProjectDataExcel.projectName);
                bmsProjectDict.setCreateTime(new Date());
                bmsProjectDictMapper.insert(bmsProjectDict);
            }
        }
        return ResponseResult.getSuccess("pk");

    }


    @GetMapping("/cleanBrand")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanBreed() {
        List<BmsProductTb> bmsProductTbList = bmsProductTbMapper.selectSelective(null);
        for (BmsProductTb bmsProductTb : bmsProductTbList) {
            if (bmsProductTb.getBrandCode() == null) {
                List<BmsOrderDetailTb> bmsOrderDetailTbList = bmsOrderDetailTbMapper.selectAllByProductInnerCode(bmsProductTb.getProductInnerCode());
                if (CollectionUtil.isNotEmpty(bmsOrderDetailTbList)) {
                    bmsProductTb.setBrandCode(bmsOrderDetailTbList.get(0).getBrandCode());
                    bmsProductTbMapper.updateById(bmsProductTb);
                }
            }

        }
        return ResponseResult.getSuccess("ok");
    }


    @GetMapping("/createBmsStockExcel")
    public void createBmsStockExcel(HttpServletResponse httpServletResponse) {
        Date pointDate = DateUtil.parse("20250501235959", DatePattern.PURE_DATETIME_PATTERN);
        //step 数据查询
        List<BmsProductStockTb> bmsProductStockTbList = bmsProductStockTbMapper.selectSelective(null);
        Map<String, BmsProductStockTb> bmsProductStockTbMap = bmsProductStockTbList.stream().collect(Collectors.toMap(bmsProductStockTb -> bmsProductStockTb.getProductInnerCode() + bmsProductStockTb.getUnitCode() + bmsProductStockTb.getBatchNo() + bmsProductStockTb.getStockCode(), bmsProductStockTb -> bmsProductStockTb));


        List<BmsProductStockInLog> bmsProductStockInLogList = bmsProductStockInLogMapper.selectList(null);
        System.out.println("bmsProductStockInLogList :" + bmsProductStockInLogList.size());

        List<BmsProductStockOutLog> bmsProductStockOutLogList = bmsProductStockOutLogMapper.selectSelective(null);
        System.out.println("bmsProductStockOutLogList :" + bmsProductStockOutLogList.size());


        List<BmsMoveOrderDetailTb> bmsMoveOrderDetailTbList = bmsMoveOrderDetailTbMapper.selectList(null);
        System.out.println("bmsMoveOrderDetailTbList :" + bmsMoveOrderDetailTbList.size());


        List<BmsReturnOrderDetailTb> bmsReturnOrderDetailTbList = bmsReturnOrderDetailTbMapper.selectList(null);
        System.out.println("bmsReturnOrderDetailTbList :" + bmsReturnOrderDetailTbList.size());

        //时间过滤
        bmsProductStockInLogList = bmsProductStockInLogList.stream().filter(bmsProductStockInLog -> bmsProductStockInLog.getCreateTime().compareTo(pointDate) > 0).collect(Collectors.toList());
        System.out.println("bmsProductStockInLogList filter:" + bmsProductStockInLogList.size());

        bmsProductStockOutLogList = bmsProductStockOutLogList.stream().filter(bmsProductStockOutLog -> bmsProductStockOutLog.getCreateTime().compareTo(pointDate) > 0).collect(Collectors.toList());
        System.out.println("bmsProductStockOutLogList filter:" + bmsProductStockOutLogList.size());

        bmsMoveOrderDetailTbList = bmsMoveOrderDetailTbList.stream().filter(bmsMoveOrderDetailTb -> bmsMoveOrderDetailTb.getCreateTime().compareTo(pointDate) > 0).collect(Collectors.toList());
        System.out.println("bmsMoveOrderDetailTbList filter:" + bmsMoveOrderDetailTbList.size());

        bmsReturnOrderDetailTbList = bmsReturnOrderDetailTbList.stream().filter(bmsReturnOrderDetailTb -> bmsReturnOrderDetailTb.getCreateTime().compareTo(pointDate) > 0).collect(Collectors.toList());
        System.out.println("bmsReturnOrderDetailTbList filter:" + bmsReturnOrderDetailTbList.size());
        //复原库存
        //先复原出库  出库的数据加到库存中
        for (BmsProductStockOutLog bmsProductStockOutLog : bmsProductStockOutLogList) {
            BmsProductStockTb bmsProductStockTb = bmsProductStockTbMap.get(bmsProductStockOutLog.getProductInnerCode() + bmsProductStockOutLog.getUnitCode() + bmsProductStockOutLog.getBatchNo() + bmsProductStockOutLog.getStockCode());
            bmsProductStockTb.setCurrentStockNumber(bmsProductStockOutLog.getOutNumber() + bmsProductStockTb.getCurrentStockNumber());
        }
        //复原退货
        for (BmsReturnOrderDetailTb bmsReturnOrderDetailTb : bmsReturnOrderDetailTbList) {
            BmsProductStockTb bmsProductStockTb = bmsProductStockTbMap.get(bmsReturnOrderDetailTb.getProductInnerCode() + bmsReturnOrderDetailTb.getUnitCode() + bmsReturnOrderDetailTb.getBatchNo() + bmsReturnOrderDetailTb.getStockCode());
            log.info("bmsReturnOrderDetailTb={}" + JSONUtil.toJsonStr(bmsReturnOrderDetailTb));
            bmsProductStockTb.setCurrentStockNumber(bmsReturnOrderDetailTb.getReturnNumber() + bmsProductStockTb.getCurrentStockNumber());
        }
        //复原调拨
        for (BmsMoveOrderDetailTb bmsMoveOrderDetailTb : bmsMoveOrderDetailTbList) {
            BmsProductStockTb bmsProductStockTb = bmsProductStockTbMap.get(bmsMoveOrderDetailTb.getProductInnerCode() + bmsMoveOrderDetailTb.getUnitCode() + bmsMoveOrderDetailTb.getBatchNo() + bmsMoveOrderDetailTb.getFromStockCode());
            bmsProductStockTb.setCurrentStockNumber(bmsMoveOrderDetailTb.getMoveNumber() + bmsProductStockTb.getCurrentStockNumber());
        }
        //回退入库的
        for (BmsProductStockInLog bmsProductStockInLog : bmsProductStockInLogList) {
            log.info("bmsProductStockInLog=" + JSONUtil.toJsonStr(bmsProductStockInLog));
            BmsProductStockTb bmsProductStockTb = bmsProductStockTbMap.get(bmsProductStockInLog.getProductInnerCode() + bmsProductStockInLog.getUnitCode() + bmsProductStockInLog.getBatchNo().trim() + bmsProductStockInLog.getStockCode());
            bmsProductStockTb.setCurrentStockNumber(bmsProductStockTb.getCurrentStockNumber() - bmsProductStockInLog.getStoreNumber());
        }
        //回退调拨的
        for (BmsMoveOrderDetailTb bmsMoveOrderDetailTb : bmsMoveOrderDetailTbList) {
            BmsProductStockTb bmsProductStockTb = bmsProductStockTbMap.get(bmsMoveOrderDetailTb.getProductInnerCode() + bmsMoveOrderDetailTb.getUnitCode() + bmsMoveOrderDetailTb.getBatchNo() + bmsMoveOrderDetailTb.getToStockCode());
            bmsProductStockTb.setCurrentStockNumber(bmsProductStockTb.getCurrentStockNumber() - bmsMoveOrderDetailTb.getMoveNumber());
        }
        List<BmsStock> bmsStockList = BeanUtils.copyListProperties(bmsProductStockTbList, BmsStock.class);

        bmsStockList = bmsStockList.stream().filter(bmsStock -> bmsStock.getCurrentStockNumber() > 0).collect(Collectors.toList());
        for (BmsStock bmsStock : bmsStockList) {
            List<BmsProductStockInLog> bmsProductStockInLogs = bmsProductStockInLogMapper.selectAllByUniqueCode(bmsStock.getUniqueCode());
            if (CollectionUtil.isNotEmpty(bmsProductStockInLogs)) {
                String projectCode = bmsProductStockInLogs.get(0).getProjectCode();
                BmsProjectDict bmsProjectDict = bmsProjectDictMapper.selectOneByProjectCode(projectCode);
                bmsStock.setProjectCode(bmsProjectDict.getProjectCode());
                bmsStock.setProjectType(bmsProjectDict.getKdProjectType());
                bmsStock.setProductName(bmsProjectDict.getKdProjectName());
            }
        }

        ExcelUtil.writeExcel("D://5月1号之后数据.xlsx", "sheet1", bmsStockList, BmsStock.class);
    }

    @Data
    public static class CmsProjectDataExcel {

        @ExcelProperty("项目编号")
        private String projectCode;

        @ExcelProperty("项目名称")
        private String projectName;

    }

    @Data
    public static class BmsStock {
        /**
         * 主键ID
         */
        @ExcelProperty("主键ID")
        private Integer id;

        /**
         * 商品名称
         */
        @ExcelProperty("商品名称")
        private String productName;


        /**
         * 所属类别编号
         */
        @ExcelProperty("所属类别编号")
        private String productCategoryCode;


        /**
         * 品牌编号
         */
        @ExcelProperty("品牌编号")
        private String brandCode;

        /**
         * 品牌名称
         */
        @ExcelProperty("品牌名称")
        private String brandName;

        /**
         * 商品规格
         */
        @ExcelProperty("商品规格")
        private String productSpecs;

        /**
         * 商品批次
         */
        @ExcelProperty("商品批次")
        private String batchNo;

        /**
         * 当前库存数量
         */
        @ExcelProperty("当前库存数量")
        private Integer currentStockNumber;

        /**
         * 单位
         */
        @ExcelProperty("单位")
        private String unitCode;


        @ExcelProperty("商品编号")
        private String productInnerCode;

        @ExcelProperty("供应商编号")
        private String supplierCode;

        @ExcelProperty("供应商名称")
        private String supplierName;


        @ExcelProperty("生产日期")
        private String produceDate;


        @ExcelProperty("库房编号")
        private String stockCode;

        @ExcelProperty("项目编号")
        private String projectCode;

        @ExcelProperty("项目名称")
        private String projectName;

        @ExcelProperty("项目类型")
        private String projectType;
        /**
         * 唯一编号
         */
        @ExcelProperty("唯一编号")
        private String uniqueCode;

        @ExcelProperty("采购单价")
        private String productPrice;
    }

    @Data
    public static class ProductCleanDataExcel {
        @ExcelProperty("供应商编号")
        private String supplierCode;

        @ExcelProperty("品牌")
        private String brandName;

        @ExcelProperty("商品名称")
        private String productName;

        @ExcelProperty("商品编码")
        private String productCode;

        @ExcelProperty("商品分类")
        private String productCategory;

        @ExcelProperty("规格")
        private String product_specs;

    }

    @Data
    public static class ProductStockCleanDataExcel {

        @ExcelProperty("商品名称")
        private String productName;

        @ExcelProperty("供应商编号")
        private String supplierCode;

        @ExcelProperty("品牌名称")
        private String brandName;

        @ExcelProperty("内部编号")
        private String productInnerCode;

        @ExcelProperty("货号")
        private String productOutCode;

        @ExcelProperty("所属类别")
        private String productCategory;

        @ExcelProperty("商品规格")
        private String product_specs;

        @ExcelProperty("批次")
        private String batchNo;

        @ExcelProperty("累计入库数量")
        private String total_store_number;

        @ExcelProperty("当前库存数量")
        private String current_stock_number;

        @ExcelProperty("累计出库数量")
        private String total_out_number;

        @ExcelProperty("生产日期")
        private String produce_date;

        @ExcelProperty("过期日期")
        private String expiration_date;

        @ExcelProperty("库存位置编号")
        private String stock_location_number;
    }

    @Data
    public static class SupplierCleanDataExcel {


        @ExcelProperty("供应商编号（合同编号）")
        private String supplierCode;

        @ExcelProperty("供应商名称")
        private String supplierName;

        @ExcelProperty("开户行")
        private String openingBank;

        @ExcelProperty("银行账号")
        private String bankAccount;

        @ExcelProperty("我方负责人")
        private String leaderUserName;

        @ExcelProperty("税号")
        private String taxId;

        @ExcelProperty("联系人")
        private String contact_user_name;

        @ExcelProperty("联系电话")
        private String contact_user_telephone;

        @ExcelProperty("经营范围")
        private String business_scope;

        @ExcelProperty("合作形式")
        private String cooperate_form;

        @ExcelProperty("框架协议编号")
        private String framework_agreement_number;

        @ExcelProperty("框架协议附件")
        private String framework_agreement_annex;

        @ExcelProperty("框架协议到期时间")
        private String expiration_date;
        @ExcelProperty("备注")
        private String remark;

    }

}
