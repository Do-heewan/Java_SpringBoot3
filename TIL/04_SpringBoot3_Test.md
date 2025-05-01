# 04장 스프링 부트3와 테스트

![타이틀](https://github.com/user-attachments/assets/ed440c6b-8b6d-4720-9c51-0031d7709657)

---

<br>

## 4.1 테스트 코드 개념 익히기
테스트 코드는 작성한 코드가 의도대로 잘 동작하고 예상치 못한 문제가 없는지 확인할 목적으로 작성하는 코드이다.

### 테스트 코드란?
테스트 코드는 test 디렉토리에서 작업한다.

테스트 코드에는 다양한 패턴이 있다. 그중 우리가 사용할 패턴은 given-when-then 패턴이다.

given-when-then 패턴은 테스트 코드를 세 단계로 구분해 작성하는 방식을 말한다.

1. given은 테스트 실행을 준비하는 단계
2. when은 테스트를 진행하는 단계
3. then은 테스트 결과를 검증하는 단계

```java
@DisplayName("새로운 메뉴를 저장한다.")
@Test
public void saveMenuTest() {
    // given : 메뉴를 저장하기 위한 준비 과정
    final String name = "아메리카노";
    final int price = 2000;
    final Menu americano = new Menu(name, price);

    // when : 실제로 메뉴를 저장
    final long savedId = menuService.save(americano);

    // then : 메뉴가 잘 추가되었는지 검증
    final Menu savedMenu = menuService.findById(savedId).get();
    assertThat(savedMenu.getName()).isEqualTo(name);
    assertThat(savedMenu.getPrice()).isEqualTo(price);
}
```

코드를 보면 세 부분으로 나누어져 있다. 메뉴를 저장하기 위해 준비하는 과정인 given절, 실제로 메뉴를 저장하는 when절, 메뉴가 잘 추가되었는지 검증하는 then절로 나누어져 있다.

---

<br>

## 4.2 스프링 부트3와 테스트
스프링 부트는 애플리케이션을 테스트하기 위한 도구와 애너테이션을 제공한다. spring-boot-starter-test 스타터에 테스트를 위한 도구가 모여있다.

![테스트도구](https://github.com/user-attachments/assets/dc09ca5c-85f2-427f-8852-75f1e7fa6b9d)

### JUnit이란?
JUnit은 자바 언어를 위한 단위 테스트 프레임워크이다. 단위 테스트란, 작성한 코드가 의도대로 작동하는지 작은 단위로 검증하는 것을 의미한다. 이때 단위는 보통 메서드가 된다. JUnit을 사용하면 단위 테스트를 작성하고 테스트하는 데 도움을 준다.

#### JUnit의 특징
    - 테스트 방식을 구분할 수 있는 애너테이션 제공
    - @Test 애너테이션으로 메서드를 호출할 때마다 새 인스턴스를 생성, 독립 테스트 가능
    - 예상 결과를 검증하는 어설션 메서드 제공
    - 사용 방법이 단순, 테스트 코드 작성 시간이 적음
    - 자동 실행, 자체 결과를 확인하고 즉각적인 피드백 제공

<br>

### JUnit으로 단위 테스트 코드 만들기
1. `JUnitTest.java` 파일을 생성하여 다음을 작성한다.
```java
public class JUnitTest {
    @DisplayName("1+2는 3이다.") // 테스트 이름
    @Test // 테스트 메서드
    public void junitTest() {
        int a = 1;
        int b = 2;
        int sum = 3;

        Assertions.assertEquals(sum, a + b); // 값이 같은지 확인
    }
}
```

@DisplayName 애너테이션은 테스트 이름을 명시한다. @Test 애너테이션을 붙인 메서드는 테스트를 수행하는 메서드가 된다. 

JUnit은 테스트끼리 영향을 주지 않도록 각 테스트를 실행할 때마다 테스트를 위한 실행 객체를 만들고 테스트가 종료되면 실행 객체를 삭제한다.

junitTest() 메서드에 대해 간단히 설명하자면, JUnit에서 제공하는 검증 메서드인 assertEquals() 메서드로 a+b와 sum의 값이 같은지 확인한다.

2. 실제 테스트 코드가 잘 동작하는지 확인해보자. JUnitTest 파일을 우클릭하여 실행해보자.

![JUnitTest](https://github.com/user-attachments/assets/6ae0560d-15fd-4d77-aaba-9d217bfc5cdd)
![테스트실행결과](https://github.com/user-attachments/assets/faea380d-a3b3-4bf1-9a06-7feb946e13d2)

3. 만약 테스트가 실패한다면? 다음의 코드를 추가해보자.

```java
@DisplayName("1+3는 4이다.") // 테스트 이름
@Test // 테스트 메서드
public void junitFailedTest() {
    int a = 1;
    int b = 3;
    int sum = 3;

    Assertions.assertEquals(sum, a + b); // 실패 케이스
}
```

![실패케이스결과](https://github.com/user-attachments/assets/56fad029-3e49-4e65-ad48-4cd8e585e37d)

4. JUnit의 애너테이션을 알아보자. 그리고 JUnitCycleTest.java 파일을 만들어 다음의 코드를 입력해보자.

```java
public class JUnitCycleTest {
    @BeforeAll // 전체 테스트를 시작하기 전에 1회 실행, 메서드는 static으로 선언
    static void beforeAll() {
        System.out.println("Before All");
    }

    @BeforeEach // 테스트 케이스를 시작하기 전마다 실행
    public void beforeEach() {
        System.out.println("Before Each");
    }

    @Test
    public void test1() {
        System.out.println("test1");
    }

    @Test
    public void test2() {
        System.out.println("test2");
    }

    @Test
    public void test3() {
        System.out.println("test3");
    }

    @AfterAll // 전체 테스트를 마치고 종료하기 전에 1번 실행, 메서드는 static으로 선언
    static void afterAll() {
        System.out.println("After All");
    }

    @AfterEach // 테스트 케이스를 종료하기 전마다 실행
    public void afterEach() {
        System.out.println("After Each");
    }
}
```

5. 출력 결과를 살펴보자.

![Cycle 결과](https://github.com/user-attachments/assets/8b77e443-3503-4fb3-9282-122c783c1efe)

<br>

### AssertJ로 검증문 가독성 높이기

AssertJ는 JUnit과 함께 사용해 검증문의 가독성을 높여주는 라이브러리다.

```java
assertThat(a+b).isEqualTo(sum);
```

AssertJ에는 값이 같은지 비교하는 isEqualTo(), isNotEqualTo() 외에도 다양한 메서드를 제공한다.

|메서드 이름|설명|
|---------|----|
|isEqualTo(A)|A 값과 같은지 검증|
|isNotEqual(A)|A값과 다른지 검증|
|contains(A)|A 값을 포함하는지 검증|
|doesNotContain(A)|A 값을 포함하지 않는지 검증|
|startsWith(A)|A로 시작하는지 검증|
|endsWith(A)|A로 끝나는지 검증|
|isEmpty()|빈 값인지 검증|
|isNotEmpty()|비어있지 않는 값인지 검증|
|isPositive()|양수인지 검증|
|isNegative()|음수인지 검증|
|isGreaterThan(1)|1보다 큰 값인지 검증|
|isLessThan(1)|1보다 작은 값인지 검증|

