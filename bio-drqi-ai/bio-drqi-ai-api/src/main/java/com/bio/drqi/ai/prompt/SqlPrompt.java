package com.bio.drqi.ai.prompt;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.ai.config.AiProperties;
import com.bio.drqi.ai.schema.AiDomainPromptDTO;

import java.util.List;

/**
 * 业务查询计划提示词。
 * 这里虽然叫 SqlPrompt，但模型只生成结构化计划，真正 SQL 由后端白名单拼装。
 */
public final class SqlPrompt {

    private SqlPrompt() {
    }

    public static String queryPlanPrompt(String preferredChartType, AiDomainPromptDTO domain) {
        return queryPlanPrompt(preferredChartType, domain, null, null);
    }

    public static String queryPlanPrompt(String preferredChartType, AiDomainPromptDTO domain, List<AiProperties.Term> terms, String conversationContext) {
        String chartType = StrUtil.blankToDefault(preferredChartType, "auto");
        return "你是业务查询计划生成器，只能输出JSON，不要输出解释，不要输出SQL。"
                + "只能使用给定的domain、fields、metrics、dimensions。"
                + "queryType只能是aggregate/detail；统计数量、比例、趋势时用aggregate；查询列表、明细、最近几条记录时用detail。"
                + "queryType=aggregate时必须返回metrics，可选dimensions；queryType=detail时必须返回selectFields，metrics和dimensions返回空数组。"
                + "chartType只能是table/bar/line/pie/auto，用户偏好的chartType=" + chartType + "。"
                + "如果用户没有指定limit，默认100，最大500。"
                + "如果用户说这些、上述、刚才、上面、它们、这些项目等指代词，必须优先参考短期会话上下文lastResultSnapshot中的columns和rows。"
                + "例如用户说这些项目下的实施方案信息，应从lastResultSnapshot提取项目编号/项目ID/项目名称，并生成对应filters。"
                + "统计输出格式：{\"domain\":\"plasmid_quality\",\"queryType\":\"aggregate\",\"selectFields\":[],"
                + "\"metrics\":[\"totalCount\"],\"dimensions\":[\"projectCode\"],"
                + "\"filters\":[{\"field\":\"qualityInspectionType\",\"op\":\"eq\",\"value\":\"3\"}],"
                + "\"orderBy\":[],\"chartType\":\"bar\",\"limit\":100}。"
                + "明细输出格式：{\"domain\":\"plasmid_quality\",\"queryType\":\"detail\","
                + "\"selectFields\":[\"projectCode\",\"plasmidName\",\"qualityInspectionType\",\"createTime\"],"
                + "\"metrics\":[],\"dimensions\":[],\"filters\":[],\"orderBy\":[{\"field\":\"createTime\",\"direction\":\"desc\"}],"
                + "\"chartType\":\"table\",\"limit\":10}。"
                + "相关业务术语：" + JSONUtil.toJsonStr(terms)
                + "。短期会话上下文：" + StrUtil.blankToDefault(conversationContext, "无")
                + "当前支持的业务域：" + JSONUtil.toJsonStr(domain);
    }

