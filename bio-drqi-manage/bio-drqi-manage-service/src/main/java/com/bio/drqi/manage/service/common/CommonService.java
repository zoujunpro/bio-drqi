package com.bio.drqi.manage.service.common;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.bio.base.api.RemoteUserService;
import com.bio.base.user.rsp.UserDetailRspDTO;
import com.bio.drqi.manage.common.OssUploadReqDTO;
import com.bio.drqi.manage.common.OssUploadRspDTO;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.oss.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommonService {

    @Resource
    private OssService ossService;

    @Value("${cer.properties.tempOssPath}")
    private String tempOssPath;

    @Resource
    public RemoteUserService remoteUserService;

    public OssUploadRspDTO upload(OssUploadReqDTO ossUploadReqDTO) {
        OssUploadRspDTO ossUploadRspDTO = new OssUploadRspDTO();
        String orgFileName = ossUploadReqDTO.getFile().getOriginalFilename();
        String tempPath = StringUtils.isEmpty(ossUploadReqDTO.getFilePath()) ? tempOssPath + "/" + DateUtil.formatDateTime(new Date()) : ossUploadReqDTO.getFilePath();
        ossService.upload(ossUploadReqDTO.getFile(), tempPath, orgFileName);
        ossUploadRspDTO.setOrgFileName(orgFileName);
        ossUploadRspDTO.setOssFileObject(tempPath + "/" + orgFileName);
        return ossUploadRspDTO;
    }


    public String getOssUrl(String ossFileObject) {
        return ossService.getUrl(ossFileObject);
    }

    public Object parseExcelData(String excelPath) {
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + excelPath;
        try {
            ossService.downloadPath(tempFilePath, excelPath);
        } catch (Exception e) {
            log.error("excel解析异常：", e);
            throw new BusinessException("文件找不到");
        }
        return ExcelUtil.readExcel(tempFilePath);
    }

    public String getPresignedObjectUrl(String objectName) {
        return ossService.getPresignedObjectUrl(objectName);
    }

    public Object getPlasmidDetail(String plasmidId) {
        String url = "http://172.16.14.2:10091/Search_plasmid_detail?name=%s&username=%s&serect_lab=%s&TJlab=False";
        ResponseResult<UserDetailRspDTO> responseResult= remoteUserService.queryUserById(SecurityContextHolder.getUserId());
        if(responseResult.isError()){
            throw new BusinessException(responseResult.getMessage());
        }
        UserDetailRspDTO userDetailRspDTO=responseResult.getData();
        List<UserDetailRspDTO.DataPermissionConfig>  dataPermissionConfigList= userDetailRspDTO.getDataPermissionConfigList();
        dataPermissionConfigList = dataPermissionConfigList.stream().filter(dataPermissionConfig -> "PLASMID_VIEW".equals(dataPermissionConfig.getPermissionType())).collect(Collectors.toList());
        String nickName = userDetailRspDTO.getNickname();
        String serect_lab = "T";

        if (CollectionUtil.isNotEmpty(dataPermissionConfigList)) {
            UserDetailRspDTO.DataPermissionConfig dataPermissionConfig = dataPermissionConfigList.get(0);
            if (dataPermissionConfig.getPermissionValue() == 1) {
                nickName = "NA";
                serect_lab = "F";
            } else if (dataPermissionConfig.getPermissionValue() == 2) {
                serect_lab = "F";
            } else if (dataPermissionConfig.getPermissionValue() == 3) {
                nickName = "NA";
            }
        }
        HttpResponse response = HttpUtil.createGet(String.format(url, plasmidId, nickName, serect_lab)).execute();
        String ss = response.body();
        System.out.println(ss);
        return JSONUtil.toBean(ss, Map.class);
    }

    public static void main(String[] args) {
        String url = "http://172.16.14.2:10091/ ?name=%s&username=%s&serect_lab=%s&TJlab=False";
        String ss = String.format(url, "EWE", "NA", "F");
        System.out.println(ss);
    }
};
