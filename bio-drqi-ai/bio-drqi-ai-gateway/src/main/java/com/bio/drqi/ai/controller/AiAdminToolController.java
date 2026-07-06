package com.bio.drqi.ai.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.ai.dto.admin.AiApiSyncResultDTO;
import com.bio.drqi.ai.dto.admin.AiBatchStatusReqDTO;
import com.bio.drqi.ai.dto.admin.AiPageReqDTO;
import com.bio.drqi.ai.entity.AiApiParam;
import com.bio.drqi.ai.entity.AiApiRegistry;
import com.bio.drqi.ai.service.AiAdminToolService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/ai/admin/tools")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", allowCredentials = "true")
public class AiAdminToolController {

    @Resource
    private AiAdminToolService aiAdminToolService;

    @Resource
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Value("${spring.application.name:bio-ai-service}")
    private String serviceName;

    private static final List<String> AI_TOOL_DENY_KEYWORDS = Arrays.asList(
            "/ai/admin", "/admin/user", "/admin/role", "/admin/menu", "/admin/dept", "/admin/sys",
            "/admin/permission", "/website/", "/common/", "/dict/", "/config/", "/print/", "/label/",
            "globalpage", "globallist", "listpage", "listall", "page", "list", "search", "save", "add", "edit", "update", "delete", "remove",
            "upload", "import", "export", "submit", "login", "logout", "sync", "syn", "config", "enable",
            "disable", "approve", "approval", "test", "clean", "devops", "template", "parseexcel",
            "oss", "presigned", "permission", "menu", "role", "user", "dept", "category"
    );

    private static final List<String> AI_TOOL_ALLOW_DOMAIN_KEYWORDS = Arrays.asList(
            "project", "subproject", "implementation", "plan", "vector", "transform", "conversion", "trans",
            "plasmid", "sample", "seed", "plant", "pollination", "harvest", "experiment", "stock", "order",
            "product", "supplier", "brand", "location", "bms", "tc", "cer", "board"
    );

    private static final List<String> AI_TOOL_QUERY_KEYWORDS = Arrays.asList(
            "query", "detail", "find", "count", "stat", "board", "summary", "overview"
    );

    @PostMapping("/apis/page")
    @WebLog(desc = "AI API注册分页")
    public ResponseResult<Page<AiApiRegistry>> apiPage(@RequestBody AiPageReqDTO reqDTO) {
        return ResponseResult.getSuccess(aiAdminToolService.apiPage(reqDTO));
    }

    @PostMapping("/apis/save")
    @WebLog(desc = "AI API注册保存")
    public ResponseResult<Boolean> saveApi(@RequestBody AiApiRegistry entity) {
        aiAdminToolService.saveApi(entity);
        return ResponseResult.getSuccess(Boolean.TRUE);
    }

    @GetMapping("/apis/delete")
    @WebLog(desc = "AI API注册删除")
    public ResponseResult<Boolean> deleteApi(@RequestParam Long id) {
        aiAdminToolService.deleteApi(id);
        return ResponseResult.getSuccess(Boolean.TRUE);
    }

    @PostMapping("/apis/batch-ai-enabled")
    @WebLog(desc = "AI API注册批量开通禁用")
    public ResponseResult<Boolean> batchApiAiEnabled(@RequestBody AiBatchStatusReqDTO reqDTO) {
        aiAdminToolService.batchUpdateApiAiEnabled(reqDTO.getIds(), reqDTO.getAiEnabled());
        return ResponseResult.getSuccess(Boolean.TRUE);
    }

    @PostMapping("/params/page")
    @WebLog(desc = "AI API参数分页")
    public ResponseResult<Page<AiApiParam>> paramPage(@RequestBody AiPageReqDTO reqDTO) {
        return ResponseResult.getSuccess(aiAdminToolService.paramPage(reqDTO));
    }

    @PostMapping("/params/save")
    @WebLog(desc = "AI API参数保存")
    public ResponseResult<Boolean> saveParam(@RequestBody AiApiParam entity) {
        aiAdminToolService.saveParam(entity);
        return ResponseResult.getSuccess(Boolean.TRUE);
    }

    @GetMapping("/params/delete")
    @WebLog(desc = "AI API参数删除")
    public ResponseResult<Boolean> deleteParam(@RequestParam Long id) {
        aiAdminToolService.deleteParam(id);
        return ResponseResult.getSuccess(Boolean.TRUE);
    }

    @PostMapping("/params/batch-required")
    @WebLog(desc = "AI API参数批量必填设置")
    public ResponseResult<Boolean> batchParamRequired(@RequestBody AiBatchStatusReqDTO reqDTO) {
        aiAdminToolService.batchUpdateParamRequired(reqDTO.getIds(), reqDTO.getRequired());
        return ResponseResult.getSuccess(Boolean.TRUE);
    }

