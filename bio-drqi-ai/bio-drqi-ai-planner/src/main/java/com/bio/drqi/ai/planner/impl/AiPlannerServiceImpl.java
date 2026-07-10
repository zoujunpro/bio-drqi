package com.bio.drqi.ai.planner.impl;

import com.bio.drqi.ai.dto.planner.AiPlanReqDTO;
import com.bio.drqi.ai.dto.planner.AiPlanRspDTO;
import com.bio.drqi.ai.dto.planner.AiPlanTaskDTO;
import com.bio.drqi.ai.dto.planner.AiToolSelectionDTO;
import com.bio.drqi.ai.planner.AiPlanGenerator;
import com.bio.drqi.ai.planner.AiPlanValidator;
import com.bio.drqi.ai.planner.AiPlannerService;
import com.bio.drqi.ai.planner.AiTaskDecomposer;
import com.bio.drqi.ai.planner.AiToolSelector;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * AI Planner 默认实现。
 *
 * <p>这个类是 Planner 的总入口，只负责串联 Planner 内部的四个核心步骤。
 * 具体业务判断不要堆在这里，应该分别下沉到任务拆解、工具选择、计划生成、计划校验四个组件中。</p>
 */
@Service
public class AiPlannerServiceImpl implements AiPlannerService {

    /**
     * 任务拆解器：把用户问题和语义结果拆成一个或多个可执行任务。
     */
    @Resource
    private AiTaskDecomposer aiTaskDecomposer;

    /**
     * 工具选择器：为拆解出来的任务匹配可用工具。
     */
    @Resource
    private AiToolSelector aiToolSelector;

    /**
     * 计划生成器：把任务和工具选择结果转换成执行步骤。
     */
    @Resource
    private AiPlanGenerator aiPlanGenerator;

    /**
     * 计划校验器：在真正执行前检查计划是否缺参数、缺步骤或需要澄清。
     */
    @Resource
    private AiPlanValidator aiPlanValidator;

    @Override
    public AiPlanRspDTO plan(AiPlanReqDTO reqDTO) {
        // 第一步：拆任务。比如一句“查询未检测且已收获的编号”，可能拆成查询取样、查询种植、合并结果等任务。
        List<AiPlanTaskDTO> tasks = aiTaskDecomposer.decompose(reqDTO);

        // 第二步：选工具。根据任务类型、意图和候选工具，决定每个任务用哪个 Tool 或是否交给 Dify。
        List<AiToolSelectionDTO> selections = aiToolSelector.select(reqDTO, tasks);

        // 第三步：生成计划。把任务和工具选择结果组装成有顺序、有入参、有输出变量的执行步骤。
        AiPlanRspDTO planRspDTO = aiPlanGenerator.generate(reqDTO, tasks, selections);

        // 第四步：校验计划。可执行计划必须有步骤；缺少必要信息时返回 CLARIFY，让前端继续追问用户。
        return aiPlanValidator.validate(reqDTO, planRspDTO);
    }
}
