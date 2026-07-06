package com.bio.drqi.ai.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.client.LlmClient;
import com.bio.drqi.ai.config.AiProperties;
import com.bio.drqi.ai.dto.admin.AiPageReqDTO;
import com.bio.drqi.ai.dto.llm.LlmCallOptionsDTO;
import com.bio.drqi.ai.dto.llm.LlmChatMessageDTO;
import com.bio.drqi.ai.dto.rsp.AiAnalysisRspDTO;
import com.bio.drqi.ai.dto.rsp.AiTableColumnDTO;
import com.bio.drqi.ai.dto.rsp.AiTableDTO;
import com.bio.drqi.ai.dto.workflow.AiWorkflowExecuteReqDTO;
import com.bio.drqi.ai.dto.workflow.AiWorkflowExecuteRspDTO;
import com.bio.drqi.ai.dto.workflow.AiWorkflowSaveReqDTO;
import com.bio.drqi.ai.dto.workflow.AiWorkflowStepDTO;
import com.bio.drqi.ai.entity.AiApiParam;
import com.bio.drqi.ai.entity.AiApiRegistry;
import com.bio.drqi.ai.entity.AiWorkflowDefinition;
import com.bio.drqi.ai.entity.AiWorkflowExecution;
import com.bio.drqi.ai.entity.AiWorkflowStepLog;
import com.bio.drqi.ai.mapper.AiApiParamMapper;
import com.bio.drqi.ai.mapper.AiApiRegistryMapper;
import com.bio.drqi.ai.mapper.AiWorkflowDefinitionMapper;
import com.bio.drqi.ai.mapper.AiWorkflowExecutionMapper;
import com.bio.drqi.ai.mapper.AiWorkflowStepLogMapper;
import com.bio.drqi.ai.service.AiWorkflowService;
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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Slf4j
public class AiWorkflowServiceImpl implements AiWorkflowService {

    private static final List<String> PASS_HEADERS = Arrays.asList(
            "Authorization", "x-traceId", "userId", "username", "nickname", "jobNum"
    );

    @Resource
    private AiWorkflowDefinitionMapper aiWorkflowDefinitionMapper;

    @Resource
    private AiWorkflowExecutionMapper aiWorkflowExecutionMapper;

    @Resource
    private AiWorkflowStepLogMapper aiWorkflowStepLogMapper;

    @Resource
    private AiApiRegistryMapper aiApiRegistryMapper;

    @Resource
    private AiApiParamMapper aiApiParamMapper;

    @Resource
    private RestTemplate aiCommandRestTemplate;

    @Resource
    private LlmClient llmClient;

    @Resource
    private AiProperties aiProperties;

    @Override
    public Page<AiWorkflowDefinition> page(AiPageReqDTO reqDTO) {
        LambdaQueryWrapper<AiWorkflowDefinition> wrapper = new LambdaQueryWrapper<AiWorkflowDefinition>()
                .eq(AiWorkflowDefinition::getDeleted, 0)
                .and(StrUtil.isNotBlank(reqDTO.getKeyword()), item -> item
                        .like(AiWorkflowDefinition::getWorkflowCode, reqDTO.getKeyword())
                        .or()
                        .like(AiWorkflowDefinition::getWorkflowName, reqDTO.getKeyword())
                        .or()
                        .like(AiWorkflowDefinition::getDescription, reqDTO.getKeyword()))
                .orderByDesc(AiWorkflowDefinition::getUpdateTime)
                .orderByDesc(AiWorkflowDefinition::getId);
        return aiWorkflowDefinitionMapper.selectPage(pageOf(reqDTO), wrapper);
    }

    @Override
    public AiWorkflowDefinition detail(Long id) {
        if (id == null) {
            throw new BusinessException("Workflow ID不能为空");
        }
        AiWorkflowDefinition entity = aiWorkflowDefinitionMapper.selectById(id);
        if (entity == null || (entity.getDeleted() != null && entity.getDeleted() == 1)) {
            throw new BusinessException("Workflow不存在");
        }
        return entity;
    }

