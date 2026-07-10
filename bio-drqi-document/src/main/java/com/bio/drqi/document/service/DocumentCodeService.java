package com.bio.drqi.document.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bio.drqi.document.domain.DocCodeSequence;
import com.bio.drqi.document.mapper.DocCodeSequenceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class DocumentCodeService {

    @Resource
    private DocCodeSequenceMapper docCodeSequenceMapper;

    @Transactional(rollbackFor = Exception.class)
    public String nextCode(String categoryCode) {
        String normalizedCategory = categoryCode == null || categoryCode.trim().isEmpty() ? "DOC" : categoryCode.trim().toUpperCase();
        String day = new SimpleDateFormat("yyyyMMdd").format(new Date());
        DocCodeSequence sequence = docCodeSequenceMapper.selectOne(new LambdaQueryWrapper<DocCodeSequence>()
                .eq(DocCodeSequence::getCategoryCode, normalizedCategory)
                .eq(DocCodeSequence::getYearMonth, day)
                .last("limit 1"));
        if (sequence == null) {
            sequence = new DocCodeSequence();
            sequence.setCategoryCode(normalizedCategory);
            sequence.setYearMonth(day);
            sequence.setCurrentSeq(1);
            sequence.setCreateTime(new Date());
            sequence.setUpdateTime(new Date());
            docCodeSequenceMapper.insert(sequence);
        } else {
            sequence.setCurrentSeq(sequence.getCurrentSeq() == null ? 1 : sequence.getCurrentSeq() + 1);
            sequence.setUpdateTime(new Date());
            docCodeSequenceMapper.updateById(sequence);
        }
        return normalizedCategory + "-" + day + "-" + String.format("%04d", sequence.getCurrentSeq());
    }
}
