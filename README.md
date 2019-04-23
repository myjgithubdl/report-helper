# ReportHelper用户操作手册

ReportHelper报表生成工具，用户只需要简单的设置数据库连接、报表查询SQL语句、报表查询参数即可生成数据表格(普通数据列表、透视表)和图表(折线图、柱状图、饼图、散点图、漏斗图等）。



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

### 3.2 数据库驱动

需要读取数据库的数据第一步则必须提供数据库的驱动、查询类、连接模板的设置。其中驱动为Java连接数据库的驱动类，查询类是在查询数据时会使用类中的方法查询数据。

1. 数据库驱动

   查询数据库的驱动类（driverClass）如下：

   - Mysql：com.mysql.cj.jdbc.Driver(最新驱动)或com.mysql.jdbc.Driver
   - Oracle：oracle.jdbc.driver.OracleDriver
   - SQLServer：com.microsoft.sqlserver.jdbc.SQLServerDriver
   - Postgresql：org.postgresql.Driver

2. 查询器

   因为不同数据库的查询语言会有区别（比如分页查询），需要根据不同的数据库定制不同的查询器（queryerClass）。

   - Mysql：com.myron.reporthelper.db.query.MySqlQueryer
   - Oracle：com.myron.reporthelper.db.query.OracleQueryer
   - SQLServer：com.myron.reporthelper.db.query.SqlServerQueryer
   - Postgresql：com.myron.reporthelper.db.query.PostgresqlQueryer

3. 连接URL

   连接数据库需要提供一个URL模板，在添加数据库连接的时候对改模板做简单的修改即可。

   - Mysql：jdbc:mysql://${host}:${port}/${database}?characterEncoding=${encoding}
   - Oracle：jdbc:oracle:thin:@${host}:${port}:${sid}
   - SQLServer：jdbc:sqlserver://${host};databaseName=${database}
   - Postgresql：jdbc:postgresql://${host}:${port}/${db}

![例子](https://raw.githubusercontent.com/myjgithubdl/report-helper/master/docs/assets/imgs/db-query-class.png)

### 3.3 数据库连接池

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

### 3.6 创建报表

​	在配置好数据库连接后即可创建报表，创建报表时需要选择报表的分类，其中报表由基本配置、查询参数、报表说明构成。

#### 3.6.1 基本设置

​	报表的基本设置主要包括报表的名称、报表展示方式、查询数据库、分页信息、查询SQL、根据查询SQL解析出的列。

![例子](https://raw.githubusercontent.com/myjgithubdl/report-helper/master/docs/assets/imgs/report-basic-set.png)

​	其中如下几个属性说明如下：

- 报表展示：报表的展示方式有数据表格、透视表、各种图表。

- SQL：是查询数据的SQL语句，其中SQL语句中支持使用${}表达式来设置（查询参数中设置）查询参数，如果表达式对应的参数有值的话会替换为参数对应的值，如果没有值则SQL中的该查询参数会被删除。

  如SQL：SELECT * FROM USER WHERE id=${id} and name=${name} and province=${province} and addr like '%${addrLike}%'

  参数：id=1 ，province=广东， addrLike=广州

  转化出SQL：SELECT * FROM USER WHERE id=1 and province='广东'   and addr like '%广州%'

- 元数据列配置：元数据列是点击执行SQL会自动生成，其属性如下

  | 属性           | 名称         | 默认值     | 说明                                                         |
  | -------------- | ------------ | ---------- | ------------------------------------------------------------ |
  | id             | id           | null       | 列唯一标识                                                   |
  | pid            | 上级id       | null       | 上级id，用于构建跨行跨列的表头                               |
  | name           | 数据库列名称 | null       | 数据库列名称                                                 |
  | text           | 显示的文字   | null       | 页面上显示的表格标题                                         |
  | dataType       | 数据类型     | null       | 数据库查询出的列类型                                         |
  | defaultValue   | 默认值       | null       | 当查询SQL列为空值时，会显示该值                              |
  | metaColumnType | 列类型       | 普通列     | 元数据列类型，在创建透视表、图表需要设置                     |
  | columnWidth    | 宽度         | 120        | 表格列的宽度                                                 |
  | precision      | 显示精度     | null       | 数值类型显示的精度                                           |
  | href           | 链接         | null       | 列是否是连接，如http://localhost:8081?name=${name}&text=${text} |
  | hrefTarget     | 打开方式     | 新窗口打开 | 如果是连接，则选择连接的打开方式，同时实现下钻功能           |
  | hidden         | 显示列       | 显示       | 列是否显示                                                   |
  | sort           | 排序         | 否         | 列排序                                                       |
  | downMergeCells | 合并等值列   | 否         | 值相同的列是否想下合并单元格                                 |
  | theadTextAlign | 对齐方式     | 居中       | 列的对齐方式                                                 |

  

#### 3.6.2 查询参数

​	查询参数对应于SQL中使用${}标识的参数，设置了之后同时会生成查询表单控件

![例子](https://raw.githubusercontent.com/myjgithubdl/report-helper/master/docs/assets/imgs/report-param-set.png)

​	其解释如下：

- 标题：显示的中文
- 参数名：HTML输入框的name属性
- 默认标题：输入框是选择框的时候可以根据该值设置默认选中
- 默认值：输入框是选择框的时候可以根据该值设置默认选中
- 标题宽度：标题宽度
- 输入宽度：输入框宽度
- 数据类型：参数的数据类型，会根据该类型解析出SQL中参数的类型
- 数据长度：允许输入的长度
- 是否必选：输入框是否必选
- 表单控件：输入框的空间类型，有文本框、选择框、日期
- 内容来源：如果是选择框的话需要设置选择框option选项的来源，支持SQL和文本
- 日期格式：如果表单控件是日期，则需要设置日期的格式
- 默认日期：整数，-1代表当前前一天，1代表后一天，0代表输入框没有默认值
- 联动参数：联动参数可以设置省市区联动，仅限表单控件是选择框且内容来源是SQL，当该列的值改变的时候会影响到该参数值对应的列的option选项重新加载。
- 内容：如果表单控件是选择框，则该值数选择框的值，支持select col1 as name , col2 as text from tb 、value1|value2、value1,text1|value2,text2

​		

#### 3.6.3 报表说明

​	可以在报表数据表格的顶部和底部设置报表的说明，说明可以是静态内容和动态内容，动态内容是SQL生成（表单参数同时适用于该SQL），在静态内容中可以使用${reportTipContent}将SQL查询的内容显示出来。

![例子](https://raw.githubusercontent.com/myjgithubdl/report-helper/master/docs/assets/imgs/report-tip-set.png)