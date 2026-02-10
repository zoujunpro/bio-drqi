package com.bio.drqi.bsm.listener;

import com.bio.drqi.domain.BmsProductStockTb;
import com.bio.drqi.feishu.FeiShuService;
import com.bio.drqi.feishu.MessageTypeEnum;
import com.bio.drqi.feishu.dto.Message;
import com.bio.drqi.feishu.dto.NoticeUserDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

@Service
public class BmsSpotCheckResultTaskListener {

    @Resource
    private FeiShuService feiShuService;


    private String messageBody = "{\n" +
            "      \"tag\": \"column_set\",\n" +
            "      \"flex_mode\": \"none\",\n" +
            "      \"background_style\": \"grey\",\n" +
            "      \"columns\": [{\n" +
            "        \"tag\": \"column\",\n" +
            "        \"width\": \"weighted\",\n" +
            "        \"weight\": 1,\n" +
            "        \"vertical_align\": \"top\",\n" +
            "        \"elements\": [{\n" +
            "          \"tag\": \"div\",\n" +
            "          \"text\": {\n" +
            "            \"content\": \"'%s'\",\n" +
            "            \"tag\": \"plain_text\"\n" +
            "          }\n" +
            "        }]\n" +
            "      }, {\n" +
            "        \"tag\": \"column\",\n" +
            "        \"width\": \"weighted\",\n" +
            "        \"weight\": 1,\n" +
            "        \"vertical_align\": \"top\",\n" +
            "        \"elements\": [{\n" +
            "          \"tag\": \"div\",\n" +
            "          \"text\": {\n" +
            "            \"content\": \"'%s'\",\n" +
            "            \"tag\": \"plain_text\"\n" +
            "          }\n" +
            "        }]\n" +
            "      }, {\n" +
            "        \"tag\": \"column\",\n" +
            "        \"width\": \"weighted\",\n" +
            "        \"weight\": 1,\n" +
            "        \"vertical_align\": \"top\",\n" +
            "        \"elements\": [{\n" +
            "          \"tag\": \"div\",\n" +
            "          \"text\": {\n" +
            "            \"content\": \"'%s'\",\n" +
            "            \"tag\": \"plain_text\"\n" +
            "          }\n" +
            "        }]\n" +
            "      }, {\n" +
            "        \"tag\": \"column\",\n" +
            "        \"width\": \"weighted\",\n" +
            "        \"weight\": 1,\n" +
            "        \"vertical_align\": \"top\",\n" +
            "        \"elements\": [{\n" +
            "          \"tag\": \"div\",\n" +
            "          \"text\": {\n" +
            "            \"content\": \"'%s'\",\n" +
            "            \"tag\": \"plain_text\"\n" +
            "          }\n" +
            "        }]\n" +
            "      }]\n" +
            "    }";


    public void notice(List<BmsProductStockTb> bmsProductStockTbList) {
        Message message = new Message();
        for (BmsProductStockTb bmsProductStockTb : bmsProductStockTbList) {
            StringBuffer stringBuffer = new StringBuffer(messageBody);
            String tempMessageBody = stringBuffer.toString();
            String.format(tempMessageBody, bmsProductStockTb.getProductInnerCode(), "test", bmsProductStockTb.getStockCode(), bmsProductStockTb.getExpirationDate());
            message.getMsgList().add(tempMessageBody);
        }
        List<NoticeUserDTO> noticeUserDTOList = new ArrayList<>();
        NoticeUserDTO noticeUserDTO = new NoticeUserDTO();
        noticeUserDTO.setUsername("zoujun");
        noticeUserDTO.setOpenId("ou_05b17b1a6234bbd3ed50587599b5162d");
        noticeUserDTOList.add(noticeUserDTO);
        feiShuService.sendCardMessage(noticeUserDTOList, message, MessageTypeEnum.spot_check_result);

    }
}
