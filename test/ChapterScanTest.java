package icu.jogeen.fishbook.test;

import icu.jogeen.fishbook.service.Chapter;
import icu.jogeen.fishbook.service.TxtBookScanner;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 章节识别回归测试（FishBook-X 新增，非原项目内容）。
 *
 * <p>用法：java icu.jogeen.fishbook.test.ChapterScanTest [小说路径]</p>
 *
 * <p>背景：2026-09 修复《凡人》目录下拉框从第 2000 章起整片消失的问题。
 * 根因是 TxtBookScanner 的中文数字字符集缺少「两」，而该书第 1999 章之后的
 * 标题由「第一千九百九十九章」改写成「第两千章」，导致其后 447 章全部匹配失败。</p>
 *
 * <p>分两部分：<br>
 *   1. 格式测试 —— 反射调用真实的 isChapterTitle，覆盖各种章节写法，不依赖任何书籍；<br>
 *   2. 集成测试 —— 用真实书籍跑真实的章节扫描，断言该书实际存在的章节能被识别。</p>
 *
 * @Maintainer SagitTariuse
 * @ThisRepo   https://github.com/SagitTariuse/FishBook-X
 */
public class ChapterScanTest {

    private static int failures = 0;

    public static void main(String[] args) {
        String path = args.length > 0 ? args[0] : "D:/18375/Documents/book/凡人.txt";

        formatTests();
        System.out.println();
        integrationTest(path);
        System.exit(summary());
    }

    // ---------------------------------------------------------------- 格式测试

    /** 必须是章节标题 */
    private static final String[] SHOULD_MATCH = {
            "第一章",
            "第一章 山边小村",
            "第一千九百九十九章 黑日",
            "第两千章 涅盘圣体",          // 含「两」—— 本次 bug 的真实触发点
            "第两百零三章",              // 仅含「两」
            "第两千零三十八章",          // 「两」+「零」组合
            "第一千零一章",
            "第一回", "第一节", "第一卷", "第一部", "第一集", "第一篇",
            "Chapter 1", "CHAPTER 12", "Chapter 1999",
            "序章", "序言", "楔子", "引子", "尾声", "后记", "番外", "序",
            "　　第三十二章 标题",        // 全角缩进
            "第 2000 章 标题",           // 数字与「章」之间有空格
    };

    /** 不能是章节标题（正文、目录残留等） */
    private static final String[] SHOULD_NOT_MATCH = {
            "",
            "   ",
            "　　那金色光球看似不大，但是方一出手之后，立刻光芒一闪。",   // 正文缩进行
            "韩立心中一动，随即盘膝坐下，进入了入定状态。",
            "第",                        // 只有「第」
            "第一",                      // 有数字但缺量词（章/回/卷…）
            "1999",                      // 纯数字
            "Chapter",                   // 只有 Chapter 没有编号
    };

    private static void formatTests() {
        System.out.println("=== 格式测试 ===");

        Method m = resolveIsChapterTitle();
        if (m == null) {
            failures++;
            System.out.println("FAIL  找不到 TxtBookScanner.isChapterTitle()");
            return;
        }

        TxtBookScanner probe = new TxtBookScanner("");
        for (String line : SHOULD_MATCH) {
            boolean actual = invoke(m, probe, line);
            report(actual, line, true);
        }
        for (String line : SHOULD_NOT_MATCH) {
            boolean actual = invoke(m, probe, line);
            report(actual, line, false);
        }
    }

    private static Method resolveIsChapterTitle() {
        try {
            Method m = TxtBookScanner.class.getDeclaredMethod("isChapterTitle", String.class);
            m.setAccessible(true);
            return m;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static boolean invoke(Method m, TxtBookScanner probe, String line) {
        try {
            return (Boolean) m.invoke(probe, line);
        } catch (Exception e) {
            failures++;
            System.out.println("FAIL  调用异常 " + line);
            return false;
        }
    }

    private static void report(boolean actual, String line, boolean expected) {
        String shown = line.trim().isEmpty() ? "(空白行)" : line.trim();
        if (actual == expected) {
            System.out.println("PASS  " + (expected ? "识别为章节  " : "识别为正文  ") + "「" + shown + "」");
        } else {
            System.out.println("FAIL  「" + shown + "」应" + (expected ? "是" : "不是") + "章节，实际"
                    + (actual ? "是" : "不是"));
            failures++;
        }
    }

    // ------------------------------------------------------------ 集成测试

    /** 《凡人》中真实存在的章节，按出现顺序 */
    private static final String[] REAL_CHAPTERS = {
            "第一章",
            "第一章 山边小村",
            "第一千九百九十九章 黑日",
            "第两千章 涅盘圣体",          // 修复点之后的第一章
            "第两千三十八章",            // 深入「两」区段
            "第十一卷 真仙降世",          // 用户报告的「幸存者」之一
    };

    private static void integrationTest(String path) {
        System.out.println("=== 集成测试：" + path + " ===");

        TxtBookScanner scanner = new TxtBookScanner(path);
        List<Chapter> chapters = scanner.getChapters();

        System.out.println("总行数  : " + scanner.getTotalLines());
        System.out.println("识别章节: " + chapters.size());

        if (chapters.isEmpty()) {
            System.out.println("FAIL  一章都没识别到");
            failures++;
            return;
        }

        for (String name : REAL_CHAPTERS) {
            find(chapters, name);
        }

        // 末章必须晚于「第十一卷 真仙降世」，证明扫描没有在中途断掉
        Chapter last = chapters.get(chapters.size() - 1);
        System.out.println("末章    : " + last.getName() + " (line " + last.getStartLine() + ")");
        if (last.getStartLine() <= 125898) {
            System.out.println("FAIL  末章停在「第十一卷 真仙降世」之前，扫描提前中断");
            failures++;
        }

        scanner.dispose();
    }

    private static void find(List<Chapter> chapters, String name) {
        for (Chapter c : chapters) {
            if (c.getName().startsWith(name)) {
                System.out.println("PASS  识别到「" + name + "」 (line " + c.getStartLine() + ")");
                return;
            }
        }
        System.out.println("FAIL  未能识别「" + name + "」");
        failures++;
    }

    public static int summary() {
        System.out.println();
        if (failures == 0) {
            System.out.println("=== ALL PASS ===");
            return 0;
        }
        System.out.println("=== " + failures + " FAILED ===");
        return 1;
    }
}
