# 레디스를 메시지 브로커로 사용하기

# Redis 메시지 큐 vs 이벤트 스트림 비교

| 항목 | 메시지 큐 (Redis List, Pub/Sub) | 이벤트 스트림 (Redis Stream) |
|------|-------------------------------|-----------------------------|
| 목적 | 실시간 작업 처리, 소비 후 제거 | 이벤트 로그 기록, 소비 후 보존 가능 |
| 메시지 소비 | 1번만 소비 (1개 Consumer) | 여러 Consumer가 독립적으로 소비 가능 |
| 메시지 저장 | 일회성 / 짧게 유지 | 지속적 저장, 조회 가능 |
| Redis 구조 | List, Pub/Sub | XADD, XREAD, XGROUP, XACK |
| 소비 순서 보장 | O | O |
| 지연/재시도 | 직접 구현 필요 | 내장 지원 (ack, retry) |
| 주 사용처 | 작업 큐, 알림 | 이벤트 소싱, 로그, 비동기 처리 |

---

## 메시지 큐 (Message Queue)

- 생산자: `LPUSH` / `RPUSH`
- 소비자: `LPOP` / `BRPOP`
- Pub/Sub은 브로드캐스트 전송 (단, 메시지 유실 위험 있음)
- 메시지는 한 번 소비되면 사라짐

### 사용 예시
- 알림 전송
- 비동기 작업 처리
- 간단한 job queue

---

## 이벤트 스트림 (Redis Streams)

- Kafka와 유사한 **이벤트 로그 저장소**
- 메시지는 삭제되지 않고 여러 소비자가 읽을 수 있음
- 컨슈머 그룹, ack, 재처리 등 내장 기능 지원

### 사용 예시
- 사용자 행동 로그 수집
- 마이크로서비스 간 이벤트 전달
- 장애 복구 및 재처리 시스템
- 
### 주요 명령어
- `XADD` : 메시지 추가
- `XREAD`, `XREADGROUP` : 읽기
- `XACK` : 처리 확인
- `XDEL` : 삭제 (선택)

---

## 관련 상황별 비교

| 시나리오 | 메시지 큐 | 이벤트 스트림 |
|----------|------------|----------------|
| 주문 처리 후 이메일 전송 | ✅ 적합 | ❌ 과함 |
| 실시간 알림 Push | ✅ 적합 | ❌ 과함 |
| 사용자 로그 기록 | ❌ 제한적 | ✅ 적합 |
| 장애 후 재처리 필요 | ❌ 구현 복잡 | ✅ 내장 지원 |
| 여러 컨슈머가 처리 | ❌ 안됨 | ✅ 가능 |

# Redis의 PUB/SUB
-  발행자(Publisher)가 메시지를 채널(Channel)에 보내면, 해당 채널을 구독(Subscribe)하고 있던 수신자(Subscriber)들이 실시간으로 메시지를 수신하는 **일대다 비동기 메시징 시스템**
-  해당 채널을 구독(SUBSCRIBE) 중인 클라이언트에게 바로 메시지를 푸시하여 redis에는 데이터를 저장하지 않는다.
- 즉 구독자가 연결이 끊키는 경우, 데이터가 유실된다.

## 구독 테스트 

터미널 1 구독
```text
redis-cli
SUBSCRIBE news
```

터미널 2 발행
```text
redis-cli
PUBLISH news "hello from publisher!"
```

## 패턴기반 구독 테스트
```text
PSUBSCRIBE news.*

PUBLISH news.sports "goal!"
PUBLISH news.weather "sunny!"
```

## 클러스터 환경의 pub/sub
- 클러스터에 노드 A, B, C가 있다고 가정
- 구독자가 노드 A에 연결되어 있음
- 발행자가 노드 B에 `PUBLISH` 실행  
  → ❌ 메시지가 구독자에게 전달되지 않음!
- 채널은 하나의 노드에서 관리가 된다.


# 레디스의 list를 메시징 큐로 사용하기
**간단한 메시징 큐**로 매우 적합 `LPUSH`(왼쪽 삽입)과 `RPOP`(오른쪽 제거)를 조합하여 큐 형태로 사용가능

## list의 EX기능
`LPUSHX` / `RPUSHX`는 조건부 푸시 명령어로, 리스트가 **이미 존재하는 경우에만** 값을 삽입
```bash
> DEL myQueue
(integer) 1

> RPUSHX myQueue "task1"
(integer) 0  # myQueue가 없으므로 추가되지 않음

> RPUSH myQueue "task0"
(integer) 1

> RPUSHX myQueue "task1"
(integer) 2  # myQueue가 존재하므로 추가됨
```

