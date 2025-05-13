# 레디스 기본 걔념

### String
 - 실제 키와 저장되는 아이템이 일대일로 연결되는 유일한 자료구조
 - NX는 키가 없을때만 저장, XX는 키가 있을때만 덮어쓴다.
```bash
 > SET hello world

 > GET hello
"world"

 > SET hello newval NX
 (nil)
 
 > SET hello newval XX
 OK
 
 > GET hello
"newval"
```

### Integer
- 숫자형태의 데이터저장, INCR, INCRBY와 같은 커맨드를 이용하면 String에 저장된 숫자를 원자적으로 조작이 된다.
```bash
 > SET cnt 100
OK

 > INCR cnt
(integer) 101

 > INCR cnt
(integer) 102

 > INCRBY cnt 50
(integer) 152
```

- DECR, DECRBY 커멘드는 동일하게 데이터를 감소시키는 커맨드다

- MSET, MGET등의 명령어를 통해 한번에 데이터를 가져오면서 네트워크 비용을 줄이는것도 방법이다.
```bash
 > MSET a 10 b 20 c 30
OK

 > MGET a b c
1) "10"
2) "20"
3) "30"
```

### List
- 순서가 있는 문자열 컬렉션으로, **스택/큐 구조** 모두 구현 가능
- `LPUSH`는 왼쪽(앞쪽) 삽입, `RPUSH`는 오른쪽(뒤쪽) 삽입
- `LPOP` / `RPOP` 으로 양쪽에서 제거 가능
- `LINDEX`로 인덱스 위치의 요소 조회 가능 (`0`부터 시작)
- `LLEN`으로 리스트 길이 확인 가능
- `LTRIM`으로 지정한 범위만 남기고 나머지는 삭제 (리스트 유지에 유용)
- `LSET`은 특정 인덱스 위치의 값을 덮어씀 (존재하지 않으면 오류 발생)
- `LINSERT`는 기준값 앞/뒤에 새 값을 삽입 (해당 값이 존재해야 함)
- `BLPOP`은 데이터가 없을 경우, **지정한 시간 동안 블로킹(wait)** 하며 대기 후 pop 시도
- **왼쪽 기준**이 기본 동작방향

```bash
> DEL mylist
(integer) 1

> LPUSH mylist "world"
(integer) 1

> LPUSH mylist "hello"
(integer) 2

> LRANGE mylist 0 -1
1) "hello"
2) "world"

> LINDEX mylist 1
"world"

> LLEN mylist
(integer) 2

> LSET mylist 1 "redis"
OK

> LRANGE mylist 0 -1
1) "hello"
2) "redis"

> LINSERT mylist AFTER "hello" "new"
(integer) 3

> LRANGE mylist 0 -1
1) "hello"
2) "new"
3) "redis"

> LTRIM mylist 0 1
OK

> LRANGE mylist 0 -1
1) "hello"
2) "new"

> DEL blockinglist
(integer) 1

# 리스트가 비어 있으므로, 5초 동안 대기 상태에 들어감
> BLPOP blockinglist 5
(nil)

# 다시 시도 — 대기 중에 다른 명령어로 값이 들어오면 즉시 반환
> BLPOP blockinglist 5
(대기 중...)

# 다른 클라이언트에서 아래 명령 실행:
> LPUSH blockinglist "hello"
(integer) 1

# 위의 BLPOP 대기 상태가 즉시 응답됨:
1) "blockinglist"
2) "hello"
> BLPOP blockinglist 5
1) "blockinglist" -> pop이 일어난 리스트의 키 이름
2) "ready" ->  pop된 실제 값
```

- 인덱스나 데이터를 이용하여  중간에 데이터에 접근하는경우, O(n) 으로 처리된다. 그리고 list가 길어질수록 성능은 저하된다.



### Hash
- 하나의 키에 여러 개의 필드-값 쌍을 저장하는 자료구조
- 관계형 DB의 Row와 유사하며, **사용자 정보, 설정값 등 구조화된 데이터 저장**에 적합
- `HSET`으로 필드-값 추가, `HGET`으로 특정 필드 조회
- `HGETALL`, `HKEYS`, `HVALS`로 전체 조회 가능
- `HDEL`로 필드 삭제, `HEXISTS`로 필드 존재 여부 확인
- `HINCRBY`로 숫자 필드 증가 가능 (카운터 용도)

