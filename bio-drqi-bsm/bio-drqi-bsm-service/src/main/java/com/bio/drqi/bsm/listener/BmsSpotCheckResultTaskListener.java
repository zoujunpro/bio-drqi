package com.bio.drqi.bsm.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.drqi.domain.BmsProductStockTb;
import com.bio.drqi.domain.BmsStockDict;
import com.bio.drqi.domain.SystemFeishuUserTb;
import com.bio.drqi.domain.SystemUserTb;
import com.bio.drqi.feishu.FeiShuService;
import com.bio.drqi.feishu.MessageTypeEnum;
import com.bio.drqi.feishu.dto.Message;
import com.bio.drqi.feishu.dto.NoticeUserDTO;
import com.bio.drqi.mapper.BmsStockDictMapper;
import com.bio.drqi.mapper.SystemFeishuUserTbMapper;
import com.bio.drqi.mapper.SystemUserTbMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BmsSpotCheckResultTaskListener {

    @Resource
    private FeiShuService feiShuService;

    @Resource
    private BmsStockDictMapper bmsStockDictMapper;

    @Resource
    private SystemUserTbMapper systemUserTbMapper;

    @Resource
    private SystemFeishuUserTbMapper systemFeishuUserTbMapper;


    public void notice(List<BmsProductStockTb> bmsProductStockTbList) {
        if (CollectionUtil.isEmpty(bmsProductStockTbList)) {
            return;
        }
        Map<String, List<BmsProductStockTb>> bmsProductStockTbListMap = bmsProductStockTbList.stream().collect(Collectors.groupingBy(BmsProductStockTb::getUnitCode));
        bmsProductStockTbListMap.forEach((unitCode, list) -> {
            List<NoticeUserDTO> noticeUserDTOList = new ArrayList<>();
            Message message = new Message();
            NoticeUserDTO noticeUserDTO = new NoticeUserDTO();
            if ("beijing".equals(unitCode)) {
                SystemUserTb systemUserTb = systemUserTbMapper.selectOneByEmail("liuru@qi-biodesign.com");
                SystemFeishuUserTb systemFeishuUserTb = systemFeishuUserTbMapper.selectOneByLocalUserId(systemUserTb.getId());
                noticeUserDTO.setUsername(systemUserTb.getUsername());
                noticeUserDTO.setOpenId(systemFeishuUserTb.getFeishuUserId());
                noticeUserDTOList.add(noticeUserDTO);
            }else {
                SystemUserTb systemUserTb = systemUserTbMapper.selectOneByEmail("leixing@qi-biodesign.com");
                SystemFeishuUserTb systemFeishuUserTb = systemFeishuUserTbMapper.selectOneByLocalUserId(systemUserTb.getId());
                noticeUserDTO.setUsername(systemUserTb.getUsername());
                noticeUserDTO.setOpenId(systemFeishuUserTb.getFeishuUserId());
                noticeUserDTOList.add(noticeUserDTO);
            }
            list.forEach(bmsProductStockTb -> {
                BmsStockDict bmsStockDict = bmsStockDictMapper.selectOneByStockCode(bmsProductStockTb.getStockCode());
                Message.Row row = new Message.Row();
                row.setProductInnerCode(bmsProductStockTb.getProductInnerCode());
                row.setProductName(bmsProductStockTb.getProductName());
                row.setStockName(bmsStockDict.getStockName());
                row.setExpirationDate(bmsProductStockTb.getExpirationDate());
                message.getRowList().add(row);
            });
            feiShuService.sendCardMessage(noticeUserDTOList, message, MessageTypeEnum.spot_check_result);
        });
    }
}
