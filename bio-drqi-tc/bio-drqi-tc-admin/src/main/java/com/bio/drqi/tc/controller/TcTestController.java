package com.bio.drqi.tc.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.TcSampleTestTb;
import com.bio.drqi.mapper.TcSampleTestTbMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("testTc")
@Slf4j
public class TcTestController {

    @Resource
    private TcSampleTestTbMapper tcSampleTestTbMapper;

    @GetMapping("/cleanSample")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanSample() {
        List<TcSampleTestTb> tcSampleTestTbList = tcSampleTestTbMapper.selectAllByExperimentNum("C0002636");
        Map<String, List<TcSampleTestTb>> map = tcSampleTestTbList.stream().collect(Collectors.groupingBy(TcSampleTestTb::getRegionNum));
        map.forEach((reginCode, list) -> {
            list = list.stream().sorted(Comparator.comparing(TcSampleTestTb::getId)).collect(Collectors.toList());
            for (int i = 0; i < list.size(); i++) {
                TcSampleTestTb tcSampleTestTb = list.get(i);
                tcSampleTestTb.setTcSampleCode(reginCode + StringUtils.padl(String.valueOf(i + 1), 3, '0'));
                tcSampleTestTbMapper.updateById(tcSampleTestTb);
            }
        });
        return ResponseResult.getSuccess("OK");

    }

}
