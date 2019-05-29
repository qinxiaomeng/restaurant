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

##### 集成Redis
集成需要简单的三步即可

###### 关键依赖

	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-data-redis</artifactId>
	</dependency>
	<dependency>
		<groupId>org.apache.commons</groupId>
		<artifactId>commons-pool2</artifactId>
	</dependency>
Lettuce 需要使用 commons-pool2 创建 Redis 连接池，所以需要引入 commons-pool2 。

###### application.yml 添加Redis配置

      redis:
        database: 0
        host: localhost
        port: 6379
        password: password
        lettuce:
          pool:
            max-active: 8
            max-wait: -1
            max-idle: 8
            min-idle: 0

###### Redis Configuration

    @SpringBootConfiguration
    @EnableCaching
    public class RedisConfig extends CachingConfigurerSupport{
    
        @Bean
        public KeyGenerator keyGenerator(){
            return new KeyGenerator(){
    
                @Override
                public Object generate(Object o, Method method, Object... objects) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(o.getClass().getName());
                    sb.append(method.getName());
                    for (Object obj : objects) {
                        sb.append(obj.toString());
                    }
                    return sb.toString();
                }
            };
        }
    }
这里可以做Redis设置一些全局配置，比如上面实现的主键生成策略 KeyGenerator ,如果不配置会默认使用参数名做为主键。  @EnableCaching 开启缓存。  
redis集成后我们通常不会直接在业务代码中去使用RedisTemplate直接使用，而应该将Redis操作做进一步的封装供其他模块调用，下面是一些简单的封装样例：

    @Service
    @Slf4j
    public class RedisService {
        @Autowired
        private RedisTemplate redisTemplate;
    
        public boolean set(final String key, Object value){
            boolean result = false;
            try {
                ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
                operations.set(key, value);
                result = true;
            }catch (Exception e){
                log.error("set error: key={}, value={}", key, value, e);
            }
    
            return result;
        }
    
        public boolean set(final String key, Object value, int expire){
            boolean result = false;
            try {
                ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
                operations.set(key, value, expire, TimeUnit.MILLISECONDS);
                result = true;
            }catch (Exception e){
                log.error("set error: key={}, value={}", key, value, e);
            }
    
            return result;
        }
    
        public boolean put(final String hash, final String key, Object value){
            boolean result = false;
    
            try {
                HashOperations<Serializable, Serializable, Object> operations = redisTemplate.opsForHash();
                operations.put(hash, key, value);
                result = true;
            }catch (Exception e){
                log.error("put hash error: hash {}, key {}, value {}", hash, key, value, e);
            }
            return result;
        }
    
        public boolean add(final String key, Object value){
            boolean result = false;
    
            try {
                ListOperations<Serializable, Object> operations = redisTemplate.opsForList();
                operations.rightPush(key, value);
                result = true;
            }catch (Exception e){
                log.error("List push error: key {}, value {}", key, value, e);
            }
            return result;
        }
    
        public boolean hasKey(final String key){
            return redisTemplate.hasKey(key);
        }
    
        public void removePattery(final String pattern){
            Set<Serializable> keys = redisTemplate.keys(pattern);
    
            if(keys.size() > 0){
                redisTemplate.delete(keys);
            }
        }
    
        public void delete(final String key){
            if(redisTemplate.hasKey(key)){
                redisTemplate.delete(key);
            }
        }
    
        public Object get(final String key){
            if(!redisTemplate.hasKey(key)){
                return null;
            }
    
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            return operations.get(key);
        }
    
        public Object get(final String hash, final String key){
            if(!redisTemplate.hasKey(hash)){
                return null;
            }
    
            HashOperations<Serializable, Serializable, Object> operations = redisTemplate.opsForHash();
            return operations.get(hash, key);
        }
    }
    
下面看一下Redis在Spring Boot中的其他应用：
###### 缓存（Cache）

最核心的三个注解：@Cacheable、@CacheEvict、@CachePut
@Cacheable：表示类／方法是可缓存的，将结果存储到缓存中以便后续使用相同参数调用时不需执行实际的方法，直接从缓存中取值。  
@CacheEvict：主要用来清楚缓存  
@CachePut：该注解和@Cacheable类似，不同的是它的请求不会检查缓存是否存在，而是直接从数据库获取值，然后放到指定的缓存。换句话说它可以用来更新缓存。  
具体使用：
###### 依赖

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-cache</artifactId>
		</dependency>
		
