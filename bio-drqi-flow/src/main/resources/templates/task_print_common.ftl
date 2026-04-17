<#macro docHeader title>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="zh-CN" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8" />
    <title>${title!''}</title>
    <style type="text/css">
        @page {
            size: A4;
            margin: 16mm 10mm 14mm;
        }

        body {
            margin: 0;
            color: #222;
            font-family: "Microsoft YaHei", SimSun, sans-serif;
            font-size: 13px;
            line-height: 1.6;
        }

        .page {
            width: 100%;
        }

        .header {
            text-align: center;
            margin-bottom: 10px;
        }

        .header h1 {
            margin: 0 0 6px;
            font-size: 24px;
            line-height: 1.2;
        }

        .sub-header {
            overflow: hidden;
            font-size: 12px;
            color: #555;
            margin-bottom: 8px;
        }

        .sub-header .left {
            float: left;
        }

        .sub-header .right {
            float: right;
            text-align: right;
        }

        .clear {
            clear: both;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            table-layout: fixed;
            margin-bottom: 10px;
        }

        td, th {
            border: 1px solid #666;
            padding: 6px 8px;
            vertical-align: top;
            word-break: break-all;
        }

        .section-title {
            background: #f4f4f4;
            font-weight: bold;
            text-align: center;
        }

        .label {
            width: 18%;
            background: #fafafa;
            font-weight: bold;
        }

        .value {
            width: 32%;
        }

        .full-value {
            text-align: left;
        }

        .json-block {
            white-space: pre-wrap;
            word-break: break-word;
            font-family: "Microsoft YaHei", SimSun, sans-serif;
            min-height: 80px;
        }
    </style>
</head>
<body>
<div class="page">
</#macro>

<#macro docFooter>
</div>
</body>
</html>
</#macro>

<#macro printHeader>
    <div class="header">
        <h1>${taskTypeName!taskTypeCode!''}</h1>
    </div>
    <div class="sub-header">
        <div class="left">申请编号: ${taskNum!''}</div>
        <div class="right">
            <div>打印人: ${printUser!''}</div>
            <div>打印时间: ${printTime!''}</div>
        </div>
        <div class="clear"></div>
    </div>
    <table>
        <tr>
            <td class="label">申请人</td>
            <td class="value">${applyUserName!''}</td>
            <td class="label">申请时间</td>
            <td class="value">${applyTime!''}</td>
        </tr>
        <tr>
            <td class="label">所属工单类型</td>
            <td class="value">${taskTypeName!''}</td>
            <td class="label">审批状态</td>
            <td class="value">${taskStatusName!''}</td>
        </tr>
        <tr>
            <td class="label">工单描述</td>
            <td colspan="3" class="full-value">${taskDesc!''}</td>
        </tr>
        <#if refTaskNum?? && refTaskNum?has_content>
            <tr>
                <td class="label">关联工单</td>
                <td colspan="3" class="full-value">${refTaskNum}</td>
            </tr>
        </#if>
    </table>
</#macro>

<#macro approvalFooter>
    <table>
        <tr>
            <td colspan="3" class="section-title">审批记录</td>
        </tr>
        <tr>
            <td style="width: 22%;" class="section-title">节点</td>
            <td style="width: 48%;" class="section-title">审批信息</td>
            <td style="width: 30%;" class="section-title">审批时间</td>
        </tr>
        <#if approveRecords?? && (approveRecords?size > 0)>
            <#list approveRecords as record>
                <tr>
                    <td>${record.nodeName!''}</td>
                    <td>
                        ${record.username!''}
                        <#if record.approveResult?? && record.approveResult?has_content>
                            / ${record.approveResult}
                        </#if>
                        <#if record.approveRemark?? && record.approveRemark?has_content>
                            <br />备注: ${record.approveRemark}
                        </#if>
                    </td>
                    <td>${record.approveTime!''}</td>
                </tr>
            </#list>
        <#else>
            <tr>
                <td colspan="3">暂无审批记录</td>
            </tr>
        </#if>
    </table>
</#macro>
