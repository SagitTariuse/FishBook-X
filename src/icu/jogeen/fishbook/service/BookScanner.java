package icu.jogeen.fishbook.service;

import java.util.List;

/**
 * @Author jogeen    （原作者，2020/6/24）
 * @Maintainer SagitTariuse  （新增 getChapters() 默认方法，2026/06）
 * @OriginalRepo https://github.com/jogeen/FishBook
 * @ThisRepo     https://github.com/SagitTariuse/FishBook-X
 * @Description
 *
 * 详细 fork 声明见同包 NOTICE.md。
 */
public interface BookScanner {
     String bookName();
     long getBookSize();
     long getTotalLines();
     List<String> getContentForPage(int page, int pageSize);

     /**
      * 获取章节列表
      * @return 章节列表（可能为空列表，表示没有识别到章节）
      */
     default List<Chapter> getChapters() {
         return java.util.Collections.emptyList();
     }
}
