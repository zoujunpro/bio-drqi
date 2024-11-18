package com.bio.drqi.manage.service.project;

import com.bio.cer.project.req.CerConversionAndTransConfirmReqDTO;
import com.bio.cer.project.req.ConversionAndTransDetailReqDTO;
import com.bio.cer.project.req.ConversionAndTransReqDTO;
import com.bio.cer.project.rsp.ConversionAndTransDetailRspDTO;
import com.bio.cer.project.rsp.ConversionAndTransRspDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface CerConversionAndTransService {
    PageInfo<ConversionAndTransRspDTO> listPage(ConversionAndTransReqDTO conversionAndTransReqDTO);

    List<ConversionAndTransRspDTO> listByVectorTask(Integer vectorTaskId);

    PageInfo<ConversionAndTransDetailRspDTO> listPageDetail( ConversionAndTransDetailReqDTO conversionAndTransDetailReqDTO);

   void transAccept( CerConversionAndTransConfirmReqDTO cerConversionAndTransConfirmReqDTO);
}
