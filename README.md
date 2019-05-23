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