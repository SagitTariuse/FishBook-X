package icu.jogeen.fishbook.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageDialogBuilder;
import com.intellij.openapi.wm.ToolWindow;
import icu.jogeen.fishbook.service.BookScanner;
import icu.jogeen.fishbook.service.BookScannerBuilder;
import icu.jogeen.fishbook.service.Chapter;
import icu.jogeen.fishbook.service.PersistentState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * @Author jogeen    （原作者，2020/6/24）
 * @Maintainer SagitTariuse  （改写为纯代码 UI / 新增章节选择，2026/06）
 * @OriginalRepo https://github.com/jogeen/FishBook
 * @ThisRepo     https://github.com/SagitTariuse/FishBook-X
 * @Description 适配 IntelliJ IDEA 2025.3.4 - 纯代码构建 UI + 章节选择功能
 *
 * 详细 fork 声明见 service 包 NOTICE.md。
 */
public class ReadUI {

    private JPanel contentPanel;
    private JButton btnPre;
    private JButton btnNext;
    private JTextField tfPageNum;
    private JButton btnJump;
    private JButton btnFirst;
    private JButton btnLast;
    private JTextPane txContetnt;
    private JLabel labTotalPages;
    private JLabel labBookName;
    private JLabel labBookNameLabel;
    private JLabel labPageLabel;
    private JLabel labChapterLabel;
    private JComboBox<Chapter> cbChapter;
    private Long totalPage;
    private final PersistentState persistentState = PersistentState.getInstance();

    private void initBookScanner() {
        BookScanner scanner = BookScannerBuilder.getBookScaner();
        if (scanner == null) {
            scanner = BookScannerBuilder.builder(null);
            if (scanner == null) {
                MessageDialogBuilder.yesNo("操作结果", "请先配置图书路径").show();
                return;
            }
        }
        totalPage = scanner.getTotalLines() % persistentState.getPageSize() == 0
                ? scanner.getTotalLines() / persistentState.getPageSize()
                : scanner.getTotalLines() / persistentState.getPageSize() + 1;
        labTotalPages.setText("" + totalPage);
        labBookName.setText(scanner.bookName());
        loadChapters();
    }

    /**
     * 加载章节列表到下拉框
     */
    private void loadChapters() {
        BookScanner scanner = BookScannerBuilder.getBookScaner();
        if (scanner == null) {
            return;
        }
        // 获取带页码的章节列表
        List<Chapter> chapters = ((icu.jogeen.fishbook.service.TxtBookScanner) scanner)
                .getChapters(persistentState.getPageSize());

        cbChapter.removeAllItems();
        if (chapters.isEmpty()) {
            // 没识别到章节时显示提示
            cbChapter.addItem(new Chapter("（未识别到章节）", 0, 1));
            cbChapter.setEnabled(false);
            return;
        }
        cbChapter.setEnabled(true);
        for (Chapter c : chapters) {
            cbChapter.addItem(c);
        }

        // 恢复上次阅读的章节
        String lastChapter = persistentState.getLastChapterName();
        if (lastChapter != null) {
            for (int i = 0; i < chapters.size(); i++) {
                if (chapters.get(i).getName().equals(lastChapter)) {
                    cbChapter.setSelectedIndex(i);
                    return;
                }
            }
        }
        // 默认选第一个章节
        if (!chapters.isEmpty()) {
            cbChapter.setSelectedIndex(0);
        }
    }

