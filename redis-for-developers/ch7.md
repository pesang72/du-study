# 레디스 데이터 백업 방법

## 레디스에서 데이터를 영구 저장하기

- AOF(Append only File) : 레디스 인스턴스가 처리한 모든 쓰기 작업을 차례대로 기록, 복원시에는 파일을 다시 읽어가며 데이터 세트 재구성
- RDB(Redis Database) : 일정 시점에 메모리에 저장된 데이터 전체를 저장 (snapshot)


## RDB 방식의 데이터 백업
###  개요
- Redis는 메모리의 데이터를 주기적으로 디스크에 저장하여 백업할 수 있다.
- 이 방식을 RDB(Redis Database) 방식이라고 하며, 일정 시점의 메모리 상태를 **스냅샷(snapshot)** 으로 저장한다.
- 저장된 파일은 `dump.rdb`이며, Redis 서버가 시작될 때 이 파일로부터 데이터를 로딩할 수 있다.

---

### 동작 방식
- Redis는 **포크(fork)** 를 통해 자식 프로세스를 생성하고, 자식 프로세스가 현재 메모리 상태를 `dump.rdb` 파일로 저장한다.
- 메인 프로세스는 클라이언트 요청을 계속 처리하므로, 서비스 중단 없이 백업이 가능하다.
- 디스크 저장이 끝나면 자식 프로세스는 종료된다.

---

### 설정 방법 (`redis.conf`)

    save 900 1      # 900초(15분) 동안 1개 이상의 키가 변경되면 저장
    save 300 10     # 300초(5분) 동안 10개 이상의 키가 변경되면 저장
    save 60 10000   # 60초(1분) 동안 10000개 이상의 키가 변경되면 저장

- 여러 `save` 조건을 동시에 설정할 수 있다.
- 모든 조건을 주석 처리하면 자동 스냅샷을 비활성화할 수 있다.

---

### 수동 저장 명령어

    SAVE      # 메인 프로세스가 직접 저장 (블로킹, 사용 자제)
    BGSAVE    # 자식 프로세스가 백그라운드에서 저장 (비블로킹, 일반적으로 사용)

- 일반적으로 `BGSAVE` 명령을 사용한다.
- 이미 저장 중이면 두 번째 `BGSAVE` 요청은 무시된다.

---

### 장점
- 저장 파일(`dump.rdb`)이 작고, 복구 시 빠르게 로딩됨
- 설정이 간단하고 주기적 스냅샷에 적합
- 운영 환경에서 CPU와 I/O 자원이 여유 있다면 효율적

---
### 단점
- 마지막 저장 시점 이후 변경된 데이터는 복구되지 않음
- 백업 주기가 길면 데이터 유실 가능성 증가
- BGSAVE 시 포크가 발생 → 메모리 이중화로 OOM(Out of Memory) 가능성 존재

---

### 복구 방법
1. Redis 서버를 중지한다.
2. 복구하려는 `dump.rdb` 파일을 설정된 디렉토리에 복사한다.
3. Redis 서버를 재시작하면 자동으로 해당 RDB 파일을 로딩한다.

> 🔸 실시간 무손실 백업이 필요할 경우, AOF(Append Only File)와 병행 사용을 고려하는 것이 좋다.


## AOF 방식의 데이터 백업
- AOF는 Redis에서 수행된 모든 **쓰기(write) 명령**을 **순차적으로 기록**하여 데이터를 복원하는 방식이다.
- RDB가 스냅샷을 저장하는 반면, AOF는 **명령어 로그**를 남겨 복원 시 **명령을 재실행**하는 구조다.
- 저장 파일은 일반적으로 `appendonly.aof`라는 이름을 가진다.
- 기본적으로 AOF는 비활성화 되어 있으며, `appendonly yes`를 설정해야 한다.

---
### AOF 기록 정책

    appendfsync always     # 모든 명령마다 fsync (가장 안전, 성능 저하)
    appendfsync everysec   # 1초마다 fsync (기본값, 성능과 안전성 균형)
    appendfsync no         # OS에 맡김 (속도 빠르지만 데이터 유실 위험 있음)

- 대부분의 운영 환경에서는 `everysec` 설정이 사용된다.

---

### 장점
- RDB에 비해 **더 잦은 백업 주기**, **더 적은 데이터 손실**
- AOF 파일은 텍스트 기반으로 사람이 직접 읽고 수정 가능
- Redis가 재시작되면 명령어를 재실행하여 데이터 복원

---

### 단점
- RDB보다 복구 속도가 느릴 수 있음 (명령어 실행 방식이므로)
- 파일 크기가 커질 수 있음 (rewrite 필요)
- 잘못된 명령도 기록되므로 명령어 단위 복구는 신중해야 함

---
### AOF rewrite
- AOF(Append Only File)는 Redis의 모든 **쓰기 명령을 순차적으로 저장**하지만,
  시간이 지남에 따라 명령이 많아져 **파일 크기가 커진다.**.
- AOF Rewrite는 기존 AOF 파일을 읽고, **현재 Redis 상태만 반영하는 최소한의 명령어 집합**으로 새 AOF 파일을 다시 생성하는 작업이다.
- 즉, **기존 명령 누적 → 현재 상태만 보존하는 명령어로 대체**.

#### 동작 개요

1. Redis는 **현재 메모리 상태**를 바탕으로 최소한의 Redis 명령어를 생성
2. 별도의 새로운 AOF 파일을 생성하고, 그곳에 명령어를 작성
3. Rewrite 도중 들어온 명령은 Redis 내부 **AOF 버퍼**에 임시로 보관
4. Rewrite가 완료되면 버퍼의 내용도 새 파일에 반영
5. 기존 AOF 파일을 교체하고 새로운 AOF 파일로 운영


---
## 백업을 사용할 때 주의할 점
- redis의 maxmemory 값을 너무 크게 설정한 경우, redis의 copy-on-write 동작으로 인해 OS메모리가 가득차는 문제로 서버가 다운될 수 있다.
- 