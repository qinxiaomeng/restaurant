# Spring Boot 构建一个 RESTful Web 服务

##### 记录系统搭建内容
大概的目录结构  
restaurant  
...|-src  
......|-main  
.........|-java  
............|-com.ej.restautant  
................|-config //相关配置类  
................|-controller   
................|-enums   
................|-mapper //mapper接口  
................|-model //实体类  
................|-params //接口请求参数  
................|-utils    
................|-RestaurantApplication.java //启动类  
.........|-resources  
............|-mybatis  
................|-mapper //mapper配置文件（.xml）   
............|-application.yml  //应用配置文件  
............|-application-dev.yml  
............|-application-uat.yml  
............|-application-pro.yml  

 

#### 需求描述
系统包括系统管理员、系统客户（商户）以及商户顾客三个角色。其中，商户在管理系统进行门店相关信息维护；商户顾客通过微信小程序扫描  二维码进行点餐下单，下单后门店管理员可以看到当前门店的订单信息。

#### Day1- 构建Web服务并使用Swagger2构建RESTful APIs
##### 添加依赖：  
    <dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-web</artifactId>
	</dependency>
	
##### 在项目跟目录添加启动类RestaurantApplication

    @SpringBootApplication
    public class RestaurantApplication {

        public static void main(String[] args) {
            SpringApplication.run(RestaurantApplication.class, args);
        }

    }
    
写个controller测试服务是否正常：
    
    @RestController
    @RequestMapping("/hello")
    public class HelloController {
    
        @GetMapping("withoutParam")
        public String sayHello(){
            return "say hello without param";
        }
    }
    
启动项目，启动如果没有报错的话，在浏览器访问http://localhost:8080/hello/withoutParam 页面应该显示"say hello without param"

#### 集成Swagger2
    <dependency>
		<groupId>io.springfox</groupId>
		<artifactId>springfox-swagger2</artifactId>
		<version>2.8.0</version>
	</dependency>
	<dependency>
		<groupId>io.springfox</groupId>
		<artifactId>springfox-swagger-ui</artifactId>
		<version>2.8.0</version>
	</dependency>

创建Swagger配置类

    @SpringBootConfiguration
    @EnableSwagger2
    public class SwaggerConfig {
        @Bean
        public Docket api(){
            return new Docket(DocumentationType.SWAGGER_2)
                    .apiInfo(apiInfo())
                    .select()
                    // 自行修改为自己的包路径
                    .apis(RequestHandlerSelectors.basePackage("com.ej.restaurant.controller"))
                    .paths(PathSelectors.any())
                    .build();
        }
    
        private ApiInfo apiInfo() {
            return new ApiInfoBuilder()
                    .title("小餐饮管理系统")
                    .description("小餐饮管理系统 API 1.0 操作文档")
                    .version("1.0")
                    .contact(new Contact("Simon", "", ""))
                    .build();
        }
    }

在Controller类及其方法上添加Swagger注解，访问http://localhost:8080/swagger-ui.html可以看到我们定义在 com.ej.restaurant.controller 的接口已经被映射到页面上了，管理接口文档非常方便。  
关于Swagger的相关注解不再记录。    

#### Day2- 使用MyBatis实现持久化，并集成阿里的Druid监控数据源
创建数据库、添加用户表并创建UserInfo实体类。
##### 添加依赖：

    <dependency>
		<groupId>mysql</groupId>
		<artifactId>mysql-connector-java</artifactId>
	</dependency>
	<dependency>
		<groupId>org.mybatis.spring.boot</groupId>
		<artifactId>mybatis-spring-boot-starter</artifactId>
		<version>2.0.0</version>
	</dependency>
	<dependency>
		<groupId>com.alibaba</groupId>
		<artifactId>druid-spring-boot-starter</artifactId>
		<version>1.1.10</version>
	</dependency>