###### 代码示例：

    @Cacheable(value = "allMenus", key = "#merchantId")
    @GetMapping("allMenus")
    public DLResponseObject<List<MenuInfo>> getAllMenus(@RequestParam(required=true) String merchantId){
        log.info(">>>>>>>>>>>>> {}", "我将执行方法获取数据");
        List<MenuInfo> menus = menuInfoService.listMenuInfoByMerchantIdJoinTypeInfo(merchantId);
        return DLResponseObject.generate(menus);
    }

    @ApiOperation(value = "添加新菜")
    @CacheEvict(value = "allMenus", key = "#merchantId", allEntries = true)
    @PostMapping("create")
    public DLResponseObject createMenu(@Valid MenuInfoParam menuInfoParam){
        menuInfoService.saveMenu(menuInfoParam.getMenuName(), menuInfoParam.getMerchantId(), menuInfoParam.getPrice(),
                menuInfoParam.getVipPrice(), menuInfoParam.getMenuImg(), menuInfoParam.getSort(), menuInfoParam.getRemarks());

        return DLResponseObject.success();
    }
    
这样就做到了数据缓存，并且在添加操作完成后，清除缓存以达到缓存-数据库数据一致。上面代码好像有个问题，具体现象如下：  
@CacheEvict 中如果指定 allEntries 为 true ，前面指定 key 好像就失效了，会清除所有缓存数据。  

我们可以利用Redis做缓存，也可以利用Redis在Spring Boot项目中实现Session 共享。

#### Day4- 集成RabbitMQ
通常队列服务，会有三个概念：发消息者、队列、收消息者。RabbitMQ 在这个基本概念之上，多做了一层抽象，在发消息者和队列之间加入了交换器（Exchange）。这样发消息者和队列就没有直接联系，转而变成发消息者把消息给交换器，交换器根据调度策略再把消息再给队列  

发消息者 ---> 交换机 ---> Queue A ---> 收消息者A  
交换机有四种类型：Direct、topic、Headers 和 Fanout

###### 依赖

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-amqp</artifactId>
		</dependency>
		
###### 配置

    spring:
      rabbitmq:
        host: 127.0.0.1
        port: 5672
        username: guest
        password: guest

###### Direct Exchange
Direct Exchange 是 RabbitMQ 默认的交换机模式，也是最简单的模式，根据 key 全文匹配去寻找队列。  

####### 定义队列

    @SpringBootConfiguration
    public class RabbitConfig {
    
        @Bean
        public Queue queue(){
            return new Queue("hello");
        }
    
        @Bean
        public Queue message(){
            return new Queue("message");
        }
    }
    
####### 发送者

    @Component
    public class HelloSender {
        @Autowired
        private AmqpTemplate rabbitTemplate;
    
        public void send(){
            String context = "hello" + new Date();
            System.out.println("Sender : " + context);
    
            this.rabbitTemplate.convertAndSend("hello", context);
        }
    }

####### 接收者

    @Component
    @RabbitListener(queues = "hello")
    public class HelloReceiver {
    
        @RabbitHandler
        public void process(String hello){
            System.out.println("Receiver : " + hello);
        }
    }
    
####### 测试

    @Test
    public void hello() throws Exception {
        helloSender.send();
        Thread.sleep(2000l);
    }

同时，该模式支持一对多、多对多的方式进行发送和接收，最终的结果好像是通过负载均衡一样，均衡的接收。

###### Topic Exchange
Topic 是 RabbitMQ 中最灵活的一种方式，可以根据 routing_key 自由的绑定不同的队列。
首先对 Topic 规则配置，这里使用两个队列来测试：

