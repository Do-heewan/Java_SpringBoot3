# 스프링 시큐리티로 로그인/로그아웃, 회원가입 구현하기

## 8.0 그림으로 이해하는 프로젝트

---

<br>

## 8.1 사전 지식 : 스프링 시큐리티

스프링 시큐리티는 스프링 기반의 애플리케이션 보안(인증, 인가, 권한)을 담당하는 스프링 하위 프레임워크다. 

**인증과 인가**

인증(authetication)은 사용자의 신원을 입증하는 과정이다. 

인가(authorization)는 사이트의 특정 부분에 접근할 수 있는지 권한을 확인하는 작업이다.

**스프링 시큐리티**

스프링 시큐리티는 스프링 기반 애플리케이션의 보안을 담당하는 스프링 하위 프레임워크이다.

스프링 시큐리티는 필터 기반으로 동작한다.

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

1. `User.java` 파일 생성.

```java
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class User implements UserDetails {
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
```

### 리포지터리 만들기

1. User 엔티티에 대한 리포지터리를 생성한다. `UserRepository.java` 파일을 다음과 같이 작성한다.

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
```

### 서비스 메서드 코드 작성하기

1. 엔티티와 리포지터리가 완성되었으니 스프링 시큐리티에서 로그인을 진행할 때 사용자 정보를 가져오는 코드를 작성한다. `UserDetailService.java` 파일을 다음과 같이 작성한다.

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

---

<br>

## 8.3 시큐리티 설정하기

1. 인증을 위한 도메인과 리포지터리, 서비스가 완성되었으니 실제 인증 처리를 하는 시큐리티 설정 파일 `WebSecurityConfig.java`를 작성한다.

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {


    private final UserDetailService userService;


    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers(new AntPathRequestMatcher("/static/**"));
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests(auth -> auth
                        .requestMatchers(
                                new AntPathRequestMatcher("/login"),
                                new AntPathRequestMatcher("/signup"),
                                new AntPathRequestMatcher("/user")
                        ).permitAll()
                        .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .defaultSuccessUrl("/articles")
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

<br>

## 8.4 회원가입 구현하기

### 서비스 메서드 코드 작성하기

1. 사용자 정보를 담고 있는 객체 작성. `AddUserRequest.java` 파일을 다음과 같이 작성한다.

```java
@Getter
@Setter
public class AddUserRequest {
    private String email;
    private String password;
}
```

2. `AddRequest` 객체를 인수로 받는 회원 정보 추가 메서드를 작성한다. `UserService.java` 파일을 작성한다.

```java
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    public Long save(AddUserRequest dto) {
        return userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .build()).getId();
    }
}
```

### 컨트롤러 작성하기

1. `UserApiController.java` 파일을 작성한다.

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
}
```

---

<br>

## 8.5 회원 가입, 로그인 뷰 작성하기

### 뷰 컨트롤러 작성하기

1. 로그인, 회원 가입 경로로 접근하면 뷰 파일을 연결하는 컨트롤러를 생성한다. `UserViewController.java` 파일을 작성한다.

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

### 로그아웃 뷰 추가하기

1. ```articleList.html```에 [로그아웃] 버튼을 추가한다.

---

<br>

## 8.7 실행 테스트하기

1. 

![로그인화면](https://github.com/user-attachments/assets/5b7af9dc-3d58-497f-9b36-6e488cbae1ce)

2. 

![회원가입화면](https://github.com/user-attachments/assets/3690c7fb-d399-4af8-9598-cc465542022e)