package com.bio.drqi.ai.provider.qwen;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.api.embedding.AiEmbeddingService;
import com.bio.drqi.ai.api.embedding.dto.AiEmbeddingReqDTO;
import com.bio.drqi.ai.api.embedding.dto.AiEmbeddingRspDTO;
import com.bio.drqi.ai.provider.config.AiEmbeddingProperties;
import com.bio.drqi.ai.provider.config.AiLlmProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 阿里千问 OpenAI 兼容向量接口实现。
 */
@Service
public class QwenAiEmbeddingServiceImpl implements AiEmbeddingService {

    private static final String EMBEDDINGS_PATH = "/embeddings";

    @Resource
    private AiEmbeddingProperties aiEmbeddingProperties;

    @Resource
    private AiLlmProperties aiLlmProperties;

    @Override
    public AiEmbeddingRspDTO embed(AiEmbeddingReqDTO reqDTO) {
        validate(reqDTO);

        JSONObject body = new JSONObject();
        body.put("model", firstText(reqDTO.getModel(), aiEmbeddingProperties.getModel()));
        body.put("input", reqDTO.getInput());
        if (aiEmbeddingProperties.getDim() != null) {
            body.put("dimensions", aiEmbeddingProperties.getDim());
        }

        String url = buildEmbeddingsUrl(firstText(aiEmbeddingProperties.getBaseUrl(), aiLlmProperties.getBaseUrl()));
        int timeout = reqDTO.getTimeoutMs() == null ? aiEmbeddingProperties.getTimeout() : reqDTO.getTimeoutMs();
        HttpResponse response = HttpRequest.post(url)
                .header(Header.AUTHORIZATION, "Bearer " + firstText(aiEmbeddingProperties.getApiKey(), aiLlmProperties.getApiKey()))
                .contentType(ContentType.JSON.getValue())
                .timeout(timeout)
                .body(body.toJSONString())
                .execute();

        String responseBody = response.body();
        if (!response.isOk()) {
            throw new BusinessException("向量模型调用失败，HTTP状态：" + response.getStatus() + "，响应：" + responseBody);
        }
        return parseResponse(responseBody);
    }

    private AiEmbeddingRspDTO parseResponse(String responseBody) {
        JSONObject jsonObject = JSONObject.parseObject(responseBody);
        JSONArray data = jsonObject.getJSONArray("data");
        if (data == null || data.isEmpty()) {
            throw new BusinessException("向量模型响应缺少 data：" + responseBody);
        }
        JSONObject first = data.getJSONObject(0);
        JSONArray array = first.getJSONArray("embedding");
        if (array == null || array.isEmpty()) {
            throw new BusinessException("向量模型响应缺少 embedding：" + responseBody);
        }

        List<Double> embedding = new ArrayList<Double>();
        for (int i = 0; i < array.size(); i++) {
            embedding.add(array.getDouble(i));
        }

        AiEmbeddingRspDTO rspDTO = new AiEmbeddingRspDTO();
        rspDTO.setModel(jsonObject.getString("model"));
        rspDTO.setDim(embedding.size());
        rspDTO.setEmbedding(embedding);
        rspDTO.setRawResponse(responseBody);
        return rspDTO;
    }

    private void validate(AiEmbeddingReqDTO reqDTO) {
        if (reqDTO == null || !hasText(reqDTO.getInput())) {
            throw new BusinessException("向量化文本不能为空");
        }
        if (!hasText(firstText(aiEmbeddingProperties.getBaseUrl(), aiLlmProperties.getBaseUrl()))) {
            throw new BusinessException("向量模型 base-url 未配置");
        }
        if (!hasText(firstText(aiEmbeddingProperties.getApiKey(), aiLlmProperties.getApiKey()))) {
            throw new BusinessException("向量模型 api-key 未配置");
        }
    }

    private String buildEmbeddingsUrl(String baseUrl) {
        String trimmed = baseUrl.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        if (trimmed.endsWith(EMBEDDINGS_PATH)) {
            return trimmed;
        }
        return trimmed + EMBEDDINGS_PATH;
    }

    private String firstText(String first, String second) {
        return hasText(first) ? first : second;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