    @PostMapping("/apis/sync")
    @WebLog(desc = "AI API注册同步")
    public ResponseResult<AiApiSyncResultDTO> syncApis() {
        List<AiApiRegistry> apiList = new ArrayList<AiApiRegistry>();
        List<AiApiParam> paramList = new ArrayList<AiApiParam>();
        List<Map.Entry<RequestMappingInfo, HandlerMethod>> mappings =
                new ArrayList<Map.Entry<RequestMappingInfo, HandlerMethod>>(requestMappingHandlerMapping.getHandlerMethods().entrySet());
        mappings.sort(Comparator.comparing(item -> item.getValue().getBeanType().getName() + "#" + item.getValue().getMethod().getName()));
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : mappings) {
            RequestMappingInfo info = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();
            for (String path : paths(info)) {
                for (String method : methods(info)) {
                    if (!queryApi(method, path, handlerMethod.getMethod().getName())) {
                        continue;
                    }
                    AiApiRegistry api = buildApi(path, method, handlerMethod);
                    apiList.add(api);
                    paramList.addAll(buildParams(api.getApiCode(), handlerMethod, api.getReadOnly() != null && api.getReadOnly() == 1));
                }
            }
        }
        return ResponseResult.getSuccess(aiAdminToolService.syncApis(apiList, paramList));
    }

    private AiApiRegistry buildApi(String path, String method, HandlerMethod handlerMethod) {
        String methodLabel = methodLabel(handlerMethod.getMethod());
        AiApiRegistry api = new AiApiRegistry();
        api.setApiCode(buildApiCode(method, path));
        api.setServiceName(serviceName);
        api.setPath(path);
        api.setMethod(method);
        api.setControllerClass(handlerMethod.getBeanType().getName());
        api.setMethodName(handlerMethod.getMethod().getName());
        api.setRequestDto(requestDto(handlerMethod));
        api.setResponseType(handlerMethod.getMethod().getGenericReturnType().getTypeName());
        api.setApiName(methodLabel == null ? handlerMethod.getMethod().getName() : methodLabel);
        api.setDescription(methodLabel == null ? handlerMethod.getBeanType().getSimpleName() + "#" + handlerMethod.getMethod().getName() : methodLabel);
        api.setReadOnly(readOnly(method, path, handlerMethod.getMethod().getName()) ? 1 : 0);
        api.setRiskLevel(api.getReadOnly() == 1 ? "low" : "high");
        api.setOwnerModule(handlerMethod.getBeanType().getSimpleName());
        api.setAiEnabled(0);
        api.setDeleted(0);
        return api;
    }

    private List<AiApiParam> buildParams(String apiCode, HandlerMethod handlerMethod, boolean apiReadOnly) {
        List<AiApiParam> result = new ArrayList<AiApiParam>();
        Set<String> names = new HashSet<String>();
        int defaultAiEnabled = 0;
        MethodParameter[] parameters = handlerMethod.getMethodParameters();
        for (int i = 0; i < parameters.length; i++) {
            MethodParameter parameter = parameters[i];
            Class<?> type = parameter.getParameterType();
            if (ignoredParamType(type)) {
                continue;
            }
            RequestBody requestBody = parameter.getParameterAnnotation(RequestBody.class);
            if (requestBody != null && !simpleType(type)) {
                addFields(apiCode, type, defaultAiEnabled, result, names);
                continue;
            }
            if (!simpleType(type) && !type.isArray()) {
                addFields(apiCode, type, defaultAiEnabled, result, names);
                continue;
            }
            String name = paramName(parameter, i);
            result.add(buildParam(apiCode, name, type.getSimpleName(), required(parameter), javaField(parameter, name),
                    sourceType(parameter), name, null, defaultAliases(name, null), defaultAiEnabled, names));
        }
        return result;
    }

    private void addFields(String apiCode, Class<?> type, int defaultAiEnabled,
                           List<AiApiParam> result, Set<String> names) {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || "serialVersionUID".equals(field.getName())) {
                    continue;
                }
                String label = annotationLabel(field);
                int fieldRequired = hasRequiredAnnotation(field) ? 1 : 0;
                result.add(buildParam(apiCode, field.getName(), field.getType().getSimpleName(), fieldRequired,
                        field.getName(), "body", field.getName(), label, defaultAliases(field.getName(), label),
                        defaultAiEnabled, names));
            }
            current = current.getSuperclass();
        }
    }

    private AiApiParam buildParam(String apiCode, String paramName, String paramType, int required, String javaField,
                                  String sourceType, String sourceField, String businessName, String aliases,
                                  int defaultAiEnabled, Set<String> names) {
        String uniqueName = uniqueParamName(paramName, names);
        AiApiParam param = new AiApiParam();
        param.setApiCode(apiCode);
        param.setParamName(uniqueName);
        param.setParamType(paramType);
        param.setRequired(required);
        param.setJavaField(javaField);
        param.setBusinessName(businessName == null || businessName.trim().length() == 0 ? uniqueName : businessName.trim());
        param.setAliases(aliases);
        param.setSourceType(sourceType);
        param.setSourceField(sourceField);
        param.setAiEnabled(defaultAiEnabled);
        param.setDeleted(0);
        return param;
    }

    private String methodLabel(Method method) {
        for (Annotation annotation : method.getAnnotations()) {
            String simpleName = annotation.annotationType().getSimpleName();
            if ("WebLog".equals(simpleName)) {
                String value = firstAnnotationValue(annotation, "desc", "value");
                if (value != null) {
                    return value;
                }
            }
            if ("ApiOperation".equals(simpleName)) {
                String value = firstAnnotationValue(annotation, "value", "notes");
                if (value != null) {
                    return value;
                }
            }
            if ("Operation".equals(simpleName)) {
                String value = firstAnnotationValue(annotation, "summary", "description");
                if (value != null) {
                    return value;
                }
            }
        }
        return null;
    }

    private String firstAnnotationValue(Annotation annotation, String... methodNames) {
        for (String methodName : methodNames) {
            String value = annotationValue(annotation, methodName);
            if (value != null && value.trim().length() > 0) {
                return value.trim();
            }
        }
        return null;
    }

    private String annotationLabel(Field field) {
        for (Annotation annotation : field.getAnnotations()) {
            String simpleName = annotation.annotationType().getSimpleName();
            if (!"ApiModelProperty".equals(simpleName) && !"Schema".equals(simpleName)) {
                continue;
            }
            String value = annotationValue(annotation, "value");
            if (value == null || value.trim().length() == 0) {
                value = annotationValue(annotation, "description");
            }
            if (value == null || value.trim().length() == 0) {
                value = annotationValue(annotation, "name");
            }
            if (value != null && value.trim().length() > 0) {
                return value.trim();
            }
        }
        return null;
    }

    private String annotationValue(Annotation annotation, String methodName) {
        try {
            Method method = annotation.annotationType().getMethod(methodName);
            Object value = method.invoke(annotation);
            return value == null ? null : String.valueOf(value);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean hasRequiredAnnotation(Field field) {
        for (Annotation annotation : field.getAnnotations()) {
            String simpleName = annotation.annotationType().getSimpleName();
            if ("NotBlank".equals(simpleName) || "NotNull".equals(simpleName) || "NotEmpty".equals(simpleName)) {
                return true;
            }
        }
        return false;
    }

    private String defaultAliases(String fieldName, String label) {
        LinkedHashSet<String> values = new LinkedHashSet<String>();
        addAlias(values, fieldName);
        addAlias(values, label);
        String lowerName = fieldName == null ? "" : fieldName.toLowerCase();
        if (lowerName.contains("projectcode")) {
            addAlias(values, "项目编号");
            addAlias(values, "项目编码");
            addAlias(values, "项目");
        }
        if (lowerName.contains("projectname")) {
            addAlias(values, "项目名称");
            addAlias(values, "项目");
        }
        if (lowerName.contains("vectortask") || lowerName.contains("implementation")) {
            addAlias(values, "实施方案");
            addAlias(values, "实施方案编号");
            addAlias(values, "方案编号");
            addAlias(values, "载体任务");
        }
        if (lowerName.contains("samplecode")) {
            addAlias(values, "取样编号");
            addAlias(values, "样品编号");
            addAlias(values, "样本编号");
        }
        if (lowerName.contains("plantcode")) {
            addAlias(values, "植株编号");
            addAlias(values, "植株编码");
        }
        if (lowerName.contains("keyword") || lowerName.contains("key")) {
            addAlias(values, "关键词");
            addAlias(values, "关键字");
            addAlias(values, "名称");
            addAlias(values, "编号");
        }
        if (lowerName.contains("status")) {
            addAlias(values, "状态");
            addAlias(values, "审批状态");
        }
        if (lowerName.contains("pagenum")) {
            addAlias(values, "页码");
            addAlias(values, "第几页");
        }
        if (lowerName.contains("pagesize")) {
            addAlias(values, "每页数量");
            addAlias(values, "条数");
        }
        return values.isEmpty() ? null : String.join(",", values);
    }

    private void addAlias(Set<String> values, String value) {
        if (value != null && value.trim().length() > 0) {
            values.add(value.trim());
        }
    }

    private String uniqueParamName(String name, Set<String> names) {
        String value = name;
        int index = 2;
        while (names.contains(value)) {
            value = name + index;
            index++;
        }
        names.add(value);
        return value;
    }

    private String paramName(MethodParameter parameter, int index) {
        RequestParam requestParam = parameter.getParameterAnnotation(RequestParam.class);
        if (requestParam != null && requestParam.value() != null && requestParam.value().length() > 0) {
            return requestParam.value();
        }
        if (requestParam != null && requestParam.name() != null && requestParam.name().length() > 0) {
            return requestParam.name();
        }
        PathVariable pathVariable = parameter.getParameterAnnotation(PathVariable.class);
        if (pathVariable != null && pathVariable.value() != null && pathVariable.value().length() > 0) {
            return pathVariable.value();
        }
        if (pathVariable != null && pathVariable.name() != null && pathVariable.name().length() > 0) {
            return pathVariable.name();
        }
        return parameter.getParameterName() == null ? "arg" + index : parameter.getParameterName();
    }

    private String javaField(MethodParameter parameter, String defaultName) {
        return parameter.getParameterName() == null ? defaultName : parameter.getParameterName();
    }

    private int required(MethodParameter parameter) {
        RequestParam requestParam = parameter.getParameterAnnotation(RequestParam.class);
        if (requestParam != null) {
            return requestParam.required() ? 1 : 0;
        }
        PathVariable pathVariable = parameter.getParameterAnnotation(PathVariable.class);
        if (pathVariable != null) {
            return pathVariable.required() ? 1 : 0;
        }
        RequestBody requestBody = parameter.getParameterAnnotation(RequestBody.class);
        return requestBody != null && requestBody.required() ? 1 : 0;
    }

    private String sourceType(MethodParameter parameter) {
        if (parameter.hasParameterAnnotation(PathVariable.class)) {
            return "path";
        }
        if (parameter.hasParameterAnnotation(RequestBody.class)) {
            return "body";
        }
        return "query";
    }

    private String requestDto(HandlerMethod handlerMethod) {
        List<String> types = new ArrayList<String>();
        for (MethodParameter parameter : handlerMethod.getMethodParameters()) {
            if (!ignoredParamType(parameter.getParameterType())) {
                types.add(parameter.getParameterType().getName());
            }
        }
        return String.join(",", types);
    }

    private List<String> paths(RequestMappingInfo info) {
        if (info.getPathPatternsCondition() != null && !info.getPathPatternsCondition().getPatternValues().isEmpty()) {
            return new ArrayList<String>(info.getPathPatternsCondition().getPatternValues());
        }
        if (info.getPatternsCondition() != null && !info.getPatternsCondition().getPatterns().isEmpty()) {
            return new ArrayList<String>(info.getPatternsCondition().getPatterns());
        }
        return new ArrayList<String>();
    }

    private List<String> methods(RequestMappingInfo info) {
        if (info.getMethodsCondition() == null || info.getMethodsCondition().getMethods().isEmpty()) {
            return Arrays.asList("ALL");
        }
        List<String> methods = new ArrayList<String>();
        for (RequestMethod method : info.getMethodsCondition().getMethods()) {
            methods.add(method.name());
        }
        return methods;
    }

    private boolean readOnly(String method, String path, String methodName) {
        String value = (method + " " + path + " " + methodName).toLowerCase();
        return queryApi(method, path, methodName);
    }

    private boolean queryApi(String method, String path, String methodName) {
        String value = (method + " " + path + " " + methodName).toLowerCase();
        value = value.replace("-", "").replace("_", "");
        if (path == null || path.trim().length() == 0) {
            return false;
        }
        if ("ALL".equalsIgnoreCase(method)) {
            return false;
        }
        if (containsAny(value, AI_TOOL_DENY_KEYWORDS)) {
            return false;
        }
        return containsAny(value, AI_TOOL_ALLOW_DOMAIN_KEYWORDS)
                && containsAny(value, AI_TOOL_QUERY_KEYWORDS);
    }

    private boolean containsAny(String value, List<String> keywords) {
        for (String keyword : keywords) {
            if (value.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String buildApiCode(String method, String path) {
        return (serviceName + "_" + method + "_" + path)
                .replaceAll("[^a-zA-Z0-9]+", "_")
                .replaceAll("^_+", "")
                .replaceAll("_+$", "")
                .toLowerCase();
    }

    private boolean simpleType(Class<?> type) {
        return ClassUtils.isPrimitiveOrWrapper(type)
                || CharSequence.class.isAssignableFrom(type)
                || Number.class.isAssignableFrom(type)
                || Boolean.class == type
                || java.util.Date.class.isAssignableFrom(type)
                || java.time.temporal.Temporal.class.isAssignableFrom(type)
                || type.isEnum();
    }

    private boolean ignoredParamType(Class<?> type) {
        String name = type.getName();
        return name.startsWith("javax.servlet.")
                || name.startsWith("jakarta.servlet.")
                || name.startsWith("org.springframework.web.multipart.")
                || name.startsWith("org.springframework.validation.")
                || name.startsWith("org.springframework.ui.");
    }
}
