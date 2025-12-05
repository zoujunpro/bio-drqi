package com.bio.drqi.applet.service.codescan.template;

import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.applet.dto.rsp.ScanCodeTcExperimentRspDTO;
import com.bio.drqi.applet.service.codescan.AbstractBaseCodeScanService;
import com.bio.drqi.applet.service.codescan.dto.unique.PlantApplyUniqueCodeDTO;
import com.bio.drqi.applet.service.codescan.dto.unique.TcExperimentUniqueCodeDTO;
import com.bio.drqi.domain.TcExperimentDesignTb;
import com.bio.drqi.domain.TcExperimentTb;
import com.bio.drqi.mapper.TcExperimentDesignTbMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TcExperimentCodeScanService extends AbstractBaseCodeScanService<TcExperimentUniqueCodeDTO, ScanCodeTcExperimentRspDTO> {

    @Resource
    private TcExperimentDesignTbMapper tcExperimentDesignTbMapper;

    @Override
    public TcExperimentUniqueCodeDTO parseUniqueCode(String uniqueCode) {
        TcExperimentUniqueCodeDTO tcExperimentUniqueCodeDTO = new TcExperimentUniqueCodeDTO();
        String[] uniqueCodeArr = uniqueCode.split("\\|");
        tcExperimentUniqueCodeDTO.setRegionNum(uniqueCodeArr[0]);
        tcExperimentUniqueCodeDTO.setSeedNum(uniqueCodeArr[1]);
        return tcExperimentUniqueCodeDTO;
    }

    @Override
    public ScanCodeTcExperimentRspDTO dealCodeContent(TcExperimentUniqueCodeDTO tcExperimentUniqueCodeDTO) {
        TcExperimentDesignTb tcExperimentDesignTb = tcExperimentDesignTbMapper.selectOneByRegionNumAndSeedNum(tcExperimentUniqueCodeDTO.getRegionNum(), tcExperimentUniqueCodeDTO.getSeedNum());
        if (tcExperimentDesignTb == null) {
            throw new BusinessException("找不到此试验信息，小区编号：" + tcExperimentUniqueCodeDTO.getRegionNum() + "种子编号：" + tcExperimentUniqueCodeDTO.getSeedNum());
        }
        return BeanUtils.copyProperties(tcExperimentDesignTb, ScanCodeTcExperimentRspDTO.class);
    }
}
