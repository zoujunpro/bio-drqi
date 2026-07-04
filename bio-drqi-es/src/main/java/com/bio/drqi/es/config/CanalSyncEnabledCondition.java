package com.bio.drqi.es.config;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class CanalSyncEnabledCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String forceDisabled = System.getProperty("bio.es.canal.force-disabled",
                System.getenv("BIO_ES_CANAL_FORCE_DISABLED"));
        if (Boolean.parseBoolean(forceDisabled)) {
            return ConditionOutcome.noMatch("bio.es.canal.force-disabled is true");
        }

        boolean esEnabled = context.getEnvironment().getProperty("bio.es.enabled", Boolean.class, false);
        boolean canalEnabled = context.getEnvironment().getProperty("bio.es.canal.enabled", Boolean.class, false);
        if (esEnabled && canalEnabled) {
            return ConditionOutcome.match("bio.es.enabled and bio.es.canal.enabled are true");
        }
        return ConditionOutcome.noMatch("bio.es.enabled or bio.es.canal.enabled is false");
    }
}
