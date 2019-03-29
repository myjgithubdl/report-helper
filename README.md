# ReportHelper用户操作手册

ReportHelper报表生成工具，用户只需要简单的设置数据库连接、报表查询SQL语句、报表查询参数即可生成数据表格(普通数据列表和Excel透视表)和图表(折线图、柱状图、饼图、散点图、漏斗图等）。



## 1.开发环境

- [jdk1.8][]
- [maven3][]
- [idea][] or [eclipse][]
- [Spring boot 2.1.1.RELEASE][]
- [Spring boot Mybatis 1.3.2][]
- [MySQL5+][]

## 2.安装与部署

系统默认登录用户:**admin** 密码:**123456**

### 2.1 从源代码安装

首先确定安装好[jdk1.8][]与[maven3][]、[MySQL5+][]，并配置好maven仓库，然后按如下步骤操作：

- step1:git clone https://github.com/myjgithubdl/report-helper.git
- step2:创建元数据库和示例数据库:
  2.1 自己新建一个或者使用原有的mysql数据库, 用某个账号密码测试,可以进行创建库等操作
  2.2 找到 your_git_repository/report-helper/docs/db/report-helper.sql,并在Mysql中执行该sql脚本,创建数据库及表结构、初始数据
  2.3 找到 your_git_repository/report-helper/docs/db/china_weather_air_mysql.zip, 解压后执行该sql脚本,创建数据库及示例数据。
- step3:修改 your_git_repository/report-helper/src/main/resources/application-dev.properties 数据库连接字符串的IP、用户与密码
- step4:Maven 打包工程 mvn install package -Dmaven.test.skip=true
- step5:经过step4之后会在target目录生成lib目录和report-helper.jar文件，然后在该目录下执行命令启动工程：java -jar report-helper.jar --spring.profiles.active=dev

## 3.使用说明

### 3.1 预备知识

报表就是用表格、图表等格式来显示数据。本工具只是简单的从数据库(MySQL,Postgresql,Oracle,SQLServer等)中读取数据，并转换成HTML表格形式展示。同时支持钻取、列合并、透视表等功能。

### 3.2 数据库驱动设置

需要读取数据库的数据第一步则必须提供数据库的驱动、查询类、连接模板的设置。其中驱动为Java连接数据库的驱动类，查询类是在查询数据时会使用类中的方法查询数据。

1. 数据库驱动

   - Mysql：com.mysql.cj.jdbc.Driver(最新驱动)或com.mysql.jdbc.Driver
   - Oracle：oracle.jdbc.driver.OracleDriver
   - SQLServer：com.microsoft.sqlserver.jdbc.SQLServerDriver
   - Postgresql：org.postgresql.Driver

2. 查询器
   - Mysql：com.myron.reporthelper.db.query.MySqlQueryer
   - Oracle：com.myron.reporthelper.db.query.OracleQueryer
   - SQLServer：com.myron.reporthelper.db.query.SqlServerQueryer
   - Postgresql：com.myron.reporthelper.db.query.PostgresqlQueryer

3. 连接模板
   - Mysql：jdbc:mysql://${host}:${port}/${database}?characterEncoding=${encoding}
   - Oracle：jdbc:oracle:thin:@${host}:${port}:${sid}
   - SQLServer：jdbc:sqlserver://${host};databaseName=${database}
   - Postgresql：jdbc:postgresql://${host}:${port}/${db}

![例子](https://raw.githubusercontent.com/myjgithubdl/report-helper/master/docs/assets/imgs/db-query-class.png)

### 3.3 数据库连接池设置

因为需要读取各类型的数据库，数据库连接的创建和销毁是很耗时的操作，所以使用连接池来管理数据库连接，建议使用Druid作为数据库连接池。该部分需要设置连接池的创建类和连接池的相关属性。

1. 连接池创建类
   - Druid：com.myron.reporthelper.db.pool.DruidDataSourcePool
   - C3p0：com.myron.reporthelper.db.pool.C3p0DataSourcePool
   - DBCP2：com.myron.reporthelper.db.pool.DBCP2DataSourcePool
   - 无连接池：com.myron.reporthelper.db.pool.NoDataSourcePool

![例子](https://raw.githubusercontent.com/myjgithubdl/report-helper/master/docs/assets/imgs/db-pool.png)

### 3.4 报表查询数据库

在设置了数据库驱动和连接池后就可以设置报表的查数据库了。

![例子](https://raw.githubusercontent.com/myjgithubdl/report-helper/master/docs/assets/imgs/db-setting.png)


