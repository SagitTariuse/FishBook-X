package icu.jogeen.fishbook.ui;

import icu.jogeen.fishbook.service.PersistentState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @Author jogeen    （原作者，2020/6/24）
 * @Maintainer SagitTariuse  （改写为纯代码 UI，2026/06）
 * @OriginalRepo https://github.com/jogeen/FishBook
 * @ThisRepo     https://github.com/SagitTariuse/FishBook-X
 * @Description 适配 IntelliJ IDEA 2025.3.4 - 纯代码构建 UI（不依赖 form 字节码插桩）
 *
 * 详细 fork 声明见 service 包 NOTICE.md。
 */
public class SettingUI {
    public JPanel settingPanel;
    public JTextField tfBookPath;
    public JButton btnChooseBook;
    public JTextField tfPageSize;
    private JLabel labBookPath;
    private JLabel labPageSize;

    public SettingUI() {
        // 程序化构建 UI（替代 GUI Designer 的 form 文件）
        settingPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // 第一行：书籍路径
        labBookPath = new JLabel("书籍路径：");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        settingPanel.add(labBookPath, gbc);

        tfBookPath = new JTextField(30);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        settingPanel.add(tfBookPath, gbc);

        btnChooseBook = new JButton("选择...");
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        settingPanel.add(btnChooseBook, gbc);

        // 第二行：页大小
        labPageSize = new JLabel("每页行数：");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        settingPanel.add(labPageSize, gbc);

        tfPageSize = new JTextField(10);
        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        settingPanel.add(tfPageSize, gbc);

        // 事件绑定
        btnChooseBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.showOpenDialog(settingPanel);
                File file = fileChooser.getSelectedFile();
                if (file != null && tfBookPath != null) {
                    tfBookPath.setText(file.getPath());
                }
            }
        });
    }

    public void init(PersistentState persistentState) {
        String bookPathText = persistentState.getBookPathText();
        if (bookPathText != null) {
            tfBookPath.setText(bookPathText);
        }
        if (tfPageSize != null) {
            tfPageSize.setText(String.valueOf(persistentState.getPageSize()));
        }
    }
}
