# 02장 스프링부트3 시작하기

![타이틀](https://github.com/user-attachments/assets/86737439-c3be-41f6-9297-99a9c80408ab)

---

<br>

### 2.0 그림으로 이해하는 프로젝트
![그림프로젝트](https://github.com/user-attachments/assets/da29764e-9eb9-4685-8db6-0c24771e6d6d)

다음 그림은 

---

<br>

### 2.1 스프링과 스프링부트

**스프링의 등장**

엔터프라이즈 애플리케이션은 대규모의 복잡한 데이터를 관리하는 애플리케이션이다. 이 엔터프라이즈 애플리케이션은 많은 사용자의 요청을 동시에 처리해야 하므로 서버 성능과 안정성, 보안이 매우 중요하다. 또한 사이트 기능, 즉 비즈니스 로직까지 신경쓰면서 개발하기에는 매우 어렵다. 

이러한 문제들을 해결하기 위해 **스프링 프레임워크**가 등장하였다.

<br>

**스프링을 더 쉽게 만들어주는 스프링 부트**

스프링은 장점이 많은 개발 도구이지만 설정이 매우 복잡하다는 단점이 있다. 이러한 단점을 극복하기 위해 스프링 부트를 출시하였다.

스프링 부트는 스프링 프레임워크를 더욱 쉽고 빠르게 이용할 수 있도록 만들어주는 도구이다. 빠르게 스프링 프로젝트를 설정할 수 있고, 의존성 세트라고 불리는 **스타터**를 사용해 간편하게 의존성을 사용하거나 관리할 수 있다.

스프링 부트는 개발자가 조금 더 비즈니스 로직 개발에만 집중할 수 있도록 만들어주는 도구인 셈이다.

![스프링부트특징](https://github.com/user-attachments/assets/0541c325-a620-44a9-809a-21bba7cca70a)

<br>

**스프링과 스프링부트의 차이점**

||스프링|스프링 부트|
|---|------------------------------------|------------------------------------|
|목적|엔터프라이즈 애플리케이션 개발을 더 쉽게 만들기|스프링의 개발을 더 빠르고 쉽게 하기|
|설정 파일|개발자가 수동으로 구성|자동 구성|
|XML|일부 파일은 XML로 직접 생성하고 관리|사용하지 않음|
|인메모리 데이터베이스 지원|지원하지 않음|인메모리 데이터베이스 자동 설정 지원|
|서버|프로젝트를 띄우는 서버(예: 톰캣, 제티)를 별도로 수동 설정|내장형 서버를 제공해 별도의 설정이 필요없음|


---

<br>

### 2.2 스프링 콘셉트 공부하기

스프링 프레임워크가 돌아가는 원리를 이해하기 위해 스프링 콘셉트를 우선 공부해야 한다.

스프링의 중요한 콘셉트라 할 수 있는 **제어의 역전**과 **의존성 주입**을 먼저 알아보고 스프링 컨테이너와 빈에 대한 개념을 알아보도록 하자.

<br>

**제어의 역전과 의존성 주입**
>스프링은 모든 기능의 기반을 제어의 역전(IoC)와 의존성 주입(DI)에 두고 있다.

<br>

**IoC(Inversion of Control)란?**

자바 코드를 작성해 객체를 생성할 때, 지금까지는 객체가 필요한 곳에서 직접 생성하였다.

```java
public class A {
    b = new B(); // 클래스 A에서 new 키워드로 클래스 B의 객체 생성
}
```

제어의 역전은 다른 객체를 직접 생성하거나 제어하는 것이 아니라 외부에서 관리하는 객체를 가져와 사용하는 것을 말한다.

위의 코드에서 제어의 역전을 적용하면 다음과 같다.

```java
public class A {
    private B b; // 코드에서 객체를 생성하지 않음. 어디선가 받아온 객체를 b에 할당
}
```

실제로 스프링은 스프링 컨테이너가 객체를 관리, 제공하는 역할을 한다.

<br>

**DI(Dependency Injection)란?**

제어의 역전을 구현하기 위해 사용하는 방법이다. DI는 어떤 클래스가 다른 클래스에 의존한다는 뜻이다.

```java
public class A {
    // A에서 B를 주입받음
    @Autowired
    B b;
}
```

`@Autowired`라는 애너테이션은 스프링 컨테이너에 있는 `빈`을 주입하는 역할을 한다.
>빈은 스프링 컨테이너에서 관리하는 객체이다.

위의 코드에서 `B b;`라고 선언했지만 직접 객체를 생성하지 않고 있다. 다시 말해 **객체를 주입**받고 있다.

![의존성주입](https://github.com/user-attachments/assets/55627d27-7d4c-49c4-888b-2df51511b90b)

<br>
<br>

**빈과 스프링 컨테이너**
>빈은 스프링에서 제공해주는 객체이고, 빈을 생성 관리하는 것이 스프링 컨테이너이다.

<br>

**스프링 컨테이너란?**

스프링은 스프링 컨테이너를 제공한다. 스프링 컨테이너는 빈을 생성하고 관리한다. 즉 빈이 생성되고 소멸되기까지의 생명주기를 이 스프링 컨테이너가 관리하는 것이다. 또한 `@Autowired`같은 애너테이션을 사용해 빈을 주입받을 수 있게 DI를 지원하기도 한다.

<br>

**빈이란?**

빈은 스프링 컨테이너가 생성하고 관리하는 객체이다. 스프링은 빈을 스프링 컨테이너에 등록하기 위해 XML 파일 설정, 애너테이션 추가 등의 방법을 제공한다.

```java
@Component // 클래스 MyBean 빈으로 등록
public class MyBean {
}
```

<br>
<br>

**관점 지향 프로그래밍**

>스프링에서 또 하나 중요한 개념인 **AOP(Aspect Oriented Programming)** 가 있다. 직역하면 관점 지향 프로그래밍이다. 

프로그래밍에 대한 관심을 핵심 관점, 부가 관점으로 나누어 관심 기준으로 모듈화하는 것을 의미한다.

<br>
<br>

**이식 가능한 서비스 추상화**

>마지막 스프링 콘셉트인 PSA(Portable Service Abstraction)이다.

스프링에서 제공하는 다양한 기술들을 추상화해 개발자가 쉽게 사용하는 인터페이스를 말한다.

<br>

#### 한 줄로 정리하는 스프링 핵심 4가지
    - IoC : 객체의 생성과 관리를 개발자가 아닌 프레임워크가 대신하는 것
    - DI : 외부에서 객체를 주입받아 사용하는 것
    - AOP : 프로그래밍을 할 때 핵심 관점과 부가 관점을 나누어 개발하는 것
    - PSA : 어느 기술을 사용하던 일관된 방식으로 처리하도록 하는 것

---

<br>

### 2.3 스프링 부트3 둘러보기

**스프링 부트3 예제 만들기**

#### 01단계 : TestController.java 작성
```java
@RestController
public class TestController {
    @GetMapping("/test")
    public String test() {
        return "Hello, world!";
    }
}
```

#### 02단계 : 스프링 부트 서버 재시작
![서버재시작](https://github.com/user-attachments/assets/3d26ecaf-1c5f-4345-8404-e57ba26ea047)


#### 03단계 : http://localhost:8080/test 로 접속
![test](https://github.com/user-attachments/assets/33e0d5d0-12d7-4ae2-a02c-d8a27d1996f6)


<br>
<br>

**스프링 부트 스타터 살펴보기**

스프링 부트 스타터는 의존성이 모여 있는 그룹이다. 스타터를 사용하면 필요한 기능을 간편하게 설정할 수 있다.
>스타터는 spring-boot-starter-{작업유형} 이라는 명명규칙이 있다.

|스타터|설명|
|-----|------------|
|spring-boot-starter-web|Spring MVC를 사용해서 RESTful 웹 서비스를 개발할 때 필요한 의존성 모음|
|spring-boot-starter-test|스프링 애플리케이션을 테스트하기 위해 필요한 의존성 모음|
|spring-boot-starter-validation|유효성 검사를 위해 필요한 의존성 모음|
|spring-boot-starter-actuator|모니터링을 통해 애플리케이션에서 제공하는 다양한 정보를 제공하기 쉽게 하는 의존성 모음|
|spring-boot-starter-data-jpa|ORM을 사용하기 위한 인터페이스의 모음인 JPA를 더 쉽게 사용하기 위한 의존성 모음|

<br>

#### 01단계 : build.gradle 파일
```
    dependencies {
        testImplementation platform('org.junit:junit-bom:5.10.0')
        testImplementation 'org.springframework.boot:spring-boot-starter-test'
        implementation 'org.springframework.boot:spring-boot-starter-web'
    }
```
>web 스타터와 test 스타터가 의존성으로 명시되어 있음을 확인할 수 있다.

#### 02단계 : web 스타터 확인
![web스타터](https://github.com/user-attachments/assets/c3719de2-bb39-44cc-bf80-edd89f1d3d71)

#### 03단계 : test 스타터 확인
![test스타터](https://github.com/user-attachments/assets/c1f0a2a5-41ef-4561-926f-0c172b5da6d3)

<br>

**자동 구성**

자동 구성은 스프링 부트의 중요한 개념이다. 스프링 부트에서는 애플리케이션이 최소한의 설정만으로도 실행되게 여러 부분을 자동으로 구성한다. 

>**개발을 진행하다가 내가 구성하지 않은 부분인데 스프링에서 자동으로 어떻게 구성하였는지 확인할 상황이 올 수 있기에 이를 알아야 한다.**

스프링 부트는 서버를 시작할 때 구성 파일을 읽어와 설정한다.
>이를 자동설정이라 하고, 자동 설정은 META-INF에 있는 spring.factories 파일에 담겨 있다.

---

<br>

### 2.4 스프링 부트3 코드 이해하기

**@SpringBootApplication 이해하기**

#### 01단계 : SpringBootDeveloperApplication.java

```java
@SpringBootApplication
public class SpringBootDeveloperApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootDeveloperApplication.class, args);
    }
}
```

이 클래스는 자바의 `main()` 메서드와 같은 역할을 한다. `@SpringBootApplication` 애너테이션을 추가하면 스프링 부트 사용에 필요한 기본 설정을 해준다. 

`SpringApplication.run()` 메서드는 애플리케이션을 실행한다. 첫 번째 인수는 스프링 부트3 애플리케이션의 메인 클래스로 사용할 클래스, 두 번째 인수는 커멘드 라인의 인수들을 전달한다.

#### 02단계 : @SpringBootApplication의 의미 파악

```java
@Target(ElementType.TYPE) 
@Retention(RetentionPolicy.RUNTIME) 
@Documented 
@Inherited 
@SpringBootConfiguration // 스프링 부트 관련 설정 
@ComponentScan(excludeFilters = { 
@Filter(type = FilterType.CUSTOM, 
    // 사용자가 등록한 빈을 읽고 등록 
    classes = TypeExcludeFilter . class), 
    @Filter(type = FilterType . CUSTOM, 
    classes = Aut oConfigurationExcludeFi l t er.class) 
}) 
@EnableAut oConfiguration // 자동으로 등록된 빈을 읽고 등록 
public @interface SpringBootApplication {
}
```

#### @springBootConfiguration
- 스프링 부트 관련 설정을 나타내는 애너테이션이다. `@Configuration`을 상속해서 만든 애너테이션이다.

#### @ComponentScan
- 사용자가 등록한 빈을 읽고 등록하는 애너테이션이다. `@Component`라는 애너테이션을 가진 클래스들을 찾아 빈으로 등록하는 역할을 한다.

![ComponentScan](https://github.com/user-attachments/assets/eb1c5d49-a946-49c6-bcac-e73ae4461a26)

#### @EnableAutoConfiguration
- 스프링 부트에서 자동 구성을 활성화하는 애너테이션이다. 스프링 부트 서버가 실행될 때 스프링 부트의 메타파일을 읽고 정의된 설정들을 자동으로 구성하는 역할을 수행한다.

<br>

**테스트 컨트롤러 살펴보기**

```java
@RestController
public class TestController {
    @GetMapping("/test") // /test GET 요청이 들어오면 
    public String test() { // test() 실행
        return "Hello, World!";
    }
}
```

`@RestController`는 라우터 역할을 하는 애너테이션이다. 라우터란 HTTP 요청과 메서드를 연결하는 장치이다.

이 애너테이션이 있어야 클라이언트의 요청에 맞는 메서드를 실행할 수 있다. 지금의 경우 TestController를 라우터로 지정해 /test라는 GET요청이 왔을 때 test() 메서드를 실행하도록 구성한 것이다.

