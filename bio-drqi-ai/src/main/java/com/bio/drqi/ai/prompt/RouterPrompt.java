package com.bio.drqi.ai.prompt;

import cn.hutool.json.JSONUtil;
import com.bio.drqi.ai.config.AiProperties;
import com.bio.drqi.ai.schema.AiDomainSummaryDTO;

import java.util.List;

/**
 * 意图和业务域路由提示词。
 */
public final class RouterPrompt {

    private RouterPrompt() {
    }

    public static String intentPrompt() {
        return "你是系统意图识别器，只能输出JSON，不要输出解释。"
                + "intent只能是chat、business_query、workflow、report_export、unknown。"
                + "chat表示普通问答、知识解释、写作、闲聊，不能查询系统实时数据。"
                + "business_query表示用户要查询、统计、分析、对比、导出系统业务数据，包含表格、图表、Excel、数量、明细、趋势等需求。"
                + "workflow表示用户要查询审批、工单、待办、已办、我发起的流程。"
                + "report_export表示用户明确要生成、下载、导出报表或Excel文件。"
                + "unknown表示用户表达不清，无法判断要做什么。"
                + "如果用户问题里出现项目编号、方案编号、种子编号、样品编号等业务编号，并且有查看、看看、查一下、统计、分析等意图，优先返回business_query。"
                + "输出格式：{\"intent\":\"business_query\",\"confidence\":0.9,\"reason\":\"用户要求统计业务数据\"}。";
    }

    public static String domainSelectPrompt(List<AiDomainSummaryDTO> domains) {
        return domainSelectPrompt(domains, null);
    }

    public static String domainSelectPrompt(List<AiDomainSummaryDTO> domains, List<AiProperties.Term> terms) {
        return "你是业务域选择器，只能输出JSON，不要输出解释。"
                + "只能从给定domains中选择一个最匹配用户问题的domain。"
                + "domains里的fields是该业务域的代表字段，用户提到项目、方案、种子、样本、质检、库存等词时，要结合name和fields判断。"
                + "如果相关业务术语给出了domain，要优先参考该domain。"
                + "如果用户问的是闲聊、知识问答、系统无关问题，或者不是查询本系统业务数据，返回{\"domain\":\"general_chat\"}。"
                + "如果用户问题可能是查询本系统业务数据，优先选择最匹配的业务域，不要返回general_chat。"
                + "输出格式：{\"domain\":\"plasmid_quality\"}。"
                + "相关业务术语：" + JSONUtil.toJsonStr(terms)
                + "当前可选domains：" + JSONUtil.toJsonStr(domains)
                + "，额外可选domain：general_chat";
    }
}
