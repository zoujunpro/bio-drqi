package com.bio.drqi.ai.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.client.LlmClient;
import com.bio.drqi.ai.config.AiProperties;
import com.bio.drqi.ai.dto.command.AiCommandPlanDTO;
import com.bio.drqi.ai.dto.llm.LlmCallOptionsDTO;
import com.bio.drqi.ai.dto.llm.LlmChatMessageDTO;
import com.bio.drqi.ai.dto.req.AiAnalysisReqDTO;
import com.bio.drqi.ai.dto.rsp.AiAnalysisRspDTO;
import com.bio.drqi.ai.dto.rsp.AiTableColumnDTO;
import com.bio.drqi.ai.dto.rsp.AiTableDTO;
import com.bio.drqi.ai.prompt.CommandPrompt;
import com.bio.drqi.ai.service.AiCommandAnalysisService;
import com.bio.drqi.ai.util.AiJsonExtractor;
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
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AiCommandAnalysisServiceImpl implements AiCommandAnalysisService {

    /**
     * 需要透传给业务服务的用户身份请求头。
     * 注意：用户身份必须来自当前请求，不能让模型在 params 里生成 userId，否则会有越权风险。
     */
    private static final List<String> PASS_HEADERS = Arrays.asList(
            "Authorization", "userId", "username", "nickname", "jobNum"
    );

    @Resource
    private AiProperties aiProperties;

    @Resource
    private LlmClient llmClient;

    @Resource
    private RestTemplate aiCommandRestTemplate;

    @Override
    public AiAnalysisRspDTO analysis(AiAnalysisReqDTO reqDTO) {
        if (CollectionUtil.isEmpty(aiProperties.getCommands())) {
            throw new BusinessException("AI命令未配置：bio.ai.commands");
        }
        long startTime = System.currentTimeMillis();

        // 1. 让模型从 bio.ai.commands 配置中选择一个命令，并提取命令参数。
        // 模型只负责“选择命令 + 提取参数”，不能决定实际调用哪个接口。
        AiCommandPlanDTO plan = buildCommandPlan(reqDTO.getQuestion());

        // 2. command 必须命中后端配置，防止模型编造命令。
        AiProperties.Command command = getRequiredCommand(plan.getCommand());

        // 3. 后端补默认参数并校验必填参数。
        // 缺少必填参数时不调用后端业务接口，而是返回澄清问题给用户。
        applyDefaultParams(plan, command);
        validateRequiredParams(plan, command);
        if (Boolean.TRUE.equals(plan.getNeedClarify())) {
            AiAnalysisRspDTO rspDTO = new AiAnalysisRspDTO();
            rspDTO.setAnswer(StrUtil.blankToDefault(plan.getClarifyQuestion(), "请补充查询条件。"));
            return rspDTO;
        }

        // 4. 通过 Nacos 服务名 + path 调用已有后端接口，复用原系统权限和业务逻辑。
        Object remoteResult = executeRemote(command, plan.getParams());

        // 5. 把远程接口结果包装成前端 AI 聊天页可展示的统一结构。
        AiAnalysisRspDTO rspDTO = buildResponse(command, remoteResult);
        log.info("AI命令调用完成，cost={}ms，command={}，params={}",
                System.currentTimeMillis() - startTime, command.getCode(), JSONUtil.toJsonStr(plan.getParams()));
        return rspDTO;
    }

    /**
     * 调用大模型生成命令计划。
     * Prompt 中只暴露 command 的 code/name/description/params，不暴露 service/path 等内部接口细节。
     */
    private AiCommandPlanDTO buildCommandPlan(String question) {
        String content = llmClient.chat(Arrays.asList(
                new LlmChatMessageDTO("system", CommandPrompt.commandPlanPrompt(aiProperties.getCommands())),
                new LlmChatMessageDTO("user", question)
        ), LlmCallOptionsDTO.of("command", aiProperties.getLlm().getCommandTemperature()));
        String json = AiJsonExtractor.extractObject(content, "AI命令计划为空", "AI命令计划不是合法JSON");
        AiCommandPlanDTO plan = JSONUtil.toBean(json, AiCommandPlanDTO.class);
        if (plan == null) {
            throw new BusinessException("AI命令计划为空");
        }
        return plan;
    }

    /**
     * 根据模型返回的 commandCode 查找后端配置。
     * 这是命令白名单校验，模型返回未配置命令时必须拒绝执行。
     */
    private AiProperties.Command getRequiredCommand(String commandCode) {
        if (StrUtil.isBlank(commandCode)) {
            throw new BusinessException("AI命令计划缺少command");
        }
        for (AiProperties.Command command : aiProperties.getCommands()) {
            if (commandCode.equals(command.getCode())) {
                return command;
            }
        }
        throw new BusinessException("不支持的AI命令：" + commandCode);
    }

    /**
     * 应用命令参数默认值。
     * 例如“查询我的待办”可以把 type 默认成待办类型，不需要用户每次都说清楚。
     */
    private void applyDefaultParams(AiCommandPlanDTO plan, AiProperties.Command command) {
        if (plan.getParams() == null) {
            plan.setParams(new LinkedHashMap<>());
        }
        for (AiProperties.CommandParam param : command.getParams()) {
            if (!plan.getParams().containsKey(param.getName()) && param.getDefaultValue() != null) {
                plan.getParams().put(param.getName(), param.getDefaultValue());
            }
        }
    }

    /**
     * 校验必填参数。
     * 缺参数时返回澄清问题，而不是让模型猜或直接调用后端接口。
     */
    private void validateRequiredParams(AiCommandPlanDTO plan, AiProperties.Command command) {
        List<String> missing = new ArrayList<>();
        for (AiProperties.CommandParam param : command.getParams()) {
            boolean required = param.getRequired() == null || param.getRequired();
            Object value = plan.getParams().get(param.getName());
            if (required && (value == null || StrUtil.isBlank(String.valueOf(value)))) {
                missing.add(param.getDescription() == null ? param.getName() : param.getDescription());
            }
        }
        if (CollectionUtil.isNotEmpty(missing)) {
            plan.setNeedClarify(Boolean.TRUE);
            plan.setClarifyQuestion("请补充：" + String.join("、", missing));
        }
    }

    /**
     * 执行远程业务接口。
     * command.service 是 Nacos 服务名，RestTemplate 带 @LoadBalanced，会通过服务发现解析地址。
     */
    private Object executeRemote(AiProperties.Command command, Map<String, Object> params) {
        String url = "http://" + command.getService() + command.getPath();
        HttpHeaders headers = buildHeaders();
        HttpMethod method = HttpMethod.resolve(StrUtil.blankToDefault(command.getMethod(), "POST").toUpperCase());
        if (method == null) {
            throw new BusinessException("不支持的AI命令HTTP方法：" + command.getMethod());
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

    /**
     * 构建透传请求头。
     * 业务服务收到这些请求头后，仍然要按原有权限逻辑校验，AI服务只负责传递身份。
     */
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

    /**
     * 把远程接口返回值转换成 AI 前端统一响应。
     * 当前先做通用包装：Map 转字段-内容表，List 转单列表。
     * 后续可以按 command 配置 resultMapping，把业务结果映射成更友好的表格或图表。
     */
    private AiAnalysisRspDTO buildResponse(AiProperties.Command command, Object remoteResult) {
        AiAnalysisRspDTO rspDTO = new AiAnalysisRspDTO();
        rspDTO.setAnswer(command.getName() + "已完成。");
        if (remoteResult instanceof Map) {
            rspDTO.getTables().add(buildKeyValueTable(command.getName(), (Map<?, ?>) remoteResult));
        } else if (remoteResult instanceof List) {
            rspDTO.getTables().add(buildListTable(command.getName(), (List<?>) remoteResult));
        }
        return rspDTO;
    }

    /**
     * Map 结果通用展示：每个 key/value 展示成一行。
     */
    private AiTableDTO buildKeyValueTable(String title, Map<?, ?> data) {
        AiTableDTO table = new AiTableDTO();
        table.setTitle(title);
        table.getColumns().add(column("字段", "key"));
        table.getColumns().add(column("内容", "value"));
        for (Map.Entry<?, ?> entry : data.entrySet()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("key", String.valueOf(entry.getKey()));
            row.put("value", entry.getValue());
            table.getData().add(row);
        }
        return table;
    }

    /**
     * List 结果通用展示：每个元素展示成一行。
     */
    private AiTableDTO buildListTable(String title, List<?> data) {
        AiTableDTO table = new AiTableDTO();
        table.setTitle(title);
        table.getColumns().add(column("结果", "value"));
        for (Object item : data) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("value", item);
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
}