    @Override
    public void save(AiWorkflowSaveReqDTO reqDTO) {
        if (reqDTO == null) {
            throw new BusinessException("Workflow不能为空");
        }
        if (StrUtil.isBlank(reqDTO.getWorkflowCode())) {
            throw new BusinessException("Workflow编码不能为空");
        }
        if (StrUtil.isBlank(reqDTO.getWorkflowName())) {
            throw new BusinessException("Workflow名称不能为空");
        }
        validateDsl(reqDTO.getDslJson());
        Date now = new Date();
        AiWorkflowDefinition entity = reqDTO.getId() == null ? new AiWorkflowDefinition() : detail(reqDTO.getId());
        if (reqDTO.getId() == null) {
            AiWorkflowDefinition existed = aiWorkflowDefinitionMapper.selectOne(new LambdaQueryWrapper<AiWorkflowDefinition>()
                    .eq(AiWorkflowDefinition::getWorkflowCode, reqDTO.getWorkflowCode().trim())
                    .last("limit 1"));
            if (existed != null && (existed.getDeleted() == null || existed.getDeleted() == 0)) {
                throw new BusinessException("Workflow编码已存在");
            }
            entity.setCreateTime(now);
            entity.setDeleted(0);
        }
        entity.setWorkflowCode(reqDTO.getWorkflowCode().trim());
        entity.setWorkflowName(reqDTO.getWorkflowName().trim());
        entity.setDescription(StrUtil.trimToNull(reqDTO.getDescription()));
        entity.setCategory(StrUtil.trimToNull(reqDTO.getCategory()));
        entity.setDslJson(reqDTO.getDslJson());
        entity.setEnabled(reqDTO.getEnabled() == null ? 1 : reqDTO.getEnabled());
        entity.setUpdateTime(now);
        if (entity.getId() == null) {
            aiWorkflowDefinitionMapper.insert(entity);
        } else {
            aiWorkflowDefinitionMapper.updateById(entity);
        }
    }

    @Override
    public void delete(Long id) {
        AiWorkflowDefinition entity = detail(id);
        entity.setDeleted(1);
        entity.setEnabled(0);
        entity.setUpdateTime(new Date());
        aiWorkflowDefinitionMapper.updateById(entity);
    }

    @Override
    public AiWorkflowExecuteRspDTO execute(AiWorkflowExecuteReqDTO reqDTO) {
        AiWorkflowDefinition workflow = loadWorkflow(reqDTO);
        if (workflow.getEnabled() == null || workflow.getEnabled() != 1) {
            throw new BusinessException("Workflow未启用");
        }
        JSONObject dsl = JSONUtil.parseObj(workflow.getDslJson());
        WorkflowGraph graph = WorkflowGraph.of(dsl);
        Map<String, Object> input = reqDTO.getInput() == null
                ? new LinkedHashMap<String, Object>()
                : new LinkedHashMap<String, Object>(reqDTO.getInput());
        Map<String, Object> context = new LinkedHashMap<String, Object>();
        context.put("input", input);

        long start = System.currentTimeMillis();
        AiWorkflowExecution execution = createExecution(workflow, input);
        AiWorkflowExecuteRspDTO rspDTO = new AiWorkflowExecuteRspDTO();
        rspDTO.setExecutionId(execution.getId());
        rspDTO.setExecutionNo(execution.getExecutionNo());
        rspDTO.setStatus("RUNNING");

        AiAnalysisRspDTO result = new AiAnalysisRspDTO();
        result.setAnswer("Workflow执行完成");
        String currentNodeId = graph.startNodeId();
        int guard = 0;
        try {
            while (StrUtil.isNotBlank(currentNodeId) && guard++ < graph.nodes.size() + 5) {
                WorkflowNode node = graph.nodes.get(currentNodeId);
                if (node == null) {
                    throw new BusinessException("Workflow节点不存在：" + currentNodeId);
                }
                StepResult step = executeNode(execution.getId(), node, input, context);
                rspDTO.getSteps().add(step.stepDTO);
                if (step.output != null) {
                    context.put(node.id, step.output);
                }
                if ("END".equalsIgnoreCase(node.type)) {
                    break;
                }
                currentNodeId = graph.nextNodeId(node.id);
            }
            result.getTables().add(buildStepTable(rspDTO.getSteps()));
            rspDTO.setResult(result);
            rspDTO.setStatus("SUCCESS");
            execution.setStatus("SUCCESS");
            execution.setOutputJson(JSONUtil.toJsonStr(result));
        } catch (Exception e) {
            log.warn("AI Workflow执行失败，workflowCode={}，executionNo={}", workflow.getWorkflowCode(), execution.getExecutionNo(), e);
            result.setSuccess(Boolean.FALSE);
            result.setAction("reject");
            result.setAnswer(StrUtil.blankToDefault(e.getMessage(), "Workflow执行失败"));
            rspDTO.setResult(result);
            rspDTO.setStatus("FAILED");
            execution.setStatus("FAILED");
            execution.setErrorMessage(StrUtil.blankToDefault(e.getMessage(), "Workflow执行失败"));
            execution.setOutputJson(JSONUtil.toJsonStr(result));
        } finally {
            int costMs = (int) (System.currentTimeMillis() - start);
            rspDTO.setCostMs(costMs);
            execution.setCostMs(costMs);
            execution.setUpdateTime(new Date());
            aiWorkflowExecutionMapper.updateById(execution);
        }
        return rspDTO;
    }