```bash
> HSET user:1 name "Alice"
(integer) 1

> HSET user:1 age 30
(integer) 1

> HGET user:1 name
"Alice"

> HGETALL user:1
1) "name"
2) "Alice"
3) "age"
4) "30"

> HKEYS user:1
1) "name"
2) "age"

> HVALS user:1
1) "Alice"
2) "30"

> HEXISTS user:1 age
(integer) 1

> HDEL user:1 age
(integer) 1

> HGETALL user:1
1) "name"
2) "Alice"

> HINCRBY user:1 login_count 1
(integer) 1

> HINCRBY user:1 login_count 5
(integer) 6
```

- HMGET 으로 동일하게 여러키를 가져올 수 있고, HGETALL로 전체 필드, 값을 가져올 수 있다.
- 키를 왜 저렇게 사용하냐? : 네임스페이스(namespace)처럼 key를 구조화해서 쓰기 위한 관례
- 단순하게 user:1은 문자열이나, 이후에 SCAN user:* 같은 방식으로 데이터를 찾거나, 확장 도메인에도 사용한다.
- ex) order:123:item:4


### Set
- **중복 없는 값들의 집합**을 저장하는 자료구조
- 내부적으로 순서가 없으며, **빠른 존재 여부 확인**이 장점
- `SADD`로 값 추가, `SREM`으로 제거, `SISMEMBER`로 포함 여부 확인
- `SMEMBERS`로 전체 값 조회, `SCARD`로 개수 확인 가능
- `SPOP`은 무작위 값 하나 제거, `SRANDMEMBER`는 제거 없이 무작위 조회
- `SUNION`, `SINTER`, `SDIFF`로 집합 연산 지원 (합집합/교집합/차집합)

```bash
> SADD set1 "apple" "banana" "cherry"
(integer) 3

> SADD set2 "banana" "cherry" "date"
(integer) 3

> SMEMBERS set1
1) "apple"
2) "banana"
3) "cherry"

> SMEMBERS set2
1) "banana"
2) "cherry"
3) "date"

# 합집합 (set1 ∪ set2)
> SUNION set1 set2
1) "apple"
2) "banana"
3) "cherry"
4) "date"

# 교집합 (set1 ∩ set2)
> SINTER set1 set2
1) "banana"
2) "cherry"

# 차집합 (set1 - set2)
> SDIFF set1 set2
1) "apple"

# 차집합 (set2 - set1)
> SDIFF set2 set1
1) "date"
```

### Sorted Set (ZSet)
- **값(value)** + **점수(score)**로 구성된 자료구조로,  
  값들은 자동으로 **점수 기준으로 정렬**됨
