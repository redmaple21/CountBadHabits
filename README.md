# CountBadHabits - 坏习惯记录与统计应用

## 项目简介

CountBadHabits 是一款Android原生应用，用于记录和统计坏习惯的触发情况，帮助用户监控和控制不良习惯。

项目代码全部用AI生成。

## 功能特性

### 🎯 核心功能
- **坏习惯管理**：添加、编辑、删除坏习惯，设置每日触发上限
- **触发记录**：记录坏习惯触发的时间和详情描述
- **实时统计**：显示当日触发次数，超限状态提醒
- **历史追踪**：日历视图查看历史记录，颜色编码显示超限状态

### 📊 数据可视化
- **自定义日历**：月视图/周视图切换，直观显示每日触发次数
- **统计图表**：柱状图显示月度/年度统计数据
- **状态指示**：绿色/红色颜色编码区分正常/超限状态

### 🌐 多语言支持
- 简体中文
- English

### 🎨 设计特点
- Material Design 界面风格
- 深色/浅色主题自动切换
- 响应式布局设计
- 流畅的用户体验

## 技术架构

### 开发环境
- **语言**：Java
- **框架**：Android Native
- **最低支持**：Android 10 (API 29)
- **目标版本**：Android 13 (API 33)

### 核心技术
- **数据库**：SQLite（Android内置）
- **架构模式**：MVC
- **UI组件**：Material Components
- **数据持久化**：SharedPreferences + SQLite

### 项目结构
```
app/src/main/java/com/felix/countbadhabits/
├── activity/          # Activity类
├── fragment/          # Fragment类
├── adapter/           # RecyclerView适配器
├── database/          # 数据库相关
├── model/             # 数据模型
├── utils/             # 工具类
└── widget/            # 自定义控件
```

## 功能模块

### 📱 主要页面
1. **当日记录**：显示今日触发记录，添加新记录
2. **历史记录**：日历视图和统计图表
3. **设置管理**：坏习惯的增删改查

### 🗄️ 数据模型
- **BadHabit**：坏习惯实体（名称、每日上限、创建日期）
- **TriggerRecord**：触发记录（时间、描述、序号）
- **DailySummary**：日统计摘要

### 🎮 自定义控件
- **CustomCalendarView**：自定义日历控件
- **BarChartView**：柱状图控件

## 安装与使用

### 环境要求
- Android Studio Arctic Fox 或更高版本
- Android SDK 33
- JDK 8 或更高版本

### 构建步骤
```bash
# 克隆项目
git clone https://github.com/YOUR_USERNAME/CountBadHabits.git

# 进入项目目录
cd CountBadHabits

# 构建APK
./gradlew assembleDebug
```

### 使用说明
1. 首次启动会自动创建示例坏习惯
2. 在"设置"页面管理你的坏习惯
3. 在"当日记录"页面记录触发情况
4. 在"历史记录"页面查看统计数据

## 开发状态

✅ **已完成功能**
- [x] 数据库设计与实现
- [x] 基础UI框架搭建
- [x] 当日记录功能
- [x] 历史记录与统计
- [x] 设置管理功能
- [x] 国际化支持
- [x] Material Design UI

🚀 **计划功能**
- [ ] 数据导出功能
- [ ] 习惯分析报告
- [ ] 提醒通知功能
- [ ] 云端数据同步

## 贡献指南

欢迎提交Issue和Pull Request！

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

- 项目链接：https://github.com/YOUR_USERNAME/CountBadHabits
- 问题反馈：https://github.com/YOUR_USERNAME/CountBadHabits/issues

---

**让我们一起养成好习惯，告别坏习惯！** 🌟