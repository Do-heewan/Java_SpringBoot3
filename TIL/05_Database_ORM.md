# 05장 데이터베이스 조작이 편해지는 ORM

![타이틀](https://github.com/user-attachments/assets/ceb6892c-5807-45b2-826f-21600af70f16)

---

<br>

## 5.1 데이터베이스란?

데이터베이스는 데이터를 매우 효율적으로 보관하고 꺼내볼 수 있는 공간이다. 데이터베이스를 사용함으로써 안전하게 데이터를 사용하고 관리할 수 있다.

<br>

### 데이터베이스 관리자, DBMS

데이터베이스를 관리하기 위한 소프트웨어를 DBMS(DataBase Management System)이라 한다.

DBMS는 사용자의 요구사항을 만족하면서도 효율적으로 데이터베이스를 관리하고 운영한다.

DBMS는 관리 특징에 따라서 관계형, 객체-관계형, 도큐먼트형, 비관계형 등으로 분류한다.

#### 관계형 DBMS

관계형 DBMS는 Relational DBMS를 줄여서 RDBMS라고 부른다. 관계형이라는 말을 쓰는 이유는 이 DBMS가 관계형 모델인 테이블 형태를 기반으로 하기 때문이다.

|회원테이블|||
|------|---|---|
|ID|이메일|나이|
|1|a@test.com|10|
|2|b@test.com|20|
|3|c@test.com|30|

이때 데이터 1, a@test.com, 10을 묶어서 한 줄을 행이라 하고, ID, 이메일, 나이와 같은 구분을 열이라 한다.

#### H2, MySQL

![H2,MySQL](https://github.com/user-attachments/assets/d1e31f25-021a-4baf-9095-280349e319e5)

H2는 자바로 작성되어 있는 스프링 부트가 지원하는 인메모리 관계형 데이터베이스이다. 

H2는 애플리케이션 자체 내부에 데이터를 저장한다는 특징이 있다. 그래서 애플리케이션을 다시 실행하면 데이터는 초기화 된다.
>간편하게 사용하기 좋아 개발 시에 테스트 용도로 많이 사용한다. 실제 서비스에서는 MySQL 등을 많이 사용한다.

<br>

### 꼭 알아야 할 데이터베이스 용어

![데이터베이스용어](https://github.com/user-attachments/assets/d1c18550-c783-4a7d-82a4-d3dc8df60655)

#### 테이블

데이터베이스를 구성하기 위한 가장 기본적인 단위. 테이블은 행과 열로 구성되며, 행은 여러 속성으로 구성된다.

#### 행

행은 테이블의 가로로 배열된 데이터의 집합을 의미. 행은 반드시 고유한 식별자인 기본키를 가진다. 행은 레코드(Record)라고도 부름.

#### 열

행에 저장되는 유형의 데이터. 데이터 무결성 보장.

#### 기본키 (Primary Key)

기본키는 행을 구분할 수 있는 식별자이다. 이 값은 테이블 내에서 유일해야 하며 중복 값을 가질 수 없다. 보통 데이터를 수정하거나 삭제하고 조회할 때 사용되며 다른 테이블과 관계를 맺어 데이터를 가져올 수도 있다. 또한 기본키의 값은 수정되어서는 안 되며 유효한 값이어야 한다.

#### 쿼리 (Query)

쿼리는 데이터베이스에서 데이터를 조회하거나 삭제, 생성, 수정 같은 처리를 하기 위해 사용하는 명령문이다. SQL이라는 데이터베이스 전용 언어를 사용하여 작성한다.

<br>

### SQL문으로 데이터베이스 조작하는 연습하기

#### 데이터 조회하기 : SELECT 문

테이블에 저장한 데이터를 조회할 때에는 SELECT문을 사용한다. 

```SQL
SELECT <무엇을?>
FROM <어디에서?>
WHERE <무슨?>
```

1. 다음과 같은 customer 테이블이 있을 때, **id가 2인 손님의 이름**을 가져오려면 어떻게 해야 할까?

![image](https://github.com/user-attachments/assets/50b2bdcb-76bc-4558-abed-d956bf932cd5)

```SQL
SELECT name FROM customer WHERE id = 2
```

>조건이 없으면 WHERE은 생략해도 된다.

컬럼을 모두 가져오고 싶을 때는 SELECT절에 모두라는 뜻을 가진 *을 사용하여 가져올 수 있다.

```SQL
SELECT * FROM customers
```

#### 조건 넣어보기 : WHERE 절

|명령어|설명|예시|
|---|---|---|
|=|특정 값과 동일한 값을 가진 행 조회|age=10|
|!= 또는 <>|특정 값과 동일하지 않은 행 조회|age!=10|
|<, >, <=, >=|특정 값과 대소 비교하여 조회|age>10|
|BETWEEN|지정된 값의 사이의 값 조회|age BETWEEN 10 AND 20|
|LIKE|패턴 매칭을 위해 사용, %를 사용하면 와일드카드 처럼 사용 가능|name LIKE '김%'|
|AND|두 조건 모두 참이면 조회|name LIKE '김%'AND '이%'|
|OR|두 조건 중 하나라도 참이면 조회|name LIKE '김%' OR '이%'|
|IS NULL, IS NOT NULL|NULL값의 존재 여부 검사|name IS NULL|

#### 데이터 추가하기 : INSERT 문

데이터베이스의 테이블에 새로운 행을 추가하고 싶을 때는 INSERT문, 삭제하고 싶을 때는 DELETE문을 사용한다.

INSERT문은 INSERT INTO와 VALUES 키워드를 사용한다.

![INSERT](https://github.com/user-attachments/assets/995fed55-a61a-476b-a3d2-b0d81bd8c4a3)

```SQL
INSERT INTO customers (name, phone_number, age) VALUES ('박사번', '010-4444-4444', 40);

INSERT INTO customers (name, phone_number, age) VALUES ('최오번', '010-5555-5555', 51);
```

![INSERT결과](https://github.com/user-attachments/assets/65c8cd50-de12-4053-b5b1-21bbcbef3260)

#### 데이터 삭제하기 : DELETE 문

```SQL
DELETE FROM customers WHERE id = 5;
```

![DELETE](https://github.com/user-attachments/assets/be1a887b-184d-4bca-96b0-4e9a96940a91)

#### 데이터 수정하기 : UPDATE 문

```SQL
UPDATE customers SET age = 11 WHERE name = '김일번';
```

UPDATE 문을 사용할 때, WHERE 절을 생략하면 테이블의 모든 레코드가 수정된다. 따라서 WHERE 절을 잘 사용하였는지 확인하고 UPDATE 문을 실행한다.

---

<br>

## 5.2 ORM이란?

ORM(Object-Relational Mapping)은 자바의 객체와 데이터베이스를 연결하는 프로그래밍 기법이다.

![image](https://github.com/user-attachments/assets/50cb22c1-d684-40a5-b0f4-44f8b9f44341)

ORM이 있다면 데이터베이스의 값을 마치 객체처럼 사용할 수 있다. 

>쉽게 말해 SQL을 몰라도 자바 언어로만 데이터베이스에 접근하여 원하는 데이터를 받아올 수 있다.

즉, 객체와 데이터베이스를 연결해 자바 언어로만 데이터베이스를 다룰 수 있게 하는 도구를 ORM이라 한다.

### ORM의 장단점
- 장점

    1. SQL을 직접 작성하지 않고 사용하는 언어로 데이터베이스에 접근할 수 있다.
    2. 객체지향적으로 코드를 작성할 수 있기 때문에 비즈니스 로직에만 집중할 수 있다.
    3. 데이터베이스의 시스템이 추상화되어 있기 때문에 MySQL에서 PostgreSQL로 전환한다고 해도 추가로 드는 작업이 거의 없다.
    4. 매핑하는 정보가 명확하기 때문에 ERD에 대한 의존도를 낮출 수 있고 유지보수할 때 유리하다.

- 단점

    1. 프로젝트의 복잡성이 커질수록 사용 난이도도 올라간다.
    2. 복잡하고 무거운 쿼리는 ORM으로 해결이 불가능한 경우가 있다.

---

<br>

## 5.3 JPA와 하이버네이트?

DBMS에도 여러 종류가 있는 것 처럼 ORM에도 여러 종류가 있다. 자바에서는 JPA(Java Persistence API)를 표준으로 사용한다.

JPA는 자바에서 관계형 데이터베이스를 사용하는 방식을 정의한 인터페이스인데, 인터페이스이므로 실제 사용을 위해선 ORM 프레임워크를 추가로 선택해야 한다. 대표적으로 하이버네이트를 많이 사용한다.

하이버네이트(Hibernate)는 JPA 인터페이스를 구현한 구현체이자 자바용 ORM 프레임워크이다. 내부적으론 JDBC API를 사용한다. 하이버네이트의 목표는 자바 객체를 통해 데이터베이스 종류에 상관없이 데이터베이스를 자유자재로 사용할 수 있게 하는 것이다.

![JPA와하이버네이트](https://github.com/user-attachments/assets/0e5c9ed3-aec2-43eb-aa51-b662bcca0598)

#### JPA와 하이버네이트의 역할

    - JAP : 자바 객체와 데이터베이스를 연결해 데이터를 관리. 객체 지향 도메인 모델과 데이터베이스의 다리 역할을 한다.
    - 하이버네이트 : JPA의 인터페이스를 구현한다. 내부적으로는 JDBC API를 사용한다.

<br>

### 엔티티 매니저란?

JPA와 하이버네이트에 대해서 알아보았으니, JPA의 중요한 컨셉 중 하나인 엔티티매니저와 영속성 컨텍스트를 알아보자.

#### 엔티티

엔티티(Entity)는 데이터베이스의 테이블과 매핑되는 객체를 의미한다. 엔티티는 본질적으로는 자바 객체이므로 일반 객체와 다르지 않다. 하지만 데이터베이스의 테이블과 직접 연결된다는 아주 특별한 특징이 있어 구분지어 부른다. 

#### 엔티티 매니저

엔티티 매니저(Entity Manager)는 엔티티를 관리해 데이터베이스와 애플리케이션 사이에서 객체를 생성, 수정, 삭제하는 등의 역할을 한다.

이러한 엔티티 매니저를 만드는 곳이 엔티티 매니저 팩토리이다.

![엔티티매니저팩토리](https://github.com/user-attachments/assets/349e7e26-315d-4374-a4a4-95038ef8f656)

>회원 2명이 동시에 회원 가입을 하려는 경우 엔티티 매니저는 다음과 같이 업무를 처리한다.

1. 회원 1의 요청에 대해서 가입 처리를 할 엔티티 매니저를 엔티티 매니저 팩토리가 생성하면 이를 통해 가입 처리해 데이터베이스에 회원 정보를 저장한다.
2. 회원 2의 요청도 동일하게 처리한다.
3. 회원 1, 2를 위해 생성된 엔티티 매니저는 필요한 시점에 데이터베이스와 연결한 뒤에 쿼리한다.

스프링 부트는 내부에서 엔티티 매니저 팩토리를 하나만 생성해서 관리하고 `@PersistenceContext`또는 `@Autowired` 애너테이션을 사용해서 엔티티 매니저를 사용한다.

```java
@PersistenceContext
EntityManager em;
```

>엔티티 매니저는 Spring Data JPA에서 관리하므로 사용자가 직접 생성하거나 관리할 필요가 없다.

<br>

### 영속성 컨텍스트란?

엔티티 매니저는 엔티티를 영속성 컨텍스트에 저장한다는 특징이 있었다. 

영속성 컨텍스트는 JPA의 중요한 특징 중 하나로, **엔티티를 관리하는 가상의 공간이다.**

영속성 컨텍스트에는 1차 캐시, 쓰기 지연, 변경 감지, 지연 로딩의 특징들이 있다.

#### 1차 캐시

영속성 컨텍스트는 내부에 1차 캐시를 가지고 있다. 이때 캐시의 키는 엔티티의 `@Id` 애너테이션이 달린 기본키 역할을 하는 식별자이며 값은 엔티티이다. 엔티티를 조회하면 1차 캐시에서 데이터를 조회하고 값이 있으면 반환한다. 값이 없으면 데이터베이스에서 조회해 1차 캐시에 저장한 다음 반환한다. 이를 통해 캐시된 데이터를 조회할 때에는 데이터베이스를 거치지 않아도 되므로 매우 빠르게 데이터를 조회할 수 있다.

#### 쓰기 지연

쓰기 지연(Transaction Write-Behind)은 트랜잭션을 커밋하기 전까지는 데이터베이스에 실제로 질의문을 보내지 않고 쿼리를 모았다가 트랜잭션을 커밋하면 모았던 쿼리를 한번에 실행하는 것을 의미한다.

적당한 묶음으로 쿼리를 요청할 수 있어 데이터베이스 시스템의 부담을 줄일 수 있다.

#### 변경 감지

트랜잭션을 커밋하면 1차 캐시에 저장되어 있는 엔티티의 값과 현재 엔티티의 값을 비교해서 변경된 값이 있다면 변경 사항을 감지해 변경된 값을 데이터베이스에 자동으로 반영한다.

쓰기 지연과 마찬가지로 적당한 묶음으로 쿼리를 요청할 수 있고, 데이터베이스 시스템의 부담을 줄일 수 있다.

#### 지연 로딩

지연 로딩(Lazy Loading)은 쿼리로 요청한 데이터를 애플리케이션에 바로 로딩하는 것이 아니라 필요할 때 쿼리를 날려 데이터를 조회하는 것을 의미한다.

>반대로 조회할 떄 쿼리를 보내 연관된 모든 데이터를 가져오는 즉시 로딩도 있다.

<br>

### 엔티티의 상태

엔티티는 4가지 상태를 가진다. 영속성 컨텍스트가 관리하고 있지 않는 분리(Delete) 상태, 영속성 컨텍스트가 관리하는 관리(Managed) 상태, 영속성 컨텍스트와 전혀 관계가 없는 비영속(Transient) 상태, 삭제된(Removed) 상태로 나누어진다.

이 상태는 특정 메서드를 호출해 변경할 수 있다.

```java
public class EntityManagerTest {
    @Autowired
    EntityManager em;

    puiblic void example() {
        // 엔티티 매니저가 엔티티를 관리하지 않는 상태(비영속 상태)
        Member member = new Member(1L, "홍길동");

        // 엔티티가 관리되는 상태
        em.persist(member);

        // 엔티티 객체가 분리된 상태
        em.detach(member);

        // 엔티티 객체가 삭제된 상태
        em.remove(member);
    }
}
```

---

<br>

## 5.4 스프링 데이터와 스프링 데이터 JPA

스프링 데이터(Spring Data)는 비즈니스 로직에 더 집중할 수 있게 데이터베이스 사용 기능을 클래스 레벨에서 추상화하였다. 스프링 데이터에서 제공하는 인터페이스를 통해서 스프링 데이터를 사용할 수 있다. 

### 스프링 데이터 JPA란?

스프링 데이터 JPA는 스프링 데이터의  공통적인 기능에서 JPA의 유용한 기능이 추가된 기술이다.

```java
@PersistenceContext
EntityManager em;

public void join() {
    // 기존에 엔티티 상태를 바꾸는 방법(메서드를 호출해서 상태 변경)
    Member member = new Member(1L, "홍길동");
    em.persist(member);
}
```

위의 예제는 앞서 배운 메서드 호출로 엔티티 상태를 바꾸는 예제이다.

스프링 데이터 JPA를 사용하면 리포지터리 역할을 하는 인터페이스를 만들어 데이터베이스의 테이블 조회, 수정, 생성, 삭제 같은 작업을 간단히 할 수 있다.

```java
public interface MemberRepository extends JpaRepository<Member, Long> {

}
```

다음과 같이 JpaRepository 인터페이스를 우리가 만든 인터페이스에서 상속받고, 제네릭에는 관리할 <엔티티 이름, 엔티티 기본키의 타입>을 입력하면 기본 CRUD 메서드를 사용할 수 있다.

<br>

### 스프링 데이터 JPA에서 제공하는 메서드 사용해보기

JPA의 메서드 사용법을 익히기 위해 학습 테스트를 진행한다.

>학습 테스트는 우리가 사용하는 라이브러리, 프레임워크에서 지원하는 기능을 검증하며 어떻게 동작하는지 파악하는 테스트.

#### 조회 메서드 사용해보기

```SQL
SELECT * FROM member;
```

1. 데이터를 조회하려면 먼저 입력된 데이터가 필요하다. `insert-members.sql` 파일을 다음과 같이 작성한다.

```SQL
INSERT INTO member (id, name) VALUES (1, 'A');
INSERT INTO member (id, name) VALUES (2, 'B');
INSERT INTO member (id, name) VALUES (3, 'C');
```

2. `application.yml` 파일을 다음과 같이 작성한다. 이 옵션은 src/main/resources 폴더 내 `data.sql` 파일을 자동으로 실행하지 않게 하는 옵션이다.

```yaml
spring:
  sql:
    init:
      mode: never
```

3. `MemberRepositoryTest.java` 파일에 다음 코드를 작성한다.

```java
@DataJpaTest
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @sql("/insert-members.sql")
    @Test
    void getAllMembers() {
        // when
        List<Member> members = memberRepository.findAll();

        // then
        assertThat(members.size()).isEqualTo(3);
    }
}
```

@Sql 애너테이션을 사용하면 테스트를 실행하기 전에 SQL 스크립트를 실행시킬 수 있다.

4. `MemberRepositoryTest.java` 파일을 테스트 실행한다.

![test](https://github.com/user-attachments/assets/a4dc3e66-47d0-4744-a907-2d2cea5e829d)

5. id가 2인 멤버를 찾는 쿼리문은 다음과 같다.

```SQL
SELECT * FROM member WHERE id = 2;
```

하지만 JPA로는 다음과 같이 작성할 수 있다.

```java
@Sql("/insert-members.sql")
@Test
void getMemberById() {
    // when
    Member member = memberRepository.findById(2L).get();

    // then
    assertThat(member.getName()).isEqualTo("B");
}
```

<br>

#### 쿼리 메서드 사용해보기

id는 모든 테이블에서 기본키로 사용하므로 값이 존재하지 않을 수 없다. 그러나 name은 값이 없을 수도 있으므로 JPA에서 기본으로 name을 찾아주는 메서드를 지원하지는 않는다. 하지만 JAP는 메서드 이름으로 쿼리를 작성하는 기능을 제공한다.

1. name의 값이 'C'인 멤버를 찾는 쿼리문은 다음과 같다.

```SQL
SELECT * FROM member WHERE name = "C";
```

이런 쿼리를 동적 메서드로 만들어보자. `MemberRepository.java` 파일을 연 뒤 `findByName()` 메소드를 추가한다.

```java
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByName(String name);
}
```

2. 실제로 잘 동작하는지 확인하기 위해 `MemberRepositoryTest.java` 파일을 다음과 같이 작성한다.

```java
@Sql("/insert_members.sql")
@Test
void getMemberByName() {
    // when
    Member member = memberRepository.findByName("B").get();

    // then
    assertThat(member.getId()).isEqualTo(2);
}
```

![image](https://github.com/user-attachments/assets/856aebf0-1d6e-40c3-9029-9f854ccea0cf)

이런 기능을 쿼리 메서드라 한다. 쿼리 메서드는 JPA가 정해준 메서드 이름 규칙을 따르면 쿼리문을 특별히 구현하지 않아도 메서드처럼 사용할 수 있다.

<br>

#### 추가, 삭제 메서드 사용해보기

쿼리문을 이용하여 데이터를 추가하기 위해선 다음과 같이 작성한다.

```SQL
INSERT INTO member (id, name) VALUES (1, 'A');
```

JPA에서는 이 쿼리를 직접 입력하는 대신에, `save()`라는 메서드를 사용한다.

1. `MemberRepositoryTest.java` 파일을 다음과 같이 수정한다.

```java
@Test
void saveMember() {
    // given
    Member member = new Member(1L, "A");

    // when
    memberRepository.save(member);

    // then
    assertThat(memberRepository.findById(1L).get().getName()).isEqualTo("A");
}
```

![image](https://github.com/user-attachments/assets/441f84ab-159c-45c6-9e51-b80bb8272e1f)

2. 여러 엔티티를 한꺼번에 저장하려면 `saveAll()` 메서드를 사용할 수 있다.

```java
@Test
void saveMembers() {
    // given
    List<Member> members = List.of(new Member(2L, "B"), new Member(3L, "C"));

    // when
    memberRepository.saveAll(members);

    // then
    assertThat(memberRepository.findAll().size()).isEqualTo(2);
}
```

3. 멤버를 삭제하려면 DELETE 문을 이용하였다.

```SQL
DELETE FROM member WHERE id = 2;
```

JPA에서는 `deleteById()`를 사용하면 아이디로 레코드를 삭제할 수 있다.

```java
@Sql("/insert-members.sql")
@Test
void deleteMemberById() {
    // when
    memberRepository.deleteById(2L);

    // then
    assertThat(memberRepository.findById(2L).isEmpty()).isTrue();
}
```

4. 모든 데이터를 삭제하고 싶다면 `deleteAll()` 메서드를 사용할 수 있다.

```SQL
DELETE FROM member
```

`MemberRepositoryTest.java` 파일에 다음과 같이 추가한다.

```java
@Sql("/insert-members.sql")
@Test
void deleteAll() {
    // when
    memberRepository.deleteAll();

    // then
    assertThat(memberRepository.findAll().size()).isZero();
}
```

하지만 해당 메서드는 실제 모든 데이터를 삭제하므로 실제 서비스 코드에서는 거의 사용하지 않는다.

5. 이를 해결하기 위해 `@AfterEach` 애너테이션을 붙여 `cleanUp()` 메서드와 같은 형태로 사용한다.

```java
@DataJpaTest
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @AfterEach
    public void cleanUp() {
        memberRepository.deleteAll();
    }
}
```

<br>

#### 수정 메서드 사용해보기

특정 레코드 값을 수정할 때에는 UPDATE 문을 사용한다.

```SQL
UPDATE member SET name = "BC" WHERE id = 2;
```

JPA에서는 `@Transactional` 애너테이션을 메서드에 추가해야 한다.

1. `Member.java` 파일에 다음을 추가한다.

```java
public class Member {
    public void changeName(String name) {
        this.name = name;
    }
}
```

이 메서드가 `@Transactional` 애너테이션이 포함된 메서드에서 호출되면 JPA는 변경 감지 기능을 통해 엔티티의 필드값이 변경될 때 그 변경 사항을 데이터베이스에 자동으로 반영한다.

만약 엔티티가 영속 상태일 때 필드값을 변경하고 트랜잭션이 커밋되면 JPA는 변경사항을 데이터베이스에 자동으로 적용한다.

2. `MemberRepositoryTest.java` 파일에 다음과 같이 추가한다.

```java
@Sql("/insert-members.sql")
@Test
void update() {
    // given
    Member member = memberRepository.findById(2L).get();

    // when
    member.changeName("BC");

    // then
    assertThat(memberRepository.findById(2L).get().getName()).isEqualTo("BC");
}
```

3. 이번 코드에는 `@Transactional` 애너테이션이 보이지 않는다. 그 이유는 `@DataJpaTest` 애너테이션을 사용하였기 때문이다.

---

<Br>

## 5.5 예제 코드 살펴보기

### `Member.java` 살펴보기

```java
@Getter
@Entity // 1. 엔티티 지정
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 2. 기본 생성자
@AllArgsConstructor
public class Member {
    public void changeName(String name) {
        this.name = name;
    }

    @Id // 3. id 필드를 기본키로 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 4. 기본키를 자동으로 1씩 증가
    @Column(name = "id", updatable = false) 
    private Long id; // DB 테이블의 'id' 컬럼과 매칭
    
    @Column(name = "name", nullable = false) // 5. name이라는 not null 컬럼과 매핑
    private String name; // DB 테이블의 'name' 컬럼과 매칭
}
```

1. `@Entity` 애너테이션은 Member 객체를 JPA가 관리하는 엔티티로 지정한다.

>즉 Member 클래스와 실제 데이터베이스의 테이블을 매핑시킨다.

`@Entity`의 속성인 name을 사용하여 테이블과 매핑 시킬 수 있다. 생략하면 클래스와 이름이 같은 테이블과 매핑된다.

```java
@Entity(name = "member_list")
public class Article {

}
```

2. protected 기본 생성자이다. 엔티티는 반드시 기본 생성자가 있어야 하고, 접근 제어자는 public 또는 protected 여야 한다.

3. `@Id`는 Long 타입의 id 필드를 테이블의 기본키로 지정한다.

4. `@GeneratedValue`는 기본키의 생성 방식을 결정한다.

5. `@Column` 애너테이션은 데이터베이스의 컬럼과 필드를 매핑해준다.

<br>

### `MemberRepository.java` 살펴보기
```java
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByName(String name);
}
```

리포지터리는 엔티티에 있는 데이터들을 조회하거나 저장, 변경, 삭제를 할 때 사용하는 인터페이스로, 스프링 데이터 JPA에서 제공하는 인터페이스인 JpaRepository 클래스를 상속받아 간단하게 구현할 수 있다.

![리포지토리](https://github.com/user-attachments/assets/7c0bfe03-7205-4c45-aabc-95be93b6626e)

---

