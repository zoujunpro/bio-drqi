package com.bio.drqi.manage.board;

import lombok.Data;

@Data
public class VectorTaskListBoardReqDTO {

    private Integer projectId;

    private Integer userId;

    private String speciesCode;


    private String taskStatus;
}
