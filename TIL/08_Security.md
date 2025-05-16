# 스프링 시큐리티로 로그인/로그아웃, 회원가입 구현하기

## 8.0 그림으로 이해하는 프로젝트

다음은 로그인의 구조를 표현한 그림이다.

![그림으로이해하는프로젝트](https://github.com/user-attachments/assets/5abc2d8f-b98a-41ae-b9b8-dad76e26a8e2)

/login 요청이 들어올 때 UserViewController가 해당 요청에 대한 분기 처리를 하고 WebSecurityConfig에 설정한 보안 관련 내용들을 실행한다. UserViewController가 분기 처리를 하여 UserDetailsService를 실행하면 요청을 성공했을 때 defaultSuccessUrl로 설정한 /articles로 리다이렉트하거나 csrf를 disable한다거나 등의 작업을 한다. 

UserDetailService에서는 loadUserByUsername() 메서드를 실행하여 이메일로 유저를 찾고 반환한다. 여기서 유저는 직접 정의한 User 클래스의 객체이고, UserRepository에서 실제 데이터를 가져온다.

![로그아웃](https://github.com/user-attachments/assets/b6805237-3084-4123-8b5d-5f4a3a846694)

다음은 로그아웃의 구성이다. 로그아웃의 구성은 단순하다. /logout 요청이 오면 UserApiController 클래스에서 로그아웃 로직을 실행한다. 로그아웃 로직은 SecurityContextLogoutHander에서 제공하는 logout() 메서드를 실행한다.

---

<br>

## 8.1 사전 지식 : 스프링 시큐리티

스프링 시큐리티는 스프링 기반의 애플리케이션 보안(인증, 인가, 권한)을 담당하는 스프링 하위 프레임워크다. 

### 인증과 인가

인증(authetication)은 사용자의 신원을 입증하는 과정이다. 사용자가 사이트에 로그인을 할 때 누구인지 확인하는 과정을 인증이라 한다.

인가(authorization)는 사이트의 특정 부분에 접근할 수 있는지 권한을 확인하는 작업이다. 예를 들어 관리자는 관리자 페이지에 들어갈 수 있지만, 일반 사용자는 관리자 페이지에 들어갈 수 없다. 이러한 권한을 확인하는 과정을 인가라고 한다.

### 스프링 시큐리티

스프링 시큐리티는 스프링 기반 애플리케이션의 보안을 담당하는 스프링 하위 프레임워크이다. 보안 관련 옵션을 많이 제공하며, 애너테이션 설정이 쉽다. CSRF 공격, 세션 고정 공격을 방어해주고 요청 헤더도 보안 처리를 해주기 때문에 부담을 크게 줄여준다.

>CSRF 공격은 사용자의 권한을 가지고 특정 동작을 수행하도록 유도하는 공격을 말한다.

>세션 고정 공격은 사용자의 인증 정보를 탈취하거나 변조하는 공격을 말한다.

스프링 시큐리티는 필터 기반으로 동작한다.

![스프링시큐리티필터](https://github.com/user-attachments/assets/7b880f9f-b79b-4707-af15-8ae5ac1f1754)

스프링 시큐리티는 다양한 필터들로 나누어져 있으며, 각 필터에서 인증, 인가와 관련된 작업을 처리한다.

![로그인](https://github.com/user-attachments/assets/ae469f6f-2d7c-4310-b47a-124efdcc586b)

1. 사용자가 폼에 아이디와 패스워드를 입력하면, HTTPServletRequest에 아이디와 비밀번호 정보가 전달된다. 이때 AuthenticationFilter가 넘어온 아이디와 비밀번호의 유효성 검사를 한다.
2. 유효성 검사가 끝나면 실제 구현체인 UsernamePasswordAuthenticationToken을 만들어 넘겨준다.
3. 전달받은 인증용 객체인 UsernamePasswordAuthenticationToken을 AuthenticationManager에게 보낸다.
4. UsernamePasswordAuthenticationToken을 AuthenticationProvider에게 보낸다.
5. 사용자 아이디를 UserDetailService에 보낸다. UserDetailService는 사용자 아이디로 찾는 사용자의 정보를 UserDetails 객체로 만들어 AuthenticationProvider에게 전달한다.
6. DB에 있는 사용자 정보를 가져온다.
7. 입력 정보와 UserDetails의 정보를 비교해 실제 인증 처리를 한다.
8. 8~10 까지 인증이 완료되면 SecurityContextHolder에 Authentication을 저장한다.

---

<br>

## 8.2 회원 도메인 만들기

### 의존성 추가하기

1. 스프링 시큐리티를 사용하기 위해 의존성을 추가한다.

```java
dependencies {
    // 스프링 시큐리티 스타터
    implementation 'org.springframework.boot:spring-boot-starter-security'
    // 타임리프에서 스프링 시큐리티를 사용하기 위한 의존성
    implementation 'org.thymeleaf.extras:thymleaf-extras-springsecurity6'
    // 스프링 시큐리티를 테스트하기 위한 의존성
    testImplementation 'org.springframework.security:spring-security-test'
}
```

### 엔티티 만들기

회원 엔티티와 매핑할 테이블의 구조는 다음과 같다.

|컬럼명|자료형|null 허용|키|설명|
|---|---|----|----|----|
|id|BIGINT|N|기본키|일련번호, 기본키|
|email|VARCHAR(255)|N||이메일|
|password|VARCHAR(255)|N||패스워드(암호화하여 저장)|
|created_at|DATETIME|N||생성 일자|
|update_at|DATETIME|N||수정 일자|

1. domain 패키지에 `User.java` 파일을 다음과 같이 작성한다.

```java
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class User implements UserDetails { // UserDetails를 상속받아 인증 객체로 사용
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Builder
    public User(String email, String password, String auth) {
        this.email = email;
        this.password = password;
    }

    @Override // 권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override // 사용자 id를 반환(고유한 값)
    public String getUsername() {
        return email;
    }

    @Override // 사용자의 패스워드 반환
    public String getPassword() {
        return password;
    }

    @Override // 계정 만료 여부 반환
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 패스워드 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 사용 가능 여부 반환
    @Override
    public boolean isEnabled() {
        return true;
    }
}
```

User 클래스가 상속한 UserDetails 클래스는 스프링 시큐리티에서 사용자의 인증 정보를 담아 두는 인터페이스이다. 스프링 시큐리티에서 해당 객체를 통해 인증 정보를 가져오려면 필수 오버라이드 메서드를 여러 개 사용해야 한다.

### 리포지터리 만들기

1. User 엔티티에 대한 리포지터리를 생성한다. repository 디렉터리에 `UserRepository.java` 파일을 다음과 같이 작성한다.

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // email로 사용자 정보를 가져옴
}
```

이메일로 사용자를 식별할 수 있기에 사용자 정보를 가져오기 위해서는 스프링 시큐리티가 이메일을 전달받아야 한다. 스프링 데이터 JPA는 메서드 규칙에 맞춰 메서드를 선언하면 이름을 분석해 자동으로 쿼리를 생성해준다. `findByEmail()` 메서드는 실제 데이터베이스에 회원 정보를 요청할 때 다음 쿼리를 실행한다.

```sql
FROM users WHERE email = #{email}
```

### 서비스 메서드 코드 작성하기

1. 엔티티와 리포지터리가 완성되었으니 스프링 시큐리티에서 로그인을 진행할 때 사용자 정보를 가져오는 코드를 작성한다. service 디렉터리에 `UserDetailService.java` 파일을 다음과 같이 작성한다.

```java
@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException((email)));
    }
}
```

스프링 시큐리티에서 사용자의 정보를 가져오는 UserDetailsService 인터페이스를 구현한다. 필수로 구현해야 하는 loadUserByUsername() 메서드를 오버라이딩해서 사용자 정보를 가져오는 로직을 작성한다.

---

<br>

## 8.3 시큐리티 설정하기

1. 인증을 위한 도메인과 리포지터리, 서비스가 완성되었으니 실제 인증 처리를 하는 시큐리티 설정 파일 `WebSecurityConfig.java`를 config 패키지에 작성한다.

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final UserDetailService userService;

    // 스프링 시큐리티 기능 비활성화
    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers(new AntPathRequestMatcher("/static/**"));
    }

    // 특정 HTTP 요청에 대한 웹 기반 보안 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth // 인증, 인가 설정
                        .requestMatchers(
                                new AntPathRequestMatcher("/login"),
                                new AntPathRequestMatcher("/signup"),
                                new AntPathRequestMatcher("/user")
                        ).permitAll()
                        .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin // 폼 기반 로그인 설정
                        .loginPage("/login")
                        .defaultSuccessUrl("/articles")
                )
                .logout(logout -> logout // 로그아웃 설정
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                )
                .csrf(AbstractHttpConfigurer::disable) // csrf 비활성화
                .build();
    }

    // 인증 관리자 관련 설정
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService); // 사용자 정보 서비스 설정
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return new ProviderManager(authProvider);
    }

    // 패스워드 인코더로 사용할 빈 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

<br>

## 8.4 회원가입 구현하기

시큐리티 설정이 완료 되었으니 회원 가입을 구현한다. 회원 정보를 추가하는 서비스 메서드를 작성한 뒤에 회원 가입 컨트롤러를 작성한다.

### 서비스 메서드 코드 작성하기

1. 사용자 정보를 담고 있는 객체 작성한다. dto 패키지에 `AddUserRequest.java` 파일을 다음과 같이 작성한다.

```java
@Getter
@Setter
public class AddUserRequest {
    private String email;
    private String password;
}
```

2. `AddRequest` 객체를 인수로 받는 회원 정보 추가 메서드를 작성한다. service 패키지에 `UserService.java` 파일을 다음과 같이 작성한다.

```java
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    public Long save(AddUserRequest dto) {
        return userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(bCryptPasswordEncoder.encode(dto.getPassword())) // 패스워드 암호화
                .build()).getId();
    }
}
```

### 컨트롤러 작성하기

회원 가입 폼에서 회원 가입 요청을 받으면 서비스 메서드를 사용해 사용자를 저장한 뒤, 로그인 페이지로 이동하는 `signup()` 메서드를 작성한다.

1. controller 패키지에 `UserApiController.java` 파일을 다음과 같이 작성한다.

```java
@RequiredArgsConstructor
@Controller
public class UserApiController {
    private final UserService userService;

