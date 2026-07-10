package com.bio.drqi.ai.semantic.impl;

import com.alibaba.fastjson2.JSONObject;
import com.bio.drqi.ai.api.llm.AiLlmService;
import com.bio.drqi.ai.api.llm.dto.AiLlmChatReqDTO;
import com.bio.drqi.ai.api.llm.dto.AiLlmChatRspDTO;
import com.bio.drqi.ai.api.llm.dto.AiLlmMessageDTO;
import com.bio.drqi.ai.common.enums.AiSemanticPatternTypeEnum;
import com.bio.drqi.ai.common.enums.AiMessageRoleEnum;
import com.bio.drqi.ai.dao.domain.AiSemanticPattern;
import com.bio.drqi.ai.dao.mapper.AiSemanticPatternMapper;
import com.bio.drqi.ai.dto.memory.AiMemoryMessageDTO;
import com.bio.drqi.ai.dto.semantic.AiQueryRewriteReqDTO;
import com.bio.drqi.ai.dto.semantic.AiQueryRewriteRspDTO;
import com.bio.drqi.ai.semantic.AiQueryRewriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 默认问题改写实现。
 *
 * <p>这个类只负责一件事：把用户当前问题改写成更完整的问题。
 * 典型场景是用户说“它延期了吗”“这个有结果吗”，需要结合上一轮会话判断“它/这个”指什么。</p>
 *
 * <p>执行顺序：
 * 1. 先判断问题里有没有指代词；
 * 2. 有指代词时优先交给 LLM 做结构化改写；
 * 3. LLM 不可用或返回异常时，使用短期记忆做规则兜底；
 * 4. 无法确定上下文时，不强行改写，避免编造业务对象。</p>
 */
@Service
public class AiQueryRewriteServiceImpl implements AiQueryRewriteService {

    /**
     * 内置指代词兜底列表。
     *
     * <p>数据库 ai_semantic_pattern 可以扩展 REFERENCE_WORD；
     * 数据库不可用或未配置时，仍然使用这里的基础指代词。</p>
     */
    private static final String[] REFERENCE_WORDS = {
            "它", "他", "她", "这个", "那个", "上个", "上一条", "刚才", "刚刚", "前面", "其", "该", "这些", "那些", "我的", "你的"
    };

    @Resource
    private AiSemanticPatternMapper aiSemanticPatternMapper;

    @Autowired(required = false)
    private AiLlmService aiLlmService;

    @Override
    public AiQueryRewriteRspDTO rewrite(AiQueryRewriteReqDTO reqDTO) {
        AiQueryRewriteRspDTO rspDTO = new AiQueryRewriteRspDTO();
        String originalQuery = reqDTO == null ? null : reqDTO.getOriginalQuery();
        rspDTO.setOriginalQuery(originalQuery);

        // 原问题为空时没有改写依据，直接返回未改写。
        if (!hasText(originalQuery)) {
            rspDTO.setRewrittenQuery(originalQuery);
            rspDTO.setRewritten(Boolean.FALSE);
            rspDTO.setReason("用户问题为空，不做改写");
            return rspDTO;
        }

        // 没有“它/这个/刚才”等指代词时，说明问题本身大概率已经完整，不需要消解上下文。
        if (!hasReferenceWord(originalQuery)) {
            rspDTO.setRewrittenQuery(originalQuery);
            rspDTO.setRewritten(Boolean.FALSE);
            rspDTO.setReason("未检测到指代词，不做改写");
            return rspDTO;
        }

        // 检测到指代词时，优先让 LLM 根据最近对话做结构化改写。
        // 例如“它延期了吗？”可以被改写为“P001 项目是否延期？”。
        AiQueryRewriteRspDTO llmResult = rewriteByLlm(reqDTO);
        if (llmResult != null && hasText(llmResult.getRewrittenQuery())) {
            return llmResult;
        }

        // LLM 不可用或返回不可解析时，使用短期记忆中的最近用户问题作为指代词兜底上下文。
        String recentTopic = findRecentTopic(reqDTO.getShortMemory(), originalQuery);
        if (!hasText(recentTopic)) {
            rspDTO.setRewrittenQuery(originalQuery);
            rspDTO.setRewritten(Boolean.FALSE);
            rspDTO.setReason("检测到指代词，但未找到可用上下文");
            return rspDTO;
        }

        rspDTO.setRewrittenQuery("结合上下文：" + recentTopic + "。用户问题：" + originalQuery);
        rspDTO.setRewritten(Boolean.TRUE);
        rspDTO.setReason("检测到指代词，使用最近一轮上下文补全问题");
        return rspDTO;
    }

    /**
     * 从短期记忆里找最近的上下文主题。
     *
     * <p>优先找最近一条用户消息。例如：</p>
     * <pre>
     * user: 查询 P001 项目进度
     * assistant: P001 项目处于实施阶段
     * user: 它延期了吗？
     * </pre>
     *
     * <p>这里优先取“查询 P001 项目进度”，用于判断“它”指 P001 项目。
     * 如果没有用户消息，再退一步取最近的非空消息兜底。</p>
     */
    private String findRecentTopic(List<AiMemoryMessageDTO> shortMemory, String originalQuery) {
        if (shortMemory == null || shortMemory.isEmpty()) {
            return null;
        }

        // 优先找上一轮用户问了什么，用它来判断“它/这个/刚才”指的是哪个业务对象。
        for (int i = shortMemory.size() - 1; i >= 0; i--) {
            AiMemoryMessageDTO message = shortMemory.get(i);
            if (message == null || !hasText(message.getContent())) {
                continue;
            }
            // 当前问题本身已经保存进短期记忆时，要跳过它，避免用“它延期了吗？”解释“它延期了吗？”。
            if (originalQuery.trim().equals(message.getContent().trim())) {
                continue;
            }
            if (AiMessageRoleEnum.USER.getCode().equals(message.getRole())) {
                return limit(message.getContent(), 120);
            }
        }

        // 如果没有找到用户消息，再退而求其次使用最近一条非空消息兜底。
        for (int i = shortMemory.size() - 1; i >= 0; i--) {
            AiMemoryMessageDTO message = shortMemory.get(i);
            if (message != null && hasText(message.getContent()) && !originalQuery.trim().equals(message.getContent().trim())) {
                return limit(message.getContent(), 120);
            }
        }
        return null;
    }

