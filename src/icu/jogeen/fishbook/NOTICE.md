# FishBook-X 致原作者声明 / Fork Attribution

> 本文件用于明确本项目的来源和归属，所有 .java 文件头部均以注释形式引用本说明。

## 原作者

- **作者**：`jogeen`（GitHub: <https://github.com/jogeen>）
- **原项目地址**：<https://github.com/jogeen/FishBook>
- **原项目初衷**：在 IntelliJ IDEA 的工具窗口中阅读本地 TXT 小说
- **原作者版权声明时间**：2020/6/24 创建于 `src/icu/jogeen/fishbook/`

## 本项目 (FishBook-X) 的归属

- **维护者**：`SagitTariuse`（GitHub: <https://github.com/SagitTariuse>）
- **本项目地址**：<https://github.com/SagitTariuse/FishBook-X>
- **基于版本**：原作者 `master` 分支（commit `6b10c34`，即"添加翻页快捷键操作"）

## 修改动机 / 改动说明

原作者 `jogeen` 的 FishBook 是一个有趣的小工具，但其代码停留在 2020 年的 IntelliJ Platform API，
无法在新版本 IDEA（2025.3.4）上运行。本项目以**学习 + 兼容新版 IDEA** 为目标，对其进行适配与增强。
所有核心思路与原有功能归属原作者，**任何商业用途或二次分发需同时保留原作者与本项目的声明**。

## 在本项目中的具体改动

1. **平台适配**：原项目面向 IDEA 2020.x 编写，无法在 2025.3.4 加载。本项目升级到 2025.3.4 平台 API。
2. **废弃 API 替换**（编译/运行必需的修改）：
   - `ServiceManager.getService()` → `ApplicationManager.getApplication().getService()` + `@Service`
   - `NotificationGroup(...)` → `NotificationGroupManager.getInstance().getNotificationGroup(...)`
   - `ContentFactory.SERVICE.getInstance().createContent(...)` → `ContentFactory.getInstance().createContent(...)`
3. **性能优化**：`TxtBookScanner` 由按行 `readLine` 改为 `BufferedReader(8KB)` + 一次性预加载全部行到内存，
   翻页时复杂度从 O(N) 降到 O(1)（基于 `ArrayList.subList`）。
4. **章节识别 + 跳转**（新增功能）：
   - 新增 `Chapter.java` 数据类
   - `TxtBookScanner` 中通过正则识别 `第N章 / Chapter N / 序章 / 楔子` 等章节标题，
     并把每个章节映射到对应页码
   - `ReadUI` 中新增章节下拉框，选择后一键跳转到该章节首页
5. **持久化扩展**：`PersistentState` 新增 `lastChapterName` 字段，记住上次阅读的章节
6. **UI 重写**：原项目使用 IntelliJ GUI Designer 的 `.form` 文件，但
   `javac` 不会执行 GUI Designer 的字节码插桩，导致 `settingConfigurable` 报
   "Select configuration element in the tree" 错误。本项目把 `ReadUI` 和
   `SettingUI` 改为**纯代码**构建，移除对 `.form` 字节码的依赖。
7. **图标生成**：使用 PIL 生成了 `icon.png`（40x40）和 `icon_150.png`（150x150）
8. **打包/发布**：因本地 Gradle 9.x 与 JDK 1.8 不兼容，使用 JBR 21 + javac 直接编译并打包成 JAR，
   上传至 JetBrains Marketplace（插件 ID: `icu.sagittariuse.fishbookx`，名称：FishBook-X）。

## 致敬

感谢原作者 `jogeen` 的创意与代码。本项目作为 fork 公开，欢迎大家回去给原作者点个 ⭐
<https://github.com/jogeen/FishBook>。
