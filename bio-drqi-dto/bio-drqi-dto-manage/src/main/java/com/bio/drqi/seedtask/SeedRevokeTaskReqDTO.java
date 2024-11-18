package com.bio.drqi.seedtask;

import lombok.Data;

@Data
public class SeedRevokeTaskReqDTO {
    private Integer taskId;

    private String reason;
}