    /**
     * 判断问题是否包含指代词。
     */
    private boolean hasReferenceWord(String query) {
        List<String> words = loadReferenceWords();
        for (String word : words) {
            if (query.contains(word)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 加载指代词列表。
     *
     * <p>先从配置表加载，方便运营或实施人员调整话术；
     * 再合并代码内置词，保证配置表为空时也能工作。</p>
     */
    private List<String> loadReferenceWords() {
        List<String> words = new ArrayList<String>();
        try {
            List<AiSemanticPattern> patterns = aiSemanticPatternMapper.selectActiveByPatternType(
                    AiSemanticPatternTypeEnum.REFERENCE_WORD.getCode()
            );
            if (patterns != null) {
                for (AiSemanticPattern pattern : patterns) {
                    if (hasText(pattern.getPatternText())) {
                        words.add(pattern.getPatternText());
                    }
                }
            }
        } catch (Exception ignored) {
            // 配置表不可用时使用内置规则兜底。
        }
        for (String word : REFERENCE_WORDS) {
            if (!words.contains(word)) {
                words.add(word);
            }
        }
        return words;
    }

    /**
     * 调用 LLM 做结构化改写。
     *
     * <p>这里允许 aiLlmService 不存在，因为本地开发或未配置模型时不能阻断聊天。
     * 失败时返回 null，外层会走规则兜底。</p>
     */
    private AiQueryRewriteRspDTO rewriteByLlm(AiQueryRewriteReqDTO reqDTO) {
        if (aiLlmService == null) {
            return null;
        }
        try {
            AiLlmChatReqDTO chatReqDTO = new AiLlmChatReqDTO();
            chatReqDTO.getMessages().add(new AiLlmMessageDTO("system", buildRewriteSystemPrompt()));
            chatReqDTO.getMessages().add(new AiLlmMessageDTO("user", buildRewriteUserPrompt(reqDTO)));
            AiLlmChatRspDTO chatRspDTO = aiLlmService.chat(chatReqDTO);
            return parseLlmRewrite(reqDTO.getOriginalQuery(), chatRspDTO.getContent());
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * 构造系统提示词，约束模型只做指代消解和上下文补全。
     */
    private String buildRewriteSystemPrompt() {
        return "你是企业AI中台的问题改写器，只做指代消解和上下文补全。"
                + "必须返回JSON，不要返回解释。JSON字段：rewrittenQuery字符串，rewritten布尔，reason字符串。"
                + "不能编造业务事实；无法确定时 rewritten=false，rewrittenQuery返回原问题。";
    }

    /**
     * 构造用户提示词，把原问题和最近对话交给模型。
     */
    private String buildRewriteUserPrompt(AiQueryRewriteReqDTO reqDTO) {
        StringBuilder builder = new StringBuilder();
        builder.append("原问题：").append(reqDTO.getOriginalQuery()).append('\n');
        builder.append("最近对话：\n");
        if (reqDTO.getShortMemory() != null) {
            for (AiMemoryMessageDTO message : reqDTO.getShortMemory()) {
                if (message != null && hasText(message.getContent())) {
                    builder.append(message.getRole()).append(": ").append(limit(message.getContent(), 200)).append('\n');
                }
            }
        }
        return builder.toString();
    }

    /**
     * 解析模型返回的 JSON。
     *
     * <p>模型有时会在 JSON 前后带说明文字，所以先截取第一个 { 到最后一个 }。
     * 如果解析失败，返回 null 让外层走规则兜底。</p>
     */
    private AiQueryRewriteRspDTO parseLlmRewrite(String originalQuery, String content) {
        if (!hasText(content)) {
            return null;
        }
        int start = content.indexOf('{');
        int end = content.lastIndexOf('}');
        if (start < 0 || end <= start) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(content.substring(start, end + 1));
        AiQueryRewriteRspDTO rspDTO = new AiQueryRewriteRspDTO();
        rspDTO.setOriginalQuery(originalQuery);
        rspDTO.setRewrittenQuery(jsonObject.getString("rewrittenQuery"));
        rspDTO.setRewritten(jsonObject.getBoolean("rewritten"));
        rspDTO.setReason(jsonObject.getString("reason"));
        if (!hasText(rspDTO.getRewrittenQuery())) {
            rspDTO.setRewrittenQuery(originalQuery);
        }
        if (rspDTO.getRewritten() == null) {
            rspDTO.setRewritten(Boolean.FALSE);
        }
        return rspDTO;
    }

    /**
     * 限制上下文长度，避免把过长历史消息塞进提示词或兜底改写结果。
     */
    private String limit(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    /**
     * 判断字符串是否有非空白内容。
     */
    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
