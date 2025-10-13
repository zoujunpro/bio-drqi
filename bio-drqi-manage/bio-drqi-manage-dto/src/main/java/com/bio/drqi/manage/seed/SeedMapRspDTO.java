package com.bio.drqi.manage.seed;

import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class SeedMapRspDTO {


    private SeedMapNode rootMap = new SeedMapNode();


    public void buildMap(SeedMapDTO currentSeed, SeedMapDTO fatherSeed, SeedMapDTO matherSeed) {
        if (rootMap.getName() == null) {
            rootMap.seedNode(currentSeed);
            rootMap.buildChildren(fatherSeed, matherSeed);
        } else {
            SeedMapNode currentNode = findCurrentNode(currentSeed, rootMap);
            if (currentNode == null) {
                throw new BusinessException("构建图谱失败，失败种子编号：" + currentSeed.seedNum);
            } else {
                currentNode.buildChildren(fatherSeed, matherSeed);
            }
        }

    }


    private SeedMapNode findCurrentNode(SeedMapDTO seedMapDTO, SeedMapNode seedMapNode) {
        if (StringUtils.isNotEmpty(seedMapDTO.seedNum) && StringUtils.isNotEmpty(seedMapNode.name) && seedMapDTO.seedNum.equals(seedMapNode.getName())) {
            return seedMapNode;
        } else {
            if (seedMapNode.getChildren() != null) {
                SeedMapNode currentNode = null;
                for (SeedMapNode childSeedMapNode : seedMapNode.getChildren()) {
                    currentNode = findCurrentNode(seedMapDTO, childSeedMapNode);
                    if (currentNode != null) {
                        return currentNode;
                    }
                }
            }
            return null;
        }
    }

    /**
     * 一个种子图谱节点
     */
    @Data
    public static class SeedMapNode {
        private String name;
        private String value;
        private List<SeedMapNode> children = new ArrayList<>();

        public SeedMapNode(SeedMapDTO seedMapDTO) {
            this.name = seedMapDTO.seedNum;
            this.value = JSONUtil.toJsonStr(seedMapDTO);
        }

        public SeedMapNode() {
        }

        public void seedNode(SeedMapDTO seedMapDTO) {
            this.name = seedMapDTO.seedNum;
            this.value = JSONUtil.toJsonStr(seedMapDTO);
        }

        public void buildChildren(SeedMapDTO fatherSeed, SeedMapDTO matherSeed) {
            //自交只有一个亲本
            if (Objects.nonNull(fatherSeed) && Objects.nonNull(matherSeed) && fatherSeed.seedNum.equals(matherSeed.seedNum)) {
                fatherSeed.setParentType("parent");
                children.add(new SeedMapNode(fatherSeed));
            } else {
                if (Objects.nonNull(fatherSeed)) {
                    fatherSeed.setParentType("father");
                    children.add(new SeedMapNode(fatherSeed));
                } else {
                    fatherSeed = new SeedMapDTO();
                    fatherSeed.setSeedNum("");
                    fatherSeed.setParentType("father");
                    children.add(new SeedMapNode(fatherSeed));
                }
                if (Objects.nonNull(matherSeed)) {
                    matherSeed.setParentType("mather");
                    children.add(new SeedMapNode(matherSeed));
                } else {
                    matherSeed = new SeedMapDTO();
                    matherSeed.setSeedNum("");
                    matherSeed.setParentType("mather");
                    children.add(new SeedMapNode(matherSeed));
                }
            }
        }
    }


    @Data
    public static class SeedMapDTO {
        private String seedNum;
        private String vectorTaskCode;
        private String generation;
        private String pollinationMethod;
        private String parentType;
        private String breedName;
    }

}


