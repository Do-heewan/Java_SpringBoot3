# 09장 JWT로 로그인/로그아웃 구현하기

![타이틀](https://github.com/user-attachments/assets/0c8927f7-c495-40e6-a3b8-6e5982384bf7)

---

<br>

## 9.0 프로젝트 구성

JWT를 도입하여 액세스 토큰, 리프레시 토큰을 사용해 토큰 유효성 검사를 하여 사용자를 인증한다. JWT를 적용한 프로젝트의 구성은 다음과 같다.

![프로젝트](https://github.com/user-attachments/assets/184af730-7ad1-480a-ae68-a3c89580c003)

---

<br>

## 9.1 사전 지식 : 토큰 기반 인증

### 토큰 기반 인증이란?

사용자가 서버에 접근할 때 이 사용자가 인증된 사용자인지 확인하는 방법은 다양하다. 대표적인 방법으로 **서버 기반 인증**과 **토큰 기반 인증**이 있다.

스프링 시큐리티에서는 기본적으로 세션 기반 인증을 제공한다.

토큰 기반 인증은 토큰을 사용하는 방법이다. 토큰은 서버에서 클라이언트를 구분하기 위한 유일한 값인데 서버가 토큰을 생성해서 클라이언트에게 제공하면, 클라이언트는 이 토큰을 가지고 있다가 여러 요청을 이 토큰과 함께 신청한다.

#### 토큰을 전달하고 인증받는 과정

![토큰과정](https://github.com/user-attachments/assets/22f417f8-ab73-4a17-93c2-c12059fd41a1)

1. 클라이언트가 아이디와 비밀번호를 서버에게 전달하면서 인증을 요청
2. 서버는 아이디와 비밀번호를 확인해 유효한 사용자인지 검증한다. 유효한 사용자면 토큰을 생성해서 응답한다.
3. 클라이언트는 서버에서 준 토큰을 저장한다.
4. 이후 인증이 필요한 API를 사용할 때 토큰을 함께 보낸다.
5. 서버는 토큰이 유효한지 검증한다.
6. 토큰이 유효하다면 클라이언트가 요청한 내용을 처리한다.

<br>

### 토큰 기반 인증의 특징

#### 무상태성

- 토큰 기반 인증에서는 클라이언트에서 인증 정보가 담긴 토큰을 생성하고 인증한다. 따라서 클라이언트에서는 사용자의 인증 상태를 유지하면서 이후 요청을 처리해야 하는데 이것을 상태 관리라 한다.
>즉 서버 입정에서는 클라이언트의 인증 정보를 저장하거나 유지하지 않아도 되기 때문에 완전한 무상태로 효율적인 검증을 할 수 있다.

#### 확장성

- 무상태성은 확장성에 영향을 준다. 세션 인증 기반은 각각 API에서 인증을 해야되는 것과는 달리 토큰 기반 인증에서는 토큰을 가지는 주체가 클라이언트이기 때문에 가지고 있는 하나의 토큰으로 서버에게 동시 요청을 보낼 수 있다.

#### 무결성

- 토큰 방식은 HMAC(Hash-based Message Authentication) 기법이라 부른다. 토큰을 발급한 이후에는 토큰 정보를 변경하는 행위를 할 수 없다.
> 즉 토큰의 무결성이 보장된다.

<br>

### JWT

발급받은 JWT를 이용해 인증을 하려면 HTTP 요청 헤더 중에 Authorization 키 값에 **Bearer + JWT 토큰값**을 넣어 보내야 한다.

![JWT](https://github.com/user-attachments/assets/b6c898e1-9c00-4977-8ddf-efdbf9c0ac46)

JWT의 구조는 `.`을 기준으로 헤더, 내용, 서명으로 이루어져 있다.

```
aaaaa . bbbbbb . cccccc
# 헤더   # 내용    # 서명
```

**헤더**에는 토큰의 타입과 해싱 알고리즘을 지정하는 정보를 담는다.

```json
{
    "typ" : "JWT", // 토큰의 타입을 지정한다.
    "alg" : "HS256" // 해싱 알고리즘을 지정한다
}
```

**내용**에는 토큰과 관련된 정보를 담는다. 내용의 한 덩어리를 클레임(claim)이라 부르며, 클레임은 키값의 한 쌍으로 이루어져 있다. 클레임은 등록된 클레임, 공개 클레임, 비공개 클레임으로 나눌 수 있다.

```json
{
    "iss" : "nhw3152@gmail.com", // 등록된 클레임
    "iat" : "1622370878", // 등록된 클레임
    "exp" : "1622372678", // 등록된 클레임
    "https://do-heewan.com/jwt_claims/is_admin" : true, // 공개 클레임
    "email" : "nhw3152@gmail.com", // 비공개 클레임
    "hello" : "안녕하세요!"// 비공개 클레임
}
```

**서명**은 해당 토큰이 조작되었거나 변경되지 않았음을 확인하는 용도로 사용하며, 헤더의 인코딩값과 내용의 인코딩값을 합친 후에 주어진 비밀키를 사용해 해시값을 생성한다.

<br>

### 토큰 유효기간

만약 토큰을 주고받는 환경이 보안에 취약해서 토큰 자체가 노출된다면? 토큰은 발급된 순간부터 그 자체로 인증 수단이 되기 때문에 서버는 토큰과 함께 들어온 요청이 토큰을 탈취한 사용자의 요청인지 확인할 수 없다. 

이러한 문제를 해결하기 위해 토큰의 유효기간을 설정하였다. 하지만 이 유효기간이 너무 짧으면 사용자 입장에서 토큰을 짧은 시간만 활용할 수 있으니 불편하다. 

이러한 불편함을 해결하기 위해 **리프레시 토큰**이 등장한다. 리프레시 토큰은 사용자를 인증하기 위한 액세스 토큰이 만료되었을 때 새로운 액세스 토큰을 발급하기 위해 사용한다.

액세스 토큰의 유효 기간을 짧게 설정하고, 리프레시 토큰의 유효 기간을 길게 설정한다.

![리프레시토큰](https://github.com/user-attachments/assets/524c7d71-2e8c-44ec-9ab9-6363747071af)

1. 클라이언트가 서버에 요청을 한다.
2. 서버는 클라이언트에서 전달한 정보를 바탕으로 인증 정보가 유효한지 확인한 뒤, 액세스 토큰과 리프레시 토큰을 만들어 클라이언트에 전달한다. 클라이언트는 전달받은 토큰을 저장한다.
3. 서버에서 생성한 리프레시 토큰은 DB에도 저장한다.
4. 인증을 필요로 하는 API를 호출할 때 클라이언트에 저장된 액세스 토큰과 함께 API를 요청한다.
5. 서버는 전달받은 액세스 토큰이 유효한지 검사한 뒤에 유효하다면 클라이언트에서 요청한 내용을 처리한다. 
6. 시간이 지나고 액세스 토큰이 만료된 뒤에 클라이언트에서 원하는 정보를 얻기 위해 서버에게 API 요청을 보낸다.
7. 서버는 액세스 토큰이 유효한지 검사한다. 
8. 클라이언트는 이 응답을 받고 저장해둔 리프레시 토큰과 함께 새로운 액세스 토큰을 발급하는 요청을 전송한다.
9. 서버는 전달받은 리프레시 토큰이 유효한지, DB에서 리프레시 토큰을 조회한 후 저장해둔 리프레시 토큰과 같은지 확인한다.
10. 유효한 리프레시 토큰이라면 새로운 액세스 토큰을 생성한 뒤 응답한다. 이후 클라이언트는 4번과 같이 다시 API 요청을 한다.

---

<br>

## 9.2 JWT 서비스 구현하기

### 의존성 추가하기

1. `build.gradle`에 의존성을 추가한다.

``` gradle
dependencies {
    // JWT 추가
    implementation 'io.jsonwebtoken:jjwt:0.9.1' // 자바 JWT 라이브러리
    implementation 'javax.xml.bind:jaxb-api:2.3.1' // XML 문서와 Java 객체 간 매핑 자동화
    testAnnotationProcessor 'org.projectlombok:lombok'
    testImplementation ' org.projectlombok:lombok'
}
```

### 토큰 제공자 추가하기

JWT를 사용해서 JWT를 생성하고 유효한 토큰인지 검증하는 클래스를 추가한다.

1. JWT를 만드려면 이슈 발급자, 비밀키를 필수로 설정해야 한다. `application.yml` 파일을 수정해준다.

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

  datasource:
    url: jdbc:h2:mem:testdb
    username: sa

  h2:
    console:
      enabled: true

jwt:
  issuer: nhw3152@gmail.com
  secret_key: study-springboot
```

2. 해당 값들을 변수로 접근하는 데 사용할 JwtProperties 클래스를 생성한다. config/jwt 패키지에 `JwtProperties.java` 파일을 다음과 같이 작성한다.

```java
@Setter
@Getter
@Component
@ConfigurationProperties("jwt")
public class JwtProperties {
    private String issuer;
    private String secretKey;
}
```

3. 토큰을 생성하고 올바른 토큰인지 유효성을 검사하고 토큰에서 필요한 정보를 가져오는 클래스를 작성한다. config/jwt 패키지에 `TokenProvider.java` 파일을 다음과 같이 작성한다.

```java
@RequiredArgsConstructor
@Service
public class TokenProvider {
    private final JwtProperties jwtProperties;

    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();

        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }
    
    // JWT 토큰 생성 메서드
    private String makeToken(Date expiry, User user) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더 typ : JWT
                .setIssuer(jwtProperties.getIssuer()) // 내용 iss
                .setIssuedAt(now) // 내용 iat : 현재 시간
                .setExpiration(expiry) // 내용 exp : expiry 멤버 변수값
                .setSubject(user.getEmail()) // 내용 sub : 유저의 이메일
                .claim("id", user.getId()) // 클레임 id : 유저 ID
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey()) // 서명 : 비밀값과 함께 해시값을 HS256 방식으로 암호화
                .compact();
    }

    // JWT 토큰 유효성 검증 메서드
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey()) // 비밀값으로 복호화
                    .parseClaimsJws(token);
            return true;
    } catch (Exception e) {
        return false;
        }
    }

    // 토큰 기반으로 인증 정보를 가져오는 메서드
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities), token, authorities);
    }

    // 토큰 기반으로 유저 ID를 가져오는 메서드
    public Long getUserId(String token) {
        Claims claims = getClaims(token);

        return claims.get("id", Long.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }
}
```

<br>

### 리프레시 토큰 도메인 구현하기

리프레시 토큰은 데이터베이스에 저장하는 정보이므로 엔티티와 리포지터리를 추가해야 한다. 만들 엔티티와 매핑되는 테이블 구조는 다음과 같다.

|컬럼명|자료형|null 허용|키|설명|
|---|---|---|---|---|
|id|BIGINT|N|기본키|일련번호_기본키|
|user_id|BIGINT|N||유저 ID|
|refresh_token|BIGINT|N||토큰값|

1. domain 디렉터리에 `RefreshToken.java` 파일을 다음과 같이 작성한다.

```java
@NoArgsConstructor
@Getter
@Entity
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    public RefreshToken(Long userId, String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }

    public RefreshToken update(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
        return this;
    }
}
```

2. repository 디렉터리에 `RefreshTokenRepository.java` 파일을 다음과 같이 작성합니다.

```java
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserId(Long userId);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
```

<br>

### 토큰 필터 구현하기

토큰 필터를 구현한다. 필터는 실제로 각종 요청을 위한 로직으로 전달되기 전후에 URL 패턴에 맞는 모든 요청을 처리하는 기능을 제공한다. 요청이 오면 헤더값을 비교해서 토큰이 있는지 확인하고 유효 토큰이라면 시큐리티 콘텍스트 홀더에 인증 정보를 저장한다.

![토큰필터](https://github.com/user-attachments/assets/a84b16f4-f83f-4f72-a265-0c5b5cfce714)

시큐리티 컨텍스트는 인증 객체가 저장되는 보관소이다. 여기서 인증 정보가 필요할 때 언제든지 인증 객체를 꺼내 사용할 수 있다. 이 클래스는 스레드마다 공간을 할당하는 스레드 로컬에 저장되므로 코드 아무 곳에서나 참조할 수 있고, 다른 스레드와 공유하지 않으므로 독립적으로 사용할 수 있다. 이러한 시큐리티 컨텍스트 객체를 저장하는 객체가 시큐리티 컨텍스트 홀더이다.

1. config 디렉터리에 `TokenAuthenticationFilter.java` 파일을 다음과 같이 작성한다.

```java
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청 헤더의 Authorization 키의 값 조회
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        // 가져온 값에서 접두사 제거
        String token = getAccessToken(authorizationHeader);
        // 가져온 토큰이 유효한지 확인하고, 유효한 때는 인증 정보 설정
        if (tokenProvider.validToken(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
```

이 필터는 액세스 토큰값이 담긴 Authorization 헤더값을 가져온 뒤 액세스 토큰이 유효하다면 인증 정보를 설정한다.

요청 헤더에서 키가 'Authorization'인 필드의 값을 가져온 다음 토큰의 접두사 Bearer를 제외한 값을 얻는다. 만약 값이 null이거나 Bearer로 시작하지 않으면 null을 반환한다.

이어서 가져온 토큰이 유효한지 확인하고, 유효하다면 인증 정보를 관리하는 시큐리티 컨텍스트에 인증 정보를 설정한다.

---

<br>

## 9.3 토큰 API 구현하기

리프레시 토큰을 전달받아 검증하고 유효한 리프레시 토큰이라면 새로운 액세스 토큰을 생성하는 토큰 API를 구현한다.

### 토큰 서비스 추가하기

리프레시 토큰을 전달받아 토큰 제공자를 사용해 새로운 액세스 토큰을 만드는 토큰 서비스 클래스를 생성한다.

1. `UserService.java` 파일에 findById() 메서드를 추가한다.

```java
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("not found"));
    }
}
```

2. service 디렉터리에 `RefreshTokenService.java` 파일을 다음과 같이 작성한다.

```java
@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new IllegalArgumentException("Unexpected Token"));
    }
}
```

3. service 디렉터리에 `TokenService.java` 파일을 다음과 같이 작성한다.

```java
@RequiredArgsConstructor
@Service
public class TokenService {
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public String createNewAccessToken(String refreshToken) {
        // 토큰 유효성 검사에 실패하면 예외 발생
        if (!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected Token");
        }

        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        User user = userService.findById(userId);

        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }
}
```

<br>

### 컨트롤러 추가하기

실제로 토큰을 발급받는 API를 생성한다.

1. dto 패키지에 토큰 생성 요청 및 응답을 담당할 DTO인 `CreateAccessTokenRequest`와 `CreateAccessTokenResponse` 클래스를 만든다.

```java
@Getter
@Setter
public class CreateAccessTokenRequest {
    private String refreshToken;
}
```

```java
@AllArgsConstructor
@Getter
public class CreateAccessTokenResponse {
    private String accessToken;
}
```

2. 실제로 요청을 받고 처리할 컨트롤러를 생성한다. /api/token POST 요청이 오면 토큰 서비스에서 리프레시 토큰을 기반으로 새로운 액세스 토큰을 만들어주면 된다. controller 패키지에 `TokenApiConroller.java` 파일을 다음과 같이 작성한다.

```java
@RequiredArgsConstructor
@RestController
public class TokenApiController {
    private final TokenService tokenService;

    @PostMapping("/api/token")
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken(@RequestBody CreateAccessTokenRequest request) {
        String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken());

        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateAccessTokenResponse(newAccessToken));
    }
}
```

---