    private StepResult executeNode(Long executionId, WorkflowNode node, Map<String, Object> input, Map<String, Object> context) {
        long start = System.currentTimeMillis();
        AiWorkflowStepLog logEntity = new AiWorkflowStepLog();
        logEntity.setExecutionId(executionId);
        logEntity.setNodeId(node.id);
        logEntity.setNodeType(node.type);
        logEntity.setNodeName(node.name);
        logEntity.setToolCode(node.toolCode());
        logEntity.setStatus("RUNNING");
        logEntity.setCreateTime(new Date());
        aiWorkflowStepLogMapper.insert(logEntity);

        AiWorkflowStepDTO stepDTO = new AiWorkflowStepDTO();
        stepDTO.setNodeId(node.id);
        stepDTO.setNodeType(node.type);
        stepDTO.setNodeName(node.name);
        stepDTO.setToolCode(node.toolCode());
        stepDTO.setStatus("RUNNING");

        Object output = null;
        try {
            if ("START".equalsIgnoreCase(node.type)) {
                output = input;
                stepDTO.setSummary("接收输入");
            } else if ("TOOL".equalsIgnoreCase(node.type)) {
                output = executeTool(node, input, context);
                stepDTO.setSummary("调用工具，结果数：" + countResult(output));
            } else if ("LLM".equalsIgnoreCase(node.type)) {
                output = executeLlm(node, context);
                stepDTO.setSummary("完成AI总结");
            } else if ("END".equalsIgnoreCase(node.type)) {
                output = context;
                stepDTO.setSummary("流程结束");
            } else {
                throw new BusinessException("暂不支持的Workflow节点类型：" + node.type);
            }
            stepDTO.setStatus("SUCCESS");
            logEntity.setStatus("SUCCESS");
            logEntity.setOutputJson(limitJson(output));
        } catch (Exception e) {
            stepDTO.setStatus("FAILED");
            stepDTO.setSummary(StrUtil.blankToDefault(e.getMessage(), "节点执行失败"));
            logEntity.setStatus("FAILED");
            logEntity.setErrorMessage(StrUtil.blankToDefault(e.getMessage(), "节点执行失败"));
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new BusinessException(StrUtil.blankToDefault(e.getMessage(), "节点执行失败"));
        } finally {
            int costMs = (int) (System.currentTimeMillis() - start);
            stepDTO.setCostMs(costMs);
            logEntity.setCostMs(costMs);
            aiWorkflowStepLogMapper.updateById(logEntity);
        }
        StepResult result = new StepResult();
        result.stepDTO = stepDTO;
        result.output = output;
        return result;
    }