####### 定义队列及规则

    @SpringBootConfiguration
    public class TopicRabbitConfig {
        final static String message = "topic.message";
        final static String messages = "topic.messages";
    
        //定义队列
        @Bean
        public Queue queueMessage(){
            return new Queue(TopicRabbitConfig.message);
        }
    
        @Bean
        public Queue queueMessages(){
            return new Queue(TopicRabbitConfig.messages);
        }
    
        //定义交换机
        @Bean
        TopicExchange exchange(){
            return new TopicExchange("exchange");
        }
    
        //将队列和交换机绑定，并设置routing key
        @Bean
        Binding bindingExchangeMessage(Queue queueMessage, TopicExchange exchange){
            return BindingBuilder.bind(queueMessage).to(exchange).with("topic.message");
        }
    
        @Bean
        Binding bindingExchangeMessages(Queue queueMessages, TopicExchange exchange){
            return BindingBuilder.bind(queueMessages).to(exchange).with("topic.#");
        }
    
    }

####### 发送者

    //topic exchange

    public void send1(){
        String context = "hi, i am message 1";
        System.out.println("Sender : " + context);
        this.rabbitTemplate.convertAndSend("exchange", "topic.message", context);
    }

    public void send2() {
        String context = "hi, i am messages 2";
        System.out.println("Sender : " + context);
        this.rabbitTemplate.convertAndSend("exchange", "topic.messages", context);
    }

####### 接收者

    @Component
    @RabbitListener(queues = "topic.message")
    public class TopicReceiver1 {
    
        @RabbitHandler
        public void process(String message){
            System.out.println("Topic Receiver1 : " + message);
        }
    }
    
复制一份TopicReceiver2。

####### 测试
测试1：

    @Test
    public void topicMessageTest() throws Exception{
        helloSender.send1();
        Thread.sleep(2000l);
    }

结果：  
Sender : hi, i am message 1  
Topic Receiver1 : hi, i am message 1  
Topic Receiver2 : hi, i am message 1  

测试2：

    @Test
    public void topicMessagesTest() throws Exception{
        helloSender.send2();
        Thread.sleep(2000l);
    }

结果：  
Sender : hi, i am messages 2  
Topic Receiver2 : hi, i am messages 2  

测试1中我们通过关键字"topic.message"发送消息，同时发送到两个队列，而测试2中通过关键字"topic.messages"发送消息，只发送到第二个队列。  

#### Day5- 监控控件 Actuator、 Admin
##### 依赖

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>

##### 配置

    management:
      endpoints:
        web:
          exposure:
            include: '*' #开启所有检查项
      endpoint:
        health:
          show-details: always #展示健康指标详细信息
##### 具体应用
###### /actuator/health
health主要用来检查应用的运行状态，这是我们使用最高频的一个监控点，通常使用此接口提醒我们应用实例的运行状态，以及应用不“健康”的原因，如数据库连接、磁盘空间不够等。
###### /actuator/beans
根据示例就可以看出，展示了 bean 的别名、类型、是否单例、类的地址、依赖等信息。
###### /actuator/conditions
Spring Boot 的自动配置功能非常便利，但有时候也意味着出问题比较难找出具体的原因。使用 conditions 可以在应用运行时查看代码了解某个配置在什么条件下生效，或者某个自动配置为什么没有生效。
###### /actuator/configprops
查看配置文件中设置的属性内容以及一些配置属性的默认值。
###### /actuator/env
展示了系统环境变量的配置信息，包括使用的环境变量、JVM 属性、命令行参数、项目使用的 jar 包等信息。和 configprops 不同的是，configprops 关注于配置信息，env 关注运行环境信息。
###### /actuator/heapdump
返回一个 GZip 压缩的 JVM 堆 dump。可以利用VisualVM 工具查看内存快照。
###### /actuator/httptrace
该端点用来返回基本的 HTTP 跟踪信息。默认情况下，跟踪信息的存储采用 org.springframework.boot.actuate.trace.InMemoryTraceRepository 实现的内存方式，始终保留最近的 100 条请求记录。
###### /actuator/metrics
最重要的监控内容之一，主要监控了 JVM 内容使用、GC 情况、类加载信息等。
###### /actuator/mappings
描述全部的 URI 路径，以及它们和控制器的映射关系。
###### /actuator/threaddump
/threaddump 接口会生成当前线程活动的快照，这个功能非常好，方便我们在日常定位问题的时候查看线程的情况，主要展示了线程名、线程 ID、线程的状态、是否等待锁资源等信息。

