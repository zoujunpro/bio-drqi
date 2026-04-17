<#import "task_print_common.ftl" as c>
<@c.docHeader title="${taskTypeName!taskTypeCode!''}打印单" />
<@c.printHeader />

<table>
    <tr>
        <td colspan="4" class="section-title">工单内容</td>
    </tr>
    <#if bodyRows?? && (bodyRows?size > 0)>
        <#list bodyRows as row>
            <tr>
                <td class="label">${row.label1!''}</td>
                <td class="value">${row.value1!''}</td>
                <td class="label">${row.label2!''}</td>
                <td class="value">${row.value2!''}</td>
            </tr>
        </#list>
    <#else>
        <tr>
            <td colspan="4" class="full-value json-block">${taskFormPretty!''}</td>
        </tr>
    </#if>
</table>

<@c.approvalFooter />
<@c.docFooter />
