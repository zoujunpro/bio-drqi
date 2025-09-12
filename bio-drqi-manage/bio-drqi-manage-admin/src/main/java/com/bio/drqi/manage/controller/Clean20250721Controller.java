package com.bio.drqi.manage.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.uuid.IdUtils;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.dto.project.*;
import com.bio.drqi.mapper.*;
import com.bio.drqi.tc.service.dto.TcExperimentTaskDTO;
import com.bio.drqi.tc.service.dto.TcPollinationTaskDTO;
import com.bio.drqi.tc.service.dto.TcSampleTestTaskDTO;
import com.lark.oapi.service.task.v1.enums.SourceEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/test")
public class Clean20250721Controller {

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private SeedStockTbMapper seedStockTbMapper;


    @Resource
    private CerSampleTestTbMapper cerSampleTestTbMapper;

    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;


    @Resource
    private TcExperimentDesignTbMapper tcExperimentDesignTbMapper;

    @Resource
    private TcSampleTestTbMapper tcSampleTestTbMapper;

    @Resource
    private TcPollinationTbMapper tcPollinationTbMapper;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerVectorTbMapper cerVectorTbMapper;

    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerTransformTbMapper cerTransformTbMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private SeedProduceAddressDictMapper seedProduceAddressDictMapper;

    @Resource
    private CerPlasmidQualityTbMapper cerPlasmidQualityTbMapper;

    @Resource
    private CerConversionAndTransRefMapper cerConversionAndTransRefMapper;

    @Resource
    private CerConversionAndTransTbMapper cerConversionAndTransTbMapper;

    @Resource
    private CerVectorStepLogMapper cerVectorStepLogMapper;

    @Resource
    private CerPlantDtlTbMapper cerPlantDtlTbMapper;

    @Resource
    private SeedStockDestructionLogMapper seedStockDestructionLogMapper;

    @Resource
    private BmsProductStockTbMapper bmsProductStockTbMapper;

    @Resource
    private BmsProductStockOutLogMapper bmsProductStockOutLogMapper;

    @Resource
    private BmsProductStockInLogMapper bmsProductStockInLogMapper;

    @Resource
    private BmsMoveOrderDetailTbMapper bmsMoveOrderDetailTbMapper;

    @Resource
    private BmsReturnOrderDetailTbMapper bmsReturnOrderDetailTbMapper;


