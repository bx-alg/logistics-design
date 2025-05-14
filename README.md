# 数智化供应链平台

一个现代化的供应链管理平台，整合订单、物流、库存和供应商数据，提供可视化分析和实时监控功能。

## 技术栈

### 后端
- Spring Boot 2.7.18
- MyBatis-Plus 3.5.4
- MySQL 8.0.33
- Knife4j 4.3.0
- JDK 11

### 前端
- Vue 2.7.14
- Element UI 2.15.14
- ECharts 5.4.3
- Axios 1.5.1
- Vue Router 3.6.5

## 项目结构

```
logistics-design
├── backend              # 后端项目
│   ├── src              # 源代码
│   ├── pom.xml          # Maven配置
│   └── ...
└── frontend             # 前端项目
    ├── public           # 静态资源
    ├── src              # 源代码
    ├── package.json     # 依赖配置
    └── ...
```

## 功能模块

1. **数据总览**  
   - 可视化图表展示供应链数据
   - 销售金额/订单数量统计
   - 时间维度分析
   - 热销商品排行

2. **订单管理**  
   - 全流程订单整合
   - 生产进度实时跟踪
   - 销量预测
   - 排产计划管理

3. **运输管理**  
   - 实时运输状态跟踪
   - 路径规划与可视化
   - 在途库存管理

4. **库存管理**  
   - 在库/在途库存实时监控
   - 库存优化分析

5. **供应商管理**  
   - KPI评分体系
   - 供应商对标分析
   - 成本分析

6. **用户管理**  
   - 多角色权限控制

## 开发环境

- JDK 11
- Node.js 18.20.2 (LTS)
- MySQL 8.0.x

## 开始使用

### 后端启动

1. 创建数据库并执行SQL脚本：
   ```
   mysql -u root -p < backend/src/main/resources/db/logistics_design.sql
   ```

2. 配置数据库连接：
   - 修改 `backend/src/main/resources/application.yml` 中的数据库配置

3. 启动应用：
   ```
   cd backend
   mvn spring-boot:run
   ```

### 前端启动

1. 安装依赖：
   ```
   cd frontend
   npm install
   ```

2. 安装 Node.js 16.x：
   ```
   nvm install 16
   nvm use 16
   ```

3. 开发模式运行：
   ```
   npm run serve
   ```

4. 构建生产环境版本：
   ```
   npm run build
   ```

## 访问应用

- 前端: http://localhost:9528
- 后端API: http://localhost:8080/api
- API文档: http://localhost:8080/api/doc.html

## 默认账号

- 用户名: admin
- 密码: 123456 