    @PostMapping("/user")
    public String signup(AddUserRequest request) {
        userService.save(request); // 회원 가입 메서드 호출
        return "redirect:/login"; // 회원 가입이 완료된 이후에 로그인 페이지로 이동
    }
}
```

---

<br>

## 8.5 회원 가입, 로그인 뷰 작성하기

회원 가입과 로그인 코드를 모두 작성했다. 사용자가 회원 가입, 로그인 경로에 접근하면 회원 가입, 로그인 화면으로 연결해주는 컨트롤러를 생성하고 사용자가 실제로 볼 수 있는 화면을 작성한다.

### 뷰 컨트롤러 작성하기

1. 로그인, 회원 가입 경로로 접근하면 뷰 파일을 연결하는 컨트롤러를 생성한다. controller 패키지에 `UserViewController.java` 파일을 다음과 같이 작성한다.

```java
@Controller
public class UserViewController {
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }
}
```

/login 경로로 접근하면 `login()` 메서드가 `login.html`을, /signup 경로에 접근하면 `signup()` 메서드가 `signup.html`을 반환한다.

### 뷰 작성하기

1. `login.html` 파일을 작성한다.

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>로그인</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.1/dist/css/bootstrap.min.css">

    <style>
        .gradient-custom {
          background: linear-gradient(to right, rgba(106, 17, 203, 1), rgba(37, 117, 252, 1))
        }
    </style>
</head>
<body class="gradient-custom">
    <section class="d-flex vh-100">
        <div class="container-fluid row justify-content-center align-content-center">
            <div class="card bg-dark" style="border-radius: 1rem;">
                <div class="card-body p-5 text-center">
                    <h2 class="text-white">LOGIN</h2>
                    <p class="text-white-50 mt-2 mb-5">서비스를 사용하려면 로그인을 해주세요!</p>

                    <div class = "mb-2">
                        <form action="/login" method="POST">
                            <input type="hidden" th:name="${_csrf?.parameterName}" th:value="${_csrf?.token}" />
                            <div class="mb-3">
                                <label class="form-label text-white">Email address</label>
                                <input type="email" class="form-control" name="username">
                            </div>
                            <div class="mb-3">
                                <label class="form-label text-white">Password</label>
                                <input type="password" class="form-control" name="password">
                            </div>
                            <button type="submit" class="btn btn-primary">Submit</button>
                        </form>

                        <button type="button" class="btn btn-secondary mt-3" onclick="location.href='/signup'">회원가입</button>
                    </div>
                </div>
            </div>
        </div>
    </section>
</body>
</html>
```

