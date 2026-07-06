package com.bio.drqi.ai.service;

import com.bio.drqi.ai.config.AiProperties;

import java.util.List;

public interface AiTermService {

    List<AiProperties.Term> recall(String question);
}
