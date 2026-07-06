package com.bio.drqi.ai.dto.admin;

import lombok.Data;

@Data
public class AiApiSyncResultDTO {

    private Integer scannedApiCount = 0;

    private Integer insertedApiCount = 0;

    private Integer updatedApiCount = 0;

    private Integer insertedParamCount = 0;

    private Integer updatedParamCount = 0;
}
