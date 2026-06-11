package icu.jogeen.fishbook.factory;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import icu.jogeen.fishbook.service.BookScanner;
import icu.jogeen.fishbook.service.BookScannerBuilder;
import icu.jogeen.fishbook.service.PersistentState;
import icu.jogeen.fishbook.ui.ReadUI;
import org.jetbrains.annotations.NotNull;

/**
 * @Author jogeen    （原作者，2020/6/24）
 * @Maintainer SagitTariuse  （适配 IDEA 2025.3.4，2026/06）
 * @OriginalRepo https://github.com/jogeen/FishBook
 * @ThisRepo     https://github.com/SagitTariuse/FishBook-X
 * @Description 适配 IntelliJ IDEA 2025.3.4 - ContentFactory.getInstance() 简化调用
 *
 * 详细 fork 声明见 service 包 NOTICE.md。
 */
public class ReadWindowFactory implements ToolWindowFactory {

    public static ReadUI readUI;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        //创建出 NoteListWindow 对象
        readUI = new ReadUI(project, toolWindow);
        //获取内容工厂的实例
        ContentFactory contentFactory = ContentFactory.getInstance();
        //获取用于 toolWindow 显示的内容
        Content content = contentFactory.createContent(readUI.getJcontent(), "", false);
        //给 toolWindow 设置内容
        toolWindow.getContentManager().addContent(content);
        BookScanner bookScanner = BookScannerBuilder.builder(null);
        if (bookScanner != null) {
            readUI.turnPage(PersistentState.getInstance().getPageNum());
        }
    }
}
