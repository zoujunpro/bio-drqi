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
import com.bio.drqi.bsm.dto.BmsCountPeriodTaskDTO;
import com.bio.drqi.bsm.dto.BmsProductInputDTO;
import com.bio.drqi.bsm.dto.BmsProductOutDTO;
import com.bio.drqi.bsm.dto.BmsPurchaseOrderDTO;
import com.bio.drqi.bsm.enums.CooperateFormEnum;
import com.bio.drqi.bsm.enums.PurchaseTypeEnum;
import com.bio.drqi.bsm.kd.KdTaskService;
import com.bio.drqi.bsm.kd.enums.FormIdEnum;
import com.bio.drqi.bsm.kd.properties.KdProperties;
import com.bio.drqi.bsm.listener.BmsSpotCheckResultTaskListener;
import com.bio.drqi.bsm.req.BmsProductAddReqDTO;
import com.bio.drqi.bsm.service.BmsCountPeriodTaskService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
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
    private BmsSpotCheckResultTaskListener bmsSpotCheckResultTaskListener;


    @GetMapping("/testNotice")
    public ResponseResult<String> testNotice() {
        bmsSpotCheckResultTaskListener.notice();

        return ResponseResult.getSuccess("ok");
    }


    @GetMapping("/cleanBmsType")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanBmsType() {
        List<BioPrintLabelInfoTb> bioPrintLabelInfoTbList = bioPrintLabelInfoTbMapper.searchAllByLabelType("bms_label_print");
        for (BioPrintLabelInfoTb bioPrintLabelInfoTb : bioPrintLabelInfoTbList) {
            String[] uniqueCodeArr = bioPrintLabelInfoTb.getUniqueCode().split("\\|");
            if (uniqueCodeArr.length == 4) {
                bioPrintLabelInfoTb.setUniqueCode(bioPrintLabelInfoTb.getUniqueCode() + "|" + "1");
                BmsLabelPrintDTO bmsLabelPrintDTO = JSONUtil.toBean(bioPrintLabelInfoTb.getLabelText(), BmsLabelPrintDTO.class);
                bmsLabelPrintDTO.setPayType("1");
                bioPrintLabelInfoTb.setLabelText(JSONUtil.toJsonStr(bmsLabelPrintDTO));
                bioPrintLabelInfoTbMapper.updateById(bioPrintLabelInfoTb);
            }
        }
        return ResponseResult.getSuccess("ok");
    }


    @GetMapping("/cleanAddProduct")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanAddProduct() {
        List<BmsOrderDetailTb> bmsOrderDetailTbList = bmsOrderDetailTbMapper.selectSelective(null);
        if (CollectionUtil.isNotEmpty(bmsOrderDetailTbList)) {
            bmsOrderDetailTbList.forEach(bmsOrderDetailTb -> {
                log.info("bmsOrderDetailTb*******" + JSONUtil.toJsonStr(bmsOrderDetailTb));

                BmsProductTb bmsProductTb = bmsProductTbMapper.selectOneByProductInnerCode(bmsOrderDetailTb.getProductInnerCode());
                if (bmsProductTb == null) {
                    bmsProductTb = new BmsProductTb();
                    bmsProductTb.setProductName(bmsOrderDetailTb.getProductName());
                    bmsProductTb.setProductOutCode(bmsOrderDetailTb.getProductOutCode());
                    bmsProductTb.setProductInnerCode(bmsOrderDetailTb.getProductInnerCode());
                    bmsProductTb.setProductCategoryCode(bmsOrderDetailTb.getProductCategoryCode());
                    bmsProductTb.setBrandCode(bmsOrderDetailTb.getBrandCode());
                    bmsProductTb.setProductSpecs(bmsOrderDetailTb.getProductSpecs());
                    bmsProductTb.setCreateTime(bmsOrderDetailTb.getCreateTime());
                    bmsProductTb.setCreateUserId(bmsOrderDetailTb.getApplyUserId());
                    bmsProductTb.setCreateUserName(bmsOrderDetailTb.getApplyUserName());
                    bmsProductTb.setProductStatus("Y");
                    bmsProductTb.setPictureUrls(null);
                    bmsProductTb.setKdNumber(null);
                    bmsProductTbMapper.insert(bmsProductTb);
                }


            });
        }
        return ResponseResult.getSuccess("ok");

    }

    @GetMapping("/cleanStockLocation")
    public ResponseResult<String> cleanStockLocation() {
        bmsStockLocationDictMapper.selectList(null).forEach(bmsStockLocationDict -> {
            if (bmsStockLocationDict.getStockCode().length() > 30) {
                bmsStockLocationDict.setStockCode(bmsStockLocationDict.getStockCode().substring(0, 30));
                bmsStockLocationDictMapper.updateById(bmsStockLocationDict);
            }
        });
        return ResponseResult.getSuccess("ok");
    }


    @GetMapping("/cleanAmount")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanAmount() {
        List<BmsProductStockTb> bmsProductStockTbList = bmsProductStockTbMapper.selectSelective(null);
        for (BmsProductStockTb bmsProductStockTb : bmsProductStockTbList) {
            log.info("bmsProductStockTb=" + JSONUtil.toJsonStr(bmsProductStockTb));
            List<BmsProductStockInLog> bmsProductStockInLogList = bmsProductStockInLogMapper.selectSelective(BmsProductStockInLog.builder().productInnerCode(bmsProductStockTb.getProductInnerCode()).unitCode(bmsProductStockTb.getUnitCode()).build());
            bmsProductStockInLogList = bmsProductStockInLogList.stream().filter(bmsProductStockInLog -> bmsProductStockInLog.getProductPrice().doubleValue() > 0).collect(Collectors.toList());
            bmsProductStockTb.setProductPrice(CollectionUtil.isNotEmpty(bmsProductStockInLogList) ? bmsProductStockInLogList.get(0).getProductPrice() : new BigDecimal("0"));
            bmsProductStockTbMapper.updateById(bmsProductStockTb);
        }
        bmsProductStockOutLogMapper.selectSelective(null).forEach(bmsProductStockOutLog -> {
            log.info("bmsProductStockOutLog=" + JSONUtil.toJsonStr(bmsProductStockOutLog));
            List<BmsProductStockInLog> bmsProductStockInLogList = bmsProductStockInLogMapper.selectSelective(BmsProductStockInLog.builder().productInnerCode(bmsProductStockOutLog.getProductInnerCode()).unitCode(bmsProductStockOutLog.getUnitCode()).batchNo(bmsProductStockOutLog.getBatchNo()).build());
            bmsProductStockInLogList = bmsProductStockInLogList.stream().filter(bmsProductStockInLog -> bmsProductStockInLog.getProductPrice().doubleValue() > 0).collect(Collectors.toList());
            bmsProductStockOutLog.setProductPrice(CollectionUtil.isNotEmpty(bmsProductStockInLogList) ? bmsProductStockInLogList.get(0).getProductPrice() : new BigDecimal("0"));
            bmsProductStockOutLog.setOutAmount(bmsProductStockOutLog.getProductPrice().multiply(bmsProductStockOutLog.getOutNumber()));
            bmsProductStockOutLogMapper.updateById(bmsProductStockOutLog);
        });

        bmsMoveOrderDetailTbMapper.selectSelective(null).forEach(bmsMoveOrderDetailTb -> {
            log.info("bmsMoveOrderDetailTb=" + JSONUtil.toJsonStr(bmsMoveOrderDetailTb));
            List<BmsProductStockInLog> bmsProductStockInLogList = bmsProductStockInLogMapper.selectSelective(BmsProductStockInLog.builder().productInnerCode(bmsMoveOrderDetailTb.getProductInnerCode()).unitCode(bmsMoveOrderDetailTb.getUnitCode()).batchNo(bmsMoveOrderDetailTb.getBatchNo()).build());
            bmsProductStockInLogList = bmsProductStockInLogList.stream().filter(bmsProductStockInLog -> bmsProductStockInLog.getProductPrice().doubleValue() > 0).collect(Collectors.toList());
            bmsMoveOrderDetailTb.setProductPrice(CollectionUtil.isNotEmpty(bmsProductStockInLogList) ? bmsProductStockInLogList.get(0).getProductPrice() : new BigDecimal("0"));
            bmsMoveOrderDetailTb.setMoveAmount(bmsMoveOrderDetailTb.getProductPrice().multiply(bmsMoveOrderDetailTb.getMoveNumber()));
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
}
