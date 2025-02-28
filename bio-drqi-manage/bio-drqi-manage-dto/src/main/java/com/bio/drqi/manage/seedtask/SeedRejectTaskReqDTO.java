package com.bio.drqi.manage.seedtask;

import lombok.Data;

@Data
public class SeedRejectTaskReqDTO {
    private Integer taskId;

    private String reason;
}
