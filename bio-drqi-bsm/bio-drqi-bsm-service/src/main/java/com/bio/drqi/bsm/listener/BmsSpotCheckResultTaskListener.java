package com.bio.drqi.bsm.listener;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.BmsProductStockTb;
import com.bio.drqi.domain.BmsStockDict;
import com.bio.drqi.domain.SystemFeishuUserTb;
import com.bio.drqi.domain.SystemUserTb;
import com.bio.drqi.feishu.FeiShuService;
import com.bio.drqi.feishu.MessageTypeEnum;
import com.bio.drqi.feishu.dto.Message;
import com.bio.drqi.feishu.dto.NoticeUserDTO;
import com.bio.drqi.mapper.BmsProductStockTbMapper;
import com.bio.drqi.mapper.BmsStockDictMapper;
import com.bio.drqi.mapper.SystemFeishuUserTbMapper;
import com.bio.drqi.mapper.SystemUserTbMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Date;
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

    @Resource
    private BmsProductStockTbMapper bmsProductStockTbMapper;


    public void notice() {
        List<BmsProductStockTb> bmsProductStockTbList = bmsProductStockTbMapper.selectSelective(null);
        //当前日期加7天
        Date originalDate = new Date(); // 当前日期
        // 使用DateUtil的offset方法来增加7天
        Date lastDate = DateUtil.offset(originalDate, DateField.DAY_OF_YEAR, 7);
        bmsProductStockTbList = bmsProductStockTbList.stream().filter(bmsProductStockTb -> StringUtils.isNotEmpty(bmsProductStockTb.getExpirationDate())&&bmsProductStockTb.getCurrentStockNumber().doubleValue()>0).filter(bmsProductStockTb -> isValidDateFormat(bmsProductStockTb.getExpirationDate()) && DateUtil.parse(bmsProductStockTb.getExpirationDate(), DatePattern.NORM_DATE_PATTERN).isBefore(lastDate)).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(bmsProductStockTbList)) {
            return;
        }
        Map<String, List<BmsProductStockTb>> bmsProductStockTbListMap = bmsProductStockTbList.stream().collect(Collectors.groupingBy(BmsProductStockTb::getUnitCode));
        bmsProductStockTbListMap.forEach((unitCode, list) -> {
            List<NoticeUserDTO> noticeUserDTOList = new ArrayList<>();
            Message message = new Message();
            NoticeUserDTO noticeUserDTO = new NoticeUserDTO();
            if ("beijing".equals(unitCode)) {
                //liuru@qi-biodesign.com
                SystemUserTb systemUserTb = systemUserTbMapper.selectOneByEmail("liuru@qi-biodesign.com");
                SystemFeishuUserTb systemFeishuUserTb = systemFeishuUserTbMapper.selectOneByLocalUserId(systemUserTb.getId());
                noticeUserDTO.setUsername(systemUserTb.getUsername());
                noticeUserDTO.setOpenId(systemFeishuUserTb.getFeishuUserId());
                noticeUserDTOList.add(noticeUserDTO);
            } else {
                //leixing@qi-biodesign.com
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

    private boolean isValidDateFormat(String date) {
        String pattern = "\\d{4}-\\d{2}-\\d{2}";
        return date.matches(pattern);
    }

}