## list의 블로킹 기능
블로킹 버전의 POP 명령어를 제공, 데이터가 없을 경우 대기한다.
```text
# 대기 중인 소비자
> BLPOP messageQueue 10
1) "messageQueue"
2) "msg1"
```
- BLPOP은 지정한 리스트에서 요소가 있을 때까지 최대 10초간 대기
- 데이터가 없으면 타임아웃까지 기다리며, 들어오면 즉시 반환
- 멀티 컨슈머 처리 시 유용



## list를 이용한 원형 큐 

RPOPLPUSH source destination
- source 리스트의 마지막 요소(RPOP) 를 꺼내서, 
- destination 리스트의 앞쪽(LPUSH) 에 넣는다.
```bash
RPUSH myQueue A B C
(integer) 3

RPOPLPUSH myQueue myQueue
"C"


앞에서 배웠던 다른방식
> LPUSH messageQueue "msg6"
> LTRIM messageQueue 0 4  # 가장 최근 5개만 유지
```

# Stream
Redis Streams는 Redis 5.0부터 도입된 **로그 구조 기반의 데이터 스트림**입니다.  
Kafka처럼 **메시지 스트리밍**, **이벤트 큐**, **비동기 처리** 등에 적합

## 특징을 좀 요약해보면

| 항목 | 설명 |
|------|------|
| 구조 | append-only log (메시지 로그 형식) |
| ID | 자동 생성 or 수동 지정 (`<millisecondsTime>-<sequence>`) |
| 메시지 | key-value 형태의 필드 묶음 |
| 소비자 그룹 | 지원 (Consumer Group) |
| 내구성 | 메시지 유지됨 (Pub/Sub과 달리) |
| 재처리 | 읽기 offset 기반으로 가능 |

## 소비자 그룹이란?
은 Kafka와 유사하게 하나의 스트림을 여러 소비자가 나누어 처리할 수 있도록 해주는 기능

| 역할            | 설명                                       |
| ------------- | ---------------------------------------- |
| 스트림           | 메시지가 계속 추가되는 로그                          |
| 그룹            | 여러 소비자가 메시지를 분산 처리                       |
| 소비자(consumer) | 그룹에 속한 개별 인스턴스 (예: consumer1, consumer2) |
| 메시지 상태        | **대기 중**, **처리 중**, **ack됨**의 상태를 가짐     |



##  메시지 추가 (XADD)
```bash
XADD mystream * userId 123 action "login"
```
- *은 서버가 자동으로 ID 부여
- 필드-값 쌍 형태로 메시지를 구성

### 여기서 메시지 ID란?
Redis Streams의 메시지 ID는 `timestamp-sequence` 구조

## XGROUP CREATE (소비자 그룹 생성)
```bash
 > XGROUP CREATE mystream mygroup $ MKSTREAM
```
| 항목 | 설명                             |
|------|--------------------------------|
| mystream | 	대상 스트림                        |
| mygroup | 생성할 소비자 그룹 이름                  |
| $ | 이후 들어오는 새 메시지만 처리 (이전 메시지는 무시) |
| MKSTREAM | 스트림이 없으면 생성 (생략하면 에러 발생)       |


## 소비자 그룹으로 메시지 읽기
```bash
XREADGROUP GROUP mygroup consumer1 COUNT 10 BLOCK 2000 STREAMS mystream >
```
| 인자                        | 의미                      |
| ------------------------- | ----------------------- |
| `GROUP mygroup consumer1` | 그룹명 + 소비자 ID            |
| `COUNT 10`                | 최대 10개 메시지 읽기           |
| `BLOCK 2000`              | 최대 2000ms(2초) 동안 대기     |
| `STREAMS mystream`        | 읽을 스트림 이름               |
| `>`                       | 아직 읽지 않은 **새 메시지만** 가져옴 |
- '>' 대신 특정 ID 사용 시 PENDING 메시지 재시도 가능


## 그럼 언제 Redis Stream을 사용하나?
| 상황                          | 설명                             |
| --------------------------- | ------------------------------ |
| 메시지 순서 보장이 필요한 경우           | 메시지 ID로 처리 순서 명확               |
| 실패한 메시지를 재처리해야 하는 경우        | `XPENDING` → `XCLAIM`으로 재전송 가능 |
| 소비자 간 병렬 처리                 | 소비자 그룹으로 메시지 분산 처리             |
| Pub/Sub보다 신뢰성 있는 시스템이 필요할 때 | 메시지 유실 없음, ack/retry 지원        |
