package com.bio.drqi.es.config;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class CanalSyncEnabledCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        boolean esEnabled = context.getEnvironment().getProperty("bio.es.enabled", Boolean.class, false);
        boolean canalEnabled = context.getEnvironment().getProperty("bio.es.canal.enabled", Boolean.class, false);
        if (esEnabled && canalEnabled) {
            return ConditionOutcome.match("bio.es.enabled and bio.es.canal.enabled are true");
        }
        return ConditionOutcome.noMatch("bio.es.enabled or bio.es.canal.enabled is false");
    }
}
