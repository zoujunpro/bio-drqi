<#import "task_print_common.ftl" as c>
<@c.docHeader title="${taskTypeName!taskTypeCode!''}打印案例" />
<@c.printHeader />

<table>
    <tr>
        <td colspan="4" class="section-title">工单内容</td>
    </tr>
    <tr>
        <td class="label">请假人</td>
        <td class="value">${formData.applicant!''}</td>
        <td class="label">主体单位</td>
        <td class="value">${formData.company!''}</td>
    </tr>
    <tr>
        <td class="label">假期类型</td>
        <td class="value">${formData.leaveType!''}</td>
        <td class="label">请假事由</td>
        <td class="value">${formData.reason!''}</td>
    </tr>
    <tr>
        <td class="label">开始时间</td>
        <td class="value">${formData.startTime!''}</td>
        <td class="label">结束时间</td>
        <td class="value">${formData.endTime!''}</td>
    </tr>
    <tr>
        <td class="label">时长</td>
        <td class="value">${formData.duration!''}</td>
        <td class="label">备注</td>
        <td class="value">${formData.remark!''}</td>
    </tr>
    <tr>
        <td class="label">补充说明</td>
        <td colspan="3" class="full-value">${formData.description!''}</td>
    </tr>
</table>

<@c.approvalFooter />
<@c.docFooter />