    @GetMapping("cleanPlasmid")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanPlasmid() {
        List<CerPlasmidQualityTb> cerPlasmidQualityTbList = cerPlasmidQualityTbMapper.selectList(null);
        for (CerPlasmidQualityTb cerPlasmidQualityTb : cerPlasmidQualityTbList) {
            log.info("cerPlasmidQualityTb={}", JSONUtil.toJsonStr(cerPlasmidQualityTb));
            if (cerPlasmidQualityTb.getQualityInspectionType().length() == 1) {
                cerPlasmidQualityTb.setQualityInspectionType(JSONUtil.toJsonStr(Arrays.asList(cerPlasmidQualityTb.getQualityInspectionType())));
                cerPlasmidQualityTbMapper.updateById(cerPlasmidQualityTb);
            }
        }
        List<BioTaskDtlTb> list = bioTaskDtlTbMapper.selectAllByTaskTypeCode("plasmid_check");
        for (BioTaskDtlTb bioTaskDtlTb : list) {
            log.info("bioTaskDtlTb={}", JSONUtil.toJsonStr(bioTaskDtlTb));
            PlasmidDTO plasmidDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlasmidDTO.class);
            if (CollectionUtil.isEmpty(plasmidDTO.getContentList())) {
                continue;
            }
            for (PlasmidDTO.Content content : plasmidDTO.getContentList()) {
                if (content.getQualityInspectionType().length() == 1) {
                    content.setQualityInspectionType(JSONUtil.toJsonStr(Arrays.asList(content.getQualityInspectionType())));
                }
            }
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(plasmidDTO));
            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
        }
        return ResponseResult.getSuccess("ok");

    }


    @GetMapping("/cleanVector")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanVector() {
        List<CerVectorTb> cerVectorTbList = cerVectorTbMapper.selectSelective(null);
        for (CerVectorTb cerVectorTb : cerVectorTbList) {
            log.info("cerVectorTb={}", JSONUtil.toJsonStr(cerVectorTb));
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(cerVectorTb.getVectorTaskId());
            if (cerVectorTaskTb != null) {
                cerVectorTb.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
                cerVectorTbMapper.updateById(cerVectorTb);
            }
        }
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/cleanSubProject")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanSubProject() {
        List<CerSubProjectTb> cerSubProjectTbList = cerSubProjectTbMapper.selectSelective(null);
        for (CerSubProjectTb cerSubProjectTb : cerSubProjectTbList) {
            CerProjectTb cerProjectTb = cerProjectTbMapper.selectById(cerSubProjectTb.getProjectId());
            if (cerProjectTb != null) {
                cerSubProjectTb.setProjectCode(cerProjectTb.getProjectCode());
                cerSubProjectTbMapper.updateById(cerSubProjectTb);
            }
        }
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/cleanConversionAndTrans")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanConversionAndTrans() {
        List<CerConversionAndTransRef> list = cerConversionAndTransRefMapper.selectList(null);
        for (CerConversionAndTransRef cerConversionAndTransRef : list) {
            log.info("cerConversionAndTransRef={}", JSONUtil.toJsonStr(cerConversionAndTransRef));
            CerConversionAndTransTb cerConversionAndTransTb = cerConversionAndTransTbMapper.selectById(cerConversionAndTransRef.getConversionAndTransId());
            cerConversionAndTransRef.setTaskNum(cerConversionAndTransTb.getTaskNum());
            cerConversionAndTransRefMapper.updateById(cerConversionAndTransRef);
        }
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/cleanSampleAcceptorMaterial")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanSampleAcceptorMaterial() {
        List<CerSampleTestTb> list = cerSampleTestTbMapper.selectList(null);
        for (CerSampleTestTb cerSampleTestTb : list) {
            log.info("cerSampleTestTb={}", JSONUtil.toJsonStr(cerSampleTestTb));
            if (cerSampleTestTb.getAcceptorMaterial() == null || cerSampleTestTb.getAcceptorMaterial().length() == 32) {
                CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(cerSampleTestTb.getTransformCode(), cerSampleTestTb.getVectorTaskCode());
                cerSampleTestTb.setAcceptorMaterial(cerTransformTb.getAcceptorMaterial());
                cerSampleTestTbMapper.updateById(cerSampleTestTb);
            }

        }
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/cleanCloneSampleCode")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanCloneSampleCode() {
        List<CerSampleTestTb> list = cerSampleTestTbMapper.selectList(null);
        for (CerSampleTestTb cerSampleTestTb : list) {
            log.info("cerSampleTestTb={}", JSONUtil.toJsonStr(cerSampleTestTb));
            if (cerSampleTestTb.getSampleCode().contains("-")) {
                cerSampleTestTb.setCloneSampleCode(cerSampleTestTb.getSampleCode().split("-")[0]);
                cerSampleTestTbMapper.updateById(cerSampleTestTb);
            }

        }
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/cleanPlantAcceptorMaterial")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanPlantAcceptorMaterial() {
        List<CerPlantDtlTb> cerPlantDtlTbList = cerPlantDtlTbMapper.selectSelective(null);
        for (CerPlantDtlTb cerPlantDtlTb : cerPlantDtlTbList) {
            log.info("cerPlantDtlTb={}", JSONUtil.toJsonStr(cerPlantDtlTb));
            if (cerPlantDtlTb.getAcceptorMaterial() != null && cerPlantDtlTb.getAcceptorMaterial().length() == 32) {
                List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllBySampleCode(cerPlantDtlTb.getSampleCode());
                cerPlantDtlTb.setAcceptorMaterial(cerSampleTestTbList.get(0).getAcceptorMaterial());
                cerPlantDtlTbMapper.updateById(cerPlantDtlTb);
            }
        }
        return ResponseResult.getSuccess("ok");

    }

    @GetMapping("/cleanStepCode")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanStepCode() {
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectSelective(null);
        for (CerVectorTaskTb cerVectorTaskTb : cerVectorTaskTbList) {
            log.info("cerVectorTaskTb={}", JSONUtil.toJsonStr(cerVectorTaskTb));
            List<CerVectorStepLog> cerVectorStepLogList = cerVectorStepLogMapper.selectAllByVectorTaskIdOrderById(cerVectorTaskTb.getId());
            if (CollectionUtil.isNotEmpty(cerVectorStepLogList)) {
                cerVectorTaskTb.setCurrentStepCode(cerVectorStepLogList.get(cerVectorStepLogList.size() - 1).getStepCode());
                cerVectorTaskTbMapper.updateById(cerVectorTaskTb);
            }
        }
        return ResponseResult.getSuccess("ok");
    }


    @GetMapping("/cleanProjectType")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult cleanProjectType() {
        List<Project> projectList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\上线\\project.xlsx", Project.class);
        for (Project project : projectList) {
            log.info("project={}", JSONUtil.toJsonStr(project));
            CerProjectTb cerProjectTb = cerProjectTbMapper.selectById(project.id);
            if ("大田作物".equals(project.type)) {
                cerProjectTb.setProjectCategoryCode("1");
            } else if ("经济作物".equals(project.type)) {
                cerProjectTb.setProjectCategoryCode("2");

            } else if ("合成学作物".equals(project.type)) {
                cerProjectTb.setProjectCategoryCode("3");

            }
            cerProjectTbMapper.updateById(cerProjectTb);

            BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerProjectTb.getTaskNum());
            ProjectAddDTO projectAddDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), ProjectAddDTO.class);
            projectAddDTO.setProjectCategoryCode(cerProjectTb.getProjectCategoryCode());
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(projectAddDTO));
            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);

        }

        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/cleanBmsStockLocation")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanBmsStockLocation() {
        List<BmsProductStockTb> bmsProductStockTbList = bmsProductStockTbMapper.selectList(null);
        for (BmsProductStockTb bmsProductStockTb : bmsProductStockTbList) {
            if (StringUtils.isEmpty(bmsProductStockTb.getStockLocationNumber())) {
                continue;
            }
            if (bmsProductStockTb.getStockLocationNumber().contains("[")) {
                continue;
            }
            bmsProductStockTb.setStockLocationNumber(JSONUtil.toJsonStr(Arrays.asList(bmsProductStockTb.getStockLocationNumber())));
            bmsProductStockTbMapper.updateById(bmsProductStockTb);
        }
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/cleanVectorTaskDeliveryMethod")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanVectorTaskDeliveryMethod() {
        List<VectorTask> vectorTaskList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\上线\\vectorTask.xlsx", VectorTask.class);
        for (VectorTask vectorTask : vectorTaskList) {
            //A 农杆菌转化 B基因枪 P原生质体转化 V病毒载体
            log.info("vectorTask={}", JSONUtil.toJsonStr(vectorTask));
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(vectorTask.id);
            if (vectorTask.getDeliveryMethod().contains("|")) {
                StringBuffer deliveryMethodBuf = new StringBuffer("");
                String[] deliveryMethodArr = vectorTask.getDeliveryMethod().split("\\|");
                for (String str : deliveryMethodArr) {
                    if ("原生质体转化".equals(str)) {
                        deliveryMethodBuf.append("P").append("|");
                    } else if ("农杆菌转化".equals(str)) {
                        deliveryMethodBuf.append("A").append("|");
                    } else if ("基因枪".equals(str)) {
                        deliveryMethodBuf.append("B").append("|");
                    } else if ("病毒载体".equals(str)) {
                        deliveryMethodBuf.append("V").append("|");
                    }
                }
                cerVectorTaskTb.setDeliveryMethod(deliveryMethodBuf.substring(0, deliveryMethodBuf.length() - 1));
            } else {
                if ("原生质体转化".equals(vectorTask.getDeliveryMethod())) {
                    cerVectorTaskTb.setDeliveryMethod("P");
                } else if ("农杆菌转化".equals(vectorTask.deliveryMethod)) {
                    cerVectorTaskTb.setDeliveryMethod("A");
                } else if ("基因枪".equals(vectorTask.deliveryMethod)) {
                    cerVectorTaskTb.setDeliveryMethod("B");
                } else if ("病毒载体".equals(vectorTask.deliveryMethod)) {
                    cerVectorTaskTb.setDeliveryMethod("V");
                }
            }
            cerVectorTaskTbMapper.updateById(cerVectorTaskTb);
            BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerVectorTaskTb.getTaskNum());
            ImplementPlanAddDTO implementPlanAddDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), ImplementPlanAddDTO.class);
            implementPlanAddDTO.setDeliveryMethod(cerVectorTaskTb.getDeliveryMethod());
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(implementPlanAddDTO));
            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
        }

        return ResponseResult.getSuccess("ok");

    }

    @GetMapping("/cleanTransDeliveryMethod")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanTransDeliveryMethod() {
        List<CerTransformTb> cerTransformTbList = cerTransformTbMapper.selectSelective(null);
        for (CerTransformTb cerTransformTb : cerTransformTbList) {
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(cerTransformTb.getVectorTaskCode());
            //A 农杆菌转化 B基因枪 P原生质体转化 V病毒载体
            if (cerVectorTaskTb.getDeliveryMethod().contains("|")) {
                if ("原生质体转化".equals(cerTransformTb.getDeliveryMethod())) {
                    cerTransformTb.setDeliveryMethod("P");
                    cerTransformTbMapper.updateById(cerTransformTb);
                } else if ("农杆菌转化".equals(cerTransformTb.getDeliveryMethod())) {
                    cerTransformTb.setDeliveryMethod("A");
                    cerTransformTbMapper.updateById(cerTransformTb);
                } else if ("基因枪".equals(cerTransformTb.getDeliveryMethod())) {
                    cerTransformTb.setDeliveryMethod("B");
                    cerTransformTbMapper.updateById(cerTransformTb);
                } else if ("病毒载体".equals(cerTransformTb.getDeliveryMethod())) {
                    cerTransformTb.setDeliveryMethod("V");
                    cerTransformTbMapper.updateById(cerTransformTb);
                }
            } else {
                cerTransformTb.setDeliveryMethod(cerVectorTaskTb.getDeliveryMethod());
                cerTransformTbMapper.updateById(cerTransformTb);
            }
        }

        return ResponseResult.getSuccess("ok");

    }

    @GetMapping("/cleanTransTask")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanTransTask() {
        List<BioTaskDtlTb> bioTaskDtlTbList = bioTaskDtlTbMapper.selectAllByTaskTypeCode("transform");
        for (BioTaskDtlTb bioTaskDtlTb : bioTaskDtlTbList) {
            TransformDTO transformDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TransformDTO.class);
            if (CollectionUtil.isNotEmpty(transformDTO.getContentList())) {
                for (TransformDTO.Content content : transformDTO.getContentList()) {
                    CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(content.getTransformCode(), transformDTO.getVectorTaskCode());
                    if (cerTransformTb != null) {
                        content.setDeliveryMethod(cerTransformTb.getDeliveryMethod());
                    }
                }
            }
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(transformDTO));
            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);

        }
        return ResponseResult.getSuccess("ok");

    }


    @GetMapping("/createBmsStockExcel")
    public void createBmsStockExcel(HttpServletResponse httpServletResponse) {
        Date pointDate = DateUtil.parse("20250701000000", DatePattern.PURE_DATETIME_PATTERN);
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
            bmsProductStockTb.setCurrentStockNumber(bmsProductStockOutLog.getOutNumber()+bmsProductStockTb.getCurrentStockNumber());
        }
        //复原退货
        for (BmsReturnOrderDetailTb bmsReturnOrderDetailTb:bmsReturnOrderDetailTbList){
            BmsProductStockTb bmsProductStockTb = bmsProductStockTbMap.get(bmsReturnOrderDetailTb.getProductInnerCode() + bmsReturnOrderDetailTb.getUnitCode() + bmsReturnOrderDetailTb.getBatchNo() + bmsReturnOrderDetailTb.getStockCode());
            bmsProductStockTb.setCurrentStockNumber(bmsReturnOrderDetailTb.getReturnNumber()+bmsProductStockTb.getCurrentStockNumber());
        }
        //复原调拨
        for (BmsMoveOrderDetailTb bmsMoveOrderDetailTb:bmsMoveOrderDetailTbList){
            BmsProductStockTb bmsProductStockTb = bmsProductStockTbMap.get(bmsMoveOrderDetailTb.getProductInnerCode() + bmsMoveOrderDetailTb.getUnitCode() + bmsMoveOrderDetailTb.getBatchNo() + bmsMoveOrderDetailTb.getFromStockCode());
            bmsProductStockTb.setCurrentStockNumber(bmsMoveOrderDetailTb.getMoveNumber()+bmsProductStockTb.getCurrentStockNumber());
        }
        //回退入库的
        for (BmsProductStockInLog bmsProductStockInLog:bmsProductStockInLogList){
            BmsProductStockTb bmsProductStockTb = bmsProductStockTbMap.get(bmsProductStockInLog.getProductInnerCode() + bmsProductStockInLog.getUnitCode() + bmsProductStockInLog.getBatchNo() + bmsProductStockInLog.getStockCode());
            bmsProductStockTb.setCurrentStockNumber(bmsProductStockTb.getCurrentStockNumber()-bmsProductStockInLog.getStoreNumber());
        }
        //回退调拨的
        for (BmsMoveOrderDetailTb bmsMoveOrderDetailTb:bmsMoveOrderDetailTbList){
            BmsProductStockTb bmsProductStockTb = bmsProductStockTbMap.get(bmsMoveOrderDetailTb.getProductInnerCode() + bmsMoveOrderDetailTb.getUnitCode() + bmsMoveOrderDetailTb.getBatchNo() + bmsMoveOrderDetailTb.getFromStockCode());
            bmsProductStockTb.setCurrentStockNumber(bmsProductStockTb.getCurrentStockNumber()-bmsMoveOrderDetailTb.getMoveNumber());
        }


    List<BmsStock> bmsStockList= BeanUtils.copyListProperties(bmsProductStockTbList,BmsStock.class);
        ExcelUtil.writeExcel("D://7月1号之后数据.xlsx","sheet1",bmsStockList,BmsStock.class);




    }


    @Data
    public static class BmsStock{
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
    }

    @Data
    public static class VectorTask {


        @ExcelProperty("id")
        private String id;


        @ExcelProperty("实施方案编号")
        private String code;

        @ExcelProperty("递送方式")
        private String deliveryMethod;

    }

    @Data
    public static class Project {

        @ExcelProperty("id")
        private Integer id;

        @ExcelProperty("项目名称")
        private String name;

        @ExcelProperty("项目编号")
        private String code;

        @ExcelProperty("项目分类")
        private String type;
    }


}
