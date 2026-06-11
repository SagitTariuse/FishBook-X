package icu.jogeen.fishbook.service;

/**
 * 章节信息
 *
 * <p>本类为 FishBook-X 新增（原项目 <https://github.com/jogeen/FishBook> 中不存在）。</p>
 * <p>详细 fork 声明见同包 NOTICE.md。</p>
 *
 * @Maintainer SagitTariuse
 * @ThisRepo   https://github.com/SagitTariuse/FishBook-X
 */
public class Chapter {
    /** 章节名称（去除空白后的纯文本） */
    private final String name;

    /** 章节起始行号（从 0 开始） */
    private final int startLine;

    /** 章节起始页码（从 1 开始，根据 pageSize 计算） */
    private final int startPage;

    public Chapter(String name, int startLine, int startPage) {
        this.name = name;
        this.startLine = startLine;
        this.startPage = startPage;
    }

    public String getName() {
        return name;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getStartPage() {
        return startPage;
    }

    @Override
    public String toString() {
        return name;
    }
}
