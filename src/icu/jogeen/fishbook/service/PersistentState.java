package icu.jogeen.fishbook.service;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @Author jogeen    （原作者，2020/6/24）
 * @Maintainer SagitTariuse  （适配 IDEA 2025.3.4 / Light Service 改造，2026/06）
 * @OriginalRepo https://github.com/jogeen/FishBook
 * @ThisRepo     https://github.com/SagitTariuse/FishBook-X
 * @Description 适配 IntelliJ IDEA 2025.3.4 - 使用 Light Service
 *
 * 详细 fork 声明见同包 NOTICE.md。
 */
@State(
        name = "PersistentState",
        storages = {@Storage(
                value = "fish-book.xml"
        )}
)
public final class PersistentState implements PersistentStateComponent<Element> {

    private String bookPathText;

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    /** 上次阅读的章节名（用于重启后恢复） */
    private String lastChapterName;

    public PersistentState() {
    }

    /**
     * 使用 ApplicationManager.getService() 替代已弃用的 ServiceManager.getService()
     * 在 2025.3 中推荐用这种方式获取 Light Service
     */
    public static PersistentState getInstance() {
        return ApplicationManager.getApplication().getService(PersistentState.class);
    }

    @Nullable
    @Override
    public Element getState() {
        Element element = new Element("PersistentState");
        if (bookPathText != null) {
            element.setAttribute("bookPathText", bookPathText);
        }
        element.setAttribute("pageNum", String.valueOf(pageNum));
        element.setAttribute("pageSize", String.valueOf(pageSize));
        if (lastChapterName != null) {
            element.setAttribute("lastChapterName", lastChapterName);
        }
        return element;
    }

    @Override
    public void loadState(@NotNull Element element) {
        this.bookPathText = element.getAttributeValue("bookPathText");
        String pageNumStr = element.getAttributeValue("pageNum");
        this.pageNum = pageNumStr == null ? pageNum : Integer.valueOf(pageNumStr);

        String pageSizeStr = element.getAttributeValue("pageSize");
        this.pageSize = pageSizeStr == null ? pageSize : Integer.valueOf(pageSizeStr);

        this.lastChapterName = element.getAttributeValue("lastChapterName");
    }

    public String getBookPathText() {
        return bookPathText;
    }

    public PersistentState setBookPathText(String bookPathText) {
        this.bookPathText = bookPathText;
        return this;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public PersistentState setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
        return this;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public PersistentState setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public String getLastChapterName() {
        return lastChapterName;
    }

    public PersistentState setLastChapterName(String lastChapterName) {
        this.lastChapterName = lastChapterName;
        return this;
    }
}