SpringBootActuator 提供了对单个 SpringBoot 应用的监控，信息包含应用状态、内存、线程、堆栈等，比较全面的监控了 Spring Boot 应用的整个生命周期。  
这样有一些问题：第一，所有的监控都需要调用固定的接口来查看，如果全面查看应用状态需要调用很多接口，并且接口返回的 JSON 信息不方便运营人员理解；第二，如果 Spring Boot 应用集群非常大，每个应用都需要调用不同的接口来查看监控信息，操作非常繁琐低效。在这样的背景下，就诞生了另外一个开源软件：Spring Boot Admin。  
    
Spring Boot Admin不仅可以监控单个 Spring Boot 应用，也可以结合 Spring Cloud 监控注册到服务中心的所有应用状态，再结合报警系统的使用就可以随时感知到应用的状态变化。  
Spring Boot Admin分为服务端和客户端，服务端其实就是一个监控后台用来汇总展示所有的监控信息，客户端就是我们的应用，使用时需要先启动服务端，在启动客户端的时候打开 Actuator 的接口，并指向服务端的地址，这样服务端会定时读取相关信息以达到监控的目的。
###### 监控单体应用
####### Admin Server 端
其实就是一个 web 服务。我定义端端口是16666
######## 关键依赖
    <dependency>
      <groupId>de.codecentric</groupId>
      <artifactId>spring-boot-admin-starter-server</artifactId>
      <version>2.1.0</version>
    </dependency>
    
######## 启动类
    @SpringBootApplication
    @EnableAdminServer
    public class AdminServerApplication {
    
        public static void main(String[] args) {
            SpringApplication.run(AdminServerApplication.class, args);
        }
        
    
    }
使用 @EnableAdminServer 指定是一个Admin Server。

####### Admin Client 端
我们这个工程就做为Client端。
######## 关键依赖
		<dependency>
			<groupId>de.codecentric</groupId>
			<artifactId>spring-boot-admin-starter-client</artifactId>
			<version>2.1.0</version>
		</dependency>
####### 配置
    #配置admin server的地址
    spirng:
      boot:
        admin:
          client:
            url: http://localhost:16666
    #actuator 监控配置
    management:
      endpoints:
        web:
          exposure:
            include: '*' #开启所有检查项
      endpoint:
        health:
          show-details: always #展示健康指标详细信息
          
启动后在admin server端就可以看到应用的监控信息。

###### 监控微服务
如果应用都注册在 Eureka 中就不需要再对每个应用进行配置，Spring Boot Admin 会自动从注册中心抓取应用的相关信息。  
其实就是把Admin Server注册到Eureka 注册中心当中。  
然后在启动类增加 @EnableDiscoveryClient 注解时注册中心可以接收Admin Server的注册。

###### 安全控制
Spring Boot Admin 后台有很多的敏感信息和操作，所以我们需要做一下安全控制。这里我们使用Spring Boot Security来实现。  
####### 依赖
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>

####### 安全控制配置类
    @SpringBootConfiguration
    public class SecuritySecureConfig extends WebSecurityConfigurerAdapter {
    
        private final String adminContextPath;
    
        public SecuritySecureConfig(AdminServerProperties adminServerProperties){
    
            this.adminContextPath = adminServerProperties.getContextPath();
        }
    
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
            successHandler.setTargetUrlParameter("redirectTo");
            successHandler.setDefaultTargetUrl(adminContextPath + "/");
    
            http.authorizeRequests()
                    .antMatchers(adminContextPath + "/assets/**").permitAll()
                    .antMatchers(adminContextPath + "/login").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin().loginPage(adminContextPath + "/login").successHandler(successHandler).and()
                    .logout().logoutUrl(adminContextPath + "/logout").and()
                    .httpBasic().and()
                    .csrf()
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .ignoringAntMatchers(
                            adminContextPath + "/instances",
                            adminContextPath + "/actuator/**"
                    );
        }
    }
这个代码先不解释，后期再单独引入研究Security的使用。

####### 配置文件中配置security的用户名密码
    server:
      port: 16666
    eureka:
      client:
        service-url:
          defaultZone: http://127.0.0.1:8888/eureka/
      instance:
        metadata-map:
          user.name: ${spring.security.user.name}
          user.password: ${spring.security.user.password}
    spring:
      application:
        name: adminServer
      #配置security框架的用户名／密码
      security:
        user:
          name: admin
          password: admin
