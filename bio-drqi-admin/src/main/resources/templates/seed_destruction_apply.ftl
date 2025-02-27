<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>打印测试案例</title>
    <style>
        @media print {
            @page {
                margin: 0;
                size: A4;
            }

            html, body {
                width: 210mm;
                height: 297mm;
            }

            body {
                -webkit-print-color-adjust: exact;
                -moz-print-color-adjust: exact;
                -ms-print-color-adjust: exact;
                print-color-adjust: exact;
            }
        }

        body {
            padding: 0;
        }

        .content {
            height: 100%;
            width: 100%;
            font-size: 16px;
            text-align: center;
            font-family: 微软雅黑;
            margin: 0 auto;
        }

        h1, h2, h3, h4, h5, h6, h7, h8 {
            text-align: center;
        }

        .title {
            width: 100%;
            height: 25px;
            line-height: 25px;
            margin: 0 auto;
        }

        .titleLeft {
            padding-left: 20px;
            float: left;
        }

        .titleRight {
            padding-right: 20px;
            float: right;
        }

        table {
            width: 100%;
            border-spacing: 0;
            border-collapse: collapse;
            border: 1px solid black;
            margin: 0 auto;
        }

        th,
        td {
            border: 1px solid black;
            font-size: 16px;
            text-align: center;
            font-family: 微软雅黑;
        }


        .td-title {
            background-color: #f2f3f5;
            width: 120px;
        }
    </style>
</head>

<body>
<div id="printContent" class="content">
    <h3><B>${taskType}申请单</B></h3>
    <div class="title">
        <span class="titleLeft">打印人:${printUser}</span>
        <span class="titleRight">打印时间:${printTime}</span>
    </div>
    <table>
        <tr>
            <td colspan="4">任务信息</td>
        </tr>
        <tr>
            <td class="td-title">任务描述：</td>
            <td colspan="3" style="text-align: left">${taskDesc}</td>
        </tr>
        <tr>
            <td class="td-title">任务编号：</td>
            <td>${taskNum}</td>
            <td class="td-title">任务类型：</td>
            <td>${taskType}</td>
        </tr>
        <tr>
            <td class="td-title">申请人：</td>
            <td>${applyName}</td>
            <td class="td-title">申请时间：</td>
            <td>${applyDate}</td>
        </tr>
        <tr>
            <td class="td-title">所属部门：</td>
            <td>${deptName}</td>
            <td class="td-title">审批状态</td>
            <td>${approveResult}</td>
        </tr>
        <tr>
            <td colspan="4">审批内容</td>
        </tr>
        <#if contentData?exists >
            <#list contentData as content>
                <tr>
                    <td colspan="4">No.${content_index+1}</td>
                </tr>
                <tr>
                    <td class="td-title">种子编号：</td>
                    <td>${content.seedNum}</td>
                    <td class="td-title">销毁数量：</td>
                    <td>${content.seedNumber}${content.unit}</td>
                </tr>
                <tr>
                    <td class="td-title">备注：</td>
                    <td colspan="3">${content.remarks}</td>
                </tr>
            </#list>
        </#if>
        <tr>
            <td colspan="4">审批进程</td>
        </tr>
        <#if nodeList?exists >
            <#list nodeList as node>
                <#list node.nodeContentList as nodeContent>
                    <#if nodeContent_index = 0>
                        <tr>
                            <td class="td-title" rowspan="${node.nodeContentList?size}">${node.nodeName}:</td>
                            <td colspan="3" style="text-align: left">${nodeContent.actorUserName}
                                /${nodeContent.eventType}
                                /${nodeContent.actorTime}</td>
                        </tr>
                    <#else>
                        <tr>
                            <td colspan="3" style="text-align: left">${nodeContent.actorUserName}
                                /${nodeContent.eventType}
                                /${nodeContent.actorTime}</td>
                        </tr>
                    </#if>
                </#list>
            </#list>
        </#if>
    </table>
</div>
</body>

</html>