    private Object executeTool(WorkflowNode node, Map<String, Object> input, Map<String, Object> context) {
        String toolCode = node.toolCode();
        if (StrUtil.isBlank(toolCode)) {
            throw new BusinessException("Tool节点未配置工具");
        }
        AiApiRegistry api = aiApiRegistryMapper.selectOne(new LambdaQueryWrapper<AiApiRegistry>()
                .eq(AiApiRegistry::getDeleted, 0)
                .eq(AiApiRegistry::getAiEnabled, 1)
                .eq(AiApiRegistry::getReadOnly, 1)
                .eq(AiApiRegistry::getApiCode, toolCode)
                .last("limit 1"));
        if (api == null) {
            throw new BusinessException("工具未启用或不是只读工具：" + toolCode);
        }
        Map<String, Object> params = buildToolParams(api.getApiCode(), node.params(), input, context);
        Object result = executeRemote(api, params);
        if (!remoteSuccess(result)) {
            throw new BusinessException(remoteMessage(result));
        }
        return result;
    }

    private Object executeLlm(WorkflowNode node, Map<String, Object> context) {
        String prompt = StrUtil.blankToDefault(node.prompt(), "请根据Workflow上下文，输出简明业务总结。");
        String content = llmClient.chat(Arrays.asList(
                new LlmChatMessageDTO("system", "你是企业AI Workflow中的总结节点，只能基于提供的上下文总结，不要编造数据。"),
                new LlmChatMessageDTO("user", prompt + "\n\n上下文：\n" + JSONUtil.toJsonStr(context))
        ), LlmCallOptionsDTO.of("workflow", aiProperties.getLlm().getChatTemperature()));
        Map<String, Object> output = new LinkedHashMap<String, Object>();
        output.put("answer", content);
        return output;
    }

    private Map<String, Object> buildToolParams(String apiCode, JSONObject configuredParams,
                                                Map<String, Object> input, Map<String, Object> context) {
        List<AiApiParam> paramList = aiApiParamMapper.selectList(new LambdaQueryWrapper<AiApiParam>()
                .eq(AiApiParam::getDeleted, 0)
                .eq(AiApiParam::getAiEnabled, 1)
                .eq(AiApiParam::getApiCode, apiCode)
                .orderByAsc(AiApiParam::getId));
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        for (AiApiParam param : paramList) {
            String name = StrUtil.blankToDefault(param.getJavaField(), param.getParamName());
            Object value = configuredParams == null ? null : configuredParams.get(name);
            if (value == null && configuredParams != null) {
                value = configuredParams.get(param.getParamName());
            }
            value = resolveValue(value, input, context);
            if (value == null) {
                value = input.get(name);
            }
            if (value == null) {
                value = input.get(param.getParamName());
            }
            if (value == null && param.getDefaultValue() != null) {
                value = param.getDefaultValue();
            }
            if (value != null && StrUtil.isNotBlank(String.valueOf(value))) {
                params.put(name, value);
            } else if (param.getRequired() != null && param.getRequired() == 1) {
                throw new BusinessException("工具缺少必填参数：" + name);
            }
        }
        if (params.isEmpty() && input != null) {
            params.putAll(input);
        }
        return params;
    }