#### Day6- 测试
为了可以在测试中获取到启动后的上下文环境（Beans），Spring Boot Test 提供了两个注解来支持。测试时只需在测试类的上面添加 @RunWith(SpringRunner.class) 和 @SpringBootTest 注解即可。

###### 示例：
    @SpringBootTest
    @RunWith(SpringRunner.class)
    public class MenuInfoTests {
    
        @Autowired
        private MenuInfoService menuInfoService;
    
        @Test
        public void utTest(){
            menuInfoService.ut();
        }
    }
执行 utTest 方法，控制台会输出 'Hello world!'。这样识别测试结果比较困难，这个环节我们了解几个工具类。

###### OutputCapture
OutputCapture来判断System是否输出了我们想要的内容。

    import org.springframework.boot.test.rule.OutputCapture;
    import static org.assertj.core.api.Assertions.assertThat;
    
    @SpringBootTest
    @RunWith(SpringRunner.class)
    public class MenuInfoTests {
    
        @Rule
        public OutputCapture outputCapture = new OutputCapture();
    
        @Autowired
        private MenuInfoService menuInfoService;
    
        @Test
        public void utTest(){
            menuInfoService.ut();
            /*
            判断System输出的内容是否包含"Hello world"
            */
            assertThat(this.outputCapture.toString().contains("Hello world")).isTrue();
        }
    }
这时测试方法执行后会直接提示是否通过，无需去观察控制台输出。
##### Web 测试

###### MockMvC
    @SpringBootTest
    @RunWith(SpringRunner.class)
    public class MenuInfoControllerTests {
    
        private MockMvc mockMvc;
    
        @Autowired
        private MenuInfoController menuInfoController;
    
        @Before
        public void setUp() throws Exception {
            mockMvc = MockMvcBuilders.standaloneSetup(menuInfoController).build();
        }
    
        @Test
        public void allMenusTest() throws Exception{
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("merchantId", "123");
            mockMvc.perform(MockMvcRequestBuilders.get("/wx/menu/allMenus")
                            //.param("merchantId", "123")
                            .params(params)
                            .accept(MediaType.APPLICATION_JSON_UTF8)).andDo(print());
        }
    }
    
####### - @Before注意意味着在测试用例执行前需要执行的操作，这里是初始化需要建立的测试环境。
####### - MockMvcRequestBuilders.post 是指支持 post 请求，这里其实可以支持各种类型的请求，比如 get 请求、put 请求、patch 请求、delete 请求等。
####### - andDo(print())，andDo()：添加 ResultHandler 结果处理器，print() 打印出请求和相应的内容
    MockHttpServletRequest:
          HTTP Method = GET
          Request URI = /menu/allMenus
           Parameters = {merchantId=[123]}
              Headers = [Accept:"application/json;charset=UTF-8"]
                 Body = <no character encoding set>
        Session Attrs = {}
    
    Handler:
                 Type = com.ej.restaurant.controller.MenuInfoController
               Method = public com.ej.restaurant.result.DLResponseObject<java.util.List<com.ej.restaurant.model.MenuInfo>> com.ej.restaurant.controller.MenuInfoController.getAllMenus(java.lang.String)
    
    Async:
        Async started = false
         Async result = null
    
    Resolved Exception:
                 Type = null
    
    ModelAndView:
            View name = null
                 View = null
                Model = null
    
    FlashMap:
           Attributes = null
    
    MockHttpServletResponse:
               Status = 200
        Error message = null
              Headers = [Content-Type:"application/json;charset=UTF-8"]
         Content type = application/json;charset=UTF-8
                 Body = {"code":100000,"msg":"ok","time":1558939536406,"body":[{"id":"098EAEB80224476180FE5FE79FADFA1F","remarks":null,"createDate":null,"updateDate":null,"status":"USEABLE","menuName":"红烧大肠","merchantId":"123","price":52.00,"vipPrice":48.00,"menuImg":null,"sort":20},{"id":"A6C882CB1C684011ACA74E3104D3317D","remarks":"推荐","createDate":null,"updateDate":null,"status":"USEABLE","menuName":"红烧腐竹","merchantId":"123","price":23.00,"vipPrice":20.00,"menuImg":"https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1558659998&di=12fb6e345c6c23088e685d9ad5dd10a2&src=http://pic0.huitu.com/res/20170716/1367773_20170716140534961121_1.jpg","sort":30},{"id":"D6B459C349484790800173E2B99F9253","remarks":"推荐","createDate":null,"updateDate":null,"status":"USEABLE","menuName":"红烧肉","merchantId":"123","price":45.00,"vipPrice":40.00,"menuImg":"https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1558659998&di=12fb6e345c6c23088e685d9ad5dd10a2&src=http://pic0.huitu.com/res/20170716/1367773_20170716140534961121_1.jpg","sort":1},{"id":"D9F834CE3D094F4E8821FBCBDB82DF94","remarks":"推荐","createDate":null,"updateDate":null,"status":"USEABLE","menuName":"红烧仔鸡","merchantId":"123","price":55.00,"vipPrice":50.00,"menuImg":"https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1558659998&di=12fb6e345c6c23088e685d9ad5dd10a2&src=http://pic0.huitu.com/res/20170716/1367773_20170716140534961121_1.jpg","sort":20}]}
        Forwarded URL = null
       Redirected URL = null
              Cookies = []

