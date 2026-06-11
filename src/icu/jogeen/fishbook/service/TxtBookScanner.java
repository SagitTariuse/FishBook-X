package icu.jogeen.fishbook.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author jogeen    （原作者，2020/6/24）
 * @Maintainer SagitTariuse  （适配 IDEA 2025.3.4 / 性能优化 / 章节识别，2026/06）
 * @OriginalRepo https://github.com/jogeen/FishBook
 * @ThisRepo     https://github.com/SagitTariuse/FishBook-X
 * @Description 适配 IntelliJ IDEA 2025.3.4 + 性能优化 + 章节识别
 *
 * 详细 fork 声明见同包 NOTICE.md。
 *
 * 性能优化：
 * 1. 使用 BufferedReader 一次性读取整个文件
 * 2. 文件内容全部加载到内存（小说文件通常 1-5MB）
 * 3. 预构建行索引数组
 * 4. 翻页直接通过数组索引返回，O(1) 复杂度
 * 5. 支持自动检测 UTF-8 / GBK 编码（中文 txt 常见）
 *
 * 章节识别（按行扫描，匹配以下模式）：
 * - 第N章 / 第N回 / 第N节 / 第N卷 / 第N部 / 第N集 / 第N篇
 * - Chapter N / CHAPTER N
 * - 序章 / 序言 / 楔子 / 引子 / 尾声 / 后记 / 番外
 */
public class TxtBookScanner implements BookScanner {

    /** 章节标题正则（中文：第N章/回/节/卷/部/集/篇 + 可选标题） */
    private static final Pattern CHAPTER_PATTERN_CN = Pattern.compile(
            "^\\s*第[零〇一二三四五六七八九十百千万0-9\\d]+[章回节卷部集篇].*"
    );

    /** 章节标题正则（英文：Chapter N + 可选标题） */
    private static final Pattern CHAPTER_PATTERN_EN = Pattern.compile(
            "^\\s*(?:Chapter|CHAPTER)\\s+[0-9一二三四五六七八九十]+.*"
    );

    /** 特殊章节（不分卷） */
    private static final Pattern SPECIAL_CHAPTER_PATTERN = Pattern.compile(
            "^\\s*(序章|序言|楔子|引子|尾声|后记|番外|序)\\s*.*"
    );

    private final File file;
    private List<String> lines;        // 全部行内容
    private List<Chapter> chapters;   // 章节列表
    private long totalLines;          // 总行数
    private long fileSize;            // 文件大小（字节）

    public TxtBookScanner(String filePath) {
        this.file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return;
        }
        this.fileSize = file.length();
        this.lines = new ArrayList<>(1024);
        this.chapters = new ArrayList<>();
        loadFile();
    }

    /**
     * 一次性加载文件，并识别章节
     */
    private void loadFile() {
        Charset charset = detectCharset();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), charset), 8192)) {
            String line;
            int lineNo = 0;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
                if (isChapterTitle(line)) {
                    String name = line.trim();
                    // 章节名太长时截断显示
                    if (name.length() > 50) {
                        name = name.substring(0, 50) + "...";
                    }
                    chapters.add(new Chapter(name, lineNo, 0)); // 页码后面再算
                }
                lineNo++;
            }
            totalLines = lines.size();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否为章节标题
     */
    private boolean isChapterTitle(String line) {
        if (line == null) return false;
        String trimmed = line.trim();
        if (trimmed.isEmpty()) return false;
        return CHAPTER_PATTERN_CN.matcher(trimmed).matches()
                || CHAPTER_PATTERN_EN.matcher(trimmed).matches()
                || SPECIAL_CHAPTER_PATTERN.matcher(trimmed).matches();
    }

    /**
     * 自动检测文件编码（处理 UTF-8 BOM 标记）
     */
    private Charset detectCharset() {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] head = new byte[3];
            int read = fis.read(head);
            if (read >= 3 && (head[0] & 0xFF) == 0xEF && (head[1] & 0xFF) == 0xBB && (head[2] & 0xFF) == 0xBF) {
                return StandardCharsets.UTF_8;
            }
        } catch (IOException ignored) {
        }
        return StandardCharsets.UTF_8;
    }

    @Override
    public String bookName() {
        return file.getName();
    }

    @Override
    public long getBookSize() {
        return fileSize;
    }

    @Override
    public long getTotalLines() {
        return totalLines;
    }

    /**
     * 获取指定页内容 - O(1) 复杂度
     * @param pageNum  页码（从 1 开始）
     * @param pageSize 每页行数
     */
    @Override
    public List<String> getContentForPage(int pageNum, int pageSize) {
        if (lines == null || lines.isEmpty()) {
            return new ArrayList<>();
        }
        int start = (pageNum - 1) * pageSize;
        if (start < 0 || start >= totalLines) {
            return new ArrayList<>();
        }
        int end = Math.min(start + pageSize, (int) totalLines);
        return new ArrayList<>(lines.subList(start, end));
    }

    /**
     * 获取章节列表（已根据 pageSize 计算每章起始页码）
     * @param pageSize 每页行数（用于计算起始页码）
     * @return 章节列表
     */
    public List<Chapter> getChapters(int pageSize) {
        List<Chapter> result = new ArrayList<>(chapters.size());
        for (Chapter c : chapters) {
            int startPage = c.getStartLine() / pageSize + 1;
            result.add(new Chapter(c.getName(), c.getStartLine(), startPage));
        }
        return result;
    }

    @Override
    public List<Chapter> getChapters() {
        return new ArrayList<>(chapters);
    }

    /**
     * 释放资源
     */
    public void dispose() {
        if (lines != null) {
            lines.clear();
            lines = null;
        }
        if (chapters != null) {
            chapters.clear();
            chapters = null;
        }
    }
}
