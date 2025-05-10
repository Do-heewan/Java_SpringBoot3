# 06장 블로그 기획하고 API 만들기

![타이틀](https://github.com/user-attachments/assets/d8d23eeb-c150-4267-b5b1-856074bfe178)

## 6.0 그림으로 이해하는 프로젝트

![그림](https://github.com/user-attachments/assets/831c840c-5698-4354-a043-cbea746fab10)

그림은 웹 브라우저가 POST 요청을 보내고, 이 요청을 BlogApiController 클래스의 특정 메서드인 addArticle() 메서드가 받아 BlogService 클래스의 save() 메서드를 실행시키는 모습이다. 이후 save() 메서드에서는 BlogRepository 클래스, Article 클래스를 거쳐 실제 테이블에 데이터를 저장한다.

---

<br>

## 6.1 사전 지식 : API와 REST API

네트워크에서 API란 프로그램 간에 상호작용하기 위한 매개체를 말한다.

### 웹의 장점을 최대한 활용하는 REST API

REST API는 웹의 장점을 최대한 활용하는 API다. REST는 Representational State Transfer를 줄인 표현인데, 쉽게 말해서 명확하고 이해하기 쉬운 API를 말한다.

#### REST API의 특징

REST API는 서버/클라이언트 구조, 무상태, 캐시 처리 가능, 계층화, 인터페이스 일관성 등의 특징이 있다.

#### REST API의 장점과 단점

REST API의 장점은 URL만 보고도 무슨 행동을 하는 API인지 명확하게 알 수 있다는 것이다. 그리고 상태가 없다는 특징이 있어 클라이언트와 서버의 역할이 명확하게 분리된다. 그리고 HTTP 표준을 사용하는 모든 플랫폼에서 사용할 수 있다.

단점으론 HTTP 메서드, 즉 GET, POST와 같은 방식의 개수에 제한이 있고, 설계를 하기 위해 공식적으로 제공되는 표준 규약이 없다.

**그럼에도 REST API는 주소와 메서드만 보고 요청의 내용을 파악할 수 있다는 강력한 장점이 있어 많은 개발자가 사용한다.**

---

<br>

## 6.2 블로그 개발을 위한 엔티티 구성하기

### 엔티티 구성하기

엔티티 구성은 다음과 같다.

|컬럼명|자료형|null 허용|키|설명|
|---|---|----|---|---|
|id|BIGINT|N|기본키|일련번호, 기본키|
|title|VARCHAR(255)|N||게시물의 제목|
|content|VARCHAR(255)|N||내용|

1. `Article.java` 파일 생성

```java
@Getter
@Entity // 엔티티 지정
@NoArgsConstructor
public class Article {
    // 게터
    @Id //  id 필드를 기본키로 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키를 자동으로 1씩 증가
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "title", nullable = false) // 'title'이라는 not null 컬럼과 매핑
    private String title;

    @Column(name = "content", nullable = false) // 'content'라는 not null 컬럼과 매핑
    private String content;

    @Builder // 빌더 패턴으로 객체 생성
    public Article(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
```

### 리포지터리 만들기

1. `BlogRepository.java` 파일 생성

```java
public interface BlogRepository extends JpaRepository<Article, Long> {
}
```

---

<br>

## 6.3 블로그 글 작성을 위한 API 구현하기

구현 과정은 다음과 같다.

![API구현](https://github.com/user-attachments/assets/b0c88043-bfc6-408f-a289-c6b74ccae1eb)

### 서비스 메서드 코드 작성하기

1. `AddArticleRequest.java` 파일을 dto 패키지에 생성. 

```java
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddArticleRequest {
    private String title;
    private String content;

    public Article toEntity() { // 생성자를 사용해 객체 생성
        return Article.builder()
                .title(title)
                .content(content)
                .build();
    }
}
```

DTO(Data Transfer Object)는 계층끼리 데이터를 교환하기 위해 사용하는 객체이다.

2. service 패키지에 `BlogService.java` 파일 생성

```java
@RequiredArgsConstructor // final이 붙거나 @Notnull이 붙은 필드의 생성자 추가
@Service // 빈으로 등록
public class BlogService {
    private final BlogRepository blogRepository;

    // 블로그 글 추가 메서드
    public Article save(AddArticleRequest request) {
        return blogRepository.save(request.toEntity());
    }
}
```

`save()` 메서드는 JpaRepository에서 지원하는 저장 메서드로, AddArticleRequest 클래스에 저장된 값들을 article 데이터베이스에 저장한다.

![구성](https://github.com/user-attachments/assets/ac1ee3d5-67bb-4989-a315-37c87303ab93)

<br>

### 컨트롤러 메서드 코드 작성하기

URL에 매핑하기 위한 컨트롤러 메서드를 추가한다. 컨트롤러 메서드에는 URL 매핑 애너테이션 `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` 등이 있다.

1. controller 패키지에 `BlogApiController.java` 파일 생성

```java
@RequiredArgsConstructor
@RestController
public class BlogApiController {
    private final BlogService blogService;

    // HTTP 메서드가 POST일 때 전달받은 URL과 동일하면 메서드로 매핑
    @PostMapping("/api/articles")
    // @RequestBody로 요청 본문 값 매핑
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request) {
        Article savedArticle = blogService.save(request);

        // 요청한 자원이 성공적으로 생성되었으며 저장된 블로그 글 정보를 응답 객체에 담아 전송
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);
    }
}

```

![응답코드](https://github.com/user-attachments/assets/2e22b958-3c0d-400b-980b-408a10be8155)

<br>

### API 실행 테스트하기

실제 데이터를 확인하기 위해 H2 콘솔을 활성화해야 한다.

1. `application.yml` 파일을 다음과 같이 작성한다.

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb

  h2:
    console:
      enabled: true
```

2. 스프링 부트 서버를 실행한다. 이후 postman을 이용하여 결과를 본다.

![image](https://github.com/user-attachments/assets/c9bd53cc-27c4-480c-a865-ca6de95eccc3)

3. h2-console에 접속하여 데이터베이스에 값이 잘 저장되었는지 확인해본다.

![image](https://github.com/user-attachments/assets/d83d2b52-9b74-404f-a0c5-dbcfb73ef76b)

---

<br>

## 6.4 블로그 글 목록 조회를 위한 API 구현하기

클라이언트는 데이터베이스에 직접 접근할 수 없기 때문에 이를 API로 구현해야 한다.

### 서비스 메서드 코드 작성하기

1. `BlogService.java` 파일에 데이터베이스에 저장된 글을 모두 가져오는 `findAll()` 메서드를 추가한다.

```java
@RequiredArgsConstructor // final이 붙거나 @Notnull이 붙은 필드의 생성자 추가
@Service // 빈으로 등록
public class BlogService {
    private final BlogRepository blogRepository;

    // 블로그 글 추가 메서드
    public Article save(AddArticleRequest request) {
        return blogRepository.save(request.toEntity());
    }

    public List<Article> findAll() {
        return blogRepository.findAll();
    }
}
```

<br>

### 컨트롤러 메서드 코드 작성하기

/api/articles GET 요청이 들어오면 글 목록을 조회할 findAllArticles() 메서드를 작성한다.

1. 응답을 위한 DTO를 먼저 작성한다. dto 디렉터리에 `ArticleResponse.java` 파일을 작성한다.

```java
@Getter
public class ArticleResponse {
    private final String title;
    private final String content;

    public ArticleResponse(Article article) {
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}
```

2. `BlogApiController.java` 파일에 전체 글을 조회한 후 반환하는 findAllArticles() 메서드를 추가한다.

```java
@RequiredArgsConstructor
@RestController
public class BlogApiController {
    private final BlogService blogService;

    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles() {
        List<ArticleResponse> articles = blogService.findAll()
                .stream()
                .map(ArticleResponse::new)
                .toList();
        return ResponseEntity.ok().body(articles);
    }
}
```

/api/articles GET 요청을 받으면 글 전체를 조회하는 `findAll()` 메서드를 호출한 후 응답용 객체인 ArticleResponse로 파싱해 body에 담아 클라이언트에게 전송한다. 

<br>

### 실행 테스트하기

1. `data.sql`을 다음과 같이 작성한다.

```SQL
INSERT INTO article (title, content) VALUES ('제목 1', '내용 1');
INSERT INTO article (title, content) VALUES ('제목 2', '내용 2');
INSERT INTO article (title, content) VALUES ('제목 3', '내용 3');
```

2. postman을 이용하여 테스트를 진행한다.

![결과](https://github.com/user-attachments/assets/23c47daf-75e9-4af2-a910-9a739eca667b)

---

<br>

## 6.5 블로그 글 조회 API 구현하기

글 하나를 조회하는 API를 구현한다.

### 서비스 메서드 코드 작성하기

1. `BlogService.java` 파일에 글 하나를 조회하는 메서드인 findById() 메서드를 추가한다.

```java
@RequiredArgsConstructor // final이 붙거나 @Notnull이 붙은 필드의 생성자 추가
@Service // 빈으로 등록
public class BlogService {
    private final BlogRepository blogRepository;

    public Article findById(long id) {
        return blogRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not found : " + id));
    }
}
```

findById() 메서드는 JPA에서 제공하는 findById() 메서드를 사용해 ID를 받아 엔티티를 조회하고 없으면 IllegalArgumentException 예외를 발생시킨다.

<br>

### 컨트롤러 메서드 코드 작성하기

1. /api/articles/{id} GET 요청이 들어오면 블로그 글을 조회하기 위해 매핑할 findArticle() 메서드를 `BlogApiController.java` 파일에 작성한다.

```java
@RequiredArgsConstructor
@RestController
public class BlogApiController {
    private final BlogService blogService;

    @GetMapping("/api/articles/{id}")
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id) {
        Article article = blogService.findById(id);

        return ResponseEntity.ok().body(new ArticleResponse(article));
    }
}
```

---

<br>

## 6.6 블로그 글 삭제 API 구현하기

글을 삭제하는 API를 구현한다.

### 서비스 메소드 코드 작성하기

1. `BlogService.java` 파일에 `delete()` 메소드를 추가한다.

```java
@RequiredArgsConstructor // final이 붙거나 @Notnull이 붙은 필드의 생성자 추가
@Service // 빈으로 등록
public class BlogService {
    private final BlogRepository blogRepository;

    public void delete(long id) {
        blogRepository.deleteById(id);
    }
}
```

### 컨트롤러 메서드 코드 작성하기

/api/articles/{id} DELETE 요청이 들어오면 글을 삭제하기 위한 `findArticles()` 메서드를 작성한다.

```java
@RequiredArgsConstructor
@RestController
public class BlogApiController {
    private final BlogService blogService;

    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id) {
        blogService.delete(id);

        return ResponseEntity.ok().build();
    }
}
```

### 실행 테스트하기

1. postman에서 실행해보기

![DELETE](https://github.com/user-attachments/assets/dea90a58-5ce9-4815-948c-59c0aef22961)

![DELETE후](https://github.com/user-attachments/assets/0d25b5c1-e01f-42c3-876d-5b173d3c4d24)

---

<br>

## 6.7 블로그 글 수정 API 구현하기

글을 수정하는 API를 구현한다.

### 서비스 메소드 코드 작성하기

1. `Article.java` 파일에 `update()` 메소드를 추가한다.

```java
@Getter
@Entity // 엔티티 지정
@NoArgsConstructor
public class Article {
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
```

2. 블로그 글 수정 요청을 받을 DTO를 작성한다. dto 디렉터리에 `UpdateArticleRequest.java` 파일을 작성한다.

```java
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateArticleRequest {
    private String title;
    private String content;
}
```

3. DTO가 완성되었으니 `BlogService.java` 파일에 `update()` 메소드를 추가한다.

```java
@RequiredArgsConstructor // final이 붙거나 @Notnull이 붙은 필드의 생성자 추가
@Service // 빈으로 등록
public class BlogService {
    private final BlogRepository blogRepository;

    @Transactional
    public Article update(long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        article.update(request.getTitle(), request.getContent());

        return article;
    }
}
```

![트랜잭션](https://github.com/user-attachments/assets/2615888c-591c-4586-8353-55bf502e731c)

### 컨트롤러 메서드 코드 작성하기

/api/articles/{id} PUT 요청이 들어오면 글을 삭제하기 위한 `updateArticle()` 메서드를 작성한다.

```java
@RequiredArgsConstructor
@RestController
public class BlogApiController {
    private final BlogService blogService;

    @PutMapping("/api/articles/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable long id, @RequestBody UpdateArticleRequest request) {
        Article updateArticle = blogService.update(id, request);

        return ResponseEntity.ok().body(updateArticle);
    }
}
```

<br>

### 실행 테스트하기

1. 포스트맨에서 실행 결과 보기

![image](https://github.com/user-attachments/assets/9cb60fb4-a115-4945-b73c-bf45eaae5f47)

![image](https://github.com/user-attachments/assets/96f32303-2245-495c-a419-647ce6476a8d)

---

