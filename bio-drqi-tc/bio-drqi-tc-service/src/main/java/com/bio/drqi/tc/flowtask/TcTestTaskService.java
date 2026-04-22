package com.bio.drqi.tc.flowtask;

import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.flow.dto.BioHtmlModelDTO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service("tc_test_task_apply")
public class TcTestTaskService  extends AbstractTcBaseTaskService{
    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {


    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {

    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }

    @Override
    public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb) {
        return Collections.emptyList();
    }
}