2. `sighup.html` 파일을 작성한다.

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>회원 가입</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.1/dist/css/bootstrap.min.css">

    <style>
        .gradient-custom {
          background: linear-gradient(to right, rgba(254, 238, 229, 1), rgba(229, 193, 197, 1))
        }
    </style>
</head>
<body class="gradient-custom">
<section class="d-flex vh-100">
    <div class="container-fluid row justify-content-center align-content-center">
        <div class="card bg-dark" style="border-radius: 1rem;">
            <div class="card-body p-5 text-center">
                <h2 class="text-white">SIGN UP</h2>
                <p class="text-white-50 mt-2 mb-5">서비스 사용을 위한 회원 가입</p>

                <div class = "mb-2">
                    <form th:action="@{/user}" method="POST">
                        <div class="mb-3">
                            <label class="form-label text-white">Email address</label>
                            <input type="email" class="form-control" name="email">
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-white">Password</label>
                            <input type="password" class="form-control" name="password">
                        </div>

                        <button type="submit" class="btn btn-primary">Submit</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</section>
</body>
</html>
```

---

<br>

## 8.6 로그아웃 구현하기

마지막으로 로그아웃 기능을 구현한다.

### 로그아웃 메서드 추가하기

1. `UserApiController.java` 파일을 다음과 같이 수정한다.

```java
@RequiredArgsConstructor
@Controller
public class UserApiController {
    private final UserService userService;

