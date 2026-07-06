package com.bio.drqi.ai.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.dto.req.AiAnalysisReqDTO;
import com.bio.drqi.ai.dto.rsp.AiAnalysisRspDTO;
import com.bio.drqi.ai.dto.rsp.AiTableColumnDTO;
import com.bio.drqi.ai.dto.rsp.AiTableDTO;
import com.bio.drqi.ai.entity.AiApiParam;
import com.bio.drqi.ai.entity.AiApiRegistry;
import com.bio.drqi.ai.mapper.AiApiParamMapper;
import com.bio.drqi.ai.mapper.AiApiRegistryMapper;
import com.bio.drqi.ai.service.AiAgentAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class AiAgentAnalysisServiceImpl implements AiAgentAnalysisService {

    private static final List<String> PASS_HEADERS = Arrays.asList(
            "Authorization", "x-traceId", "userId", "username", "nickname", "jobNum",
            "userInfo", "user_info", "user", "permissions", "permission", "roles", "roleIds", "dataPermissionConfigList"
    );
    private static final Set<String> AUTH_REQUIRED_PATHS = new LinkedHashSet<>(Arrays.asList(
            "/plasmid/listByVectorTask",
            "/sampleOneResult/listPage",
            "/sampleTwoResult/listPage"
    ));
    private static final Pattern IMPLEMENTATION_CODE_PATTERN = Pattern.compile("(?i)([a-z]{1,12}\\d+[a-z0-9_-]*)");

    @Resource
    private RestTemplate aiCommandRestTemplate;

    @Resource
    private AiApiRegistryMapper aiApiRegistryMapper;

    @Resource
    private AiApiParamMapper aiApiParamMapper;

    @Override
    public boolean support(AiAnalysisReqDTO reqDTO) {
        AgentIntent intent = detectIntent(reqDTO == null ? null : reqDTO.getQuestion());
        return intent != AgentIntent.NONE;
    }

    @Override
    public AiAnalysisRspDTO analysis(AiAnalysisReqDTO reqDTO) {
        String question = reqDTO == null ? null : reqDTO.getQuestion();
        AgentIntent intent = detectIntent(question);
        if (intent == AgentIntent.NONE) {
            throw new BusinessException("未命中受控业务Agent能力");
        }
        String implementationCode = extractImplementationCode(question);
        if (StrUtil.isBlank(implementationCode)) {
            AiAnalysisRspDTO rspDTO = new AiAnalysisRspDTO();
            rspDTO.setAction("clarify");
            rspDTO.setClarifyType("missing_implementation_code");
            rspDTO.setNextQuestion("请补充实施方案编号，例如 XS1-01。");
            rspDTO.setAnswer("请补充实施方案编号，例如 XS1-01。");
            return rspDTO;
        }

        AgentContext context = new AgentContext(implementationCode);
        List<AgentStepResult> stepResults = new ArrayList<>();
        stepResults.add(executeStep("implementation", "实施方案", "/implementationPlan/detailByCode", context));
        stepResults.add(executeStep("steps", "步骤定义", "/implementationPlan/stepListByCode", context));

        if (intent == AgentIntent.PROGRESS) {
            stepResults.add(executeStep("vectorBuild", "载体构建", "/vectorBuild/detail", context));
            stepResults.add(executeStep("plasmid", "质粒质检", "/plasmid/listByVectorTask", context));
            stepResults.add(executeStep("transform", "转化", "/transform/listByVectorTask", context));
            stepResults.add(executeStep("plant", "种植", "/plantSingleStock/listByVectorTaskIdDetail", context));
            stepResults.add(executeStep("sample", "取样", "/sampleApply/listByVectorTask", context));
            stepResults.add(executeStep("sampleOne", "一代测序", "/sampleOneResult/listPage", context));
            stepResults.add(executeStep("sampleTwo", "二代测序", "/sampleTwoResult/listPage", context));
            stepResults.add(executeStep("seed", "种子", "/seedStock/queryList", context));
        } else {
            if (containsAny(question, "转化", "移苗")) {
                stepResults.add(executeStep("transform", "转化", "/transform/listByVectorTask", context));
                stepResults.add(executeStep("conversion", "转化移苗", "/conversionAndTrans/listByVectorTask", context));
            }
            if (containsAny(question, "种植", "苗库", "植株")) {
                stepResults.add(executeStep("plant", "种植", "/plantSingleStock/listByVectorTaskIdDetail", context));
            }
            if (containsAny(question, "取样", "测序", "检测")) {
                stepResults.add(executeStep("sample", "取样", "/sampleApply/listByVectorTask", context));
                stepResults.add(executeStep("sampleOne", "一代测序", "/sampleOneResult/listPage", context));
                stepResults.add(executeStep("sampleTwo", "二代测序", "/sampleTwoResult/listPage", context));
            }
            if (containsAny(question, "种子", "种子库")) {
                stepResults.add(executeStep("seed", "种子", "/seedStock/queryList", context));
            }
        }

        AiAnalysisRspDTO rspDTO = new AiAnalysisRspDTO();
        rspDTO.setAnswer(buildAnswer(intent, context, stepResults));
        rspDTO.getTables().add(buildProgressTable(stepResults));
        if (intent == AgentIntent.FULL_DETAIL) {
            appendDetailTables(rspDTO, stepResults);
        }
        log.info("AI受控业务Agent执行完成，intent={}，implementationCode={}，steps={}",
                intent, implementationCode, stepResults.size());
        return rspDTO;
    }

    private AgentIntent detectIntent(String question) {
        if (StrUtil.isBlank(question)) {
            return AgentIntent.NONE;
        }
        if (!containsAny(question, "实施方案")) {
            return AgentIntent.NONE;
        }
        if (containsAny(question, "执行到哪", "执行到哪一步", "进度", "步骤", "每一步", "执行情况", "分析")) {
            return AgentIntent.PROGRESS;
        }
        if (containsAny(question, "明细", "详情", "记录", "全链路")
                && containsAny(question, "转化", "取样", "种植", "种子", "测序")) {
            return AgentIntent.FULL_DETAIL;
        }
        return AgentIntent.NONE;
    }

    private String extractImplementationCode(String question) {
        if (StrUtil.isBlank(question)) {
            return null;
        }
        Matcher matcher = IMPLEMENTATION_CODE_PATTERN.matcher(question);
        while (matcher.find()) {
            String value = matcher.group(1);
            if (!"ai".equalsIgnoreCase(value)) {
                return value.toUpperCase(Locale.ROOT);
            }
        }
        return null;
    }

    private AgentStepResult executeStep(String code, String name, String path, AgentContext context) {
        AgentStepResult stepResult = new AgentStepResult(code, name, path);
        AiApiRegistry api = findApi(path);
        if (api == null) {
            stepResult.status = "未配置";
            stepResult.summary = "未找到已启用的只读接口";
            return stepResult;
        }
        Map<String, Object> params = buildParams(api.getApiCode(), context);
        if (params.isEmpty()) {
            stepResult.status = "跳过";
            stepResult.summary = "缺少可用入参";
            return stepResult;
        }
        if (AUTH_REQUIRED_PATHS.contains(path) && StrUtil.isBlank(currentAuthorization())) {
            stepResult.status = "需授权";
            stepResult.summary = "该步骤接口需要登录token，当前AI请求缺少Authorization";
            stepResult.params = params;
            return stepResult;
        }
        try {
            Object result = executeRemote(api, params);
            stepResult.params = params;
            stepResult.rawResult = result;
            if (!remoteSuccess(result)) {
                stepResult.status = "异常";
                stepResult.summary = remoteMessage(result);
                return stepResult;
            }
            stepResult.resultCount = countResult(result);
            stepResult.status = stepResult.resultCount > 0 ? "已执行" : "未开始";
            stepResult.summary = stepResult.resultCount > 0
                    ? "查到" + stepResult.resultCount + "条结果"
                    : "未查到结果";
            context.capture(result);
        } catch (Exception e) {
            log.warn("AI受控业务Agent步骤执行失败，step={}，path={}，params={}", code, path, JSONUtil.toJsonStr(params), e);
            stepResult.status = "异常";
            stepResult.summary = StrUtil.blankToDefault(e.getMessage(), "接口调用失败");
        }
        return stepResult;
    }

    private AiApiRegistry findApi(String path) {
        return aiApiRegistryMapper.selectOne(new LambdaQueryWrapper<AiApiRegistry>()
                .eq(AiApiRegistry::getDeleted, 0)
                .eq(AiApiRegistry::getAiEnabled, 1)
                .eq(AiApiRegistry::getReadOnly, 1)
                .eq(AiApiRegistry::getPath, path)
                .last("limit 1"));
    }

    private Map<String, Object> buildParams(String apiCode, AgentContext context) {
        List<AiApiParam> paramList = aiApiParamMapper.selectList(new LambdaQueryWrapper<AiApiParam>()
                .eq(AiApiParam::getDeleted, 0)
                .eq(AiApiParam::getAiEnabled, 1)
                .eq(AiApiParam::getApiCode, apiCode)
                .orderByAsc(AiApiParam::getId));
        Map<String, Object> params = new LinkedHashMap<>();
        for (AiApiParam param : paramList) {
            String name = StrUtil.blankToDefault(param.getJavaField(), param.getParamName());
            Object value = context.valueFor(name);
            if (value != null && StrUtil.isNotBlank(String.valueOf(value))) {
                params.put(name, value);
            } else if (param.getDefaultValue() != null) {
                params.put(name, param.getDefaultValue());
            } else if (param.getRequired() != null && param.getRequired() == 1) {
                return new LinkedHashMap<>();
            }
        }
        if (params.isEmpty()) {
            params.put("vectorTaskCode", context.implementationCode);
        }
        return params;
    }

    private Object executeRemote(AiApiRegistry api, Map<String, Object> params) {
        String url = "http://" + resolveService(api.getServiceName()) + resolvePath(api.getServiceName(), api.getPath());
        HttpHeaders headers = buildHeaders();
        HttpMethod method = HttpMethod.resolve(StrUtil.blankToDefault(api.getMethod(), "POST").toUpperCase());
        if (method == null) {
            throw new BusinessException("不支持的AI Agent HTTP方法：" + api.getMethod());
        }
        ResponseEntity<Object> response;
        if (HttpMethod.GET.equals(method)) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                builder.queryParam(entry.getKey(), entry.getValue());
            }
            response = aiCommandRestTemplate.exchange(builder.toUriString(), method, new HttpEntity<>(headers), Object.class);
        } else {
            response = aiCommandRestTemplate.exchange(url, method, new HttpEntity<>(params, headers), Object.class);
        }
        return response.getBody();
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        HttpServletRequest request = currentRequest();
        if (request == null) {
            return headers;
        }
        for (String header : PASS_HEADERS) {
            String value = request.getHeader(header);
            if (StrUtil.isNotBlank(value)) {
                headers.set(header, value);
            }
        }
        return headers;
    }

    private String currentAuthorization() {
        HttpServletRequest request = currentRequest();
        return request == null ? null : request.getHeader("Authorization");
    }

    private HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes)) {
            return null;
        }
        return ((ServletRequestAttributes) attributes).getRequest();
    }

    private String resolveService(String serviceName) {
        if (StrUtil.isNotBlank(serviceName) && serviceName.startsWith("bio-drqi-")) {
            return "bio-cer-service";
        }
        return serviceName;
    }

    private String resolvePath(String serviceName, String path) {
        String value = StrUtil.blankToDefault(path, "");
        if (StrUtil.isNotBlank(serviceName) && serviceName.startsWith("bio-drqi-") && !value.startsWith("/cer/")) {
            return "/cer" + (value.startsWith("/") ? value : "/" + value);
        }
        return value;
    }

    private String buildAnswer(AgentIntent intent, AgentContext context, List<AgentStepResult> steps) {
        if (intent == AgentIntent.FULL_DETAIL) {
            return "已按实施方案 " + context.implementationCode + " 查询相关明细，结果见下方表格。";
        }
        String current = "未开始";
        int abnormalCount = 0;
        for (AgentStepResult step : steps) {
            if ("异常".equals(step.status)) {
                abnormalCount++;
            }
            if ("已执行".equals(step.status) && isBusinessProgressStep(step)) {
                current = step.name;
            }
        }
        String suffix = abnormalCount > 0 ? "其中" + abnormalCount + "个步骤接口返回异常，需结合表格核对。" : "各步骤结果见下方表格。";
        return "实施方案 " + context.implementationCode + " 当前推断执行到：" + current + "。" + suffix;
    }

    private boolean isBusinessProgressStep(AgentStepResult step) {
        return step != null
                && !"implementation".equals(step.code)
                && !"steps".equals(step.code);
    }

    private AiTableDTO buildProgressTable(List<AgentStepResult> steps) {
        AiTableDTO table = new AiTableDTO();
        table.setTitle("AI Agent执行步骤");
        table.getColumns().add(column("步骤", "name"));
        table.getColumns().add(column("状态", "status"));
        table.getColumns().add(column("结果数", "resultCount"));
        table.getColumns().add(column("说明", "summary"));
        table.getColumns().add(column("接口", "path"));
        for (AgentStepResult step : steps) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("name", step.name);
            row.put("status", step.status);
            row.put("resultCount", step.resultCount);
            row.put("summary", step.summary);
            row.put("path", step.path);
            table.getData().add(row);
        }
        return table;
    }

    private void appendDetailTables(AiAnalysisRspDTO rspDTO, List<AgentStepResult> steps) {
        for (AgentStepResult step : steps) {
            List<?> list = extractDataList(step.rawResult);
            if (CollectionUtil.isEmpty(list)) {
                continue;
            }
            AiTableDTO table = new AiTableDTO();
            table.setTitle(step.name + "明细");
            table.getColumns().add(column("结果", "value"));
            int limit = Math.min(list.size(), 50);
            for (int i = 0; i < limit; i++) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("value", list.get(i));
                table.getData().add(row);
            }
            rspDTO.getTables().add(table);
        }
    }

    private AiTableColumnDTO column(String title, String dataIndex) {
        AiTableColumnDTO column = new AiTableColumnDTO();
        column.setTitle(title);
        column.setDataIndex(dataIndex);
        return column;
    }

    private int countResult(Object result) {
        List<?> list = extractDataList(result);
        if (list != null) {
            return list.size();
        }
        Object data = unwrapData(result);
        if (data instanceof Map && !((Map<?, ?>) data).isEmpty()) {
            return 1;
        }
        return data == null ? 0 : 1;
    }

    private boolean remoteSuccess(Object result) {
        if (!(result instanceof Map)) {
            return true;
        }
        Map<?, ?> map = (Map<?, ?>) result;
        Object success = map.get("success");
        if (success instanceof Boolean && !((Boolean) success)) {
            return false;
        }
        Object error = map.get("error");
        if (error instanceof Boolean && ((Boolean) error)) {
            return false;
        }
        Object code = map.get("code");
        if (code == null) {
            return true;
        }
        String codeText = String.valueOf(code);
        return "0".equals(codeText) || "0000".equals(codeText) || "200".equals(codeText);
    }

    private String remoteMessage(Object result) {
        if (result instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) result;
            Object message = firstValue(map, "message", "msg", "errorMessage");
            if (message != null && StrUtil.isNotBlank(String.valueOf(message))) {
                return String.valueOf(message);
            }
        }
        return "业务接口返回失败";
    }

    private List<?> extractDataList(Object result) {
        Object data = unwrapData(result);
        if (data instanceof List) {
            return (List<?>) data;
        }
        if (data instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) data;
            Object records = firstValue(map, "records", "list", "rows", "data");
            if (records instanceof List) {
                return (List<?>) records;
            }
        }
        return null;
    }

    private Object unwrapData(Object result) {
        if (result instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) result;
            Object data = firstValue(map, "data", "result");
            if (data != null) {
                return data;
            }
        }
        return result;
    }

    private Object firstValue(Map<?, ?> map, String... keys) {
        for (String key : keys) {
            if (map.containsKey(key)) {
                return map.get(key);
            }
        }
        return null;
    }

    private boolean containsAny(String text, String... keywords) {
        if (StrUtil.isBlank(text)) {
            return false;
        }
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private enum AgentIntent {
        NONE,
        PROGRESS,
        FULL_DETAIL
    }

    private static class AgentStepResult {
        private final String code;
        private final String name;
        private final String path;
        private String status = "未开始";
        private String summary = "";
        private int resultCount;
        private Map<String, Object> params = new LinkedHashMap<>();
        private Object rawResult;

        private AgentStepResult(String code, String name, String path) {
            this.code = code;
            this.name = name;
            this.path = path;
        }
    }

    private static class AgentContext {
        private final String implementationCode;
        private final Map<String, Object> values = new LinkedHashMap<>();

        private AgentContext(String implementationCode) {
            this.implementationCode = implementationCode;
            values.put("implementationPlanCode", implementationCode);
            values.put("vectorTaskCode", implementationCode);
            values.put("code", implementationCode);
        }

        private Object valueFor(String name) {
            if (StrUtil.isBlank(name)) {
                return null;
            }
            if (values.containsKey(name)) {
                return values.get(name);
            }
            String lower = name.toLowerCase(Locale.ROOT);
            if (lower.contains("vectortaskcode") || lower.contains("implementationplancode")) {
                return implementationCode;
            }
            if ("vectortaskid".equals(lower) && values.containsKey("vectorTaskId")) {
                return values.get("vectorTaskId");
            }
            return null;
        }

        private void capture(Object result) {
            captureObject(unwrapResult(result));
        }

        @SuppressWarnings("unchecked")
        private Object unwrapResult(Object result) {
            if (result instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) result;
                Object data = map.get("data");
                if (data != null) {
                    return unwrapResult(data);
                }
                Object records = map.get("records");
                if (records != null) {
                    return records;
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        private void captureObject(Object value) {
            if (value instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) value;
                captureValue(map, "id", "vectorTaskId");
                captureValue(map, "vectorTaskId", "vectorTaskId");
                captureValue(map, "vectorTaskCode", "vectorTaskCode");
                captureValue(map, "code", "vectorTaskCode");
                captureValue(map, "taskNum", "taskNum");
                captureValue(map, "seedNum", "seedNum");
            } else if (value instanceof List && !((List<?>) value).isEmpty()) {
                captureObject(((List<?>) value).get(0));
            }
        }

        private void captureValue(Map<String, Object> map, String sourceKey, String targetKey) {
            Object value = map.get(sourceKey);
            if (value != null && StrUtil.isNotBlank(String.valueOf(value))) {
                values.put(targetKey, value);
            }
        }
    }
}
