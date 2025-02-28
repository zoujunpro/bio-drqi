package com.bio.drqi.manage.seedtask;

import lombok.Data;

@Data
public class SeedRevokeTaskReqDTO {
    private Integer taskId;

    private String reason;
}
