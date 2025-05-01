# 03장 스프링 부트3 구조 이해하기

![타이틀](https://github.com/user-attachments/assets/e038eafd-f191-405e-8a37-698b6234224d)

---

<br>

## 3.0 그림으로 이해하는 프로젝트

![그림으로이해하는프로젝트](https://github.com/user-attachments/assets/0c846a3d-5023-404f-be7a-8ff710db2c8c)

그림에는 전체적인 구조 설명을 위해 웹 브라우저, 스프링 부트 애플리케이션, 각 클래스, 의존성 간의 관계를 표시하였다.

그림을 보면 웹 브라우저의 요청이 있고, 이 요청을 TestController 클래스에서 받아 분기 처리하여 TestService 클래스에 다음 작업을 요청한다.

>TestController 클래스는 "어떤 요청"인지 판단하고, TestService 클래스는 "그 요청에 맞는 작업"을 실행한다.

Member 클래스, MemberRepository 클래스는 데이터베이스를 위한 클래스이다. Member 클래스로 구현한 클래스를 MemberRepository 클래스가 실제 테이블과 연결한다.

---

<br>

## 3.1 스프링 부트3 구조 살펴보기

스프링 부트는 각 계층이 양 옆의 계층과 통신하는 구조를 따른다.

![계층](https://github.com/user-attachments/assets/dc305388-b1dd-40bd-beef-65e0e28828a9)

>계층이란 각자의 역할과 책임이 있는 어떤 소프트웨어의 구성 요소

각 계층은 서로 소통할 수는 있지만 다른 계층에 직접 간섭하거나 영향을 미치지 않는다.

<br>

### 계층 이해하기

#### 프레젠테이션 계층

HTTP 요청을 받고 이 요청을 비즈니스 계층으로 전송하는 역할을 한다. 컨트롤러가 바로 프레젠테이션 역할을 한다. 컨트롤러는 스프링 부트 내 여러 개 존재 가능.

#### 비즈니스 계층

모든 비즈니스 로직을 처리한다. 비즈니스 로직이란 서비스를 만들기 위한 로직을 말한다. 

#### 퍼시스턴스 계층

모든 데이터베이스 관련 로직을 처리한다. 이 과정에서 데이터베이스에 접근하는 DAO 객체를 사용할 수도 있다.

>DAO는 데이터베이스 계층과 상호작용하기 위한 객체라고 이해하면 된다.

<br>

### 스프링 부트 프로젝트 디렉토리 구성하며 살펴보기

#### main

실제 코드를 작성하는 공간. 프로젝트 실행에 필요한 소스 코드나 리소스 파일은 모두 이 폴더 안에 있다.

#### test

프로젝트의 소스 코드를 테스트할 목적의 코드나 리소스 파일이 들어있다.

#### build.gradle

빌드를 설정하는 파일이다. 의존성이나 플러그인 설정 등과 같이 빌드에 필요한 설정을 할 때 사용한다.

#### settings.gradle

빌드할 프로젝트의 정보를 설정하는 파일이다.

![프로젝트 디렉토리](https://github.com/user-attachments/assets/091889d3-34c1-4f1c-8983-f3f0a0fba097)

<br>

### main 디렉토리 구성하기

01. HTML과 같은 뷰 관련 파일들을 넣을 `templates` 디렉토리를 생성한다.
02. static 디렉토리는 JS, CSS, 이미지와 같은 정적 파일을 넣는 용도로 사용한다.
03. 스프링 부트 설정을 할 수 있는 `application.yml` 파일을 생성한다.

![main디렉토리](https://github.com/user-attachments/assets/66379c8d-6929-4a45-87d2-93917b48e983)

---

<br>

## 3.2 스프링 부트3 프로젝트 발전시키기

앞서 언급한 계층에 코드를 추가해보자. 계층이 무엇이고 스프링 부트에서는 계층을 어떻게 나누는지 감을 조금씩 잡아가보도록 하자.

<br>

### build.gradle에 의존성 추가하기

1. `build.gradle`에 필요한 의존성을 추가한다.

스프링 부트용 JPA인 스프링 데이터 JPA, 로컬 환경과 테스트 환경에서 사용할 인메모리 데이터베이스인 H2, 반복 메서드 작성 작업을 줄여주는 라이브러리인 롬복을 추가했다.

```java
dependencies {
    testImplementation platform('org.junit:junit-bom:5.10.0')
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    
    // 스프링 데이터 JPA
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    runtimeOnly 'com.h2database:h2' // 인메모리 데이터베이스
    compileOnly 'org.projectlombok:lombok' // 롬복
    annotationProcessor 'org.projectlombok:lombok'
}
```

2. 오른쪽에 있는 [Gradle] 탭에서 새로고침 버튼을 통해 앞서 추가한 의존성을 다운로드 할 수 있다.

![gradle](https://github.com/user-attachments/assets/58a37848-17cf-4bcb-8ec3-15c92ed80a56)

<br>

### 프레젠테이션, 서비스, 퍼시스턴스 계층 만들기

1. 프레젠테이션 계층에 속하는 **컨트롤러 관련 코드**를 `TestController.java`에 작성한다.

```java
@RestController
public class TestController {
    @Autowired 
    TestService testService; // TestService 빈 주입

    @GetMapping("/test")
    public List<Member> getAllMembers() {
        List<Member> members = testService.getAllMembers();
        return members;
    }
}
```

2. **비즈니스 계층 코드**를 `TestService.java`에 작성한다.

```java
@Service
public class TestService {
    @Autowired
    MemberRepository memberRepository; // 빈 주입

    public List<Member> getAllMembers() {
        return memberRepository.findAll(); // 멤버 목록 얻기
    }
}
```

다음 코드를 그림으로 표현한다면 다음과 같다.

![image](https://github.com/user-attachments/assets/332fb616-cb25-4083-81f7-bc39abb8a22d)

3. **퍼시스턴스 계층 코드**를 `Member.java`에 작성한다.

```java
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id; // DB 테이블의 'id' 컬럼과 매칭
    
    @Column(name = 'name', nullable = false)
    private String name; // DB 테이블의 'name' 컬럼과 매칭
}
```

4. 매핑 작업에는 인터페이스 파일이 필요하다. `MemberRepository.java` 인터페이스 파일을 새로 생성해 필요한 코드를 작성한다.

```java
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
}
```

이 인터페이스는 DB에서 데이터를 가져오는 퍼시스턴스 계층 역할을 한다. 
>자세한 설명은 6장에서 하도록 한다.

<br>

### 임포트 오류 처리하기

`Alt+Enter`를 눌러 해결~!

<br>

### 작동 확인하기

계층 코드 작성을 완료하였다. 이제 스프링 부트 애플리케이션을 실행해보자.

아직은 데이터베이스에 결과물을 볼 수 있는 데이터가 하나도 입력되지 않은 상태이다. 보통은 이런 실행 테스트를 하기 위해 애플리케이션을 실행할 때마다 SQL문을 실행해 데이터베이스에 직접 데이터를 넣는데, 현재는 인메모리 데이터베이스를 사용하고 있기 때문에 애플리케이션을 새로 실행할 때마다 데이터가 사라져 불편하다. 이를 해결하기 위해 애플리케이션을 실행할 때 원하는 데이터를 자동으로 넣는 작업을 하겠다.

1. resource 디렉토리에 `data.sql` 파일을 생성하고 다음과 같이 작성한다.

```sql
INSERT INTO member (id, name) VALUES (1, 'name 1');
INSERT INTO member (id, name) VALUES (2, 'name 2');
INSERT INTO member (id, name) VALUES (3, '노희완');
```

2. application.yml 파일에 다음과 같이 작성한다.

``` yaml
spring:
  jpa:
    # 전송 쿼리 확인
    show-sql: true
    properties:
      hibernate:
        format_sql:true

    # 테이블 생성 후에 data.sql 실행
    defer-datasource-initialization: true
```

show-sql, format_sql 옵션은 애플리케이션 실행 과정에 데이터베이스에 쿼리할 일이 있으면 실행 구문을 모두 보여주는 옵션이고, defer-datasource-initialization 옵션은 애플리케이션을 실행할 때 테이블을 생성하고 data.sql 파일에 있는 쿼리를 실행하도록 하는 옵션이다.

3. 서버 실행 후 서버 콘솔창에서 table이 잘 만들어졌는지 확인한다.

![create table](https://github.com/user-attachments/assets/e9f65078-4268-4203-bbc5-91591a95cf58)

4. 포스트맨으로 HTTP 요청을 해본다. [GET]으로 http://127.0.0.1:8080/test 에 [Send] 하고 결과를 확인해보자.

![포스트맨](https://github.com/user-attachments/assets/33b2e475-da2d-4f8c-88d1-7d4f49c3211b)

포스트맨으로 데이터를 보기까지의 과정은 다음 그림으로 표현할 수 있다.

![포스트맨과정](https://github.com/user-attachments/assets/ebeddf76-1a37-4471-af21-07a5f1e01f66)

---

<br>

## 3.3 스프링 부트 요청-응답 과정 한 방에 이해하기

스프링 부트로 만든 애플리케이션에서 HTTP 요청이 오면 어떤 과정을 거치며 실행되고 응답하는지 알아보자. 

![스프링부트과정](https://github.com/user-attachments/assets/283273fb-e4c4-464b-a870-7171cd026503)

1. 포스트맨에서 톰캣에 /test GET 요청. 이 요청은 스프링 부트 내로 이동. 이때 스프링 부트의 디스패처 서블릿이라는 녀석이 URL을 분석하고, 이 요청을 처리할 수 있는 컨트롤러를 찾는다. TestController가 /test라는 패스에 대한 GET 요청을 처리할 수 있는 getAllMembers() 메서드를 가지고 있으므로 디스패처 서블릿은 TestController에게 /test GET 요청을 전달한다.

2. 마침내 /test GET 요청을 처리할 수 있는 getAllMembers() 메서드와 이 요청이 매치된다. 그리고 getAllMembers() 메서드에서는 비즈니스 계층과 퍼시스턴스 계층을 통하면서 필요한 데이터를 가져온다.

3. 뷰 리졸버는 템플릿 엔진을 사용해 HTML 문서를 만들거나 JSON, XML 등의 데이터를 생성한다.

4. members를 리턴하고 그 데이터를 포스트맨에서 볼 수 있다.