    private Object executeRemote(AiApiRegistry api, Map<String, Object> params) {
        String url = "http://" + resolveService(api.getServiceName()) + resolvePath(api.getServiceName(), api.getPath());
        HttpHeaders headers = buildHeaders();
        HttpMethod method = HttpMethod.resolve(StrUtil.blankToDefault(api.getMethod(), "POST").toUpperCase(Locale.ROOT));
        if (method == null) {
            throw new BusinessException("不支持的Workflow Tool HTTP方法：" + api.getMethod());
        }
        ResponseEntity<Object> response;
        if (HttpMethod.GET.equals(method)) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                builder.queryParam(entry.getKey(), entry.getValue());
            }
            response = aiCommandRestTemplate.exchange(builder.toUriString(), method, new HttpEntity<Object>(headers), Object.class);
        } else {
            response = aiCommandRestTemplate.exchange(url, method, new HttpEntity<Object>(params, headers), Object.class);
        }
        return response.getBody();
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes)) {
            return headers;
        }
        HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
        for (String header : PASS_HEADERS) {
            String value = request.getHeader(header);
            if (StrUtil.isNotBlank(value)) {
                headers.set(header, value);
            }
        }
        return headers;
    }

    private AiWorkflowDefinition loadWorkflow(AiWorkflowExecuteReqDTO reqDTO) {
        if (reqDTO == null) {
            throw new BusinessException("Workflow执行请求不能为空");
        }
        AiWorkflowDefinition workflow;
        if (reqDTO.getWorkflowId() != null) {
            workflow = detail(reqDTO.getWorkflowId());
        } else if (StrUtil.isNotBlank(reqDTO.getWorkflowCode())) {
            workflow = aiWorkflowDefinitionMapper.selectOne(new LambdaQueryWrapper<AiWorkflowDefinition>()
                    .eq(AiWorkflowDefinition::getWorkflowCode, reqDTO.getWorkflowCode())
                    .eq(AiWorkflowDefinition::getDeleted, 0)
                    .last("limit 1"));
        } else {
            throw new BusinessException("Workflow ID或编码不能为空");
        }
        if (workflow == null) {
            throw new BusinessException("Workflow不存在");
        }
        return workflow;
    }

    private AiWorkflowExecution createExecution(AiWorkflowDefinition workflow, Map<String, Object> input) {
        AiWorkflowExecution execution = new AiWorkflowExecution();
        execution.setExecutionNo("WF" + IdUtil.fastSimpleUUID());
        execution.setWorkflowId(workflow.getId());
        execution.setWorkflowCode(workflow.getWorkflowCode());
        execution.setInputJson(JSONUtil.toJsonStr(input));
        execution.setStatus("RUNNING");
        execution.setCreateTime(new Date());
        execution.setUpdateTime(new Date());
        aiWorkflowExecutionMapper.insert(execution);
        return execution;
    }

    private void validateDsl(String dslJson) {
        if (StrUtil.isBlank(dslJson)) {
            throw new BusinessException("Workflow DSL不能为空");
        }
        JSONObject dsl = JSONUtil.parseObj(dslJson);
        JSONArray nodes = dsl.getJSONArray("nodes");
        if (nodes == null || nodes.isEmpty()) {
            throw new BusinessException("Workflow至少需要一个节点");
        }
    }

    private Page pageOf(AiPageReqDTO reqDTO) {
        long pageNum = reqDTO == null || reqDTO.getPageNum() == null ? 1 : reqDTO.getPageNum();
        long pageSize = reqDTO == null || reqDTO.getPageSize() == null ? 10 : reqDTO.getPageSize();
        return new Page(pageNum, pageSize);
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

    private Object resolveValue(Object value, Map<String, Object> input, Map<String, Object> context) {
        if (!(value instanceof String)) {
            return value;
        }
        String text = (String) value;
        if (!text.startsWith("${") || !text.endsWith("}")) {
            return value;
        }
        String expression = text.substring(2, text.length() - 1);
        if (expression.startsWith("input.")) {
            return input.get(expression.substring("input.".length()));
        }
        if (expression.startsWith("context.")) {
            return resolvePathValue(context, expression.substring("context.".length()));
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    private Object resolvePathValue(Object root, String path) {
        Object current = root;
        for (String part : path.split("\\.")) {
            if (current instanceof Map) {
                current = ((Map<String, Object>) current).get(part);
            } else if (current instanceof JSONObject) {
                current = ((JSONObject) current).get(part);
            } else {
                return null;
            }
        }
        return current;
    }

    private AiTableDTO buildStepTable(List<AiWorkflowStepDTO> steps) {
        AiTableDTO table = new AiTableDTO();
        table.setTitle("Workflow执行轨迹");
        table.getColumns().add(column("节点", "nodeName"));
        table.getColumns().add(column("类型", "nodeType"));
        table.getColumns().add(column("工具", "toolCode"));
        table.getColumns().add(column("状态", "status"));
        table.getColumns().add(column("耗时ms", "costMs"));
        table.getColumns().add(column("说明", "summary"));
        for (AiWorkflowStepDTO step : steps) {
            Map<String, Object> row = new LinkedHashMap<String, Object>();
            row.put("nodeName", step.getNodeName());
            row.put("nodeType", step.getNodeType());
            row.put("toolCode", step.getToolCode());
            row.put("status", step.getStatus());
            row.put("costMs", step.getCostMs());
            row.put("summary", step.getSummary());
            table.getData().add(row);
        }
        return table;
    }

    private AiTableColumnDTO column(String title, String dataIndex) {
        AiTableColumnDTO column = new AiTableColumnDTO();
        column.setTitle(title);
        column.setDataIndex(dataIndex);
        return column;
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

    private int countResult(Object result) {
        Object data = result;
        if (result instanceof Map) {
            data = firstValue((Map<?, ?>) result, "data", "result");
        }
        if (data instanceof Map) {
            Object list = firstValue((Map<?, ?>) data, "records", "list", "rows", "data");
            if (list instanceof List) {
                return ((List<?>) list).size();
            }
            return ((Map<?, ?>) data).isEmpty() ? 0 : 1;
        }
        if (data instanceof List) {
            return ((List<?>) data).size();
        }
        return data == null ? 0 : 1;
    }

    private Object firstValue(Map<?, ?> map, String... keys) {
        for (String key : keys) {
            if (map.containsKey(key)) {
                return map.get(key);
            }
        }
        return null;
    }

    private String limitJson(Object value) {
        String json = JSONUtil.toJsonStr(value);
        return json.length() > 12000 ? json.substring(0, 12000) : json;
    }

    private static class StepResult {
        private AiWorkflowStepDTO stepDTO;
        private Object output;
    }

    private static class WorkflowGraph {
        private final Map<String, WorkflowNode> nodes = new LinkedHashMap<String, WorkflowNode>();
        private final List<WorkflowEdge> edges = new ArrayList<WorkflowEdge>();

        private static WorkflowGraph of(JSONObject dsl) {
            WorkflowGraph graph = new WorkflowGraph();
            JSONArray nodes = dsl.getJSONArray("nodes");
            for (int i = 0; i < nodes.size(); i++) {
                WorkflowNode node = WorkflowNode.of(nodes.getJSONObject(i));
                graph.nodes.put(node.id, node);
            }
            JSONArray edges = dsl.getJSONArray("edges");
            if (edges != null) {
                for (int i = 0; i < edges.size(); i++) {
                    graph.edges.add(WorkflowEdge.of(edges.getJSONObject(i)));
                }
            }
            return graph;
        }

        private String startNodeId() {
            for (WorkflowNode node : nodes.values()) {
                if ("START".equalsIgnoreCase(node.type)) {
                    return node.id;
                }
            }
            return nodes.isEmpty() ? null : nodes.keySet().iterator().next();
        }

        private String nextNodeId(String sourceNodeId) {
            for (WorkflowEdge edge : edges) {
                if (sourceNodeId.equals(edge.sourceNodeId)) {
                    return edge.targetNodeId;
                }
            }
            return null;
        }
    }

    private static class WorkflowNode {
        private String id;
        private String type;
        private String name;
        private JSONObject properties;

        private static WorkflowNode of(JSONObject json) {
            WorkflowNode node = new WorkflowNode();
            node.id = json.getStr("id");
            node.type = StrUtil.blankToDefault(json.getStr("type"), "TOOL").toUpperCase(Locale.ROOT);
            node.name = StrUtil.blankToDefault(json.getStr("name"), json.getStr("text"));
            node.properties = json.getJSONObject("properties");
            if (node.properties == null) {
                node.properties = new JSONObject();
            }
            if (StrUtil.isBlank(node.name)) {
                node.name = node.type;
            }
            return node;
        }

        private String toolCode() {
            return properties.getStr("toolCode");
        }

        private String prompt() {
            return properties.getStr("prompt");
        }

        private JSONObject params() {
            return properties.getJSONObject("params");
        }
    }

    private static class WorkflowEdge {
        private String sourceNodeId;
        private String targetNodeId;

        private static WorkflowEdge of(JSONObject json) {
            WorkflowEdge edge = new WorkflowEdge();
            edge.sourceNodeId = StrUtil.blankToDefault(json.getStr("sourceNodeId"), json.getStr("source"));
            edge.targetNodeId = StrUtil.blankToDefault(json.getStr("targetNodeId"), json.getStr("target"));
            return edge;
        }
    }
}
