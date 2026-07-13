package com.bio.drqi.ai.common.util;

import com.bio.drqi.ai.common.constant.AiDocumentIndexConstant;
import com.bio.drqi.ai.common.enums.AiDocumentChunkTypeEnum;
import com.bio.drqi.ai.common.model.AiDocumentChunk;
import com.bio.drqi.ai.common.model.AiFileParseBlock;
import com.bio.drqi.ai.common.spi.AiTokenEstimator;
import com.bio.drqi.ai.common.spi.DefaultAiTokenEstimator;

import java.util.ArrayList;
import java.util.List;

/**
 * 文档分块工具。
 * 上线使用时不能只按固定字符数硬切，否则标题、段落、页码、Sheet 和表格行容易被截断，影响向量召回质量。
 *
 * 分片基本原理：
 * 1. 先保留文档结构，再控制 chunk 大小。
 *    RAG 检索不是简单把全文切成若干字符串，而是要尽量保留原文的语义边界。
 *    例如 PDF 的页码、Word 的标题和表格、Excel 的 Sheet 和行，这些信息会影响召回后的回答质量。
 *
 * 2. 解析层负责把不同文件类型转成 AiFileParseBlock。
 *    PDF：通常按页生成 block，pageNo 表示来源页；
 *    Word：按段落和表格生成 block，标题会尽量映射为 sectionTitle；
 *    Excel：按 Sheet 和行生成 block，sheetName 和 rowNo 表示来源位置；
 *    纯文本：按段落生成 block。
 *
 * 3. 分块层把 AiFileParseBlock 转换成内部 TextBlock。
 *    TextBlock 是分块算法内部使用的临时结构，用来统一处理纯文本入口和结构化入口。
 *    它会携带 text、sectionTitle、pageNo、sheetName、blockType 和 tokenCount。
 *
 * 4. 按 token 目标值把多个 TextBlock 合并成一个 AiDocumentChunk。
 *    如果每个段落、每一行都直接 embedding，chunk 会太碎，召回时上下文不足；
 *    如果整篇文档或整个章节直接 embedding，chunk 又太大，向量会表达得很宽泛。
 *    所以这里使用 CHUNK_TARGET_TOKEN_COUNT 控制理想大小，把相邻小块合并成适中的 chunk。
 *
 * 5. 单个 TextBlock 超过最大 token 限制时继续拆分。
 *    有些 PDF 页、Word 段落、Excel 单元格内容可能特别长，超过 CHUNK_MAX_TOKEN_COUNT 后，
 *    会进入 splitLargeBlock，优先按句号、分号、换行等自然边界切开，避免硬切断句子。
 *
 * 6. 相邻 chunk 保留少量重叠上下文。
 *    当答案刚好跨两个 chunk 时，如果完全不重叠，召回任意一个 chunk 都可能缺上下文。
 *    所以 flush 一个 chunk 后，会取尾部文本作为下一个 chunk 的开头上下文。
 *
 * 7. chunk 会保留来源元数据。
 *    最终生成的 AiDocumentChunk 不只包含 chunkText，还会保留 pageNo、sheetName、sectionTitle、chunkType、tokenCount。
 *    这些字段会写入 PGVector 表和 metadata，方便后续回答时说明“来源页码/来源 Sheet”，也方便排查召回质量。
 *
 * 8. 最终流程是：
 *    文件解析 -> 结构化块 AiFileParseBlock -> 内部 TextBlock -> token 合并/拆分 -> AiDocumentChunk -> embedding -> PGVector。
 *
 * 生产系统通常也是类似思路：
 * 先结构化解析，再语义分块，最后用 token 限制大小。
 * 不建议只按字符数切，因为中文、英文、表格、PDF 页码的语义边界都不一样。
 *
 * 当前版本没有引入模型官方 tokenizer，所以 estimateTokenCount 是近似估算。
 * 如果后续模型固定，比如固定使用通义、OpenAI 或 bge embedding，建议替换成对应 tokenizer，
 * 这样可以更准确地控制 embedding 输入长度。
 */
public final class AiDocumentChunkUtil {

    private static final AiTokenEstimator DEFAULT_TOKEN_ESTIMATOR = new DefaultAiTokenEstimator();

    private AiDocumentChunkUtil() {
    }