根据打印的 Body 信息可以得知MenuInfoController 的 getAllMenus() 方法测试成功。

###### 详细代码示例：
    @Test
    public void allMenusTest() throws Exception{
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("merchantId", "123");
        mockMvc.perform(MockMvcRequestBuilders.get("/wx/menu/allMenus")
                        //.param("merchantId", "123")
                        .params(params)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                        //.andDo(print()); //打印结果
                        //.andReturn().getResponse().getContentAsString(); //获取结果字符串
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(100000));

    }
1）perform 构建一个请求，并且返回 ResultActions 实例  
2）参数传递的两种方法，第一种可以直接调用parma()方法，另一种使用parmas(MultiValueMap params)  
3）contentType(MediaType.APPLICATION_JSON_UTF8) 代表发送端发送的数据格式  
4）accept(MediaType.APPLICATION_JSON_UTF8) 代表客户端希望接受的数据类型格式  
5）andExpect(...) 可以在 perform(...) 函数调用后多次调用，表示对多个条件的判断  
6）status().isOk() 判断请求状态是否返回 200  

##### 另外还用经常使用的Junit测试方法、Assert（断言）的使用，之前的例子中有简单应用

#### Day7 - 部署
使用SpringBoot基本都是做微服务应用，大的分布式架构肯定要利用工具来部署应用，比如：jenkins、docker容器等等。这里我们使用Docker容器来完成部署，第一次接触docker，从网上找了个简单教程，先照着来一遍。  
需要先安装docker。
##### plugins添加docker插件

		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<configuration>
					<fork>true</fork>
				</configuration>
			</plugin>
			<plugin>
				<groupId>com.spotify</groupId>
				<artifactId>docker-maven-plugin</artifactId>
				<version>1.2.0</version>
				<configuration>
					<imageName>${docker.image.prefix}/${project.artifactId}</imageName>
					<dockerDirectory>src/main/docker</dockerDirectory>
					<resources>
						<resource>
							<targetPath>/</targetPath>
							<directory>${project.build.directory}</directory>
							<include>${project.build.finalName}.jar</include>
						</resource>
					</resources>
				</configuration>
			</plugin>
		</plugins>
		
1）${docker.image.prefix}，自定义的镜像名称  
2）${project.artifactId}，项目的 artifactId  
3）${project.build.directory}，构建目录，缺省为 target  
4）${project.build.finalName}，产出物名称，缺省为 ${project.artifactId}-${project.version}  

##### 在目录 src/main/docker 下创建 Dockerfile 文件，Dockerfile 文件用来说明如何来构建镜像
    FROM openjdk:8-jdk-alpine
    VOLUME /tmp
    ADD restaurant-1.0.jar app.jar
    ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
不解释，因为看不懂。

##### 构建
mvn clean package docker:build -Dmaven.test.skip=true

如果构建成功，输入：docker images 会看到docker镜像已经完成。

##### 运行容器
docker run -p 8089:8089 -t springboot/restaurant

这样我们就运行来容器，同时看到应用的启动日志。启动成功后可以像之前访问系统一样去访问应用。  
到目前把常用的工具、组件都集成了。下一步就是完善各个功能。系统上线后，我就去开餐馆了！