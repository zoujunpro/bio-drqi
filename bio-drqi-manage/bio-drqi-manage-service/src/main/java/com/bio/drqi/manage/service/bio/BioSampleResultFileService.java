package com.bio.drqi.manage.service.bio;

import com.bio.drqi.manage.sample.req.SampleResultFileListPageReqDTO;
import com.bio.drqi.manage.sample.req.SampleResultFileUploadFileReqDTO;
import com.bio.drqi.manage.sample.rsp.SampleResultFileListPageRspDTO;
import com.github.pagehelper.PageInfo;


public interface BioSampleResultFileService {


    /**
     * 取样检测批量检测结果上送管理-分页查询
     *
     * @param sampleResultFileListPageReqDTO
     * @return
     */
    PageInfo<SampleResultFileListPageRspDTO> listPage(SampleResultFileListPageReqDTO sampleResultFileListPageReqDTO);


    /**
     * 取样检测批量检测结果上送管理-结果文件上送
     *
     * @param sampleResultFileUploadFileReqDTO
     * @return
     */

    void uploadFile(SampleResultFileUploadFileReqDTO sampleResultFileUploadFileReqDTO);
}
