INSERT INTO ai_workflow_definition (
    workflow_code,
    workflow_name,
    description,
    category,
    dsl_json,
    enabled,
    deleted,
    create_time,
    update_time
) VALUES (
    'implementation_risk_analysis',
    '实施方案风险分析 Agent',
    '根据实施方案编码查询基础信息、步骤、时间计划、取样、转化、转化移苗、种植等明细，并进行执行风险分析。',
    'agent',
    '{
      "nodes": [
        {
          "id": "start",
          "type": "START",
          "name": "开始",
          "x": 80,
          "y": 280,
          "properties": {}
        },
        {
          "id": "tool_plan_detail",
          "type": "TOOL",
          "name": "实施方案详情",
          "x": 280,
          "y": 280,
          "properties": {
            "toolCode": "bio_drqi_manage_admin_get_implementationplan_detailbycode",
            "inputMapping": {
              "vectorTaskCode": {
                "sourceType": "INPUT",
                "sourcePath": "vectorTaskCode"
              }
            }
          }
        },
        {
          "id": "tool_step_list",
          "type": "TOOL",
          "name": "实施步骤",
          "x": 520,
          "y": 80,
          "properties": {
            "toolCode": "bio_drqi_manage_admin_get_implementationplan_steplistbycode",
            "inputMapping": {
              "vectorTaskCode": {
                "sourceType": "INPUT",
                "sourcePath": "vectorTaskCode"
              }
            }
          }
        },
        {
          "id": "tool_time_plan",
          "type": "TOOL",
          "name": "计划时间",
          "x": 520,
          "y": 180,
          "properties": {
            "toolCode": "bio_drqi_manage_admin_get_vectortasktimeplan_list",
            "inputMapping": {
              "vectorTaskCode": {
                "sourceType": "INPUT",
                "sourcePath": "vectorTaskCode"
              }
            }
          }
        },
        {
          "id": "tool_sample",
          "type": "TOOL",
          "name": "取样明细",
          "x": 520,
          "y": 280,
          "properties": {
            "toolCode": "bio_drqi_manage_admin_post_sampleapply_listbyvectortask",
            "inputMapping": {
              "vectorTaskId": {
                "sourceType": "NODE_OUTPUT",
                "sourceNodeId": "tool_plan_detail",
                "sourcePath": "data.id"
              },
              "sourceCode": {
                "sourceType": "FIXED",
                "value": "project"
              },
              "pageSize": {
                "sourceType": "FIXED",
                "value": 999
              },
              "pageNum": {
                "sourceType": "FIXED",
                "value": 1
              }
            }
          }
        },
        {
          "id": "tool_transform",
          "type": "TOOL",
          "name": "转化明细",
          "x": 520,
          "y": 380,
          "properties": {
            "toolCode": "bio_drqi_manage_admin_post_transform_listbyvectortask",
            "inputMapping": {
              "vectorTaskId": {
                "sourceType": "NODE_OUTPUT",
                "sourceNodeId": "tool_plan_detail",
                "sourcePath": "data.id"
              }
            }
          }
        },
        {
          "id": "tool_conversion",
          "type": "TOOL",
          "name": "转化移苗",
          "x": 520,
          "y": 480,
          "properties": {
            "toolCode": "bio_drqi_manage_admin_get_conversionandtrans_listbyvectortask",
            "inputMapping": {
              "vectorTaskId": {
                "sourceType": "NODE_OUTPUT",
                "sourceNodeId": "tool_plan_detail",
                "sourcePath": "data.id"
              }
            }
          }
        },
        {
          "id": "tool_plant",
          "type": "TOOL",
          "name": "种植明细",
          "x": 520,
          "y": 580,
          "properties": {
            "toolCode": "bio_drqi_manage_admin_post_plantsinglestock_listbyvectortaskiddetail",
            "inputMapping": {
              "vectorTaskId": {
                "sourceType": "NODE_OUTPUT",
                "sourceNodeId": "tool_plan_detail",
                "sourcePath": "data.id"
              },
              "pageSize": {
                "sourceType": "FIXED",
                "value": 999
              },
              "pageNum": {
                "sourceType": "FIXED",
                "value": 1
              }
            }
          }
        },
        {
          "id": "analysis_risk",
          "type": "ANALYSIS",
          "name": "风险分析",
          "x": 820,
          "y": 330,
          "properties": {
            "analysisCode": "implementation_risk",
            "inputRefs": [
              "tool_plan_detail",
              "tool_step_list",
              "tool_time_plan",
              "tool_sample",
              "tool_transform",
              "tool_conversion",
              "tool_plant"
            ]
          }
        },
        {
          "id": "end",
          "type": "END",
          "name": "结束",
          "x": 1060,
          "y": 330,
          "properties": {}
        }
      ],
      "edges": [
        {
          "id": "edge_start_detail",
          "sourceNodeId": "start",
          "targetNodeId": "tool_plan_detail"
        },
        {
          "id": "edge_detail_steps",
          "sourceNodeId": "tool_plan_detail",
          "targetNodeId": "tool_step_list"
        },
        {
          "id": "edge_detail_time",
          "sourceNodeId": "tool_plan_detail",
          "targetNodeId": "tool_time_plan"
        },
        {
          "id": "edge_detail_sample",
          "sourceNodeId": "tool_plan_detail",
          "targetNodeId": "tool_sample"
        },
        {
          "id": "edge_detail_transform",
          "sourceNodeId": "tool_plan_detail",
          "targetNodeId": "tool_transform"
        },
        {
          "id": "edge_detail_conversion",
          "sourceNodeId": "tool_plan_detail",
          "targetNodeId": "tool_conversion"
        },
        {
          "id": "edge_detail_plant",
          "sourceNodeId": "tool_plan_detail",
          "targetNodeId": "tool_plant"
        },
        {
          "id": "edge_steps_analysis",
          "sourceNodeId": "tool_step_list",
          "targetNodeId": "analysis_risk"
        },
        {
          "id": "edge_time_analysis",
          "sourceNodeId": "tool_time_plan",
          "targetNodeId": "analysis_risk"
        },
        {
          "id": "edge_sample_analysis",
          "sourceNodeId": "tool_sample",
          "targetNodeId": "analysis_risk"
        },
        {
          "id": "edge_transform_analysis",
          "sourceNodeId": "tool_transform",
          "targetNodeId": "analysis_risk"
        },
        {
          "id": "edge_conversion_analysis",
          "sourceNodeId": "tool_conversion",
          "targetNodeId": "analysis_risk"
        },
        {
          "id": "edge_plant_analysis",
          "sourceNodeId": "tool_plant",
          "targetNodeId": "analysis_risk"
        },
        {
          "id": "edge_analysis_end",
          "sourceNodeId": "analysis_risk",
          "targetNodeId": "end"
        }
      ]
    }',
    1,
    0,
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE
    workflow_name = VALUES(workflow_name),
    description = VALUES(description),
    category = VALUES(category),
    dsl_json = VALUES(dsl_json),
    enabled = 1,
    deleted = 0,
    update_time = NOW();
