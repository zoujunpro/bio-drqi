<#import "task_print_common.ftl" as c>
<@c.docHeader title="${modelHeader.taskTypeName!modelHeader.taskTypeCode!''}打印单" />
<@c.printHeader />

<#if sections?? && (sections?size > 0)>
    <#list sections as section>
        <#if section.type == "field">
            <table>
                <tr>
                    <td colspan="4" class="section-title">${section.title!'工单内容'}</td>
                </tr>
                <#assign fieldList = section.data />
                <#if fieldList?? && (fieldList?size > 0)>
                    <#list fieldList as field>
                        <#if field_index % 2 == 0>
                            <tr>
                                <td class="label">${field.label!''}</td>
                                <td class="value">${field.value!''}</td>
                                <#if (field_index + 1) lt fieldList?size>
                                    <td class="label">${fieldList[field_index + 1].label!''}</td>
                                    <td class="value">${fieldList[field_index + 1].value!''}</td>
                                <#else>
                                    <td class="label"></td>
                                    <td class="value"></td>
                                </#if>
                            </tr>
                        </#if>
                    </#list>
                </#if>
            </table>
        <#elseif section.type == "table">
            <#assign tableData = section.data />
            <table class="table-wrap">
                <thead>
                <tr>
                    <th colspan="${(tableData.headers?size)!1}" class="section-title">${section.title!'明细表'}</th>
                </tr>
                <tr>
                    <#list tableData.headers as header>
                        <th class="group-title">${header}</th>
                    </#list>
                </tr>
                </thead>
                <tbody>
                <#if tableData.rows?? && (tableData.rows?size > 0)>
                    <#list tableData.rows as row>
                        <tr>
                            <#list tableData.headers as header>
                                <td>${row[header]!''}</td>
                            </#list>
                        </tr>
                    </#list>
                <#else>
                    <tr>
                        <td colspan="${(tableData.headers?size)!1}">暂无数据</td>
                    </tr>
                </#if>
                </tbody>
            </table>
        </#if>
    </#list>
</#if>

<@c.approvalFooter />
<@c.docFooter />