    public ReadUI(Project project, ToolWindow toolWindow) {
        // 程序化构建 UI
        contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // 顶部信息栏
        JPanel topPanel = new JPanel(new BorderLayout(5, 0));
        JPanel bookInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        labBookNameLabel = new JLabel("当前书目：");
        labBookName = new JLabel("");
        bookInfoPanel.add(labBookNameLabel);
        bookInfoPanel.add(labBookName);
        topPanel.add(bookInfoPanel, BorderLayout.NORTH);

        // 章节选择栏
        JPanel chapterPanel = new JPanel(new BorderLayout(5, 0));
        chapterPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        labChapterLabel = new JLabel("章节：");
        cbChapter = new JComboBox<>();
        cbChapter.setMaximumRowCount(15); // 一次显示 15 个章节
        // 限制下拉框显示宽度（约 15 个中文字符 ≈ 210px）
        // 注意：下拉弹窗中仍会显示完整章节名
        Dimension fixedSize = new Dimension(210, cbChapter.getPreferredSize().height);
        cbChapter.setPreferredSize(fixedSize);
        cbChapter.setMaximumSize(fixedSize);
        cbChapter.setMinimumSize(fixedSize);
        // 用 WEST 容器包一层，避免 BorderLayout.CENTER 把下拉框拉满
        JPanel cbWrapper = new JPanel(new BorderLayout());
        cbWrapper.add(cbChapter, BorderLayout.WEST);
        chapterPanel.add(labChapterLabel, BorderLayout.WEST);
        chapterPanel.add(cbWrapper, BorderLayout.CENTER);
        topPanel.add(chapterPanel, BorderLayout.SOUTH);

        contentPanel.add(topPanel, BorderLayout.NORTH);

        // 中间内容区
        txContetnt = new JTextPane();
        txContetnt.setEditable(false);
        txContetnt.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(txContetnt);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // 底部控制栏
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 0));

        // 翻页按钮
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        btnFirst = new JButton("|<");
        btnPre = new JButton("<");
        btnNext = new JButton(">");
        btnLast = new JButton(">|");
        btnPanel.add(btnFirst);
        btnPanel.add(btnPre);
        btnPanel.add(btnNext);
        btnPanel.add(btnLast);
        bottomPanel.add(btnPanel, BorderLayout.WEST);

        // 跳转区
        JPanel jumpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        labPageLabel = new JLabel("页码：");
        tfPageNum = new JTextField(5);
        btnJump = new JButton("跳转");
        labTotalPages = new JLabel("0");
        jumpPanel.add(labPageLabel);
        jumpPanel.add(tfPageNum);
        jumpPanel.add(btnJump);
        jumpPanel.add(new JLabel("/"));
        jumpPanel.add(labTotalPages);
        bottomPanel.add(jumpPanel, BorderLayout.CENTER);

        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        // 事件绑定
        btnFirst.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                turnPage(1);
            }
        });
        btnPre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                turnPage(persistentState.getPageNum() - 1);
            }
        });
        btnNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                turnPage(persistentState.getPageNum() + 1);
            }
        });
        btnLast.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (totalPage != null) {
                    turnPage(totalPage.intValue());
                }
            }
        });
        btnJump.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = tfPageNum.getText();
                    int pageNnum = Integer.parseInt(text);
                    turnPage(pageNnum);
                } catch (NumberFormatException ex) {
                    MessageDialogBuilder.yesNo("提示", "请输入有效的页码").show();
                }
            }
        });
        // 章节选择
        cbChapter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Chapter selected = (Chapter) cbChapter.getSelectedItem();
                if (selected != null && selected.getStartPage() > 0) {
                    persistentState.setLastChapterName(selected.getName());
                    turnPage(selected.getStartPage());
                }
            }
        });
        // 键盘左右翻页
        txContetnt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                int keyCode = e.getKeyCode();
                if (37 == keyCode) {
                    turnPage(persistentState.getPageNum() - 1);
                } else if (39 == keyCode) {
                    turnPage(persistentState.getPageNum() + 1);
                }
            }
        });
        txContetnt.setFocusable(true);
    }

    public void turnPage(int i) {
        initBookScanner();
        if (i < 0 || totalPage == null || i > totalPage) {
            return;
        }
        persistentState.setPageNum(i);
        BookScanner scanner = BookScannerBuilder.getBookScaner();
        List<String> contentForPage = scanner.getContentForPage(persistentState.getPageNum(), persistentState.getPageSize());
        txContetnt.setText("");
        StringBuilder sb = new StringBuilder();
        for (String s : contentForPage) {
            sb.append(s).append("\r\n");
        }
        txContetnt.setText(sb.toString());
        tfPageNum.setText(i + "");

        // 翻页后自动同步章节下拉框（如果当前页属于某个章节）
        syncChapterByPage(i);
    }

    /**
     * 根据当前页码，自动选中对应的章节
     */
    private void syncChapterByPage(int currentPage) {
        BookScanner scanner = BookScannerBuilder.getBookScaner();
        if (!(scanner instanceof icu.jogeen.fishbook.service.TxtBookScanner)) {
            return;
        }
        List<Chapter> chapters = ((icu.jogeen.fishbook.service.TxtBookScanner) scanner)
                .getChapters(persistentState.getPageSize());
        if (chapters.isEmpty()) {
            return;
        }
        // 找到当前页所属的章节（最后一个 startPage <= currentPage 的章节）
        Chapter matched = chapters.get(0);
        for (Chapter c : chapters) {
            if (c.getStartPage() <= currentPage) {
                matched = c;
            } else {
                break;
            }
        }
        // 避免循环触发事件
        Chapter currentSelected = (Chapter) cbChapter.getSelectedItem();
        if (currentSelected == null || !currentSelected.getName().equals(matched.getName())) {
            cbChapter.setSelectedItem(matched);
            persistentState.setLastChapterName(matched.getName());
        }
    }

    public JPanel getJcontent() {
        return contentPanel;
    }
}
