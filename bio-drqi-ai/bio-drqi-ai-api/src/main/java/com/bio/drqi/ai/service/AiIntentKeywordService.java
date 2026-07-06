package com.bio.drqi.ai.service;

import com.bio.drqi.ai.entity.AiIntentKeyword;

import java.util.List;

public interface AiIntentKeywordService {

    List<String> listKeywords(String intent);

    List<AiIntentKeyword> listKeywordRules();
}
