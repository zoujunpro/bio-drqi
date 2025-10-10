package com.bio.drqi.manage.seed;

import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SeedMapRspDTO {


    private SeedMapNode rootMap = new SeedMapNode();


    public void buildMap(String seedNum, String fatherSeedNum, String matherSeedNum) {
        if (rootMap.getName() == null) {
            rootMap.setName(seedNum);
            rootMap.buildChildren(fatherSeedNum, matherSeedNum);
        } else {
            SeedMapNode currentNode = findCurrentNode(seedNum, rootMap);
            if (currentNode == null) {
                throw new BusinessException("构建图谱失败，失败种子编号：" + seedNum);
            } else {
                currentNode.buildChildren(fatherSeedNum, matherSeedNum);
            }
        }

    }


    private SeedMapNode findCurrentNode(String seedNum, SeedMapNode seedMapNode) {
        if (seedNum.equals(seedMapNode.getName())) {
            return seedMapNode;
        } else {
            if (seedMapNode.getChildren() != null) {
                SeedMapNode currentNode = null;
                for (SeedMapNode childSeedMapNode : seedMapNode.getChildren()) {
                    currentNode = findCurrentNode(seedNum, childSeedMapNode);
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
        private List<SeedMapNode> children;

        public SeedMapNode(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public SeedMapNode() {
        }

        public void buildChildren(String fatherSeedNum, String matherSeedNum) {
            children = new ArrayList<>();
            if (StringUtils.isNotEmpty(fatherSeedNum)) {
                children.add(new SeedMapNode(fatherSeedNum, null));
            }
            if (StringUtils.isNotEmpty(matherSeedNum) && !fatherSeedNum.equals(matherSeedNum)) {
                children.add(new SeedMapNode(matherSeedNum, null));
            }
        }
    }
}


