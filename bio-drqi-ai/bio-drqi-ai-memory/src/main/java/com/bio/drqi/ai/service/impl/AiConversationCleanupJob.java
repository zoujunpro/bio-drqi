package com.bio.drqi.ai.service.impl;

import com.bio.drqi.ai.config.AiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class AiConversationCleanupJob {

    @Resource
    private ObjectProvider<JdbcTemplate> jdbcTemplateProvider;

    @Resource
    private AiProperties aiProperties;

    /**
     * 每小时清理一次过期短期记忆。
     */
    @Scheduled(cron = "${bio.ai.memory.cleanup-cron:0 0 * * * ?}")
    public void cleanupExpiredConversation() {
        if (aiProperties.getMemory() == null || !Boolean.TRUE.equals(aiProperties.getMemory().getCleanupEnabled())) {
            return;
        }
        JdbcTemplate jdbcTemplate = jdbcTemplateProvider.getIfAvailable();
        if (jdbcTemplate == null) {
            return;
        }
        try {
            int messageRows = jdbcTemplate.update("delete m from ai_message m left join ai_conversation c on m.conversation_id = c.conversation_id where c.expire_time is not null and c.expire_time < now()");
            int contextRows = jdbcTemplate.update("delete from ai_query_context where expire_time is not null and expire_time < now()");
            int conversationRows = jdbcTemplate.update("delete from ai_conversation where expire_time is not null and expire_time < now()");
            if (messageRows > 0 || contextRows > 0 || conversationRows > 0) {
                log.info("AI过期会话清理完成，messageRows={}，contextRows={}，conversationRows={}",
                        messageRows, contextRows, conversationRows);
            }
        } catch (Exception e) {
            log.warn("AI过期会话清理失败", e);
        }
    }
}
