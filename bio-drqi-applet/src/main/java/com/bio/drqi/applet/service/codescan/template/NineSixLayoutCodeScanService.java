package com.bio.drqi.applet.service.codescan.template;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.applet.dto.rsp.NineSixLayoutRspDTO;
import com.bio.drqi.applet.service.codescan.AbstractBaseCodeScanService;
import com.bio.drqi.common.dto.SampleUnitDTO;
import com.bio.drqi.domain.BioSampleLayoutTb;
import com.bio.drqi.mapper.BioSampleLayoutTbMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class NineSixLayoutCodeScanService extends AbstractBaseCodeScanService<String, NineSixLayoutRspDTO> {

    @Resource
    private BioSampleLayoutTbMapper bioSampleLayoutTbMapper;

    @Override
    public String parseUniqueCode(String uniqueCode) {
        return uniqueCode;
    }

    @Override
    public NineSixLayoutRspDTO dealCodeContent(String layoutNo) {
        NineSixLayoutRspDTO nineSixLayoutRspDTO=new NineSixLayoutRspDTO();
        String applyNo = layoutNo.split("-")[0];
        String number = layoutNo.split("-")[1];
        BioSampleLayoutTb bioSampleLayoutTb = bioSampleLayoutTbMapper.selectOneByApplyNo(applyNo);
        JSONArray layoutListJsonArray = JSONUtil.parseArray(bioSampleLayoutTb.getPlateContent());
        JSONArray layoutJsonArray = JSONUtil.parseArray(layoutListJsonArray.get(Integer.valueOf(number) - 1).toString());
        for (int j = 0; j < layoutJsonArray.size(); j++) {
            List<SampleUnitDTO> rowList = JSONUtil.toList(layoutJsonArray.getJSONArray(j).toString(), SampleUnitDTO.class);
            nineSixLayoutRspDTO.getLayout().add(rowList);
        }
        return nineSixLayoutRspDTO;
    }


}