    public static List<AiDocumentChunk> split(String text) {
        return split(text, DEFAULT_TOKEN_ESTIMATOR);
    }

    public static List<AiDocumentChunk> split(String text, AiTokenEstimator tokenEstimator) {
        if (!hasText(text)) {
            return new ArrayList<AiDocumentChunk>();
        }
        // 兼容旧入口：如果调用方只有纯文本，就先按空行、标题、表格行推断结构，再进入统一分块流程。
        return splitBlocks(buildBlocks(normalize(text), tokenEstimator), tokenEstimator);
    }

    public static List<AiDocumentChunk> split(List<AiFileParseBlock> parseBlocks) {
        return split(parseBlocks, DEFAULT_TOKEN_ESTIMATOR);
    }

    public static List<AiDocumentChunk> split(List<AiFileParseBlock> parseBlocks, AiTokenEstimator tokenEstimator) {
        if (parseBlocks == null || parseBlocks.isEmpty()) {
            return new ArrayList<AiDocumentChunk>();
        }
        // 推荐入口：解析层已经识别出页码、Sheet、表格等结构信息，分块时可以完整保留下来。
        return splitBlocks(buildBlocks(parseBlocks, tokenEstimator), tokenEstimator);
    }

    /**
     * 近似估算 token 数。
     *
     * 为什么还要按 token 处理：
     * 1. embedding 模型都有最大输入长度，超过限制会报错或被截断；
     * 2. 单个 chunk 太长，会混入太多主题，向量表达会变“平均”，召回精度下降；
     * 3. 单个 chunk 太短，又会丢上下文，用户问题跨段落时召回不完整。
     *
     * 估算出 token 数后的处理方式：
     * 1. 单个 TextBlock 已经超过 CHUNK_MAX_TOKEN_COUNT：
     *    说明这个段落、表格行或页面文本本身就太大，需要进入 splitLargeBlock 做二次拆分。
     * 2. 当前 chunk + 下一个 TextBlock 超过 CHUNK_TARGET_TOKEN_COUNT：
     *    说明当前 chunk 已经接近理想大小，先 flush 成一个 AiDocumentChunk，
     *    再把下一个 TextBlock 放进新的 chunk。
     * 3. 当前 chunk + 下一个 TextBlock 没超过 CHUNK_TARGET_TOKEN_COUNT：
     *    继续合并，减少过碎 chunk，保留更完整的上下文。
     * 4. chunk 落库时保存 tokenCount：
     *    后续可以用来排查分片是否过大、过小，以及不同 embedding 模型是否需要调整阈值。
     *
     * 当前估算策略：
     * 中文、标点等非 ASCII 字符按 1 个 token 粗略计算；
     * 连续英文/数字按 1 个词粗略计算。
     * 这是工程上的保守近似，不等于模型真实 tokenizer。
     */
    public static int estimateTokenCount(String text) {
        return DEFAULT_TOKEN_ESTIMATOR.estimate(text);
    }

    private static List<AiDocumentChunk> splitBlocks(List<TextBlock> blocks, AiTokenEstimator tokenEstimator) {
        AiTokenEstimator estimator = resolveTokenEstimator(tokenEstimator);
        List<AiDocumentChunk> chunks = new ArrayList<AiDocumentChunk>();
        StringBuilder current = new StringBuilder();
        String currentSectionTitle = null;
        Integer currentPageNo = null;
        String currentSheetName = null;
        AiDocumentChunkTypeEnum currentType = null;

        for (TextBlock block : blocks) {
            if (!hasText(block.getText())) {
                continue;
            }
            if (block.getTokenCount() > AiDocumentIndexConstant.CHUNK_MAX_TOKEN_COUNT) {
                // 单个语义块已经超过模型输入建议上限时，先结束当前 chunk，再对这个大块做二次拆分。
                flushChunk(chunks, current, currentSectionTitle, currentPageNo, currentSheetName, currentType, estimator);
                currentSectionTitle = null;
                currentPageNo = null;
                currentSheetName = null;
                currentType = null;
                splitLargeBlock(chunks, block, estimator);
                continue;
            }

            int nextTokenCount = estimator.estimate(current.toString()) + block.getTokenCount();
            if (current.length() > 0 && nextTokenCount > AiDocumentIndexConstant.CHUNK_TARGET_TOKEN_COUNT) {
                // 当前 chunk 接近目标 token 数后就落盘成一个 chunk，同时取尾部文本作为下一个 chunk 的重叠上下文。
                String overlapText = flushChunk(chunks, current, currentSectionTitle, currentPageNo, currentSheetName, currentType, estimator);
                append(current, overlapText);
                currentSectionTitle = block.getSectionTitle();
                currentPageNo = block.getPageNo();
                currentSheetName = block.getSheetName();
                currentType = block.getType();
            }

            // 如果合并的多个块来自不同页或不同 Sheet，这里会把 pageNo/sheetName 置空，避免误标成单一来源。
            currentSectionTitle = mergeText(currentSectionTitle, block.getSectionTitle());
            currentPageNo = mergeInteger(currentPageNo, block.getPageNo());
            currentSheetName = mergeText(currentSheetName, block.getSheetName());
            currentType = mergeType(currentType, block.getType());
            append(current, formatBlock(block));
        }

        flushChunk(chunks, current, currentSectionTitle, currentPageNo, currentSheetName, currentType, estimator);
        renumber(chunks);
        return chunks;
    }