- **중복 없는 값**만 저장되며, 점수는 중복 가능
- `ZADD`로 값 추가, `ZRANGE`로 정렬된 결과 조회
- `ZREM`으로 값 삭제, `ZINCRBY`로 점수 증가
- `ZRANK` / `ZREVRANK`로 순위 확인 가능
- `ZRANGEBYSCORE`는 **점수 기반 범위 조회**를 지원하며, `-inf`, `+inf`, `(숫자)`를 사용해 **무한대 범위 및 미포함 조건** 표현 가능
```bash
> ZADD leaderboard 100 "alice"
(integer) 1

> ZADD leaderboard 150 "bob"
(integer) 1

> ZADD leaderboard 120 "charlie"
(integer) 1

> ZRANGE leaderboard 0 -1 WITHSCORES
1) "alice"
2) "100"
3) "charlie"
4) "120"
5) "bob"
6) "150"

> ZREVRANGE leaderboard 0 1 WITHSCORES
1) "bob"
2) "150"
3) "charlie"
4) "120"

> ZINCRBY leaderboard 20 "alice"
"120"

> ZSCORE leaderboard "alice"
"120"

> ZRANK leaderboard "alice"
1

> ZREVRANK leaderboard "alice"
1

> ZREM leaderboard "charlie"
(integer) 1

> ZRANGE leaderboard 0 -1 WITHSCORES
1) "alice"
2) "120"
3) "bob"
4) "150"


-------------

> ZADD leaderboard 100 "alice"
(integer) 1
> ZADD leaderboard 150 "bob"
(integer) 1
> ZADD leaderboard 120 "charlie"
(integer) 1

> ZRANGE leaderboard 0 -1 WITHSCORES
1) "alice"
2) "100"
3) "charlie"
4) "120"
5) "bob"
6) "150"

# ZRANGEBYSCORE: 점수 범위로 필터링

> ZRANGEBYSCORE leaderboard 100 130
1) "alice"
2) "charlie"

> ZRANGEBYSCORE leaderboard (100 130
1) "charlie"   # 100 초과, 130 이하

> ZRANGEBYSCORE leaderboard 100 +inf
1) "alice"
2) "charlie"
3) "bob"

> ZRANGEBYSCORE leaderboard -inf 120
1) "alice"
2) "charlie"

> ZRANGEBYSCORE leaderboard (120 +inf
1) "bob"       # 120 초과
```

### 📌 범위 기호 정리

| 표현       | 의미                           |
|------------|--------------------------------|
| `100 130`  | 100 이상, 130 이하              |
| `(100 130` | 100 초과, 130 이하              |
| `-inf`     | 음의 무한대 (가장 낮은 값 포함) |
| `+inf`     | 양의 무한대 (가장 높은 값 포함) |
| `(120 +inf`| 120 초과, 무한대까지             |



## 레디스에서 키를 관리하는 법


### 키의 자동 생성과 삭제
- 레디스에서는 특정 자료구조(stream, set, sorted set, hash)를 사용할 때, 명시적으로 키를 생성하거나 삭제하지 않아도 자동으로 관리된다.
  - 예: HSET user:1 name "Alice"를 실행하면 user:1 키가 자동으로 생성
- 모든 아이템을 삭제하게되면 키도 자동으로 삭제된다 (stream은 제외)
- 키가 없는 상태에서 키삭제 아이템 삭제, 자료구조 크기조회 등을 입력하면 에러대신, 키가있으나 아이탬이 없는것처럼 동작한다.
```bash
 > DEL mylist 
(integer) 0

 > LLEN mylist
(integer) 0

 > LPOP mylist
(nil)
```

### 키와 관련된 커맨드
- EXISTS: 특정 키의 존재 여부를 확인
  -  사용 예: EXISTS user:1
- KEYS : 레디스에 저장된 모든키를 조회하는 커맨드
> 🔔 **Note**: KEYS는 위험한 커멘드다 KEY가 만약 백만개 저장되어있다면, 메인스레드가 처리를 하면서 다른 요청이 다 블락이 되어버린다. 이로인해 의도하지않은 페일오버도 발생이 가능하니 주의하자
- SCAN : KEYS를 대체하여 키를 조회할 때 사용하는 커맨드다.
  - 커서를 기반으로 특정범위의 키만 조회하기에 안전하게 사용가능 
```bash
SCAN 0 MATCH user:*
1) "0"
2) 1) "user:1001"
   2) "user:1002"
   3) "user:1003"
```
  - 커서를 기반으로 특정범위의 키만 조회하기에 안전하게 사용가능 

- DEL : 키를 즉시 삭제, 삭제 작업은 **동기적으로 처리**  큰 키 삭제 시 성능에 영향이 있을 수 있다
- UNLINK : 키를 **비동기적으로 삭제**. 실제 메모리에서 제거되는 작업은 백그라운드에서 이루어지므로, **큰 키 삭제 시 성능 저하를 방지**할 수 있다
- EXPIRE : 키에 **TTL(Time-To-Live)**을 설정. 설정한 시간(초)이 지나면 키는 자동으로 만료
- TTL : 키의 남은 만료시간 조회
- FLUSHALL : 운영에서 사용하면 많은 개발자의 관심을 받을 수 있다


