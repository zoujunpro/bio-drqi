package com.bio.drqi.document.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.document.domain.DocCategoryInfo;
import com.bio.drqi.document.domain.DocFileInfo;
import com.bio.drqi.document.dto.DocumentCategorySaveDTO;
import com.bio.drqi.document.mapper.DocCategoryInfoMapper;
import com.bio.drqi.document.mapper.DocFileInfoMapper;
import org.springframework.beans.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DocumentCategoryService {

    @Resource
    private DocCategoryInfoMapper docCategoryInfoMapper;

    @Resource
    private DocFileInfoMapper docFileInfoMapper;

    @Resource
    private DocumentIdentityService documentIdentityService;

    public List<DocCategoryInfo> list(String categoryType) {
        Long userId = documentIdentityService.currentUserId();
        return docCategoryInfoMapper.selectList(new LambdaQueryWrapper<DocCategoryInfo>()
                .eq(DocCategoryInfo::getDeleted, "N")
                .eq(StringUtils.isNotBlank(categoryType), DocCategoryInfo::getCategoryType, categoryType)
                .eq("PERSONAL".equals(categoryType), DocCategoryInfo::getCreateBy, userId)
                .orderByAsc(DocCategoryInfo::getSortNum)
                .orderByDesc(DocCategoryInfo::getId));
    }

    public Map<Long, Long> countDocuments(String categoryType) {
        QueryWrapper<DocFileInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("category_id AS categoryId", "COUNT(1) AS total")
                .eq("deleted", "N")
                .isNotNull("category_id")
                .eq(StringUtils.isNotBlank(categoryType), "space_type", categoryType)
                .groupBy("category_id");
        return docFileInfoMapper.selectMaps(queryWrapper).stream()
                .filter(item -> item.get("categoryId") != null)
                .collect(Collectors.toMap(
                        item -> Long.valueOf(String.valueOf(item.get("categoryId"))),
                        item -> Long.valueOf(String.valueOf(item.get("total"))),
                        Long::sum
                ));
    }

    @Transactional(rollbackFor = Exception.class)
    public Long save(DocumentCategorySaveDTO dto) {
        DocCategoryInfo entity = new DocCategoryInfo();
        BeanUtils.copyProperties(dto, entity);
        Date now = new Date();
        Long userId = documentIdentityService.currentUserId();
        if (entity.getId() == null) {
            entity.setCreateBy(userId);
            entity.setCreateTime(now);
            entity.setDeleted("N");
            entity.setStatus(1);
            entity.setCategoryType(StringUtils.defaultIfBlank(entity.getCategoryType(), "ENTERPRISE"));
            entity.setInheritPermission(StringUtils.defaultIfBlank(entity.getInheritPermission(), "Y"));
            entity.setManagerScope(StringUtils.defaultIfBlank(entity.getManagerScope(), "TREE"));
            if (entity.getParentId() == null) {
                entity.setParentId(0L);
            }
            docCategoryInfoMapper.insert(entity);
        } else {
            entity.setUpdateBy(userId);
            entity.setUpdateTime(now);
            docCategoryInfoMapper.updateById(entity);
        }
        return entity.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Long childCount = docCategoryInfoMapper.selectCount(new LambdaQueryWrapper<DocCategoryInfo>()
                .eq(DocCategoryInfo::getParentId, id)
                .eq(DocCategoryInfo::getDeleted, "N"));
        if (childCount != null && childCount > 0) {
            throw new BusinessException("请先删除子目录");
        }
        Long fileCount = docFileInfoMapper.selectCount(new LambdaQueryWrapper<DocFileInfo>()
                .eq(DocFileInfo::getCategoryId, id)
                .eq(DocFileInfo::getDeleted, "N"));
        if (fileCount != null && fileCount > 0) {
            throw new BusinessException("目录下存在文档，不能删除");
        }
        DocCategoryInfo entity = new DocCategoryInfo();
        entity.setId(id);
        entity.setDeleted("Y");
        entity.setUpdateBy(documentIdentityService.currentUserId());
        entity.setUpdateTime(new Date());
        docCategoryInfoMapper.updateById(entity);
    }
}
