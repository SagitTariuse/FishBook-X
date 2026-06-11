# FishBook-X

> 📖 在 IntelliJ IDEA 中读小说的工具窗口插件 —— 本项目是 [`jogeen/FishBook`](https://github.com/jogeen/FishBook) 的 fork，适配 **IntelliJ IDEA 2025.3.4** 并加入多项新功能。

> ⚠️ **版权声明 / Fork Attribution**
>
> 本项目**全部核心设计与原代码归属原作者 [`jogeen`](https://github.com/jogeen)**（项目创建于 2020/06/24）。
> 本 fork 仅做**兼容性适配 + 功能增强**，请勿将原作者的成果据为己有。
> 详细的逐项改动说明见 [`src/icu/jogeen/fishbook/NOTICE.md`](src/icu/jogeen/fishbook/NOTICE.md)。

---

## ✨ 功能 / Features

### 🆕 本 fork 新增的功能

| 功能 | 说明 |
|------|------|
| **章节识别 + 一键跳转** | 自动识别 `第N章 / Chapter N / 序章 / 楔子 / 尾声` 等章节标题，工具窗口提供**章节下拉框**，选中后自动跳到该章节的首页 |
| **记忆上次阅读章节** | 关闭 IDEA 再打开，仍会自动定位到上次阅读的章节 |
| **键盘翻页** | 在阅读区按 `←` / `→` 即可前后翻页 |
| **加载性能优化** | `BufferedReader(8KB)` 一次性加载整本书到内存，翻页 O(1)（基于 `ArrayList.subList`） |
| **IDEA 2025.3.4 平台适配** | 替换 `ServiceManager`、`NotificationGroup` 构造器、`ContentFactory.SERVICE` 等已废弃 API |

### 🎁 继承自原作者的功能

- 📖 在右侧 Tool Window 阅读本地 TXT 小说
- 🔖 自动保存阅读进度
- ⏭ 首页 / 上一页 / 下一页 / 末页 / 指定页码跳转
- 🔢 可自定义每页行数
- ⚙️ 配置面板：Settings → Tools → FishBook Config

---

## 🆚 与原项目的差异

| 项目 | 原项目 | FishBook-X（本 fork） |
|------|--------|------------------------|
| **仓库** | [`jogeen/FishBook`](https://github.com/jogeen/FishBook) | [`SagitTariuse/FishBook-X`](https://github.com/SagitTariuse/FishBook-X) |
| **作者** | `jogeen` | 维护者：`SagitTariuse`（基于 `jogeen` 的工作） |
| **平台兼容** | IntelliJ IDEA 2020.x | **IntelliJ IDEA 2025.3.4**（build 253.x） |
| **章节识别** | ❌ | ✅ |
| **章节跳转** | ❌ | ✅ |
| **键盘翻页** | ❌ | ✅ |
| **加载方式** | `RandomAccessFile` 读单行 | **`BufferedReader` 整本预加载** |
| **持久化** | `ServiceManager` | **Light Service**（`@State` + `ApplicationManager`） |
| **UI 渲染** | GUI Designer `.form`（需 IDEA 编译器做字节码插桩） | **纯代码构建**（无 `.form` 字节码依赖，javac 也能跑） |
| **发布渠道** | JetBrains Marketplace（原作者版本） | JetBrains Marketplace（插件 ID：`icu.sagittariuse.fishbookx`） |

> **为什么不用原项目？** 原项目最新一次提交停留在 2020 年，依赖了一堆 2020 年的 IntelliJ Platform API，
> 在 2025.3.4 上完全跑不起来（`ServiceManager` / `NotificationGroup` 构造器 / `ContentFactory.SERVICE` 都已废弃），
> 而且 `.form` 文件脱离了 IDEA 编译器后无法使用。本 fork 的目标就是**让它在 2025.3.4 上跑起来**。

---

## 📦 安装 / Install

### 从 JetBrains Marketplace 安装（推荐）

1. 打开 IDEA → `Settings` → `Plugins` → `Marketplace`
2. 搜索 **FishBook-X**
3. 点击 **Install**，重启 IDEA

### 从本地 JAR 安装

1. 克隆本仓库
2. IDEA → `Settings` → `Plugins` → ⚙️ → `Install Plugin from Disk...`
3. 选择 `build/FishBook-X-2.0.0.jar`

---

## 🚀 使用方法 / Usage

1. **Settings → Tools → FishBook Config**
2. 点击「**选择...**」选一本 TXT 小说
3. 设置每页行数（默认 10）
4. 点击 **Apply**
5. 打开右侧 **FishBook** 工具窗口
6. 用工具栏按钮翻页，或用**章节下拉框**跳转到指定章节

### 键盘快捷键

| 按键 | 动作 |
|------|------|
| `←` | 上一页 |
| `→` | 下一页 |

### 章节识别格式

支持以下章节标题格式（首行匹配即识别）：

- `第N章 / 第N回 / 第N节 / 第N卷 / 第N部 / 第N集 / 第N篇`
- `Chapter N` / `CHAPTER N`
- 特殊：`序章 / 序言 / 楔子 / 引子 / 尾声 / 后记 / 番外 / 序`

---

## 🛠 编译 / Build

本项目使用 **Gradle 9 + JDK 21**（IntelliJ Platform 2025.3 要求）：

```bash
# 需要 JDK 21
export JAVA_HOME=/path/to/jbr-21

./gradlew buildPlugin
# 产物在 build/distributions/FishBook-X-2.0.0.zip
```

> 💡 **本机环境提示**：若你的机器上 Gradle 9 与 JDK 8 冲突，可以直接用 javac 编译：
> 参考 `compile.bat`。

---

## 📁 项目结构 / Structure

```
FishBook/
├── src/icu/jogeen/fishbook/
│   ├── NOTICE.md              ← ⚠️ Fork 声明（必看）
│   ├── factory/               ← ToolWindow 工厂、SettingConfig 工厂
│   ├── service/               ← 业务逻辑（BookScanner、PersistentState、Chapter）
│   └── ui/                    ← Swing UI（ReadUI、SettingUI）
├── resources/
│   ├── META-INF/plugin.xml    ← 插件清单（含 fork 说明）
│   ├── icon.png / icon_150.png
│   └── ...
├── build.gradle.kts           ← Gradle 构建脚本
└── README.md                  ← 本文件
```

---

## 🙏 致谢 / Credits

本项目**基于 [jogeen/FishBook](https://github.com/jogeen/FishBook) 二次开发**。

如果没有原作者 `jogeen` 的原创工作（2020），就不会有这个 fork。
请大家去给原作者的仓库点个 ⭐：
👉 <https://github.com/jogeen/FishBook>

---

## 📜 License

本项目以 **Apache License 2.0** 发布，与原项目保持一致。
请在使用、修改、分发时**同时保留原作者与本 fork 的声明**。
