<#macro docHeader title>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="zh-CN" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8" />
    <title>${title!''}</title>
    <style type="text/css">
        html {
            background: #e9edf2;
        }

        @page {
            size: A4;
            margin: 10mm 8mm 10mm;
        }

        body {
            margin: 0;
            color: #222;
            font-family: "Microsoft YaHei", SimSun, sans-serif;
            font-size: 13px;
            line-height: 1.6;
            background: #e9edf2;
        }

        .preview-shell {
            padding: 16px 0 32px;
        }

        .page {
            width: 190mm;
            min-height: 267mm;
            margin: 0 auto;
            padding: 10mm 8mm 10mm;
            box-sizing: border-box;
            background: #fff;
            box-shadow: 0 2px 14px rgba(0, 0, 0, 0.08);
        }

        .header {
            position: relative;
            margin-bottom: 6px;
            text-align: center;
            min-height: 88px;
            padding-right: 96px;
            box-sizing: border-box;
        }

        .header h1 {
            margin: 0 0 4px;
            font-size: 22px;
            line-height: 1.2;
            padding-top: 8px;
        }

        .header-qr {
            position: absolute;
            top: -12px;
            right: 0;
            width: 80px;
            height: 80px;
            text-align: center;
        }

        .header-qr img {
            width: 80px;
            height: 80px;
            display: block;
        }

        .header-qr-text {
            margin-top: 2px;
            font-size: 10px;
            color: #666;
            line-height: 1.2;
        }

        .sub-header {
            display: table;
            width: 100%;
            font-size: 12px;
            color: #555;
            margin-bottom: 6px;
        }

        .sub-header .item {
            display: table-cell;
            width: 33.33%;
            vertical-align: middle;
        }

        .sub-header .left {
            text-align: left;
        }

        .sub-header .center {
            text-align: center;
        }

        .sub-header .right {
            text-align: right;
        }

        .clear {
            clear: both;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            table-layout: fixed;
            margin-bottom: 6px;
            border: 1px solid #7d7d7d;
        }

        td, th {
            border: 1px solid #7d7d7d;
            padding: 4px 6px;
            vertical-align: top;
            word-break: break-all;
        }

        .section-title {
            background: #e3e3e3;
            font-weight: bold;
            text-align: center;
        }

        .group-title {
            background: #f3f3f3;
            font-weight: bold;
            text-align: left;
        }

        .label {
            width: 18%;
            background: #e8e8e8;
            color: #111;
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

        .table-wrap {
            page-break-inside: auto;
        }

        .table-wrap tr {
            page-break-inside: avoid;
            page-break-after: auto;
        }

        .table-wrap thead {
            display: table-header-group;
        }

        .table-wrap tbody {
            display: table-row-group;
        }

        @media print {
            html, body {
                background: #fff;
            }

            body {
                font-size: 12px;
                line-height: 1.45;
            }

            .preview-shell {
                padding: 0;
            }

            .page {
                width: 100%;
                min-height: auto;
                margin: 0;
                padding: 0;
                box-shadow: none;
            }

            .header {
                min-height: 82px;
                margin-bottom: 4px;
                padding-right: 92px;
            }

            .header h1 {
                font-size: 20px;
                padding-top: 6px;
            }

            .header-qr {
                top: -8px;
                width: 76px;
                height: 76px;
            }

            .header-qr img {
                width: 76px;
                height: 76px;
            }

            .sub-header {
                margin-bottom: 4px;
                font-size: 11px;
            }

            table {
                margin-bottom: 4px;
            }

            td, th {
                padding: 3px 5px;
            }
        }
    </style>
</head>
<body>
<div class="preview-shell">
<div class="page">
</#macro>

<#macro docFooter>
</div>
</div>
</body>
</html>
</#macro>

<#macro printHeader>
    <div class="header">
        <#if modelHeader.qrCodeUrl?? && modelHeader.qrCodeUrl?has_content>
            <div class="header-qr">
                <img src="${modelHeader.qrCodeUrl}" alt="二维码" />
                <div class="header-qr-text">${modelHeader.qrCodeText!'扫码查看'}</div>
            </div>
        </#if>
        <h1>${modelHeader.taskTypeName!modelHeader.taskTypeCode!''}</h1>
    </div>
    <div class="sub-header">
        <div class="item left">申请编号: ${modelHeader.taskNum!''}</div>
        <div class="item center">打印人: ${modelHeader.printUser!''}</div>
        <div class="item right">打印时间: ${modelHeader.printTime!''}</div>
    </div>
    <table>
        <tr>
            <td class="label">申请人</td>
            <td class="value">${modelHeader.applyUserName!''}</td>
            <td class="label">申请时间</td>
            <td class="value">${modelHeader.applyTime!''}</td>
        </tr>
        <tr>
            <td class="label">所属工单类型</td>
            <td class="value">${modelHeader.taskTypeName!''}</td>
            <td class="label">审批状态</td>
            <td class="value">${modelHeader.taskStatusName!''}</td>
        </tr>
        <tr>
            <td class="label">工单描述</td>
            <td colspan="3" class="full-value">${modelHeader.taskDesc!''}</td>
        </tr>
        <#if modelHeader.refTaskNum?? && modelHeader.refTaskNum?has_content>
            <tr>
                <td class="label">关联工单</td>
                <td colspan="3" class="full-value">${modelHeader.refTaskNum}</td>
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
        <#if modelBottomList?? && (modelBottomList?size > 0)>
            <#list modelBottomList as record>
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
