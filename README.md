# fast-dbauto [![Maven Central](https://img.shields.io/maven-central/v/com.rdxer/fast-dbauto.svg)](https://search.maven.org/search?q=g:com.rdxer%20a:fast-dbauto)

fast - dbauto by springboot
自动建表工具
目前支持 MySql，PGSql。

自动识别数据类型：
    
MySql：

    int,long,float,double,bool,char,string

PGSql：

    int,long,float,double,bool,char,string

不识别的数据类型，例如text，请使用 @AutoColumn 的 define 解决：
    
    // 自定义字段类型为 text
    @AutoColumn(define = "text")
    private String bio;

# 集成
## 1. 添加依赖
最新版：[![Maven Central](https://img.shields.io/maven-central/v/com.rdxer/fast-dbauto.svg)](https://search.maven.org/search?q=g:com.rdxer%20a:fast-dbauto)

    <dependency>
        <groupId>com.rdxer</groupId>
        <artifactId>fast-dbauto</artifactId>
        <version>请替换为最新版</version>
    </dependency>

## 2. 配置`application.properties`
在 `application.properties` 中配置数据库类型
    
    # MySql配置如下
    com.rdxer.db.auto.dbtype=MySql
    
    # or

    # PGSql配置如下
    com.rdxer.db.auto.dbtype=PGSql

    # 其他配置 
    # 禁用
    com.rdxer.db.auto.enable=false

在 Spring Boot 启动类中添加 `@EnableDBAuto` 注解，启用此功能，如下：
    
    @SpringBootApplication
    @EnableDBAuto
    public class ServerApplication {
        public static void main(String[] args) {
            SpringApplication.run(ServerApplication.class, args);
        }
    }

## 3. 配置数据库模型类
假如存在`Account.java`类，（此处使用了 [Lombok](https://www.projectlombok.org/)  简化代码）

    @Data
    public class Account {
        private Long id;
        private String name;
        private Integer age;
        private String email;
        private String bio;
    }

添加配置

    @Data
    @AutoTable  // 添加此注解后，此类会被扫描到
    public class Account {
        // 标记为 主键
        @AutoColumn(isKey = true)
        private Long id;
        // 唯一，并且长度150
        @AutoColumn(isUnique = true,len = "150")
        private String name;
        // 不标记也会识别为字段
        private Integer age;
        private String email;
        // 自定义字段类型为 text
        @AutoColumn(define = "text")
        private String bio;
    }

## 4. 启动项目，稍等片刻.... 即可大功告成~ 


# 参考配置1如下： 
 
    @Data
    @AutoTable(name = "account", comment = "账户表")
    public class Account {
        @JsonSerialize(using= ToStringSerializer.class)
        // @TableId(type = IdType.ASSIGN_ID) // Mybatis+ 的配置
        @AutoColumn(isKey = true,comment = "id字段")
        private Long id;
        @AutoColumn(isUnique = true)
        private String username;
        @JsonIgnore
        private String password;
        // 显示名称
        private String displayName;
        @AutoColumn(isUnique = true)
        private String email;
        @AutoColumn(isUnique = true)
        private String phone;
        @AutoColumn(define = "text")
        private String avatar;
        // 忽略此字段
        @AutoColumnIgnore
        private String avatarIgnore;
        
        // 创建时间
        private Long createAt;
        // 如果为 null 则 未禁用
        // 禁用时间
        private Long disabledAt;
        // 删除时间
        private Long deleteAt;
    }

# 参考配置2如下： 

    @Data
    @AutoTable
    public class BookshelfItem {
    
        // id  bigserial primary key,
        @AutoColumn(isKey = true, isAutoIncrement = true)
        private Long id;
        // strv1    varchar(255),
        private String strv1;
        // strv2    varchar(255),
        private String strv2;
    
        // vint     integer          not null,
        private int vint;
        // vfloat   real             not null,
        private float vfloat;
        // vdouble  double precision not null,
        private double vdouble;
    
        //  vint2    integer
        private Integer vint2;
        // vfloat2  real,
        private Float vfloat2;
        // vdouble2 double precision,
        private Double vdouble2;
        private Double vdouble222;
        @AutoColumn(isNull = false)
        private Double vdouble22222;
        @AutoColumn(isUnique = true)
        private Integer intkey1;
    
        // alter table bookshelf add column vdouble21 float8

    }

# 文档

    源代码没几个类，可以直接看代码 /笑哭

# 注意
1. 表不存在则建表，字段不存在则根据现有配置创建字段，如果需要修改字段，目前暂不支持，请直接手动改数据库。
2. 此工具不会执行任何`删除`操作。危险操作请手动完成。