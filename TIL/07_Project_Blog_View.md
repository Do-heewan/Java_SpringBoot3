# 07장 블로그 화면 구성하기

![타이틀](https://github.com/user-attachments/assets/ede4053c-1b2c-4cf0-901a-2b7b7eb455ff)

---

<br>

## 7.1 사전 지식 : 타임리프

타임리프는 템플릿 엔진이다. 템플릿 엔진이란? 스프링 서버에서 데이터를 받아 우리가 보는 웹 페이지, 즉 HTML 상에 그 데이터를 넣어 보여주는 도구이다.

### 템플릿 엔진 개념 잡기

```html
<h1 text=${이름}>
<p text=${나이}>
```

h1 태그에는 ${이름}이 text 어트리뷰트로 할당되어 있다. 이것이 템플릿 문법이다.

서버에서 이름, 나이라는 키로 데이터를 템플릿 엔진에 넘겨주면 템플릿 엔진은 이를 받아 HTML에 값을 적용한다.

```json
{
    이름 : "홍길동",
    나이 : 11
}
```

값이 달라지면 그때그때 화면에 반영하니 동적인 웹 페이지를 만들 수 있다.

![image](https://github.com/user-attachments/assets/cfffb4a7-9523-4f51-a7aa-cf3dea3a9e7b)

#### 타임리프 표현식과 문법

<p align = "center">
    <img src = "https://github.com/user-attachments/assets/c74289dd-a4f9-4784-98bc-c44920f849e6">
    <img src = "https://github.com/user-attachments/assets/1d69ef0e-953f-49d8-8ddc-c670800d9669">
</p>

<br>

### 타임리프 사용을 위한 의존성 추가

```gradlee
dependencies {
    ...
    implementation 'org.springframework.boot:spring-boot-starter-thymeleaf' // 타임리프
}
```

---

<br>

## 7.2 블로그 글 목록 뷰 구현하기

컨트롤러의 메서드를 만들고 HTML 뷰를 만들어 뷰를 테스트한다.

### 컨트롤러 메서드 작성하기

요청을 받아 사용자에게 뷰를 보여주려면 **뷰 컨트롤러**가 필요하다. 뷰 컨트롤러 메서드는 뷰의 이름을 반환하고, 모델 객체에 값을 담는다.

1. dto 패키지에 `ArticleListViewResponse.java` 파일을 작성한다.

```java
@Getter
public class ArticleListViewResponse {
    private final Long id;
    private final String title;
    private final String content;

    public ArticleListViewResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}
```

2. controller 패키지에 `BlogViewController.java` 파일을 만들어 /arricles GET 요청을 처리할 코드를 작성한다.

```java
@RequiredArgsConstructor
@Controller
public class BlogViewController {
    private final BlogService blogService;

    @GetMapping("/articles")
    public String getArticles(Model model) {
        List<ArticleListViewResponse> articles = blogService.findAll().stream()
                .map(ArticleListViewResponse::new).toList();
        model.addAttribute("articles", articles); // 블로그 글 리스트 저장

        return "articleList"; // articleList.html 라는 뷰 조회
    }
}
```

`addAttribute()` 메서드를 사용해 모델이 값을 저장하였다. "articles" 키에 블로그 글 리스트를 저장한다. 반환값인 "articlesList"는 resource/templates/articleList.html을 조회한다.

<br>

### HTML 뷰 만들고 테스트하기

1. resources/templates 디렉터리에 articleList.html을 만들고 모델에 전달할 블로그 글 리스트 개수만큼 반복해 글 정보를 보여주도록 코드를 작성한다.

```html
<!DOCTYPE html>
<html xmlns:th = "http://www.thymeleaf.org">
<head>
    <meta charset = "UTF=8">
    <title>블로그 글 목록</title>
    <link rel = "stylesheet" href = "https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css">
</head>
<body>
    <div class = "p-5 mb-5 text-center</> bg-light">
        <h1 class = "mb-3">My Blog</h1>
        <h4 class = "mb-3">블로그에 오신 것을 환영합니다.</h4>
    </div>

<div class = "container">
    <div class = "row-6" th:each="item : ${articles}">
        <div class = "card">
            <div class = "card-header" th:text = "${item.id}">
            </div>
            <div class = card-body>
                <h5 class = "card-title" th:text = "${item.title}"></h5>
                <p class = "card-text" th:text = "${item.content}"></p>
                <a href = "#" class = "btn btn-primary">보러 가기</a>
            </div>
        </div>
    </div>
</div>
</body>
</html>
```

2. http://localhost:8080/articles 에 접속하여 결과를 확인한다.

![image](https://github.com/user-attachments/assets/13f2fc58-9f25-4a0e-9907-e942ef979696)

---

<br>

## 7.3 블로그 글 뷰 구현하기

블로그 화면 상의 [보러가기] 버튼을 누르면 블로그 글이 보이도록 블로그 글 뷰를 구현한다. 엔티티에 생성 시간, 수정 시간을 추가하고 컨트롤러 메서드를 만든 다음 HTML 뷰를 만들고 확인한다.

### 엔티티에 생성, 수정 시간 추가하기

1. `Article.java` 파일에 다음을 추가한다.

```java
public class Article {
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createAt;

    @LastModifiedDate // 엔티티가 수정될 때 수정 시간 저장
    @Column(name = "updated_at")
    private LocalDateTime updateAt;
}
```

2. 엔티티를 생성하면 생성 시간과 수정 시간이 자동으로 저장된다. `data.sql`에 생성 시간과 수정 시간을 추가하여 실행할 때 마다 바뀌도록 한다.

```SQL
INSERT INTO article (title, content, created_at, updated_at) VALUES ('제목 1', '내용 1', NOW(), NOW());
INSERT INTO article (title, content, created_at, updated_at) VALUES ('제목 2', '내용 2', NOW(), NOW());
INSERT INTO article (title, content, created_at, updated_at) VALUES ('제목 3', '내용 3', NOW(), NOW());
```

3. `SpringBootDeveloperApplication.java` 파일에 엔티티의 created_at, update_at을 자동으로 업데이트하기 위한 애너테이션을 추가한다.

```java
@EnableJpaAuditing
@SpringBootApplication
public class SpringBootDeveloperApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootDeveloperApplication.class, args);
    }
}

```

<br>

### 컨트롤러 메서드 작성하기

1. 뷰에서 사용할 DTO를 작성한다. dto 패키지에 `ArticleViewResponse.java`를 작성한다.

```java
@NoArgsConstructor
@Getter
public class ArticleViewResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public ArticleViewResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.createdAt = article.getCreateAt();
    }
}
```

2. 블로그 글을 반환할 컨트롤러의 메서드를 작성한다. `BlogViewController.java`에 다음을 추가한다.

```java
@RequiredArgsConstructor
@Controller
public class BlogViewController {
    private final BlogService blogService;

    @GetMapping("/articles/{id}")
    public String getArticle(@PathVariable Long id, Model model) {
        Article article = blogService.findById(id);
        model.addAttribute("article", new ArticleViewResponse(article));

        return "article";
    }
}
```

<br>

### HTML 뷰 만들기

1. resources/templates 디렉터리에 `article.html` 파일을 작성한다.

```html
<!DOCTYPE html>
<html xmlns:th = "http://www.thymeleaf.org">
<head>
    <meta charset = "UTF=8">
    <title>블로그 글</title>
    <link rel = "stylesheet" href = "https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css">
</head>
<body>
<div class = "p-5 mb-5 text-center</> bg-light">
    <h1 class = "mb-3">My Blog</h1>
    <h4 class = "mb-3">블로그에 오신 것을 환영합니다.</h4>
</div>

<div class = "container mt-5">
    <div class = "row">
        <div class = "col-lg-8">
            <article>
                <header class = "mb-4">
                    <h1 class = "fw-bolder mb-1" th:text = "${article.title}"></h1>
                    <div class = "text-muted fst-italic mb-2" th:text="|posted on ${#temporals.format(article.createdAt, 'yyyy-mm-dd HH:mm')}|"></div>
                </header>
                <section class = "mb-5">
                    <p class = "fs-5 mb-4" th:text = "${article.content}"></p>
                </section>
                <button type = "button" class = "btn btn-primary btn-sm">수정</button>
                <button type = "button" class = "btn btn-secondary btn-sm">삭제</button>
            </article>
        </div>
    </div>
</div>
</body>
</html>
```

2. 글 목록 화면에서 [보러가기] 버튼을 수정해준다.

```html
<a th:href = "@{/articles/{id}(id=${item.id})}" class = "btn btn-primary">보러 가기</a>
```

<br>

### 실행 테스트하기

1. http://localhost:8080/articles 에 접속해 보러가기 버튼을 눌러 결과를 확인해본다.

![image](https://github.com/user-attachments/assets/88fb67e2-a00a-4bdf-bf8b-abb52aadfd3b)

---

<br>

## 7.4 삭제 기능 추가하기

글 상세 화면에서 [삭제] 버튼을 눌러 글을 삭제한다.

### 삭제 기능 코드 작성하기

1. resources/static/js에 `article.js` 파일을 작성한다.

```javascript
// 삭제 기능
const deleteButton = document.getElementById('delete-btn');

if (deleteButton) {
    deleteButton.addEventListener('click', event => {
        let id = document.getElementById('article-id').value;
        fetch(`/api/articles/${id}`, {
            method: 'DELETE'
        })
            .then(() => {
                alert("삭제완료");
                location.replace('/articles');
            });
    });
}
```

2. [삭제] 버튼을 눌렀을 때 삭제하도록 [삭제] 버튼 엘리먼트에 delete-btn이라는 아이디 값을 추가하고 `article.js`가 동작하도록 임포트한다.

```html
<!DOCTYPE html>
<html xmlns:th = "http://www.thymeleaf.org">
<head>
    <meta charset = "UTF=8">
    <title>블로그 글</title>
    <link rel = "stylesheet" href = "https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css">
</head>
<body>
<div class = "p-5 mb-5 text-center</> bg-light">
    <h1 class = "mb-3">My Blog</h1>
    <h4 class = "mb-3">블로그에 오신 것을 환영합니다.</h4>
</div>

<div class = "container mt-5">
    <div class = "row">
        <div class = "col-lg-8">
            <article>
                <input type = "hidden" id = "article-id" th:value = "${article.id}">
                <header class = "mb-4">
                    <h1 class = "fw-bolder mb-1" th:text = "${article.title}"></h1>
                    <div class = "text-muted fst-italic mb-2" th:text="|posted on ${#temporals.format(article.createdAt, 'yyyy-mm-dd HH:mm')}|"></div>
                </header>
                <section class = "mb-5">
                    <p class = "fs-5 mb-4" th:text = "${article.content}"></p>
                </section>
                <button type = "button" class = "btn btn-primary btn-sm">수정</button>
                <button type = "button" id = "delete-btn" class = "btn btn-secondary btn-sm">삭제</button>
            </article>
        </div>
    </div>
</div>

<script src = "/js/articles.js"></script>
</body>
</html>
```

<br>

### 실행 테스트 하기

1. http://localhost:8080/articles/1 에 접속하여 [삭제] 버튼을 눌러 잘 삭제 되는지 확인한다.

![image](https://github.com/user-attachments/assets/3da8a6b0-bfdf-4ac2-982f-c294cd271f85)![image](https://github.com/user-attachments/assets/b63f00ff-cf00-4745-b30d-66e3746fca7b)

---

<br>

## 7.5 수정/생성 기능 추가하기

### 수정/생성 뷰 컨트롤러 작성하기

수정과 생성은 다음과 같은 흐름을 따른다.

![수정생성](https://github.com/user-attachments/assets/a717d0d6-9253-4ae8-b6d6-1a87cab6885d)

1. 수정 화면을 보여주기 위한 컨트롤러 메서드를 추가한다.

```java
@RequiredArgsConstructor
@Controller
public class BlogViewController {
    private final BlogService blogService;

    @GetMapping("/new-article")
    public String newArticle(@RequestParam(required = false) Long id, Model model) {
        if (id == null) {
            model.addAttribute("article", new ArticleViewResponse());
        }
        else {
            Article article = blogService.findById(id);
            model.addAttribute("article", new ArticleViewResponse(article));
        }

        return "newArticle";
    }
}
```



<br>

### 수정/생성 뷰 만들기

1. 컨트롤러 메서드에서 반환하는 `newArticle.html`을 구현한다.

```html
<!DOCTYPE html>
<html xmlns:th = "http://www.thymeleaf.org">
<head>
    <meta charset = "UTF=8">
    <title>블로그 글</title>
    <link rel = "stylesheet" href = "https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css">
</head>
<body>
<div class = "p-5 mb-5 text-center</> bg-light">
    <h1 class = "mb-3">My Blog</h1>
    <h4 class = "mb-3">블로그에 오신 것을 환영합니다.</h4>
</div>

<div class = "container mt-5">
    <div class = "row">
        <div class = "col-lg-8">
            <article>
                <input type = "hidden" id = "article-id" th:value = "${article.id}">
                <header class = "mb-4">
                    <input type = "text" class = "form-control" placeholder = "제목" id = "title" th:value = "${article.title}">
                </header>
                <section class = "mb-5">
                    <p class = "fs-5 mb-4" th:text = "${article.content}"></p>
                </section>
                <button th:if = "${article.id} != null" type = "button" id = "modify-btn" class = "btn btn-primary btn-sm">수정</button>
                <button th:if = "${article.id} == null" type = "button" id = "create-btn" class = "btn btn-primary btn-sm">등록</button>
            </article>
        </div>
    </div>
</div>

<script src = "/js/articles.js"></script>
</body>
</html>
```

2. 실제 수정, 등록 기능을 위한 API를 구현한다.

```javascript
// 수정 기능
const modifyButton = document.getElementById('modify-btn');

if (modifyButton) {
    // 클릭 이벤트가 감지되면 수정 API 요청
    modifyButton.addEventListener('click', event => {
        let params = new URLSearchParams(location.search);
        let id = params.get('id');

        fetch(`/api/articles/${id}`, {
            method: 'PUT',
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                title: document.getElementById('title').value,
                content: document.getElementById('content').value
            })
        })
            .then(() => {
                alert('수정 완료');
                location.replace(`/articles/${id}`);
            });
    });
}
```

3. `article.html` 파일에 [수정] 버튼을 수정한다.

```html
<!DOCTYPE html>
<html xmlns:th = "http://www.thymeleaf.org">
<head>
    <meta charset = "UTF=8">
    <title>블로그 글</title>
    <link rel = "stylesheet" href = "https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css">
</head>
<body>
<div class = "p-5 mb-5 text-center</> bg-light">
    <h1 class = "mb-3">My Blog</h1>
    <h4 class = "mb-3">블로그에 오신 것을 환영합니다.</h4>
</div>

<div class = "container mt-5">
    <div class = "row">
        <div class = "col-lg-8">
            <article>
                <input type = "hidden" id = "article-id" th:value = "${article.id}">
                <header class = "mb-4">
                    <h1 class = "fw-bolder mb-1" th:text = "${article.title}"></h1>
                    <div class = "text-muted fst-italic mb-2" th:text="|posted on ${#temporals.format(article.createdAt, 'yyyy-mm-dd HH:mm')}|"></div>
                </header>
                <section class = "mb-5">
                    <p class = "fs-5 mb-4" th:text = "${article.content}"></p>
                </section>
                <button type = "button" id = "modify-btn" th:onclick = "|location.href = '@{/new-article?id={articleId}(articleId=${article.id})}'|" class = "btn btn-primary btn-sm">수정</button>
                <button type = "button" id = "delete-btn" class = "btn btn-secondary btn-sm">삭제</button>
            </article>
        </div>
    </div>
</div>

<script src = "/js/articles.js"></script>
</body>
</html>
```

<br>

### 실행 테스트하기

1. http://localhost:8080/articles/1 에서 수정 버튼 클릭하여 수정해보기

![image](https://github.com/user-attachments/assets/218a77eb-8633-4b55-97e1-1eb17666165d)![image](https://github.com/user-attachments/assets/42fba9a3-9eff-4d63-a532-776b1de86ebb)

---

<br>

## 7.6 생성 기능 마무리하기

### 생성 기능 작성하기

1. `article.js` 파일에 [등록] 버튼을 누르면 입력 칸에 있는 데이터를 가져와 게시글 생성 API에 글 생성 관련 요청을 보내주는 코드를 작성한다.

```javascript
// 등록 기능
const createButton = document.getElementById("create-btn");

if (createButton) {
    // 클릭 이벤트가 감지되면 생성 API 요청
    createButton.addEventListener("click", (event) => {
        fetch("/api/articles", {
            method: "POST",
            headers: {
                "Conten-Type": "application/json",
            },
            body: JSON.stringify({
                title: document.getElementById("title").value,
                content: document.getElementById("content").value,
            }),
        }).then(() => {
            alert('등록 완료');
            location.replace("/articles");
        });
    });
}
```

2. `articleList.html`에 [생성] 버튼을 추가한다.

```html
<!DOCTYPE html>
<html xmlns:th = "http://www.thymeleaf.org">
<head>
    <meta charset = "UTF=8">
    <title>블로그 글 목록</title>
    <link rel = "stylesheet" href = "https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css">
</head>
<body>
    <div class = "p-5 mb-5 text-center</> bg-light">
        <h1 class = "mb-3">My Blog</h1>
        <h4 class = "mb-3">블로그에 오신 것을 환영합니다.</h4>
    </div>

<div class = "container">
    <button type = "button" id = "create-btn" th:onclick = "|location.href = '@{/new-article}'|" class = "btn btn-secondary btn-sm mb-3">글 등록</button>
    
    <div class = "row-6" th:each="item : ${articles}">
        <div class = "card">
            <div class = "card-header" th:text = "${item.id}">
            </div>
            <div class = card-body>
                <h5 class = "card-title" th:text = "${item.title}"></h5>
                <p class = "card-text" th:text = "${item.content}"></p>
                <a th:href = "@{/articles/{id}(id=${item.id})}" class = "btn btn-primary">보러 가기</a>
            </div>
        </div>
    </div>
</div>
</body>
</html>
```

<br>

### 실행 테스트하기

![image](https://github.com/user-attachments/assets/fd35f9dc-67a3-430f-ae6f-c07e894dcd9a)![image](https://github.com/user-attachments/assets/6516cc22-1667-4a76-b304-d38241124d34)![image](https://github.com/user-attachments/assets/8986ecb2-ea00-4b70-adbd-5bf12b4a2c8e)

---