    @PostMapping("/user")
    public String signup(AddUserRequest request) {
        userService.save(request);
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response,
                SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }
}
```

/logout GET 요청을 하면 로그아웃을 담당하는 핸들러인 SecurityContextLogoutHandler의 `logout()` 메서드를 호출해서 로그아웃 한다.

### 로그아웃 뷰 추가하기

1. ```articleList.html```에 [로그아웃] 버튼을 추가한다.

```html
<button type="button" class="btn btn-secondary" onclick="location.href='/logout'">로그아웃</button>
```

---

<br>

## 8.7 실행 테스트하기

### 테스트를 위한 환경 변수 추가하기

1. 테스트를 진행하기 위해 `application.yml` 파일에 환경변수를 추가한다.

```yaml
spring:
  jpa:
    # 전송 쿼리 확인
    show-sql: true
    properties:
      hibernate:
        format_sql: true

    # 테이블 생성 후에 data.sql 실행
    defer-datasource-initialization: true

  datasource: # 데이터베이스 정보 추가
    url: jdbc:h2:mem:testdb
    username: sa

  h2: # h2 콘솔 활성화
    console:
      enabled: true
```

1. http://localhost:8080/articles 에 접근하면 /aritcles는 인증된 사용자만 들어갈 수 있는 페이지므로 로그인 페이지인 /login으로 리다이렉트 된다.

![로그인화면](https://github.com/user-attachments/assets/5b7af9dc-3d58-497f-9b36-6e488cbae1ce)

2. 회원 가입을 진행한다. http://localhost:8080/signup 이나 [회원가입] 버튼을 눌러 회원 가입 페이지로 이동한다. 회원 가입 페이지는 `permitAll()` 메서드를 사용했기에 별도 인증 없이 접근할 수 있다.

![회원가입화면](https://github.com/user-attachments/assets/3690c7fb-d399-4af8-9598-cc465542022e)

3. 회원 가입 이후 로그인을 진행한다. 로그인을 성공하면 글 목록 페이지로 이동한다.

![Image](https://github.com/user-attachments/assets/c4af4aa1-36f9-4f54-b117-0b0d4ce69fa0)

---