    /**
     * 把解析后的整篇纯文本先整理成较小的语义块。
     * 这里还不是最终入库的 chunk，主要目的是尽量保留文档结构：
     * 1. 空行表示一个段落结束；
     * 2. 标题行只更新当前章节，不直接作为正文块；
     * 3. 表格行会标记为 TABLE，后续写入 metadata，方便检索结果判断来源。
     */
    private static List<TextBlock> buildBlocks(String text, AiTokenEstimator tokenEstimator) {
        AiTokenEstimator estimator = resolveTokenEstimator(tokenEstimator);
        List<TextBlock> blocks = new ArrayList<TextBlock>();
        String[] lines = text.split("\n");
        String sectionTitle = null;
        String sheetName = null;
        StringBuilder paragraph = new StringBuilder();
        AiDocumentChunkTypeEnum paragraphType = AiDocumentChunkTypeEnum.TEXT;

        for (String rawLine : lines) {
            String line = rawLine == null ? "" : rawLine.trim();
            if (!hasText(line)) {
                // 空行表示当前段落结束，把已累计的段落保存成一个 TextBlock。
                flushBlock(blocks, paragraph, sectionTitle, null, sheetName, paragraphType, estimator);
                paragraphType = AiDocumentChunkTypeEnum.TEXT;
                continue;
            }

            if (line.startsWith("Sheet:")) {
                flushBlock(blocks, paragraph, sectionTitle, null, sheetName, paragraphType, estimator);
                sheetName = line.substring("Sheet:".length()).trim();
                sectionTitle = sheetName;
                paragraphType = AiDocumentChunkTypeEnum.TEXT;
                continue;
            }

            if (isHeading(line)) {
                // 标题用于标记后续内容所属章节，避免把标题和正文切散后丢失上下文。
                flushBlock(blocks, paragraph, sectionTitle, null, sheetName, paragraphType, estimator);
                sectionTitle = cleanHeading(line);
                paragraphType = AiDocumentChunkTypeEnum.TEXT;
                continue;
            }

            // Excel、Word 表格解析后通常带 tab 或 |，这里保留表格类型，后续可用于前端展示或检索调权。
            AiDocumentChunkTypeEnum lineType = isTableLine(line)
                    ? AiDocumentChunkTypeEnum.TABLE : AiDocumentChunkTypeEnum.TEXT;
            paragraphType = mergeType(paragraphType, lineType);
            append(paragraph, line);
        }
        // 文件最后一段可能没有空行结尾，需要在循环结束后补一次保存。
        flushBlock(blocks, paragraph, sectionTitle, null, sheetName, paragraphType, estimator);
        return blocks;
    }