    public static String reportPlanPrompt(List<AiDomainPromptDTO> domains) {
        return "你是业务报表计划生成器。必须直接输出一个JSON对象，回复第一个字符必须是{，最后一个字符必须是}。"
                + "禁止输出Markdown，禁止输出```代码块，禁止输出解释说明，禁止输出SQL文本，禁止输出Python，禁止输出操作建议。"
                + "你只能生成后端可执行的结构化查询计划，不能回答用户如何手工查询。"
                + "报表由多个steps组成，每个step必须包含stepCode、sheetName、queryPlan。"
                + "一个用户问题里如果同时要求多个统计、明细、Excel或表格，要拆成多个steps。"
                + "同一张表的多个统计指标可以放在一个aggregate step；不同表要拆成不同step。"
                + "如果用户要求明细，必须额外生成detail step。"
                + "如果用户要求合并、整理成一张表、对比多个结果，且多个step有共同字段，要在aggregations中生成leftJoin计划。"
                + "leftJoin只能使用各step实际输出字段作为baseKey/joinKey/baseFields/joinFields；无法确认共同字段时aggregations返回空数组。"
                + "queryPlan只能使用给定的domain、fields、metrics、dimensions。"
                + "queryType只能是aggregate/detail；统计汇总用aggregate，明细列表用detail。"
                + "aggregate必须返回metrics，可选dimensions；detail必须返回selectFields，metrics和dimensions返回空数组。"
                + "过滤条件要尽量从用户问题中提取，例如方案编号、项目编号、种子编号、时间范围。"
                + "用户说方案、实施方案、试验方案时，优先匹配字段标签为实施方案编号或字段名类似vectorTaskCode/vector_task_code的字段。"
                + "用户说成功/失败时，优先匹配检测结果、审核结果等枚举字段；不确定时仍按最相关字段生成计划。"
                + "每个step的limit默认500，最大500。"
                + "输出格式：{\"reportCode\":\"plasmid_quality_report\",\"reportName\":\"质粒质检报表\","
                + "\"steps\":[{\"stepCode\":\"summary\",\"sheetName\":\"项目汇总\",\"queryPlan\":"
                + "{\"domain\":\"plasmid_quality\",\"queryType\":\"aggregate\",\"selectFields\":[],"
                + "\"metrics\":[\"totalCount\",\"passCount\",\"failCount\",\"passRate\"],\"dimensions\":[\"projectCode\"],"
                + "\"filters\":[],\"orderBy\":[],\"chartType\":\"table\",\"limit\":500}},"
                + "{\"stepCode\":\"detail\",\"sheetName\":\"gRNA明细\",\"queryPlan\":"
                + "{\"domain\":\"plasmid_quality\",\"queryType\":\"detail\","
                + "\"selectFields\":[\"projectCode\",\"plasmidName\",\"qualityInspectionType\",\"grnaSequence\",\"createTime\"],"
                + "\"metrics\":[],\"dimensions\":[],\"filters\":[{\"field\":\"qualityInspectionType\",\"op\":\"eq\",\"value\":\"3\"}],"
                + "\"orderBy\":[{\"field\":\"createTime\",\"direction\":\"desc\"}],\"chartType\":\"table\",\"limit\":500}}],"
                + "\"aggregations\":[{\"type\":\"leftJoin\",\"sheetName\":\"项目质检合并结果\",\"baseStepCode\":\"summary\","
                + "\"joinStepCode\":\"detail\",\"baseKey\":\"projectCode\",\"joinKey\":\"projectCode\","
                + "\"baseFields\":[\"projectCode\",\"totalCount\",\"passCount\"],\"joinFields\":[\"plasmidName\",\"qualityInspectionType\"]}]}。"
                + "当前支持的业务域：" + JSONUtil.toJsonStr(domains);
    }

    public static String reportRepairPrompt(String question, List<AiDomainPromptDTO> domains, String badContent) {
        return "你刚才的输出不是合法JSON。现在必须把它修正为一个后端可解析的JSON对象。"
                + "只输出JSON，不要输出解释、SQL、Markdown、代码块或自然语言。"
                + "JSON格式必须是：{\"reportCode\":\"...\",\"reportName\":\"...\",\"steps\":[{\"stepCode\":\"...\",\"sheetName\":\"...\",\"queryPlan\":{...}}],\"aggregations\":[]}。"
                + "queryPlan只能使用给定的domain、fields、metrics、dimensions，不能输出SQL。"
                + "queryType只能是aggregate/detail；aggregate必须返回metrics；detail必须返回selectFields。"
                + "用户问题：" + question
                + "。允许的业务域：" + JSONUtil.toJsonStr(domains)
                + "。你刚才的错误输出：" + limitText(badContent, 3000);
    }

    private static String limitText(String text, int maxLength) {
        if (text == null) {
            return null;
        }
        String normalized = text.replaceAll("[\\r\\n\\t]+", " ");
        if (normalized.length() <= maxLength) {
            return normalized;
        }
        return normalized.substring(0, maxLength) + "...";
    }
}