##### 关键配置：
SpringBoot的配置文件为application.properties，同时支持YAML的配置文件，我采用YAML配置文件

    server:
      port: 8089  #可通过该配置修改服务端口
    spring:
      datasource:
        type: com.alibaba.druid.pool.DruidDataSource
        url: jdbc:mysql://localhost:3306/restaurant?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&useSSL=true
        username: root
        password: password
        driver-class-name: com.mysql.cj.jdbc.Driver
        ###以下是druid的配置
        druid:
          initial-size: 3
          min-idle: 3
          max-active: 10
          max-wait: 60000 #配置获取连接等待超时的时间
          stat-view-servlet: # 监控后台账号和密码
            login-username: admin
            login-password: admin
          filter:
            stat:
              log-slow-sql: true
              slow-sql-millis: 2000
    mybatis:
      config-location: classpath:mybatis/mybatis-config.xml
      mapper-locations: classpath:mybatis/mapper/*.xml
      type-aliases-package: com.ej.restaurant.model
##### 启动类：
在启动类中添加对 Mapper 包扫描 @MapperScan，Spring Boot 启动的时候会自动加载包路径下的 Mapper。

    @SpringBootApplication
    @MapperScan("com.ej.restaurant.mapper")
    public class RestaurantApplication {
    
        public static void main(String[] args) {
            SpringApplication.run(RestaurantApplication.class, args);
        }
    
    }

mybatis-config.xml，主要配置常用的typeAliases，设置类型别名它只和 XML 配置有关，存在的意义仅在于用来减少类完全限定名的冗余。

    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-config.dtd">
    <configuration>
        <typeAliases>
            <typeAlias alias="Integer" type="java.lang.Integer" />
            <typeAlias alias="Long" type="java.lang.Long" />
            <typeAlias alias="HashMap" type="java.util.HashMap" />
            <typeAlias alias="LinkedHashMap" type="java.util.LinkedHashMap" />
            <typeAlias alias="ArrayList" type="java.util.ArrayList" />
            <typeAlias alias="LinkedList" type="java.util.LinkedList" />
        </typeAliases>
    </configuration>
    
添加UserInfo的配置文件，UserInfoMapper.xml

    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
    <!--第一步：指明对应文件的Mapper类地址 -->
    <mapper namespace="com.ej.restaurant.mapper.UserInfoMapper">
    
        <!--第二步：配置表结构和类的对应关系 -->
        <resultMap id="BaseResultMap" type="com.ej.restaurant.model.UserInfo">
            <id column="id" property="id" jdbcType="VARCHAR"/>
            <result column="login_name" property="loginName" jdbcType="VARCHAR"/>
            <result column="mobile" property="mobile" jdbcType="VARCHAR"/>
            <result column="password" property="password" jdbcType="VARCHAR"/>
            <result column="create_date" property="createDate" jdbcType="BIGINT"/>
            <result column="status" property="status" javaType="com.ej.restaurant.enums.DataStatus"/>
        </resultMap>
    
        <!--第三步：写具体的SQL -->
        <!--PS：MyBatis XML 有一个特点是可以复用 XML，比如我们公用的一些 XML 片段可以提取出来，在其他 SQL 中去引用。例如： -->
        <sql id="base_column_list">
            id, login_name, mobile, password, create_date, status
        </sql>
    
        <!--从查询参数中封装查询条件 -->
        <sql id="base_param_list">
            <if test="mobile != null and mobile != ''">
                and mobile = #{mobile}
            </if>
            <if test="status != null and status != ''">
                and status = #{status}
            </if>
        </sql>
        <select id="listUsers" resultMap="BaseResultMap" parameterType="com.ej.restaurant.params.UserInfoParam">
            SELECT
              <include refid="base_column_list"/>
            FROM user_info
            WHERE 1=1
              <include refid="base_param_list" />
            ORDER by create_date DESC
            limit #{beginLine}, #{pageSize}
        </select>
    
        <select id="getCount" resultType="Integer" parameterType="com.ej.restaurant.params.UserInfoParam">
            select
            count(1)
            from user_info
            where 1=1
            <include refid="base_param_list" />
        </select>
    
        <insert id="insert" parameterType="com.ej.restaurant.model.UserInfo">
            INSERT INTO
              user_info(
                id,
                login_name,
                mobile,
                password,
                create_date,
                status)
            VALUES (
              #{id},
              #{loginName},
              #{mobile},
              #{password},
              #{createDate},
              #{status})
        </insert>
        <update id="update" parameterType="com.ej.restaurant.model.UserInfo">
            UPDATE user_info
            <trim prefix="set" suffixOverrides=",">
                <if test="loginName != null and loginName != ''">
                    login_name = #{loginName},
                </if>
                <if test="mobile != null and mobile != ''">
                    mobile = #{mobile},
                </if>
                <if test="password != null and password != ''">
                    password = #{password},
                </if>
                <if test="status != null">
                    status = #{status},
                </if>
    
            </trim>
            WHERE id = #{id}
        </update>
        <select id="getUserByLoginName" parameterType="String" resultMap="BaseResultMap">
            SELECT
              <include refid="base_column_list"/>
            FROM user_info
            WHERE login_name = #{loginName}
        </select>
    
    </mapper>
    
创建Mapper接口类，映射文件中的id属性值必须和Mapper接口类的方法名对应，否则会报找不到sql映射的异常。  
编写测试类：

    @SpringBootTest
    @RunWith(SpringRunner.class)
    public class UserInfoMapperTests {
    
        @Autowired
        private UserInfoMapper userInfoMapper;
    
        @Test
        public void createTest(){
            UserInfo user = new UserInfo();
            user.preInsert();
            user.setMobile("22222222222");
            user.setLoginName("222222");
            user.setPassword("222222");
    
            userInfoMapper.insert(user);
        }
    }
    
最后检查以下Druid的集成情况，http://localhost:8089/druid，输入我们在配置文件中指定的用户名／密码就可以访问。功能很强大可以做数据源监控、慢sql记录、应用监控等等，按照系统需求配置即可。

#### Day3- 通用类库封装
一个接口交互过程中涉及入参参数（params）、接口结果（result）以及异常处理。入参参数、接口结果不用多解释，针对异常处理，我们并不想在每个接口方法中去捕获处理，希望能有一个异常统一处理入口。

##### 入参参数
很多为了省事，常有开发人员将实体类做为接口方法的入参参数，这样做固然可以少写一些代码，但是，接口并不需要实体类的每个属性，导致接口参数过多，给前端开发人员造成很大的困难。所以，我个人更倾向于为每个接口创建单独的params类，便于维护。  例如：

    @Data
    public class UserInfoParam {
        @NotEmpty
        private String mobile;
        private String loginName;
        @NotEmpty
        private String password;
        private String rearks;
    }

======================

    @Data
    public class UserInfoQueryParam extends PageParam {
        private String mobile;
        private String status;
    }
    
这样做的好处一是可以减少和前端开发的沟通成本，另外，对参数的控制可以结合框架和注解的方式轻松实现。

##### 接口结果
对于接口返回，我们通常会返回接口编码、错误消息、数据体等内容。我们封装一个Response对象来实现。

    @ToString
    public class DLResponseObject<T> implements Serializable{
      public long code;
      public String msg;
      public long time;
      public T body;
    
      public DLResponseObject(long code, String msg, long time, T body) {
        this.code = code;
        this.msg = msg;
        this.time = time;
        this.body = body;
      }
    
      public static <T> DLResponseObject<T> generate(T body) {
        return new DLResponseObject<>(DLExceptionType.SUCCESS.getCode(), DLExceptionType.SUCCESS.getMsg(), DLClock.now(), body);
      }
    
      public static DLResponseObject<String> success() {
        return generate("");
      }
    
      public static DLResponseObject<List> emptyList() {
        return generate(Collections.emptyList());
      }
    
      public static DLResponseObject<String> fromError(DLException ex) {
        return new DLResponseObject<>(ex.getDlExceptionType().getCode(), ex.getMessage(), DLClock.now(), "");
      }
    
      public static DLResponseObject<String> fromErrorType(DLExceptionType dlExceptionType) {
        return new DLResponseObject<>(dlExceptionType.getCode(), dlExceptionType.getMsg(), DLClock.now(), "");
      }
    }
    
当然，也可以简单封装一个分页数据对象，配合使用。

    @Data
    public class Page<E> implements Serializable {
    
        private static final long serialVersionUID = 2759730160214944840L;
    
        private int currentPage = 1; //当前页数
        private long totalPage;       //总页数
        private long totalNumber;    //总记录数
        private List<E> list;        //数据集
    
        public Page(PageParam pageParam, long totalNumber, List<E> list){
            super();
            this.currentPage = pageParam.getCurrentPage();
            this.totalNumber = totalNumber;
            this.list = list;
            this.totalPage = totalNumber % pageParam.getPageSize() == 0 ? totalNumber / pageParam.getPageSize() : totalNumber / pageParam.getPageSize() + 1;
        }
    }
    
##### 异常处理

首先我们封装一个异常类，用来将系统异常、错误转成人话，便于将结果回传给前端显示。

###### 异常类型
通过定义一个枚举来管理异常类型，如下：

    public enum DLExceptionType {
      SUCCESS(100000, "ok"),
      COMMON_SERVER_ERROR(100001, "网络错误", ELogType.ERROR),
      COMMON_ILLEGAL_ARGUMENT(100002, "参数错误"),
      NOT_LOGIN(100003, "请登录"),
      USER_REGISTERED(100005, "您已经注册过, 请直接登录"),
      ARGUMENT_MISS(100006, "填写的信息有误或不全, 请再次检查"),
      ADMIN_PERMISSION_DENY(100008, "没有权限"),
      USER_MANUAL_REGISTERED(100009, "请到登录页面重置密码"),
      VCODE_MISMATCH(100011, "验证码错误"),
      LOGINNAME_PW_ERROR(100013, "用户名、密码错误"),
      CUST_HAVENOT_REGISTED(1000011, "请您先注册");
    
    
    
      private long code;
      private String msg;
      private ELogType eLogType;
    
      DLExceptionType(long c, String m) {
        code = c;
        msg = m;
        this.eLogType = ELogType.WARNING;
      }
    
      DLExceptionType(long c, String m, ELogType eLogType) {
        code = c;
        msg = m;
        this.eLogType = eLogType;
      }
    
      public long getCode() {
        return code;
      }
    
      public String getMsg() {
        return msg;
      }
    
      public ELogType geteLogType() {
        return eLogType;
      }
    
      public static enum ELogType {
        WARNING,
        ERROR
      }
    }
    
###### 自定义异常类

    public class DLException extends RuntimeException {
      private DLExceptionType dlExceptionType;
      private String detail = null;
    
      public DLException(String msg) {
        super(msg);
        this.dlExceptionType = DLExceptionType.COMMON_SERVER_ERROR;
        detail = msg;
      }
    
      public DLException(DLExceptionType type) {
        super(type.getMsg());
        this.dlExceptionType = type;
      }
    
      public DLException(DLExceptionType type, String msg) {
        super(msg);
        this.dlExceptionType = type;
        this.detail = msg;
      }
    
      public DLException(DLExceptionType type, Throwable cause) {
        super(cause);
        this.dlExceptionType = type;
      }
    
      public DLException(DLExceptionType type, String msg, Throwable cause) {
        super(msg, cause);
        this.dlExceptionType = type;
        this.detail = msg;
      }
    
      public DLExceptionType getDlExceptionType() {
        return dlExceptionType;
      }
    
      @Override
      public String getMessage() {
        if (detail != null) {
          return detail;
        }
        return dlExceptionType.getMsg();
      }
    
      public static DLException wrap(Throwable throwable) {
        if (throwable instanceof DLException) {
          return (DLException) throwable;
        }
        return new DLException(DLExceptionType.COMMON_SERVER_ERROR, throwable);
      }
    
      public static DLException wrap(Throwable throwable, DLExceptionType type) {
        return new DLException(type, throwable);
      }
    }
    
 ###### 定义接口异常的统一处理
 我们使用 @ControllerAdvice 注解，可以用于定义@ExceptionHandler、@InitBinder、@ModelAttribute，并应用到所有@RequestMapping中。  这样便实现了异常的全局处理。
 
 
     @ControllerAdvice
     public class CommonExceptionHandler {
     
         Logger log = LoggerFactory.getLogger(getClass());
     
         @ExceptionHandler(MethodArgumentNotValidException.class)
         public DLResponseObject handleBindException(MethodArgumentNotValidException ex){
             FieldError fieldError = ex.getBindingResult().getFieldError();
             log.info("参数校验异常:{}({})", fieldError.getDefaultMessage(),fieldError.getField());
     
             return DLResponseObject.fromErrorType(DLExceptionType.COMMON_ILLEGAL_ARGUMENT);
         }
     
         @ExceptionHandler(BindException.class)
         public DLResponseObject handleBindException(BindException ex){
             FieldError fieldError = ex.getBindingResult().getFieldError();
             log.info("必填校验异常:{}({})", fieldError.getDefaultMessage(),fieldError.getField());
             return DLResponseObject.fromErrorType(DLExceptionType.COMMON_ILLEGAL_ARGUMENT);
         }
     
         @ExceptionHandler(DLException.class)
         public DLResponseObject exceptionHandler(DLException ex){
             return DLResponseObject.fromError(ex);
         }
     
         @ExceptionHandler(Exception.class)
         @ResponseBody
         public DLResponseObject exceptionHandler(Exception e){
             log.error("unchecked exception:", e);
             return DLResponseObject.fromErrorType(DLExceptionType.COMMON_SERVER_ERROR);
         }
     }
     
其他的工具类，就不贴代码了。