    private static List<TextBlock> buildBlocks(List<AiFileParseBlock> parseBlocks, AiTokenEstimator tokenEstimator) {
        AiTokenEstimator estimator = resolveTokenEstimator(tokenEstimator);
        List<TextBlock> blocks = new ArrayList<TextBlock>();
        for (AiFileParseBlock parseBlock : parseBlocks) {
            if (parseBlock == null || !hasText(parseBlock.getText())) {
                continue;
            }
            TextBlock block = new TextBlock();
            block.setText(parseBlock.getText().trim());
            block.setSectionTitle(parseBlock.getSectionTitle());
            block.setPageNo(parseBlock.getPageNo());
            block.setSheetName(parseBlock.getSheetName());
            block.setType(resolveChunkType(parseBlock.getBlockType()));
            block.setTokenCount(estimator.estimate(block.getText()));
            blocks.add(block);
        }
        return blocks;
    }

    private static void splitLargeBlock(List<AiDocumentChunk> chunks, TextBlock block, AiTokenEstimator tokenEstimator) {
        String text = formatBlock(block);
        int maxChars = Math.max(200, AiDocumentIndexConstant.CHUNK_TARGET_TOKEN_COUNT * 2);
        int overlapChars = Math.max(0, AiDocumentIndexConstant.CHUNK_OVERLAP_TOKEN_COUNT * 2);
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(text.length(), start + maxChars);
            if (end < text.length()) {
                end = findSplitPoint(text, start, end);
            }
            String chunkText = text.substring(start, end).trim();
            if (chunkText.length() > 0) {
                AiDocumentChunk chunk = new AiDocumentChunk();
                chunk.setChunkText(chunkText);
                chunk.setSectionTitle(block.getSectionTitle());
                chunk.setPageNo(block.getPageNo());
                chunk.setSheetName(block.getSheetName());
                chunk.setChunkType(block.getType() == null
                        ? AiDocumentChunkTypeEnum.TEXT.getCode() : block.getType().getCode());
                chunk.setTokenCount(tokenEstimator.estimate(chunkText));
                chunks.add(chunk);
            }
            if (end >= text.length()) {
                break;
            }
            start = Math.max(end - overlapChars, start + 1);
        }
    }

    private static int findSplitPoint(String text, int start, int end) {
        for (int i = end - 1; i > start; i--) {
            char ch = text.charAt(i);
            if (ch == '\n' || ch == '。' || ch == '；' || ch == ';' || ch == '.') {
                return i + 1;
            }
        }
        return end;
    }

    private static String flushChunk(List<AiDocumentChunk> chunks, StringBuilder current,
                                     String sectionTitle, Integer pageNo, String sheetName,
                                     AiDocumentChunkTypeEnum chunkType, AiTokenEstimator tokenEstimator) {
        String chunkText = current.toString().trim();
        current.setLength(0);
        if (!hasText(chunkText)) {
            return "";
        }
        AiDocumentChunk chunk = new AiDocumentChunk();
        chunk.setChunkText(chunkText);
        chunk.setSectionTitle(sectionTitle);
        chunk.setPageNo(pageNo);
        chunk.setSheetName(sheetName);
        chunk.setChunkType(chunkType == null
                ? AiDocumentChunkTypeEnum.TEXT.getCode() : chunkType.getCode());
        chunk.setTokenCount(tokenEstimator.estimate(chunkText));
        chunks.add(chunk);
        return tailText(chunkText);
    }

    private static void flushBlock(List<TextBlock> blocks, StringBuilder paragraph, String sectionTitle,
                                   Integer pageNo, String sheetName, AiDocumentChunkTypeEnum paragraphType,
                                   AiTokenEstimator tokenEstimator) {
        String text = paragraph.toString().trim();
        paragraph.setLength(0);
        if (!hasText(text)) {
            return;
        }
        TextBlock block = new TextBlock();
        block.setText(text);
        block.setSectionTitle(sectionTitle);
        block.setPageNo(pageNo);
        block.setSheetName(sheetName);
        block.setType(paragraphType == null ? AiDocumentChunkTypeEnum.TEXT : paragraphType);
        block.setTokenCount(tokenEstimator.estimate(text));
        blocks.add(block);
    }

    private static String formatBlock(TextBlock block) {
        StringBuilder builder = new StringBuilder();
        if (hasText(block.getSheetName())) {
            builder.append("Sheet：").append(block.getSheetName()).append('\n');
        }
        if (block.getPageNo() != null) {
            builder.append("页码：").append(block.getPageNo()).append('\n');
        }
        if (hasText(block.getSectionTitle()) && !block.getText().startsWith(block.getSectionTitle())) {
            builder.append("章节：").append(block.getSectionTitle()).append('\n');
        }
        builder.append(block.getText());
        return builder.toString();
    }

    private static String tailText(String text) {
        int overlapChars = Math.max(0, AiDocumentIndexConstant.CHUNK_OVERLAP_TOKEN_COUNT * 2);
        if (overlapChars <= 0 || text.length() <= overlapChars) {
            return "";
        }
        return text.substring(text.length() - overlapChars).trim();
    }

    private static void append(StringBuilder builder, String text) {
        if (!hasText(text)) {
            return;
        }
        if (builder.length() > 0) {
            builder.append("\n\n");
        }
        builder.append(text.trim());
    }

    private static AiDocumentChunkTypeEnum mergeType(AiDocumentChunkTypeEnum left, AiDocumentChunkTypeEnum right) {
        if (left == null) {
            return right;
        }
        if (right == null || left == right) {
            return left;
        }
        return AiDocumentChunkTypeEnum.MIXED;
    }

    private static AiDocumentChunkTypeEnum resolveChunkType(String code) {
        if (AiDocumentChunkTypeEnum.TABLE.getCode().equals(code)) {
            return AiDocumentChunkTypeEnum.TABLE;
        }
        if (AiDocumentChunkTypeEnum.MIXED.getCode().equals(code)) {
            return AiDocumentChunkTypeEnum.MIXED;
        }
        return AiDocumentChunkTypeEnum.TEXT;
    }

    private static String mergeText(String left, String right) {
        if (!hasText(left)) {
            return right;
        }
        if (!hasText(right) || left.equals(right)) {
            return left;
        }
        return null;
    }

    private static Integer mergeInteger(Integer left, Integer right) {
        if (left == null) {
            return right;
        }
        if (right == null || left.equals(right)) {
            return left;
        }
        return null;
    }

    private static void renumber(List<AiDocumentChunk> chunks) {
        for (int i = 0; i < chunks.size(); i++) {
            chunks.get(i).setChunkNo(i + 1);
        }
    }

    private static AiTokenEstimator resolveTokenEstimator(AiTokenEstimator tokenEstimator) {
        return tokenEstimator == null ? DEFAULT_TOKEN_ESTIMATOR : tokenEstimator;
    }

    /**
     * 只统一不同系统的换行符，不压缩连续空行。
     * 连续空行是后续 buildBlocks 判断段落边界的重要依据，不能在这里合并掉。
     */
    private static String normalize(String text) {
        return text.replace("\r\n", "\n").replace('\r', '\n').trim();
    }

    private static boolean isHeading(String line) {
        if (!hasText(line)) {
            return false;
        }
        if (line.startsWith("#")) {
            return true;
        }
        return line.matches("^(第.{1,20}[章节部分篇]|[一二三四五六七八九十]+[、.．]|\\d+(\\.\\d+)*[、.．])\\s*.+");
    }

    private static String cleanHeading(String line) {
        if (line.startsWith("#")) {
            return line.replaceFirst("^#+", "").trim();
        }
        return line;
    }

    private static boolean isTableLine(String line) {
        return line.indexOf('\t') >= 0 || line.indexOf('|') >= 0;
    }

    private static boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    private static class TextBlock {

        private String text;

        private String sectionTitle;

        private Integer pageNo;

        private String sheetName;

        private AiDocumentChunkTypeEnum type;

        private int tokenCount;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getSectionTitle() {
            return sectionTitle;
        }

        public void setSectionTitle(String sectionTitle) {
            this.sectionTitle = sectionTitle;
        }

        public Integer getPageNo() {
            return pageNo;
        }

        public void setPageNo(Integer pageNo) {
            this.pageNo = pageNo;
        }

        public String getSheetName() {
            return sheetName;
        }

        public void setSheetName(String sheetName) {
            this.sheetName = sheetName;
        }

        public AiDocumentChunkTypeEnum getType() {
            return type;
        }

        public void setType(AiDocumentChunkTypeEnum type) {
            this.type = type;
        }

        public int getTokenCount() {
            return tokenCount;
        }

        public void setTokenCount(int tokenCount) {
            this.tokenCount = tokenCount;
        }
    }
}
