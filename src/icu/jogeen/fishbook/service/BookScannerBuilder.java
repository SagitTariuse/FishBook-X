package icu.jogeen.fishbook.service;

import java.io.File;

/**
 * @Author jogeen    （原作者，2020/6/24）
 * @Maintainer SagitTariuse  （适配 IDEA 2025.3.4，2026/06）
 * @OriginalRepo https://github.com/jogeen/FishBook
 * @ThisRepo     https://github.com/SagitTariuse/FishBook-X
 * @Description 适配 IntelliJ IDEA 2025.3.4 - NotificationGroupManager 替代旧 API
 *
 * 详细 fork 声明见同包 NOTICE.md。
 */
public class BookScannerBuilder {

    private static BookScanner bookScaner = null;
    private static final PersistentState persistentState = PersistentState.getInstance();

    public static BookScanner builder(String bookPath) {
        if (bookScaner == null) {
            if (bookPath == null) {
                bookPath = persistentState.getBookPathText();
            }
            if (doBuild(bookPath)) return null;
        }
        return bookScaner;
    }

    public static BookScanner rebuild(String bookPath) {
        if (doBuild(bookPath)) return null;
        persistentState.setPageNum(1);
        return bookScaner;
    }

    private static boolean doBuild(String bookPath) {
        if (!checkPath(bookPath)) {
            return true;
        }
        bookScaner = new TxtBookScanner(bookPath);
        return false;
    }

    public static BookScanner getBookScaner() {
        return bookScaner;
    }

    private static boolean checkPath(String bookPath) {
        if (bookPath == null) {
            return false;
        }
        File file = new File(bookPath);
        return file.exists();
    }

}
